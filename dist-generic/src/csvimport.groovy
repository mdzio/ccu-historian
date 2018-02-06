// *** configuration ***
dbName="jdbc:h2:./data/history"
dbUser="sa"
dbPassword="ccu-historian"

// *** script ***
verbose=false
catchToLog {

    // start message
    log "*** CSV-Import v1.0  ***"
    log "(C)2018 info@ccu-historian.de"

    // define command line options
    CliBuilder cli=new CliBuilder(usage:"csvimport [options] <csv file>")
    StringWriter usage=[]
	cli.writer=new PrintWriter(usage)
    cli.header="options:"
    cli.h("prints this help message")
    cli.m(args:2, valueSeparator:"=", argName:"column=DP ID", "map CSV column number to data point ID (at least one mapping is required)")
    cli.nh("CSV file has no header line")
    cli.t("test run without inserting the time series into the database")
    cli.tf(args:1, argName:"format", "format of the timestamp (default: dd.MM.yy HH:mm:ss)")
    cli.tc(args:1, argName:"column", "column number of the timestamp (default: 1)")
    cli.v("verbose log output (prints each time series entry)")

    // parse command line options
    def options=cli.parse(args)
    if (options==null || options.h || !options.ms || options.arguments().size()!=1) {
        cli.usage()
        log usage
        System.exit 1
    }
    verbose=options.v
    def noHeader=options.nh
    def testRun=options.t
    def tsPat=options.tf?:"dd.MM.yy HH:mm:ss"
    def tsCol=options.tc?options.tc.toInteger()-1:0
    def fileName=options.arguments()[0]

    // connect to database
    log "connecting to database"
    Sql.withInstance(dbName, dbUser, dbPassword, "org.h2.Driver") { db ->
    
        // read data point list
        log "reading data points from database"
        def dpIdToTableName=db.rows("SELECT DP_ID, TABLE_NAME FROM DATA_POINTS").collectEntries { row -> [row.DP_ID, row.TABLE_NAME] }
        log "${dpIdToTableName.size()} data points found"

        // read CSV file
        new File(fileName).withReader { reader ->

            // build mapping
            def mappings=options.ms.collate(2).collect { colNo, dpId -> 
                // options valid?
                if (!colNo.isInteger() || !dpId.isInteger()) {
                    throw new IllegalArgumentException("Option -m invalid: Not a number: $colNo=$dpId")
                }
                def tableName=dpIdToTableName[dpId.toInteger()]
                if (!tableName) {
                    throw new IllegalArgumentException("Option -m invalid: Data point with ID $dpId not found")
                }
                // build mapping
                new Mapping(colNo: colNo.toInteger()-1, dpId: dpId.toInteger(), tableName: tableName)
            }
            log "mappings:"
            mappings.each { m ->
                log "  CSV column: ${ m.colNo+1 } -> table: $m.tableName, DP ID: $m.dpId"
            }

            // discard header line?
            if (!noHeader) {
                log "skipping csv header line"
                reader.readLine()
            }
            
            // insert timeseries
            def dateFormat=new SimpleDateFormat(tsPat)
            def csvRows=0
            reader.eachLine { line ->
                def fields=line.split(",")*.trim()
                def ts=dateFormat.parse(fields[tsCol])
                mappings.each { m ->
                    def vt=fields[m.colNo]
                    if (vt!=null && vt.isDouble()) {
                        def v=vt.toDouble()
                        logv "inserting ${dateFormat.format(ts)}, $v into $m.tableName"
                        if (!testRun) {
                            db.executeUpdate "INSERT INTO $m.tableName VALUES (?, ?, ?)" as String, ts, v, 2
                        }
                        m.numPoints++
                    }
                }
                csvRows++
            }
            
            // statistics
            log "read $csvRows csv rows"
            mappings.each { m ->
                log "inserted $m.numPoints entries into table $m.tableName"
            }
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
import java.text.SimpleDateFormat
import org.codehaus.groovy.runtime.StackTraceUtils

// a csv column to data point mapping
class Mapping {
    def colNo
    def dpId, tableName
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
