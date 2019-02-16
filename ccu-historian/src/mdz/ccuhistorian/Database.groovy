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
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.sql.ResultSet

import groovy.sql.Sql
import groovy.transform.CompileStatic
import groovy.util.logging.Log

import org.h2.tools.Server

import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.hc.persistence.Storage
import mdz.hc.timeseries.TimeSeries
import mdz.Exceptions

@Log
public class Database implements Storage {

	DatabaseConfig config
	Base base
	
	private Server webServer, tcpServer, pgServer
	private Sql db
	private final static long TIMESERIES_COPY_INTERVAL=30l*24*60*60*1000 // 1 month
	private String backupLast
	private ScheduledFuture backupFuture
	
	// Prefixes of history tables from V0.6.0-dev5 and above
	private final static String TABLE_PREFIX_DOUBLE='D_' // VALUE columns is of type DOUBLE
	private final static String TABLE_PREFIX_STRING='C_' // VALUE column is of type VARCHAR
	
	// Prefixes of history tables up to and including V0.6.0-dev4
	private final static String TABLE_PREFIX_DEVICE_DOUBLE='V_'
	private final static String TABLE_PREFIX_DEVICE_STRING='VS_'
	private final static String TABLE_PREFIX_SYSVAR_DOUBLE='S_'
	private final static String TABLE_PREFIX_SYSVAR_STRING='SS_'

	private final static String CONFIG_DATABASE_VERSION='internal.databaseVersion'
		
	public Database(DatabaseConfig config, Base base) {
		log.info 'Connecting to database'
		this.config=config
		this.base=base
		config.logDebug()
		try {
			db=Sql.newInstance(getUrl(config), config.user, config.password, 'org.h2.Driver')
			prepareDatabase()
			if (config.webEnable) {
				log.info 'Starting database web server'
				def args=['-ifExists', '-webPort', config.webPort as String]
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
			stop();	throw ex
		}
	}
	
	@CompileStatic
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
	
	@CompileStatic
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
				// ignore
			}
			throw e;
		}
	}

	@Override
	@CompileStatic
	public synchronized Date getFirstTimestamp(DataPoint dp) {
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		def row=db.firstRow("SELECT MIN(TS) FROM $dp.historyTableName" as String)
		row?(Date)row[0]:null
	}

	@Override
	@CompileStatic
	public synchronized ProcessValue getLast(DataPoint dp) {
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		def row=db.firstRow("SELECT TS, VALUE, STATE FROM $dp.historyTableName WHERE TS=(SELECT MAX(TS) FROM $dp.historyTableName)" as String)
		row?new ProcessValue((Date)row[0], row[1], (int)row[2]):null
	}
	
	@Override
	@CompileStatic
	public synchronized TimeSeries getTimeSeriesRaw(DataPoint dp, Date begin, Date end) {
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
	
	@Override
	@CompileStatic
	public synchronized TimeSeries getTimeSeries(DataPoint dp, Date begin, Date end) {
		log.finer "Database: Retrieving time series for ${dp.id}, begin: $begin, end: $end"
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		TimeSeries ts=new TimeSeries(dp)
		ProcessValue bv=getFirstBefore(dp, begin, true)
		if (bv) ts.add begin.time, bv.value, bv.state
		db.query("SELECT TS, VALUE, STATE FROM $dp.historyTableName WHERE TS>? AND TS<=? ORDER BY TS" as String, 
			[(Object)begin, (Object)end]) { ResultSet rs ->
			ts.add(rs);
		}
		if (ts.size) {
			bv=ts[ts.size-1]
			if (bv.timestamp<end) {
				Date t
				if (getFirstAfter(dp, end, false)) {
					t=end
				} else {
					t=new Date(); if (t>end) t=end
				}
				ts.add t.time, bv.value, bv.state
			}
		}
		log.finer "Database: Retrieved $ts.size points"
		ts
	}

	@Override
	@CompileStatic
	public synchronized int getCount(DataPoint dp, Date startTime, Date endTime) {
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
	@CompileStatic
	public synchronized List<DataPoint> getDataPoints() {
		log.finer 'Database: Getting data points'
		db.rows('SELECT * FROM DATA_POINTS ORDER BY INTERFACE, DISPLAY_NAME, ADDRESS, IDENTIFIER').collect { getRowAsDataPoint(it) }
	}

	@Override
	@CompileStatic
	public synchronized List<DataPoint> getDataPointsOfInterface(String itfName) {
		db.rows(
			"""SELECT * FROM DATA_POINTS WHERE INTERFACE=$itfName
			ORDER BY DISPLAY_NAME, ADDRESS, IDENTIFIER"""
		).collect { getRowAsDataPoint(it) }
	}

	@Override
	@CompileStatic
	public synchronized DataPoint getDataPoint(int idx) {
		log.finer "Database: Getting data point with index $idx"
		def row=db.firstRow("SELECT * FROM DATA_POINTS WHERE DP_ID=$idx")
		row?getRowAsDataPoint(row):null
	}
	
	@Override
	@CompileStatic
	public synchronized DataPoint getDataPoint(DataPointIdentifier id) {
		log.finer "Database: Getting data point with id $id"
		def row=db.firstRow("""SELECT * FROM DATA_POINTS WHERE
			INTERFACE=$id.interfaceId AND ADDRESS=$id.address AND IDENTIFIER=$id.identifier""")
		row?getRowAsDataPoint(row):null
	}

	@Override
	@CompileStatic
	public synchronized void createDataPoint(DataPoint dp) throws Exception {
		normalizeDataPoint(dp)
		dp.historyTableName=getDataPointTableName(dp)
		createDataPointTable(dp)
		createDataPointEntry(dp)
	}

	@Override
	@CompileStatic
	public synchronized void updateDataPoint(DataPoint dp) {
		normalizeDataPoint(dp)
		if (isStringTable(dp.historyTableName)!=dp.historyString) {
			log.fine "Database: Data point $dp.id has changed value type"
			dp.historyTableName=getDataPointTableName(dp)
			createDataPointTable(dp)
		}
		log.fine "Database: Updating data point description: $dp"
		if (db.executeUpdate("""UPDATE DATA_POINTS SET
			TABLE_NAME=$dp.historyTableName, STATE=$dp.managementFlags,
			
			INTERFACE=$dp.id.interfaceId, ADDRESS=$dp.id.address,
			IDENTIFIER=$dp.id.identifier,
	
			PREPROC_TYPE=$dp.attributes.preprocType, PREPROC_PARAM=$dp.attributes.preprocParam,
	
			DISPLAY_NAME=$dp.attributes.displayName, ROOM=$dp.attributes.room, 
			FUNCTION=$dp.attributes.function, COMMENT=$dp.attributes.comment,
			
			PARAM_SET=$dp.attributes.paramSet,	TAB_ORDER=$dp.attributes.tabOrder,
			MAXIMUM=$dp.attributes.maximum, UNIT=$dp.attributes.unit,
			MINIMUM=$dp.attributes.minimum, CONTROL=$dp.attributes.control,
			OPERATIONS=$dp.attributes.operations, FLAGS=$dp.attributes.flags,
			TYPE=$dp.attributes.type, DEFAULT_VALUE=$dp.attributes.defaultValue
							
			WHERE DP_ID=$dp.idx""")!=1)
			throw new Exception("Update of data point ${dp.id} failed")
	}

	@Override
	@CompileStatic
	public synchronized void deleteDataPoint(DataPoint dp) {
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
	 
	// not synchronized, the deletion may take longer 
	@Override
	@CompileStatic
	public int deleteTimeSeries(DataPoint dp, Date startTime, Date endTime) {
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
	
	// not synchronized, the copy may take longer 
	@Override
	@CompileStatic
	public int copyTimeSeries(DataPoint dstDp, DataPoint srcDp, Date startTime, Date endTime, Date newStartTime) {
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
	@CompileStatic
	public int replaceTimeSeries(DataPoint dstDp, Iterable<ProcessValue> srcSeries, Date startTime, Date endTime) throws Exception {
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

	@CompileStatic
	public void normalizeDataPoint(DataPoint dp) {
		def a=dp.attributes
		if (a.containsKey('minimum'))
			a.minimum=asDoubleOrNull(a.minimum)
		if (a.containsKey('maximum'))
			a.maximum=asDoubleOrNull(a.maximum)
		if (a.containsKey('defaultValue'))
			a.defaultValue=asDoubleOrNull(a.defaultValue)
	}

	@CompileStatic
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
	@CompileStatic
	public synchronized void consume(Event e) throws Exception {
		log.fine "Database: Inserting ($e.pv.timestamp, $e.pv.value, $e.pv.state) into $e.dataPoint.historyTableName"
		if (!e.dataPoint.historyTableName)
			throw new Exception('Table name of data point is not set')
		def value=TimeSeries.getNormalizedValue(e.pv.value)
		db.executeInsert "INSERT INTO $e.dataPoint.historyTableName (TS, VALUE, STATE) VALUES (?, ?, ?)" as String,
			[e.pv.timestamp, value, e.pv.state]
	}

	@CompileStatic
	public static void compact(DatabaseConfig config) {	
		log.info 'Starting compaction of database'
		config.logDebug()
		String tmpFileName='temp.sql'
		log.fine "Dumping database to $tmpFileName"
		org.h2.tools.Script.main('-url', getUrl(config), '-user', config.user, '-password', config.password, 
			'-script', tmpFileName, '-options', 'DROP')
		log.fine 'Deleting database files'
		org.h2.tools.DeleteDbFiles.execute(config.dir, config.name, true)
		log.fine "Restoring database from $tmpFileName"
		org.h2.tools.RunScript.execute(getUrl(config), config.user, config.password, tmpFileName, null, false)
		log.fine "Deleting $tmpFileName"
		File tmpFile=[tmpFileName]
		if (tmpFile.exists()) tmpFile.delete()
		log.info 'Compaction of database completed'
	}

	@CompileStatic
	public static void dump(DatabaseConfig config, String fileName) {
		log.info 'Starting dump of database'
		config.logDebug()
		log.fine "Dumping database to $fileName"
		org.h2.tools.Script.main('-url', getUrl(config), '-user', config.user, '-password', config.password, 
			'-script', fileName, '-options', 'DROP')
		log.info 'Dump of database completed'
	}
	
	@CompileStatic
	public static void runScript(DatabaseConfig config, String fileName) {
		log.info "Running script $fileName on database"
		config.logDebug()
		org.h2.tools.RunScript.main('-url', getUrl(config), '-user', config.user, '-password', config.password,
			'-script', fileName)
		log.info 'Script run completed'
	}

	@CompileStatic
	private ProcessValue getFirstBefore(DataPoint dp, Date ts, boolean incBoundary=false) {
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		def row=db.firstRow("""SELECT TS, VALUE, STATE FROM $dp.historyTableName WHERE
			TS=(SELECT MAX(TS) FROM $dp.historyTableName WHERE TS${incBoundary?'<=':'<'}?)""" as String, ts)
		row?new ProcessValue((Date)row[0], row[1], (int)row[2]):null
	}

	@CompileStatic
	private ProcessValue getFirstAfter(DataPoint dp, Date ts, boolean incBoundary=true) {
		if (!dp.historyTableName)
			throw new Exception('Table name of data point is not set')
		def row=db.firstRow("""SELECT TS, VALUE, STATE FROM $dp.historyTableName WHERE
			TS=(SELECT MIN(TS) FROM $dp.historyTableName WHERE TS${incBoundary?'>=':'>'}?)""" as String, ts)
		row?new ProcessValue((Date)row[0], row[1], (int)row[2]):null
	}

	@CompileStatic
	private static Double asDoubleOrNull(value) {
		if (value instanceof Boolean)
			((Boolean) value).booleanValue() ? 1.0D : 0.0D;
		else if (value instanceof Number)
			((Number) value).doubleValue();
		else
			null
	}

	@CompileStatic
	private boolean tableExists(String tableName) {
		def row=db.firstRow("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='$tableName'" as String)
		row[0]!=0
	}

	private DataPoint getRowAsDataPoint(def row) {
		new DataPoint(
			idx:row.DP_ID, historyTableName:row.TABLE_NAME,
			managementFlags:(row.STATE?:0),
			
			id: new DataPointIdentifier(row.INTERFACE, row.ADDRESS,	row.IDENTIFIER),
			
			attributes: [
				preprocType:row.PREPROC_TYPE, preprocParam:row.PREPROC_PARAM,
				
				displayName:row.DISPLAY_NAME, room:row.ROOM,
				function:row.FUNCTION, comment:row.COMMENT,
			
				paramSet:row.PARAM_SET,	tabOrder:row.TAB_ORDER,
				maximum:row.MAXIMUM, unit:row.UNIT,
				minimum:row.MINIMUM, control:row.CONTROL,
				operations:row.OPERATIONS, flags:row.FLAGS,
				type:row.TYPE, defaultValue:row.DEFAULT_VALUE
			]
		)
	}

	@CompileStatic
	private void createDataPointTable(DataPoint dp) {
		log.fine "Database: Creating table $dp.historyTableName"
		String valueType=dp.historyString?'VARCHAR':'DOUBLE'
		db.execute """CREATE TABLE IF NOT EXISTS $dp.historyTableName (TS DATETIME, VALUE $valueType, STATE INT);
			CREATE INDEX IF NOT EXISTS ${dp.historyTableName}_IDX ON $dp.historyTableName (TS)""" as String
	}

	@CompileStatic
	private void createTemporaryTable(String tableName, boolean historyString) {
		log.fine "Database: Creating temporary table $tableName"
		String valueType=historyString?'VARCHAR':'DOUBLE'
		db.execute """CREATE CACHED TEMP TABLE $tableName (TS DATETIME, VALUE $valueType, STATE INT);
			CREATE INDEX ${tableName}_IDX ON $tableName (TS)""" as String
	}

	@CompileStatic
	private void createDataPointEntry(DataPoint dp) {
		normalizeDataPoint(dp)
		log.fine "Database: Inserting into DATA_POINTS: $dp"
		db.executeInsert """INSERT INTO DATA_POINTS (
				DP_ID, TABLE_NAME, STATE,
				INTERFACE, ADDRESS, IDENTIFIER,
				PREPROC_TYPE, PREPROC_PARAM,
				DISPLAY_NAME, COMMENT,
				PARAM_SET, TAB_ORDER, MAXIMUM, UNIT, MINIMUM, CONTROL,
				OPERATIONS, FLAGS, TYPE, DEFAULT_VALUE
			) VALUES (
				$dp.idx, $dp.historyTableName, $dp.managementFlags,
				$dp.id.interfaceId, $dp.id.address, $dp.id.identifier,
				$dp.attributes.preprocType, $dp.attributes.preprocParam,
				$dp.attributes.displayName, $dp.attributes.comment,
				$dp.attributes.paramSet, $dp.attributes.tabOrder, 
				$dp.attributes.maximum, $dp.attributes.unit, $dp.attributes.minimum, 
				$dp.attributes.control,	$dp.attributes.operations, 
				$dp.attributes.flags, $dp.attributes.type, 
				$dp.attributes.defaultValue
			)"""
	}
	
	private static String getUrl(DatabaseConfig config) {
		if (!config.dir.isEmpty() && !config.dir.endsWith('/')) config.dir+='/'
		"jdbc:h2:file:$config.dir$config.name;DB_CLOSE_ON_EXIT=FALSE"
	}

	@CompileStatic
	private static String getDataPointTableName(DataPoint dp) {
		String tableName=dp.historyString?TABLE_PREFIX_STRING:TABLE_PREFIX_DOUBLE
		tableName+="${dp.id.interfaceId}_${dp.id.address}_${dp.id.identifier}"
		tableName.toUpperCase().replaceAll(/[^A-Z0-9_]/, '_')
	}
	
	@CompileStatic
	private static boolean isStringTable(String tableName) {
		// up to and including V0.6.0-dev4
		tableName.startsWith(TABLE_PREFIX_DEVICE_STRING) || tableName.startsWith(TABLE_PREFIX_SYSVAR_STRING) ||
		// from V0.6.0-dev5 upwards
		tableName.startsWith(TABLE_PREFIX_STRING)
	}
	
	@CompileStatic
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
	
	@CompileStatic
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
	
	@CompileStatic
	private synchronized void createBackup(String fileName) {
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
	
	@CompileStatic
	private void prepareDatabase() {
		log.fine 'Preparing database'
		
		// from V0.7.6 upwards
		db.execute '''CREATE TABLE IF NOT EXISTS DATA_POINTS (
			DP_ID INT IDENTITY,	TABLE_NAME VARCHAR NOT NULL,
			STATE INT,
			
			INTERFACE VARCHAR NOT NULL, ADDRESS VARCHAR NOT NULL,
			IDENTIFIER VARCHAR NOT NULL,

			PREPROC_TYPE INT, PREPROC_PARAM DOUBLE,

			DISPLAY_NAME VARCHAR, ROOM VARCHAR, FUNCTION VARCHAR, COMMENT VARCHAR,
			
			PARAM_SET VARCHAR, TAB_ORDER INT,
			MAXIMUM DOUBLE, UNIT VARCHAR,
			MINIMUM DOUBLE, CONTROL VARCHAR,
			OPERATIONS INT, FLAGS INT,
			TYPE VARCHAR, DEFAULT_VALUE DOUBLE
		); CREATE UNIQUE INDEX IF NOT EXISTS DATA_POINTS_IDX
			ON DATA_POINTS (INTERFACE, ADDRESS, IDENTIFIER)'''
		
		// -> V0.7.5
		db.executeUpdate "UPDATE DATA_POINTS SET STATE=BITOR(STATE, 0x40) WHERE TABLE_NAME REGEXP '(?i)^(C_|VS_|SS_)'" as String
		
		// -> V0.7.6
		db.execute 'ALTER TABLE DATA_POINTS ADD IF NOT EXISTS PREPROC_TYPE INT BEFORE DISPLAY_NAME'
		db.execute 'ALTER TABLE DATA_POINTS ADD IF NOT EXISTS PREPROC_PARAM DOUBLE BEFORE DISPLAY_NAME'
		
		// -> V0.7.7
		// delete STATE bits 0..3 for later usage
		db.executeUpdate 'UPDATE DATA_POINTS SET STATE=BITAND(STATE, 0xFFFFFFF0)'
		// add ROOM and FUNCTION
		db.execute 'ALTER TABLE DATA_POINTS ADD IF NOT EXISTS ROOM VARCHAR BEFORE COMMENT'
		db.execute 'ALTER TABLE DATA_POINTS ADD IF NOT EXISTS FUNCTION VARCHAR BEFORE COMMENT'
		
		// -> V2.0.0
		// add database functions
		db.execute 'CREATE ALIAS IF NOT EXISTS TS_TO_UNIX DETERMINISTIC FOR "mdz.ccuhistorian.DatabaseExtensions.TS_TO_UNIX"'
		db.execute 'CREATE ALIAS IF NOT EXISTS UNIX_TO_TS DETERMINISTIC FOR "mdz.ccuhistorian.DatabaseExtensions.UNIX_TO_TS"'
		// add configuration table
		db.execute 'CREATE TABLE IF NOT EXISTS CONFIG (NAME VARCHAR(128) NOT NULL, VALUE VARCHAR(8192))'
		// introduce database version
		if (getConfig(CONFIG_DATABASE_VERSION)==null)
			setConfig(CONFIG_DATABASE_VERSION, '0')
			
		// migrate database
		// initialize continuous flag for existing data points
		migrateTo(1, '''UPDATE DATA_POINTS SET STATE=BITOR(STATE, 0x80) WHERE IDENTIFIER IN (
			'ACTUAL_HUMIDITY', 'ACTUAL_TEMPERATURE', 'AIR_PRESSURE', 'BRIGHTNESS',
			'CURRENT', 'ENERGY_COUNTER', 'FREQUENCY', 'HUMIDITY', 'ILLUMINATION',
			'LUX', 'POWER', 'RAIN_COUNTER', 'SUNSHINEDURATION', 'TEMPERATURE',
			'VOLTAGE', 'WIND_SPEED')''')
	}
	
	@CompileStatic 
	public String getConfig(String name) {
		def row=db.firstRow('SELECT VALUE FROM CONFIG WHERE NAME=?', name)
		String value=row?(String)(row[0]):null
		log.fine("Read config: $name=$value")
		value
	}
	
	@CompileStatic
	public void setConfig(String name, String value) {
		log.fine("Writing config: $name=$value")
		int cnt=db.executeUpdate('UPDATE CONFIG SET VALUE=? WHERE NAME=?', value, name)
		if (cnt==0) {
			db.executeUpdate('INSERT INTO CONFIG VALUES (?, ?)', name, value)
		}
	}
	
	@CompileStatic
	private void migrateTo(int toVersion, String sql) {
		if (getConfig(CONFIG_DATABASE_VERSION).toInteger() < toVersion) {
			log.info "Migrating database to version $toVersion"
			db.execute sql
			setConfig(CONFIG_DATABASE_VERSION, toVersion.toString())
		}
	}
}
