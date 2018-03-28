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

import groovy.transform.CompileStatic
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.WeekFields
import java.util.Date
import java.util.regex.Pattern
import mdz.Text

@CompileStatic
class TextFormat {

	private static NumberFormat numberFormat=new DecimalFormat('#,##0.0#####')
	
	public static String formatNumber(Number number) {
		synchronized(numberFormat) {
			number!=null?numberFormat.format(number):''
		}
	}

	public static Number parseNumber(String str) {
		if (str==null) return null
		try {
			synchronized(numberFormat) { numberFormat.parse(str) }
		} catch (e) {
			null
		}
	}

	// date format for formatting
	private static DateFormat dateFormat=new SimpleDateFormat('dd.MM.yyyy HH:mm:ss')
	// date formats for parsing
	private static List<SimpleDateFormat> dateFormats=[
			'dd.MM.yyyy HH:mm:ss', 'dd.MM.yyyy HH:mm', 'dd.MM.yyyy HH', 'dd.MM.yyyy', 'MM.yyyy',
			'yyyyMMddHHmmss', 'yyyyMMddHHmm', 'yyyyMMddHH', 'yyyyMMdd', 'yyyyMM', 'yyyy'
		].collect { new SimpleDateFormat(it) }

	// parser regex for relative date tokens
	private static final String relDateToken = /(((-|\+)?\d+)([YMDWhms]))|((\d+)(=[YMDWwhms]))|(z)/
	private static final Pattern relDateTokenPat = ~relDateToken
	
	// sequence of tokens, allow whitespace between tokens
	private static final String relDateSequence = /\s*((/ + relDateToken + /)\s*)*/
	private static final Pattern relDateSequencePat = ~relDateSequence

	public static String formatDate(Date date) {
		synchronized(dateFormat) {
			date!=null?dateFormat.format(date):''
		}
	}

	public static Date parseDate(String str) {
		if (str==null) return null
		(Date)dateFormats.findResult { DateFormat df ->
			try {
				synchronized(df) { df.parse(str) }
			} catch (e) {
				null
			}
		}
	}
	
	public static boolean isRelativeDate(String str) {
		str ==~ relDateSequencePat
	}
	
	public static Date parseDate(Date relativeTo, String str) {
		// not a relative date?
		if (!isRelativeDate(str)) {
			// return absolute date
			return parseDate(str)
		}

		// relativeTo must now be set
		if (!relativeTo) return null

		// parse relative date
		ZonedDateTime res=ZonedDateTime.ofInstant(relativeTo.toInstant(), ZoneId.systemDefault())
		Text.forEachMatch(str, relDateTokenPat) { String[] grps ->
			
			// relative adjustments
			if (grps[1]!=null) {
				Integer n=grps[2] as Integer
				
				switch(grps[4]) {
					case 'Y': res=res.plusYears(n); break
					case 'M': res=res.plusMonths(n); break
					case 'D': res=res.plusDays(n); break
					case 'W': res=res.plusWeeks(n); break
					case 'h': res=res.plusHours(n); break
					case 'm': res=res.plusMinutes(n); break
					case 's': res=res.plusSeconds(n); break
				}
			}
			
			// set fields
			if (grps[5]!=null) {
				Integer n=grps[6] as Integer
				
				switch (grps[7]) {
					// year
					case '=Y': res=res.withYear(n); break
					// month of year (1..12)
					case '=M': res=res.withMonth(n); break
					// day of month
					case '=D': res=res.withDayOfMonth(n); break
					// day of week
					case '=w': res=res.with(ChronoField.DAY_OF_WEEK, n); break
					// week of year
					case '=W': res=res.with(WeekFields.of(Locale.getDefault()).weekOfYear(), n); break
					// hour of day
					case '=h': res=res.withHour(n); break
					// minute of hour
					case '=m': res=res.withMinute(n); break
					// second of minute
					case '=s': res=res.withSecond(n).withNano(0); break
				}
			}
			
			// special actions
			if (grps[8]!=null) {
				switch(grps[8]) {
					// clear time components
					case 'z': res=res.withHour(0).withMinute(0).withSecond(0).withNano(0); break
				}
			}
		}
		Date.from(res.toInstant())
	}
	
	private static final long MILLISECONDS_PER_SECOND=1000
	private static final long MILLISECONDS_PER_MINUTE=MILLISECONDS_PER_SECOND*60
	private static final long MILLISECONDS_PER_HOUR=MILLISECONDS_PER_MINUTE*60
	private static final long MILLISECONDS_PER_DAY=MILLISECONDS_PER_HOUR*24
	private static final long MILLISECONDS_PER_WEEK=MILLISECONDS_PER_DAY*7
	
	public static String formatDuration(long milliSeconds) {
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
}
