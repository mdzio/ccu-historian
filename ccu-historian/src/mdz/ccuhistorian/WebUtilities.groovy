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

import groovy.time.TimeCategory
import groovy.time.BaseDuration
import java.security.MessageDigest
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.logging.Logger
import mdz.Exceptions
import mdz.Text
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.persistence.DataPointStorage

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
		synchronized(dateFormat) {
			date!=null?dateFormat.format(date):''
		}
	}

	@CompileStatic
	public String format(Number number) {
		synchronized(numberFormat) {
			number!=null?numberFormat.format(number):''
		}
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
		if (str==null) return null
		(Date)dateFormats.findResult { DateFormat df -> 
			try {
				synchronized(df) { df.parse(str) } 
			} catch (e) { 
				null 
			} 
		}
	}

	// parser regex for relative date tokens
	private static final String relativeDateToken = /(((-|\+)?\d+)([YMDWhms]))|((\d+)(=[YMDWwhms]))|(z)/
	// expect at least one token, allow whitespace between tokens
	private static final String relativeDateSequence = /\s*((/ + relativeDateToken + /)\s*)+/
	
	public Date parseDate(Date relativeTo, String str) {
		// not a relative date?
		if (!(str==~relativeDateSequence)) { 
			// return absolute date
			return parseDate(str)
		}

		// relativeTo must now be set
		if (!relativeTo) return null

		// parse relative date
		Date res=relativeTo.clone()
		Text.forEachMatch(str, ~relativeDateToken) { grps ->
			
			// relative adjustments
			if (grps[1]!=null) {
				String num=grps[2]
				if (num.startsWith('+')) num=num.substring(1)
				Integer n=num as Integer
				
				use(TimeCategory) {
					switch(grps[4]) {
						case 'Y': res+=n.years; break
						case 'M': res+=n.months; break
						case 'D': res+=n.days; break
						case 'W': res+=n.weeks; break
						case 'h': res+=n.hours; break
						case 'm': res+=n.minutes; break
						case 's': res+=n.seconds; break
					}
				}
			}
			
			// set fields
			if (grps[5]!=null) {
				Integer n=grps[6] as Integer
				
				switch (grps[7]) {
					// year
					case '=Y': res[Calendar.YEAR]=n; break
					// month of year (1..12)
					case '=M': res[Calendar.MONTH]=(n-1)+Calendar.JANUARY; break
					// day of month
					case '=D': res[Calendar.DAY_OF_MONTH]=n; break
					// day of week
					case '=w': res[Calendar.DAY_OF_WEEK]=n; break
					// week of year
					case '=W': res[Calendar.WEEK_OF_YEAR]=n; break
					// hour of day
					case '=h': res[Calendar.HOUR_OF_DAY]=n; break
					// minute of hour
					case '=m': res[Calendar.MINUTE]=n; break
					// second of minute
					case '=s': res[Calendar.SECOND]=n; res[Calendar.MILLISECOND]=0; break
				}
			}
			
			// special actions
			if (grps[8]!=null) {
				switch(grps[8]) {
					// clear time components
					case 'z': res.clearTime(); break
				}
			}
		}
		res
	}
	
	@CompileStatic
	public Number parseNumber(String str) {
		if (str==null) return null
		try { 
			synchronized(numberFormat) { numberFormat.parse(str) } 
		} catch (e) { 
			null 
		}
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
		str?Text.escapeXml(str):''
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
		Exceptions.catchToLog(log, cl)
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
