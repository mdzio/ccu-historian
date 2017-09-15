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

import groovy.time.*

import java.security.MessageDigest
import java.text.*
import java.util.regex.Matcher

import mdz.Utilities
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.persistence.DataPointStorage

import org.slf4j.Logger
import groovy.transform.CompileStatic

class WebUtilities {

	DateFormat dateFormat=new SimpleDateFormat('dd.MM.yyyy HH:mm:ss')
	NumberFormat numberFormat=new DecimalFormat('#,##0.0#####')
	
	private SimpleDateFormat[] dateFormats=[
			'dd.MM.yyyy HH:mm:ss', 'dd.MM.yyyy HH:mm', 'dd.MM.yyyy HH', 'dd.MM.yyyy', 'MM.yyyy',
			'yyyyMMddHHmmss', 'yyyyMMddHHmm', 'yyyyMMddHH', 'yyyyMMdd', 'yyyyMM', 'yyyy'
		].collect { new SimpleDateFormat(it) }

	@CompileStatic
	public String format(String txt) {
		if (txt==null) return ''
		if (txt.length()>20) txt=txt[0..19]+'...' 
		escapeHtml(txt) 
	}
	
	@CompileStatic
	public String format(Date date) {
		date!=null?dateFormat.format(date):''
	}

	@CompileStatic
	public String format(Number number) {
		number!=null?numberFormat.format(number):''
	}

	@CompileStatic
	public String format(Object obj) {
		if (obj==null) ''
		else escapeHtml(obj.toString()) 
	}
	
	private static final long MILLISECONDS_PER_SECOND=1000
	private static final long MILLISECONDS_PER_MINUTE=MILLISECONDS_PER_SECOND*60
	private static final long MILLISECONDS_PER_HOUR=MILLISECONDS_PER_MINUTE*60
	private static final long MILLISECONDS_PER_DAY=MILLISECONDS_PER_HOUR*24
	private static final long MILLISECONDS_PER_WEEK=MILLISECONDS_PER_DAY*7
	
	@CompileStatic
	public String formatDuration(long milliSeconds) {
		long weeks=(long)milliSeconds.intdiv(MILLISECONDS_PER_WEEK)
		milliSeconds-=weeks*MILLISECONDS_PER_WEEK
		long days=(long)milliSeconds.intdiv(MILLISECONDS_PER_DAY)
		milliSeconds-=days*MILLISECONDS_PER_DAY
		long hours=(long)milliSeconds.intdiv(MILLISECONDS_PER_HOUR)
		milliSeconds-=hours*MILLISECONDS_PER_HOUR
		long minutes=(long)milliSeconds.intdiv(MILLISECONDS_PER_MINUTE)
		milliSeconds-=minutes*MILLISECONDS_PER_MINUTE
		long seconds=(long)milliSeconds.intdiv(MILLISECONDS_PER_SECOND)
		(weeks?((weeks as String)+'W '):'')+(days?((days as String)+'D '):'')+
		(hours?((hours as String)+'h '):'')+(minutes?((minutes as String)+'m '):'')+
		(seconds?((seconds as String)+'s '):'')
	}
	
	@CompileStatic
	public Date parseDate(String str) {
		(Date)(str?(dateFormats.findResult { DateFormat df -> try { df.parse(str) } catch (e) { null } }):null)
	}
	
	@CompileStatic
	public Number parseNumber(String str) {
		if (!str) null
		else try { numberFormat.parse(str) } catch (e) { null }
	}
	
	public BaseDuration parseDuration(str) {
		if (!str || !(str==~/\s*(((-|\+)?\d+)([YMDWhms])\s*)+/)) null
		else {
			def dur
			use(TimeCategory) {
				dur=0.seconds
				(str=~/\s*((-|\+)?(\d+))([YMDWhms])\s*/).each { all, num, d1, d2, period ->
					if (num.startsWith('+')) num=num.substring(1)
					num=num as Integer
					switch (period) {
						case 'Y': dur+=num.years; break
						case 'M': dur+=num.months; break
						case 'D': dur+=num.days; break
						case 'W': dur+=num.weeks; break
						case 'h': dur+=num.hours; break
						case 'm': dur+=num.minutes; break
						case 's': dur+=num.seconds; break
			}	}	}
			dur
		}
	}
	
	@CompileStatic
	public String escapeHtml(String str) {
		if (str==null || str=='') return ''
		str.replaceAll(~/&/, '&amp;').replaceAll(~/</, '&lt;').replaceAll(~/>/, '&gt;').
		replaceAll(~/"/, '&quot;').replaceAll(~/ü/, '&uuml;').replaceAll(~/Ü/, '&Uuml;').
		replaceAll(~/ö/, '&ouml;').replaceAll(~/Ö/, '&Ouml;').replaceAll(~/ä/, '&auml;').
		replaceAll(~/Ä/, '&Auml;').replaceAll(~/ß/, '&szlig;')
	}
	
	@CompileStatic
	public String secureHash(String str) {
		if (str==null) return null
		MessageDigest md=MessageDigest.getInstance('SHA-1')
		md.update(str.getBytes('UTF-8'))
		new BigInteger(1, md.digest()).toString(16).padLeft(40, '0')
	}
	
	@CompileStatic
	public String getHistorianVersion() {
		escapeHtml(Main.version)
	}
	
	@CompileStatic
	public static Throwable catchToLog(Logger log, Closure cl) {
		Utilities.catchToLog(log, cl)
	}

	@CompileStatic
	public static DataPoint parseDataPoint(String id, DataPointStorage db) {
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

	@CompileStatic
	public static List<DataPoint> parseDataPointList(param, DataPointStorage db) {
		if (param instanceof String[] || param instanceof Iterable || param instanceof Object[])
			param.collect { parseDataPoint(it as String, db) }
		else
			[parseDataPoint(param as String, db)]
	}
}
