/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2017 MDZ (info@ccu-historian.de)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package mdz.ccuhistorian

import java.util.Date
import java.util.List;
import java.util.logging.Logger
import java.util.logging.Level
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.sql.ResultSet
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovy.util.logging.Log
import org.h2.tools.Server
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.hc.persistence.Storage
import mdz.hc.timeseries.TimeSeries
import mdz.Exceptions
import mdz.ccuhistorian.eventprocessing.Preprocessor

@Log
public class Database implements Storage {

	DatabaseConfig config
	Base base
	
	private Server webServer, tcpServer, pgServer
	private Sql db
	private final static long TIMESERIES_COPY_INTERVAL=30l*24*60*60*1000 // 1 month
	private String backupLast
	private ScheduledFuture backupFuture
	
	// Prefixes of history tables
	private final static String TABLE_PREFIX_DOUBLE='D_' // VALUE columns is of type DOUBLE
	private final static String TABLE_PREFIX_STRING='C_' // VALUE column is of type VARCHAR
	
	// Prefixes of history tables up to and including V0.6.0-dev4
	private final static String TABLE_PREFIX_DEVICE_DOUBLE='V_'
	private final static String TABLE_PREFIX_DEVICE_STRING='VS_'
	private final static String TABLE_PREFIX_SYSVAR_DOUBLE='S_'
	private final static String TABLE_PREFIX_SYSVAR_STRING='SS_'

	private final static String CONFIG_DATABASE_VERSION='internal.databaseVersion'
		
	public Database(DatabaseConfig config, Base base) {
		this.config=config
		this.base=base
		config.logDebug()
		// start database
		Exceptions.catchToLog(log) { connect() }
	}
	
	private void connect() {
		if (db==null) {
			try {
				log.info 'Connecting to database'
				db=Sql.newInstance(getUrl(config), config.user, config.password, 'org.h2.Driver')
				prepareDatabase()
				if (config.webEnable) {
					log.info 'Starting database web server'
					def args=['-ifExists', '-webPort', config.webPort as String, '-webAdminPassword', config.password]
					if (config.webAllowOthers) args << '-webAllowOthers'
					webServer=Server.createWebServer(args as String[])
					webServer.start()
					log.fine "Database management URL: ${webServer.getURL()}"
				}
				if (config.tcpEnable) {
					log.info 'Starting database TCP server'
					def args=['-ifExists', '-tcpPort', config.tcpPort as String]
					if (config.tcpAllowOthers) args << '-tcpAllowOthers'
					tcpServer=Server.createTcpServer(args as String[])
					tcpServer.start()
					log.info "Databse TCP server port: ${tcpServer.getPort()}"
				}
				if (config.pgEnable) {
					log.info 'Starting database PG server'
					def args=['-ifExists', '-pgPort', config.pgPort as String]
					if (config.pgAllowOthers) args << '-pgAllowOthers'
					pgServer=Server.createPgServer(args as String[])
					pgServer.start()
					log.info "PG ODBC driver port: ${pgServer.getPort()}"
				}
				if (config.backup) {
					Calendar cal=Calendar.instance
					backupLast=formatTimestamp(config.backup, cal.time)
					long initialDelay=-cal.timeInMillis
					cal[Calendar.MINUTE]=0
					cal[Calendar.SECOND]=0
					cal[Calendar.MILLISECOND]=0
					cal.add(Calendar.HOUR_OF_DAY, 1)
					initialDelay+=cal.timeInMillis
					backupFuture=base.executor.scheduleAtFixedRate(
						this.&checkBackupTime,
						initialDelay, 3600*1000, TimeUnit.MILLISECONDS)
				}
			} catch (Exception ex) {
				// partial initialization clean up
				stop();	throw ex
			}
		}
	}
	protected synchronized stop() {
		if (db) {
			log.info 'Stopping database'
			if (backupFuture) { backupFuture.cancel(false); backupFuture=null }
			if (pgServer) { pgServer.stop(); pgServer=null }
			if (tcpServer) { tcpServer.stop(); tcpServer=null }
			if (webServer) { webServer.stop(); webServer=null }
			db.close(); db=null 
		}
	}
	
	public synchronized transactional(Closure cl) {
		cl=(Closure) cl.clone()
		cl.delegate=this
		db.connection.autoCommit=false
		try {
			cl(this)
			db.connection.commit()
			db.connection.autoCommit=true
		} catch(Exception e) {
			try {
				db.connection.rollback()
				db.connection.autoCommit=true
			} catch(Exception e2) {
				// rollback failed
				Exceptions.logTo(log, Level.SEVERE, e2)
			}
			throw e;
		}
	}

	@Override
	public synchronized Date getFirstTimestamp(DataPoint dp) {
		connect()
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		def row=db.firstRow("SELECT MIN(TS) FROM $dp.historyTableName" as String)
		row?(Date)row[0]:null
	}

	@Override
	public synchronized ProcessValue getLast(DataPoint dp) {
		connect()
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		def row=db.firstRow("SELECT TS, VALUE, STATE FROM $dp.historyTableName WHERE TS=(SELECT MAX(TS) FROM $dp.historyTableName)" as String)
		row?new ProcessValue((Date)row[0], row[1], (int)row[2]):null
	}
	
	/**
	 * getTimeSeriesRaw returns raw entries from database without any interpolation, whose timestamps fulfill
	 * the condition begin<=TS<end.
	 */
	@Override
	public synchronized TimeSeries getTimeSeriesRaw(DataPoint dp, Date begin, Date end) {
		connect()
		log.finer "Database: Retrieving raw time series for ${dp.id}, begin: $begin, end: $end"
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		TimeSeries ts=new TimeSeries(dp)
		db.query("SELECT TS, VALUE, STATE FROM $dp.historyTableName WHERE TS>=? AND TS<? ORDER BY TS" as String,
				[(Object)begin, (Object)end]) { ResultSet rs ->
					ts.add(rs);
				}
		log.finer "Database: Retrieved $ts.size points"
		ts
	}

	/**
	 * getTimeSeries returns entries from database with interpolated values on the boundaries (begin and end).
	 */
	@Override
	public synchronized TimeSeries getTimeSeries(DataPoint dp, Date begin, Date end) {
		connect()
		log.finer "Database: Retrieving time series for ${dp.id}, begin: $begin, end: $end"
		if (!dp.historyTableName) {
			throw new Exception('Table name of data point is not set')
		}
		if (dp.historyString) {
			throw new Exception("Data point ${dp.id} is not numeric")
		}

		// extent time range to get boundary values
		Date beginExt=getFirstBeforeIncl(dp, begin)
		if (beginExt==null) {
			beginExt=begin
		}
		Date endExt=getFirstAfterIncl(dp, end)
		if (endExt==null) {
			endExt=end
		}
		
		// retrieve time series (add 1 ms to include boundary value)
		TimeSeries ts=getTimeSeriesRaw(dp, beginExt, new Date(endExt.time+1))
		if (ts.size()==0) {
			log.finer "Database: Returning 0 points"
			return ts
		}

		// interpolate?
		Preprocessor.Type preprocType=Preprocessor.Type.ofDataPoint(dp)
		boolean interpLinear=dp.continuous && !preprocType.clearsContinuous()

		// calculate boundary values
		if (interpLinear) {
			// * linear interpolation *
			 
			// cut at begin of time range
			ProcessValue entry1=ts[0]
			if (entry1.timestamp<begin) {
				// exists another entry?
				if (ts.size()>=2) {
					// interpolate linear
					ProcessValue entry2=ts[1]
					ProcessValue interpEntry=interpolate(entry1, entry2, begin)
					if (interpEntry!=null) {
						// replace first entry
						ts[0]=interpEntry
					}
				} else {
					// remove the out of range entry 
					ts.remove(0)
				}
			}

			// cut at end of time range
			if (ts.size()==0) {
				log.finer "Database: Returning 0 points"
				return ts
			}
			ProcessValue entry2=ts[ts.size()-1]
			if (entry2.timestamp>end) {
				// exists another entry?
				if (ts.size()>=2) {
					// interpolate linear
					entry1=ts[ts.size()-2]
					ProcessValue interpEntry=interpolate(entry1, entry2, end)
					if (interpEntry!=null) {
						// replace last entry
						ts[ts.size()-1]=interpEntry
					}
				} else {
					// remove the out of range entry
					ts.remove(ts.size()-1)
				}
			}
			
		} else {
			// * hold value interpolation *
			
			// cut at begin of time range
			ProcessValue firstEntry=ts[0]
			if (firstEntry.timestamp<begin) {
				// move first entry to begin of time range
				firstEntry.timestamp=begin
				ts[0]=firstEntry
			}

			// cut at end of time range
			ProcessValue lastEntry=ts[ts.size()-1]
			if (lastEntry.timestamp<end) {
				// last entry before end: hold value until now or end timestamp
				def holdEnd=new Date()
				if (holdEnd>end) {
					holdEnd=end
				}
				ts.add(holdEnd.time, lastEntry.value, lastEntry.state)
				
			} else if (lastEntry.timestamp==end) {
				// last entry exactly on end: do nothing
				
			} else {
				// last entry after end: remove entry
				ts.remove(ts.size()-1)
				// hold previous value until end
				if (ts.size()>0) {
					lastEntry=ts[ts.size()-1]
					ts.add(end.time, lastEntry.value, lastEntry.state)
				}
			}
		}
		log.finer "Database: Returning $ts.size points"
		ts
	}
	
	private ProcessValue interpolate(ProcessValue first, ProcessValue second, Date ts) {
		double tdiff=second.timestamp.time-first.timestamp.time
		if (tdiff==0) {
			return null
		}
		double slope=(second.value-first.value)/tdiff
		double value=(ts.time-first.timestamp.time)*slope+first.value
		new ProcessValue(ts, value, first.state)
	}

	@Override
	public synchronized int getCount(DataPoint dp, Date startTime, Date endTime) {
		connect()
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		if (startTime!=null) {
			if (endTime!=null)
				(int)db.firstRow("SELECT COUNT(*) FROM $dp.historyTableName WHERE TS>=? AND TS<?" as String, startTime, endTime)[0]
			else
				(int)db.firstRow("SELECT COUNT(*) FROM $dp.historyTableName WHERE TS>=?" as String, startTime)[0]
		} else {
			if (endTime!=null)
				(int)db.firstRow("SELECT COUNT(*) FROM $dp.historyTableName WHERE TS<?" as String, endTime)[0]
			else
				(int)db.firstRow("SELECT COUNT(*) FROM $dp.historyTableName" as String)[0]
		}
	}
		
	@Override
	public synchronized List<DataPoint> getDataPoints() {
		connect()
		log.finer 'Database: Getting data points'
		db.rows('SELECT * FROM DATA_POINTS ORDER BY INTERFACE, DISPLAY_NAME, ADDRESS, IDENTIFIER').collect { getRowAsDataPoint(it) }
	}

	@Override
	public synchronized List<DataPoint> getDataPointsOfInterface(String itfName) {
		connect()
		db.rows(
			"""SELECT * FROM DATA_POINTS WHERE INTERFACE=$itfName
			ORDER BY DISPLAY_NAME, ADDRESS, IDENTIFIER"""
		).collect { getRowAsDataPoint(it) }
	}

	@Override
	public synchronized DataPoint getDataPoint(int idx) {
		connect()
		log.finer "Database: Getting data point with index $idx"
		def row=db.firstRow("SELECT * FROM DATA_POINTS WHERE DP_ID=$idx")
		row?getRowAsDataPoint(row):null
	}
	
	@Override
	public synchronized DataPoint getDataPoint(DataPointIdentifier id) {
		connect()
		log.finer "Database: Getting data point with id $id"
		def row=db.firstRow("""SELECT * FROM DATA_POINTS WHERE
			INTERFACE=$id.interfaceId AND ADDRESS=$id.address AND IDENTIFIER=$id.identifier""")
		row?getRowAsDataPoint(row):null
	}

	@Override
	public synchronized void createDataPoint(DataPoint dp) throws Exception {
		connect()
		normalizeDataPoint(dp)
		dp.historyTableName=getDataPointTableName(dp)
		createDataPointTable(dp)
		createDataPointEntry(dp)
	}

	@Override
	public synchronized void updateDataPoint(DataPoint dp) {
		connect()
		normalizeDataPoint(dp)
		if (isStringTable(dp.historyTableName)!=dp.historyString) {
			log.fine "Database: Data point $dp.id has changed value type"
			dp.historyTableName=getDataPointTableName(dp)
			createDataPointTable(dp)
		}
		log.fine "Database: Updating data point description: $dp"
		
		// create JSON for custom attribute
		String custom=JsonOutput.toJson(dp.attributes.custom)

		if (db.executeUpdate("""UPDATE DATA_POINTS SET
			TABLE_NAME=$dp.historyTableName, STATE=$dp.managementFlags,
			
			INTERFACE=$dp.id.interfaceId, ADDRESS=$dp.id.address,
			IDENTIFIER=$dp.id.identifier,
	
			PREPROC_TYPE=$dp.attributes.preprocType, PREPROC_PARAM=$dp.attributes.preprocParam,
	
			DISPLAY_NAME=$dp.attributes.displayName, ROOM=$dp.attributes.room, 
			FUNCTION=$dp.attributes.function, COMMENT=$dp.attributes.comment,
			CUSTOM=$custom,
			
			PARAM_SET=$dp.attributes.paramSet,	TAB_ORDER=$dp.attributes.tabOrder,
			MAXIMUM=$dp.attributes.maximum, UNIT=$dp.attributes.unit,
			MINIMUM=$dp.attributes.minimum, CONTROL=$dp.attributes.control,
			OPERATIONS=$dp.attributes.operations, FLAGS=$dp.attributes.flags,
			TYPE=$dp.attributes.type, DEFAULT_VALUE=$dp.attributes.defaultValue
							
			WHERE DP_ID=$dp.idx""")!=1)
			throw new Exception("Update of data point ${dp.id} failed")
	}

	@Override
	public synchronized void deleteDataPoint(DataPoint dp) {
		connect()
		log.fine "Database: Deleting data point ${dp.id}"
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		if (dp.idx==null)
			throw new Exception('ID of data point is not set')
		db.execute "DROP TABLE IF EXISTS $dp.historyTableName" as String
		// delete table of other data type also
		dp.historyString=!dp.historyString
		String tableName=getDataPointTableName(dp)
		dp.historyString=!dp.historyString
		db.execute "DROP TABLE IF EXISTS $tableName" as String
		db.execute "DELETE FROM DATA_POINTS WHERE DP_ID=$dp.idx"
	} 
	 
	@Override
	public synchronized int deleteTimeSeries(DataPoint dp, Date startTime, Date endTime) {
		connect()
		log.fine "Database: Deleting timeseries of data point ${dp.id} (start time: $startTime, end time: $endTime)"
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		int affectedRows
		if (startTime!=null) {
			if (endTime!=null)
				affectedRows=db.executeUpdate "DELETE FROM $dp.historyTableName WHERE TS>=? AND TS<?" as String, startTime, endTime
			else 
				affectedRows=db.executeUpdate "DELETE FROM $dp.historyTableName WHERE TS>=?" as String, startTime
		} else {
			if (endTime!=null)
				affectedRows=db.executeUpdate "DELETE FROM $dp.historyTableName WHERE TS<?" as String, endTime
			else 
				affectedRows=db.executeUpdate "DELETE FROM $dp.historyTableName" as String
		}
		log.fine "$affectedRows entries deleted"
		affectedRows
	}
	
	@Override
	public synchronized int copyTimeSeries(DataPoint dstDp, DataPoint srcDp, Date startTime, Date endTime, Date newStartTime) {
		connect()
		log.fine "Database: Copying timeseries of data point ${srcDp.id} to data point "+
			"${dstDp.id} (start time: $startTime, end time: $endTime, new start time: $newStartTime)"
		if (!dstDp.historyTableName)
			throw new Exception('Table name of destination data point is not set')
		if (!srcDp.historyTableName)
			throw new Exception('Table name of source data point is not set')
		if (isStringTable(dstDp.historyTableName)!=isStringTable(srcDp.historyTableName))
			throw new Exception('Data types does not match')
		if (startTime==null) startTime=getFirstTimestamp(srcDp)
		int counter=0
		if (startTime!=null) {
			if (endTime==null) endTime=getLast(srcDp).timestamp
			long timeMod=0
			if (newStartTime!=null) timeMod=newStartTime.time-startTime.time
			while (startTime<endTime) {
				Date tmpEndTime=(Date)startTime.clone()
				tmpEndTime.time+=TIMESERIES_COPY_INTERVAL
				TimeSeries timeSeries=getTimeSeriesRaw(srcDp, startTime, tmpEndTime)
				counter+=timeSeries.size
				timeSeries.each { ProcessValue e ->
					e.timestamp.time+=timeMod
					db.executeInsert "INSERT INTO $dstDp.historyTableName (TS, VALUE, STATE) VALUES (?, ?, ?)" as String, [e.timestamp, e.value, e.state]
				}
				startTime=tmpEndTime
			}
		}
		log.fine "$counter entries copied"
		counter
	}
	
	@Override
	public synchronized int replaceTimeSeries(DataPoint dstDp, Iterable<ProcessValue> srcSeries, Date startTime, Date endTime) throws Exception {
		connect()
		log.fine "Database: Replacing timeseries of data point ${dstDp.id} (start time: $startTime, end time: $endTime)"
		// store all entries in temporary table
		createTemporaryTable('REPLACE_TEMP', dstDp.historyString)
		int counter=0
		try {
			srcSeries.each { ProcessValue pv ->
				def value=TimeSeries.getNormalizedValue(pv.value)
				db.executeInsert 'INSERT INTO REPLACE_TEMP (TS, VALUE, STATE) VALUES (?, ?, ?)', [pv.timestamp, value, pv.state]
				counter++
			}
			// delete time range
			deleteTimeSeries(dstDp, startTime, endTime)
			// insert all entries from the temporary table
			db.executeInsert "INSERT INTO $dstDp.historyTableName SELECT TS, VALUE, STATE FROM REPLACE_TEMP" as String
		} finally {
			try { db.execute "DROP TABLE IF EXISTS REPLACE_TEMP"
			} catch (Exception e) {	/* ignore */ }
		}
		log.fine "$counter entries inserted"
		counter
	}

	public void normalizeDataPoint(DataPoint dp) {
		def a=dp.attributes
		if (a.containsKey('minimum'))
			a.minimum=asDoubleOrNull(a.minimum)
		if (a.containsKey('maximum'))
			a.maximum=asDoubleOrNull(a.maximum)
		if (a.containsKey('defaultValue'))
			a.defaultValue=asDoubleOrNull(a.defaultValue)
	}

	public synchronized DataPoint prepareDataPoint(DataPoint dataPoint) {
		boolean valueIsString=dataPoint.historyString
		// exists the data point
		DataPoint dataPointDb=getDataPoint(new DataPointIdentifier(
			dataPoint.id.interfaceId, dataPoint.id.address, dataPoint.id.identifier
		))
		if (dataPointDb) {
			// changed the data type?
			if (isStringTable(dataPointDb.historyTableName)!=valueIsString) {
				// update table name
				dataPointDb.historyTableName=getDataPointTableName(dataPointDb)
				updateDataPoint(dataPointDb)
				// if necessary, create a table
				createDataPointTable(dataPointDb)
			}	
			dataPoint=dataPointDb
		} else {
			// create data point and table
			createDataPoint(dataPoint)
		}
		dataPoint
	}
	
	@Override
	public synchronized void consume(Event e) throws Exception {
		connect()
		log.finer "Database: Inserting ($e.pv.timestamp, $e.pv.value, $e.pv.state) into $e.dataPoint.historyTableName"
		if (!e.dataPoint.historyTableName)
			throw new Exception('Table name of data point is not set')
		def value=TimeSeries.getNormalizedValue(e.pv.value)
		db.executeInsert "INSERT INTO $e.dataPoint.historyTableName (TS, VALUE, STATE) VALUES (?, ?, ?)" as String,
			[e.pv.timestamp, value, e.pv.state]
	}

	public static void compact(DatabaseConfig config) {	
		log.info 'Starting compaction of database'
		config.logDebug()
		String tmpFileName='temp.sql'
		log.info "Dumping database to $tmpFileName"
		org.h2.tools.Script.main('-url', getUrl(config), '-user', config.user, '-password', config.password, 
			'-script', tmpFileName, '-options', 'DROP')
		log.fine 'Deleting database files'
		org.h2.tools.DeleteDbFiles.execute(config.dir, config.name, true)
		log.info "Restoring database from $tmpFileName"
		org.h2.tools.RunScript.execute(getUrl(config), config.user, config.password, tmpFileName, null, false)
		log.fine "Deleting $tmpFileName"
		File tmpFile=[tmpFileName]
		if (tmpFile.exists()) tmpFile.delete()
		log.info 'Compaction of database completed'
	}

	public static void createScript(DatabaseConfig config, String fileName) {
		log.info 'Starting dump of database'
		config.logDebug()
		log.fine "Cleaning up database"
		org.h2.tools.RunScript.main('-url', getUrl(config), '-user', config.user, '-password', config.password,
			'-script', 'cleanup.sql')
		log.fine "Dumping database to $fileName"
		org.h2.tools.Script.main('-url', getUrl(config), '-user', config.user, '-password', config.password, 
			'-script', fileName, '-options', 'DROP', 'SCHEMA', 'PUBLIC')
		log.info 'Dump of database completed'
	}
	
	public static void runScript(DatabaseConfig config, String fileName) {
		log.info "Running script $fileName on database"
		config.logDebug()
		org.h2.tools.RunScript.main('-url', getUrl(config), '-user', config.user, '-password', config.password,
			'-script', fileName)
		log.info 'Script run completed'
	}

	public Date getFirstBeforeIncl(DataPoint dp, Date ts) {
		def row=db.firstRow("""SELECT MAX(TS) FROM $dp.historyTableName WHERE TS<=?""" as String, ts)
		row?(Date)row[0]:null
	}

	public Date getFirstAfterIncl(DataPoint dp, Date ts) {
		def row=db.firstRow("""SELECT MIN(TS) FROM $dp.historyTableName WHERE TS>=?""" as String, ts)
		row?(Date)row[0]:null
	}

	private static Double asDoubleOrNull(value) {
		if (value instanceof Boolean)
			((Boolean) value).booleanValue() ? 1.0D : 0.0D;
		else if (value instanceof Number)
			((Number) value).doubleValue();
		else
			null
	}

	private DataPoint getRowAsDataPoint(def row) {
		// decode custom attribute
		def custom
		if (!row.CUSTOM) {
			custom=[:]
		} else {
			try {
				custom=new JsonSlurper().parseText(row.CUSTOM)
				if (!(custom instanceof Map)) {
					log.warning "Invalid content in table DATA_POINTS field CUSTOM (expected JSON object): $row"
					custom=[:]
				}
			} catch (e) {
				log.warning "Invalid content in table DATA_POINTS field CUSTOM (expected JSON): $row"
				custom=[:]
			}
		}

		// create DataPoint
		new DataPoint(
			idx:row.DP_ID, historyTableName:row.TABLE_NAME,
			managementFlags:(row.STATE?:0),
			
			id: new DataPointIdentifier(row.INTERFACE, row.ADDRESS,	row.IDENTIFIER),
			
			attributes: [
				preprocType:row.PREPROC_TYPE, preprocParam:row.PREPROC_PARAM,
				
				displayName:row.DISPLAY_NAME, room:row.ROOM,
				function:row.FUNCTION, comment:row.COMMENT,
				custom:custom,
			
				paramSet:row.PARAM_SET,	tabOrder:row.TAB_ORDER,
				maximum:row.MAXIMUM, unit:row.UNIT,
				minimum:row.MINIMUM, control:row.CONTROL,
				operations:row.OPERATIONS, flags:row.FLAGS,
				type:row.TYPE, defaultValue:row.DEFAULT_VALUE
			]
		)
	}

	private void createDataPointTable(DataPoint dp) {
		log.fine "Database: Creating table $dp.historyTableName"
		String valueType=dp.historyString?'VARCHAR':'DOUBLE'
		db.execute """CREATE TABLE IF NOT EXISTS $dp.historyTableName (TS DATETIME, VALUE $valueType, STATE INT);
			CREATE INDEX IF NOT EXISTS ${dp.historyTableName}_IDX ON $dp.historyTableName (TS)""" as String
	}

	private void createTemporaryTable(String tableName, boolean historyString) {
		log.fine "Database: Creating temporary table $tableName"
		String valueType=historyString?'VARCHAR':'DOUBLE'
		db.execute """CREATE CACHED TEMP TABLE $tableName (TS DATETIME, VALUE $valueType, STATE INT);
			CREATE INDEX ${tableName}_IDX ON $tableName (TS)""" as String
	}

	private void createDataPointEntry(DataPoint dp) {
		normalizeDataPoint(dp)
		log.fine "Database: Inserting into DATA_POINTS: $dp"
		
		// create JSON for custom attribute
		String custom=JsonOutput.toJson(dp.attributes.custom)
		
		db.executeInsert """INSERT INTO DATA_POINTS (
				DP_ID, TABLE_NAME, STATE,
				INTERFACE, ADDRESS, IDENTIFIER,
				PREPROC_TYPE, PREPROC_PARAM,
				DISPLAY_NAME, ROOM, FUNCTION, COMMENT, CUSTOM,
				PARAM_SET, TAB_ORDER, MAXIMUM, UNIT, MINIMUM, CONTROL,
				OPERATIONS, FLAGS, TYPE, DEFAULT_VALUE
			) VALUES (
				$dp.idx, $dp.historyTableName, $dp.managementFlags,
				$dp.id.interfaceId, $dp.id.address, $dp.id.identifier,
				$dp.attributes.preprocType, $dp.attributes.preprocParam,
				$dp.attributes.displayName, $dp.attributes.room,
				$dp.attributes.function, $dp.attributes.comment,
				$custom,
				$dp.attributes.paramSet, $dp.attributes.tabOrder, 
				$dp.attributes.maximum, $dp.attributes.unit, $dp.attributes.minimum, 
				$dp.attributes.control,	$dp.attributes.operations, 
				$dp.attributes.flags, $dp.attributes.type, 
				$dp.attributes.defaultValue
			)"""
	}
	
	private static String getUrl(DatabaseConfig config) {
		if (!config.dir.isEmpty() && !config.dir.endsWith('/')) config.dir+='/'
		// DB_CLOSE_ON_EXIT=FALSE: the database is explicitly closed.
		// BUILTIN_ALIAS_OVERRIDE=TRUE: no error, if overriding aliases.
		// MAX_COMPACT_COUNT=0: disables chunk rewriting. faster database shutdown.
		"jdbc:h2:file:$config.dir$config.name;DB_CLOSE_ON_EXIT=FALSE;BUILTIN_ALIAS_OVERRIDE=TRUE;MAX_COMPACT_COUNT=0"
	}

	private static String getDataPointTableName(DataPoint dp) {
		String tableName=dp.historyString?TABLE_PREFIX_STRING:TABLE_PREFIX_DOUBLE
		tableName+="${dp.id.interfaceId}_${dp.id.address}_${dp.id.identifier}"
		tableName.toUpperCase().replaceAll(/[^A-Z0-9_]/, '_')
	}
	
	private static boolean isStringTable(String tableName) {
		// up to and including V0.6.0-dev4
		tableName.startsWith(TABLE_PREFIX_DEVICE_STRING) || tableName.startsWith(TABLE_PREFIX_SYSVAR_STRING) ||
		// from V0.6.0-dev5 upwards
		tableName.startsWith(TABLE_PREFIX_STRING)
	}
	
	public static String formatTimestamp(String pattern, Date timestamp) {
		Calendar cal=Calendar.instance
		cal.time=timestamp
		pattern.replaceAll(~/%([%YMWDh])/, { List<String> captures ->
			switch (captures[1]) {
			case 'Y': cal[Calendar.YEAR]; break
			case 'M': ((cal[Calendar.MONTH]-Calendar.JANUARY+1) as String).padLeft(2, '0'); break
			case 'W': (cal[Calendar.WEEK_OF_YEAR] as String).padLeft(2, '0'); break
			case 'D': (cal[Calendar.DAY_OF_MONTH] as String).padLeft(2, '0'); break
			case 'h': (cal[Calendar.HOUR_OF_DAY] as String).padLeft(2, '0'); break
			case '%': '%'; break
			}
		})
	}
	
	private synchronized void checkBackupTime() {
		if (!backupFuture) return
		Exceptions.catchToLog(log) {
			log.finest "Checking backup time"
			String nextBackup=formatTimestamp(config.backup, new Date())
			if (backupLast!=nextBackup)
				createBackup nextBackup
			backupLast=nextBackup
		}
	}
	
	private synchronized void createBackup(String fileName) {
		connect()
		log.info "Creating backup of database to file $fileName"
		long start=System.currentTimeMillis()
		switch (fileName) {
		case ~/(?i).*\.zip/:
			db.execute "SCRIPT DROP TO $fileName COMPRESSION ZIP"
			break
		case ~/(?i).*\.gz/:
			db.execute "SCRIPT DROP TO $fileName COMPRESSION GZIP"
			break
		default:	
			db.execute "SCRIPT DROP TO $fileName"
		}
		log.fine "Backup created in ${(System.currentTimeMillis()-start)/1000} seconds"
	} 
	
	private void prepareDatabase() {
		log.fine 'Preparing database'

		// create configuration table
		db.execute 'CREATE TABLE IF NOT EXISTS CONFIG (NAME VARCHAR(128) NOT NULL, "VALUE" VARCHAR(65536))'
		
		// check database version
		if (getConfig(CONFIG_DATABASE_VERSION)==null) {
			
			// new database detected
			db.execute '''CREATE TABLE DATA_POINTS (
				DP_ID INT IDENTITY,	TABLE_NAME VARCHAR NOT NULL,
				STATE INT,
				
				INTERFACE VARCHAR NOT NULL, ADDRESS VARCHAR NOT NULL,
				IDENTIFIER VARCHAR NOT NULL,
	
				PREPROC_TYPE INT, PREPROC_PARAM DOUBLE,
	
				DISPLAY_NAME VARCHAR, ROOM VARCHAR, FUNCTION VARCHAR, COMMENT VARCHAR,
				CUSTOM VARCHAR DEFAULT '{}',
				
				PARAM_SET VARCHAR, TAB_ORDER INT,
				MAXIMUM DOUBLE, UNIT VARCHAR,
				MINIMUM DOUBLE, CONTROL VARCHAR,
				OPERATIONS INT, FLAGS INT,
				TYPE VARCHAR, DEFAULT_VALUE DOUBLE
			); CREATE UNIQUE INDEX IF NOT EXISTS DATA_POINTS_IDX
				ON DATA_POINTS (INTERFACE, ADDRESS, IDENTIFIER)'''

			// add database functions
			db.execute 'CREATE ALIAS TS_TO_UNIX DETERMINISTIC FOR "mdz.ccuhistorian.DatabaseExtensions.TS_TO_UNIX"'
			db.execute 'CREATE ALIAS UNIX_TO_TS DETERMINISTIC FOR "mdz.ccuhistorian.DatabaseExtensions.UNIX_TO_TS"'
			
			// set current version (keep aligned with database migration)
			setConfig(CONFIG_DATABASE_VERSION, '4')
			
		} else {
			// migrate database
			
			// initialize continuous flag for existing data points
			migrateTo(1, '''UPDATE DATA_POINTS SET STATE=BITOR(STATE, 0x80) WHERE IDENTIFIER IN (
			'ACTUAL_HUMIDITY', 'ACTUAL_TEMPERATURE', 'AIR_PRESSURE', 'BRIGHTNESS',
			'CURRENT', 'ENERGY_COUNTER', 'FREQUENCY', 'HUMIDITY', 'ILLUMINATION',
			'LUX', 'POWER', 'RAIN_COUNTER', 'SUNSHINEDURATION', 'TEMPERATURE',
			'VOLTAGE', 'WIND_SPEED')''')
			
			// add attribute 'custom'
			migrateTo(2, '''ALTER TABLE DATA_POINTS ADD IF NOT EXISTS CUSTOM VARCHAR DEFAULT '{}' AFTER COMMENT''')
			
			// replace invalid values for attribute 'custom'
			migrateTo(3, '''UPDATE DATA_POINTS SET CUSTOM='{}' WHERE CUSTOM='null' OR CUSTOM IS NULL''')
			
			// config values up to 1M
			migrateTo(4, '''ALTER TABLE CONFIG ALTER COLUMN "VALUE" VARCHAR(65536)''')
		}
	}
	
	public synchronized String getConfig(String name) {
		connect()
		def row=db.firstRow('SELECT VALUE FROM CONFIG WHERE NAME=?', name)
		String value=row?(String)(row[0]):null
		log.fine("Read config: $name=$value")
		value
	}
	
	public synchronized void setConfig(String name, String value) {
		connect()
		log.fine("Writing config: $name=$value")
		int cnt=db.executeUpdate('UPDATE CONFIG SET VALUE=? WHERE NAME=?', value, name)
		if (cnt==0) {
			db.executeUpdate('INSERT INTO CONFIG VALUES (?, ?)', name, value)
		}
	}
	
	private void migrateTo(int toVersion, String sql) {
		if (getConfig(CONFIG_DATABASE_VERSION).toInteger() < toVersion) {
			log.info "Migrating database to version $toVersion"
			db.execute sql
			setConfig(CONFIG_DATABASE_VERSION, toVersion.toString())
		}
	}
}
