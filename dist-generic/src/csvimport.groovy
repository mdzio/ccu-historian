// *** configuration ***
dbName="jdbc:h2:./data/history"
dbUser="sa"
dbPassword="ccu-historian"

// *** script ***
verbose=false
catchToLog {

    // start message
    log "*** CSV-Import v1.0  ***"
    log "(C)2018-2025 info@ccu-historian.de"

    // define command line options
    CliBuilder cli=new CliBuilder(usage:"csvimport [options] <csv file>")
    StringWriter usage=[]
	cli.writer=new PrintWriter(usage)
    cli.header="options:"
    cli.d("delete import time range in database before inserting")
    cli.h("prints this help message")
    cli.m(args:2, valueSeparator:"=", argName:"column=DP ID", "map CSV column number to data point ID (at least one mapping is required)")
    cli.nh("CSV file has no header line")
    cli.t("test run without modifying the database")
    cli.tf(args:1, argName:"format", "format of the timestamp (default: dd.MM.yy HH:mm:ss)")
    cli.tc(args:1, argName:"column", "column number of the timestamp (default: 1)")
    cli.v("verbose log output (e.g. prints each time series entry)")

    // parse command line options
    def options=cli.parse(args)
    if (options==null || options.h || !options.ms || options.arguments().size()!=1) {
        // print usage
        cli.usage()
        log usage
        System.exit 1
    }
    verbose=options.v
    def testRun=options.t
    
    // build the CSV parser
    def tsTable=new TsTable(
        csvFile: new CsvFile(
            file: new File(options.arguments()[0]),
            skipHeaders: options.nh ? 0 : 1
        ), 
        timestampCol: options.tc ? options.tc.toInteger()-1 : 0,
        timestampFormat: new SimpleDateFormat(options.tf ?: "dd.MM.yy HH:mm:ss")
    )
    
    // connect to database
    log "connecting to database"
    Sql.withInstance(dbName, dbUser, dbPassword, "org.h2.Driver") { db ->
    
        // read data point list
        log "reading data points from database"
        def dpIdToTableName=db.rows("SELECT DP_ID, TABLE_NAME FROM DATA_POINTS").collectEntries { row -> [row.DP_ID, row.TABLE_NAME] }
        log "${dpIdToTableName.size()} data points found"

        // build mapping
        def mappings=options.ms.collate(2).collect { mapOpt -> 
            // options valid?
            if (!mapOpt*.isInteger().every()) {
                throw new IllegalArgumentException("Option -m invalid: Not a number")
            }
            def (colNo, dpId)=mapOpt*.toInteger()
            // check column
            colNo--
            if (colNo<0 || colNo==tsTable.timestampCol) {
                throw new IllegalArgumentException("Option -m invalid: Invalid column: ${colNo+1}")
            }
            // check DP ID
            def tableName=dpIdToTableName[dpId]
            if (!tableName) {
                throw new IllegalArgumentException("Option -m invalid: Data point with ID $dpId not found")
            }
            // add mapping
            new Mapping(colNo: colNo, dpId: dpId, tableName: tableName)
        }
        log "mappings:"
        mappings.each { m ->
            log "  CSV column: ${m.colNo+1} -> table: $m.tableName, DP ID: $m.dpId"
        }
        
        // delete time range
        if (options.d) {
            log "deleting destination time ranges:"
            // scan CSV file to get min and max timestamp for each value column
            def consumerMap=mappings.collectEntries { m -> [
				m.colNo, 
				{ ts, v -> 
					if (m.begin==null || ts<m.begin) m.begin=ts
					if (m.end==null || ts>m.end) m.end=ts
				}
            ] }
            tsTable.eachEntry consumerMap
            // execute DELETE
            mappings.each { m ->
                if (m.begin && m.end) {
                    log "  DP ID: $m.dpId, first: $m.begin, last: $m.end" 
                    if (!testRun) {
                        db.executeUpdate "DELETE FROM $m.tableName WHERE TS>=? AND TS<=?" as String, m.begin, m.end
                    }
                }
            }
        }
        
        // import
        log "importing:"
        def consumerMap=mappings.collectEntries { m -> [
			m.colNo, 
			{ ts, v -> 
				logv "  inserting $ts, $v into $m.tableName"
				if (!testRun) {
					// use state 2 (GOOD)
					db.executeUpdate "INSERT INTO $m.tableName VALUES (?, ?, ?)" as String, ts, v, 2
				}
				m.numPoints++                    
			}
		] }
        tsTable.eachEntry consumerMap
            
        // statistics
        mappings.each { m ->
            log "  inserted $m.numPoints entries into table $m.tableName"
        }

        // work done
        log "finished"
        if (testRun) {
            log "WARNING: This was a test run, nothing inserted!"
            System.exit 1
        }
    } 
}

// *** helpers ***
import groovy.sql.Sql
import groovy.cli.commons.CliBuilder
import java.text.SimpleDateFormat
import org.codehaus.groovy.runtime.StackTraceUtils

class CsvFile {
    def file
    def skipHeaders=1
    def separator=","
    
    def eachRecord(recordConsumer) {
       file.withReader { reader ->
            skipHeaders.times { reader.readLine() }
            reader.eachLine { line ->
                recordConsumer line.split(separator)*.trim()
            }
        }
    }
}

class TsTable {
    def csvFile
    def timestampCol=0
    def timestampFormat=new SimpleDateFormat("dd.MM.yy HH:mm:ss")
    
    // valueConsumers is a map of column numbers to consumers of time series entries
    def eachEntry(valueConsumers) {
        csvFile.eachRecord { record ->
            def ts=timestampFormat.parse(record[timestampCol])
            valueConsumers.each { col, consumer ->
                def vt=record[col]
                if (vt!=null && vt.isDouble()) {
                    def v=vt.toDouble()
                    consumer ts, v
                }
            }
        }
    }
}

// a csv column to data point mapping
class Mapping {
    def colNo
    def dpId, tableName
    def begin, end
    def numPoints=0
}

def log(msg) {
    System.err.println msg
}

def logv(msg) {
    if (verbose) {
        log msg
    }
}

def catchToLog(cl) {
    try {
        cl()
    } catch (e) {
        log "ERROR: " + e.message?:e.class.name
        if (verbose) {
            StackTraceUtils.sanitize e
            StackTraceUtils.sanitizeRootCause e
            def trace=new StringWriter()
            e.printStackTrace new PrintWriter(trace)
            logv trace.toString()
        }
        System.exit 1
    }
}
