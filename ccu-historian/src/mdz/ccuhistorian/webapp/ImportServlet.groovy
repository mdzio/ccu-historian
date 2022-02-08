/*
 CCU-Historian, a long term archive for the HomeMatic CCU
 Copyright (C) 2021-2022 MDZ (info@ccu-historian.de)
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
package mdz.ccuhistorian.webapp

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.util.logging.Log
import java.text.SimpleDateFormat
import java.util.logging.Logger
import mdz.ccuhistorian.webapp.WebServer
import mdz.Exceptions
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.CSVParser

@CompileStatic
@Log
class ImportServlet extends HttpServlet {

	private final static SimpleDateFormat dateFormat=new SimpleDateFormat('yyyy-MM-dd HH:mm:ss.SSS')

	public enum Mode {
		CLEAR_IMPORT_TIME_RANGE,
		CLEAR_ALL
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.contentType='text/plain'
		resp.characterEncoding='UTF8'
		def out=resp.writer
		out.println 'Datebankimportprotokoll'
		out.println '-----------------------'
		def startTime=System.currentTimeMillis()
		def db=WebServer.instance.database

		def e=Exceptions.catchToLog(log) {
			// check API-KEY
			if (WebServer.instance.config.apiKeys) {
				String k=req.getParameter('k')
				if (!k || !WebServer.instance.config.apiKeys.contains(k)) {
					throw new Exception('Zugriff verweigert')
				}
			}

			// safety check
			String safety=req.getParameter('safety')
			if (safety!='ack') {
				throw new Exception('Sicherheitshinweis nicht bestätigt')
			}

			// import options
			Mode mode
			out.print 'Import-Modus: '
			switch (req.getParameter('mode')) {
				case 'clear-import-time-range':
					mode=Mode.CLEAR_IMPORT_TIME_RANGE
					out.println 'Nur importierten Zeitbereich löschen'
					break
				case 'clear-all':
					mode=Mode.CLEAR_ALL
					out.println 'Zeitreihen komplett löschen'
					break
				default:
					throw new Exception('Invalid mode parameter')
			}
			boolean meta=false;
			if (req.getParameter('meta')) {
				meta=true;
			}
			out.println 'Datenpunkteigenschaften: '+(meta?'Aktualisieren':'Nicht aktualisieren')

			// check file
			def part=req.getPart("input-file")
			if (part==null) {
				throw new Exception("Keine Datei hochgeladen.")
			}
			out.println "Dateiname: $part.submittedFileName"
			out.println "Dateigröße: $part.size Bytes"
			if (part.size==0) {
				throw new Exception("Datei ist leer.")
			}
			log.fine "Importing file $part.submittedFileName with size $part.size"

			// import CSV
			out.println 'Importiere Zeitreihen:'
			CSVParser parser=new CSVParser()
			Reader reader=new BufferedReader(new InputStreamReader(part.inputStream, 'UTF-8'))
			long row=1
			long totalCount=0
			long count=0
			DataPoint dataPoint
			parser.parse(reader, { List<String> fields ->
				// end of time series?
				if (fields.size()==1 && fields[0].isEmpty()) {
					if (dataPoint!=null) {
						// update statistics
						totalCount+=count
						out.println "$count Einträge importiert."
						// reset data point
						dataPoint=null
						count=0
						out.flush()
					}

					// next data point?
				} else if (fields.size()==25 && dataPoint==null) {
					def dpid=new DataPointIdentifier(fields[0], fields[1], fields[2])
					out.print "$dpid: "

					// exist data point?
					dataPoint=db.getDataPoint(dpid)
					if (dataPoint!=null) {
						// update data point meta
						out.print "Datenpunkt vorhanden. "
						if (meta) {
							updateProperties(dataPoint, fields)
							db.updateDataPoint(dataPoint)
						}

						// delete current time series
						switch(mode) {
							case Mode.CLEAR_IMPORT_TIME_RANGE:
								if (fields[23] && fields[24]) {
									def begin=dateFormat.parse(fields[23])
									def end=dateFormat.parse(fields[24])
									db.deleteTimeSeries(dataPoint, begin, new Date(end.time+1))
								}
								break
							case Mode.CLEAR_ALL:
								db.deleteTimeSeries(dataPoint, null, null)
								break
						}

					} else {
						// create data point
						out.print "Neuer Datenpunkt. "
						dataPoint=new DataPoint(id:dpid)
						updateProperties(dataPoint, fields)
						db.createDataPoint(dataPoint)
					}
					log.fine "Importing data point $dataPoint.id"
					out.flush()

				} else if (fields.size()==3 && dataPoint!=null) {
					// store time series entry
					Date ts=dateFormat.parse(fields[0])
					def val
					if (dataPoint.isHistoryString()) {
						val=fields[1]
					} else {
						val=fields[1] as double
					}
					int st=fields[2] as int
					db.consume(new Event(dataPoint: dataPoint, pv:new ProcessValue(ts, val, st)))
					count++

				} else {
					throw new Exception("Ungültige Importdatei (Zeile $row)")
				}
				row++
				true
			} as CSVParser.Handler)

			// handle possibly missing empty line after last time series
			if (dataPoint!=null) {
				totalCount+=count
				out.println "$count Einträge importiert."
			}

			// print statistics
			out.println "Gesamtanzahl Einträge: $totalCount"
			out.println "Gesamtdauer (ohne Übermittlung): ${System.currentTimeMillis()-startTime} ms"
			out.println 'Import abgeschlossen.'
			log.fine "Imported $totalCount entries"
		}

		// print error
		if (e!=null) {
			def msg=e.getMessage()
			if (msg==null) {
				msg=e.class.name
			}
			out.println ''
			out.println "FEHLER: $msg"
		}
	}

	private void updateProperties(DataPoint dp, List<String> fields) {
		dp.managementFlags=convert(fields[5], Integer)
		dp.attributes[DataPoint.ATTR_PREPROC_TYPE]=convert(fields[6], Integer)
		dp.attributes[DataPoint.ATTR_PREPROC_PARAM]=convert(fields[7], Double)
		dp.attributes[DataPoint.ATTR_DISPLAY_NAME]=fields[8]?:null
		dp.attributes[DataPoint.ATTR_ROOM]=fields[9]?:null
		dp.attributes[DataPoint.ATTR_FUNCTION]=fields[10]?:null
		dp.attributes[DataPoint.ATTR_COMMENT]=fields[11]?:null
		dp.attributes[DataPoint.ATTR_CUSTOM]=fields[12]?new JsonSlurper().parseText(fields[12]):[:]
		dp.attributes[DataPoint.ATTR_PARAM_SET]=fields[13]?:null
		dp.attributes[DataPoint.ATTR_TAB_ORDER]=convert(fields[14], Integer)
		dp.attributes[DataPoint.ATTR_MAXIMUM]=convert(fields[15], Double)
		dp.attributes[DataPoint.ATTR_UNIT]=fields[16]?:null
		dp.attributes[DataPoint.ATTR_MINIMUM]=convert(fields[17], Double)
		dp.attributes[DataPoint.ATTR_CONTROL]=fields[18]?:null
		dp.attributes[DataPoint.ATTR_OPERATIONS]=convert(fields[19], Integer)
		dp.attributes[DataPoint.ATTR_FLAGS]=convert(fields[20], Integer)
		dp.attributes[DataPoint.ATTR_TYPE]=fields[21]?:null
		dp.attributes[DataPoint.ATTR_DEFAULT_VALUE]=convert(fields[22], Double)
	}

	private <T> T convert(String str, Class<T> clazz) {
		if (str=='') {
			return null
		}
		try {
			return str.asType(clazz)
		} catch (e) {
			throw new Exception("Ungültiger Wert für $clazz.name: $str")
		}
	}
}
