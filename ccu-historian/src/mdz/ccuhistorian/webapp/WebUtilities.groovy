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
package mdz.ccuhistorian.webapp

import groovy.time.TimeCategory
import groovy.time.BaseDuration
import groovy.transform.CompileStatic
import java.security.MessageDigest
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.logging.Logger
import mdz.ccuhistorian.Main
import mdz.Exceptions
import mdz.Text
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.persistence.DataPointStorage

@CompileStatic
class WebUtilities {

	public String format(String txt) {
		if (txt==null) return ''
		if (txt.length()>20) txt=txt[0..19]+'...'
		Text.escapeXml(txt)
	}

	public String format(Object obj) {
		if (obj==null) ''
		else Text.escapeXml(obj.toString())
	}

	public String format(Number number) {
		TextFormat.format(number)
	}
	
	public Number parseNumber(String str) {
		TextFormat.parseNumber(str)
	}
	
	public String format(Date date) {
		TextFormat.format(date)
	}

	public Date parseDate(String str) {
		TextFormat.parseDate(str)
	}

	public Date parseDate(Date relativeTo, String str) {
		TextFormat.parseDate(relativeTo, str)
	}
	
	public String formatDuration(long milliSeconds) {
		TextFormat.formatDuration(milliSeconds)
	}
	
	public String escapeHtml(String str) {
		str?Text.escapeXml(str):''
	}
	
	public String secureHash(String str) {
		if (str==null) return null
		MessageDigest md=MessageDigest.getInstance('SHA-1')
		md.update(str.getBytes('UTF-8'))
		new BigInteger(1, md.digest()).toString(16).padLeft(40, '0')
	}
	
	public String getHistorianVersion() {
		escapeHtml(Main.version)
	}
	
	public Throwable catchToLog(Logger log, Closure cl) {
		Exceptions.catchToLog(log, cl)
	}

	public DataPoint parseDataPoint(String id, DataPointStorage db) {
		def dataPoint
		if (id==~/\d+/) {
			dataPoint=db.getDataPoint(id as Integer)
		} else {
			Matcher matcher=(id=~/^([^\.]*)\.([^\.]*)\.([^\.]*)$/)
			if (matcher.matches())
				dataPoint=db.getDataPoint(
					new DataPointIdentifier(matcher.group(1), matcher.group(2), matcher.group(3))
				)
		}
		if (dataPoint==null)
			throw new Exception("Unknown data point: $id")
		dataPoint
	}

	public List<DataPoint> parseDataPointList(param, DataPointStorage db) {
		if (param instanceof String[] || param instanceof Iterable || param instanceof Object[])
			param.collect { parseDataPoint(it as String, db) }
		else
			[parseDataPoint(param as String, db)]
	}
	
	public newTimeRange(String begin, String end) {
		new TimeRange(begin, end)
	}
}
