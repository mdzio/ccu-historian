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
package mdz

import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.StackTraceUtils
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class Utilities {

	public static boolean asBoolean(value) {
		if (value instanceof Boolean)
			return value
		else if (value instanceof Number)
			return value.asBoolean()
		else if (value instanceof String) 
			switch (value.trim().toLowerCase()) {
				case '0': case 'false': case 'off': return false
				case '1': case 'true': case 'on': return true
			}
		throw new Exception("Can't interpret $value as boolean value")
	}
	
	public static String unescapeXml(String str) {
		if (str==null || str=='') return ''
		str.replaceAll(~/&lt;/, '<').replaceAll(~/&gt;/, '>').replaceAll(~/&quot;/, '"').
		replaceAll(~/&apos;/, '\'').replaceAll(~/&#(\d+);/, { List<String> captures -> (captures[1] as Integer) as Character }).
		replaceAll(~/&#x(\p{XDigit}+);/, { List<String> captures -> Integer.valueOf(captures[1], 16) as Character }).
		replaceAll(~/&amp;/, '&')
	}

	private static final int BYTES_PER_ROW = 32
	
	public static String prettyPrint(byte[] data) {
		int pos=0
		boolean firstLine=true
		StringBuilder sb=[]
		while (data.length>pos) {
			if (firstLine) firstLine=false; else sb.append('\n')
			sb.append Integer.toHexString(pos).toUpperCase().padLeft(4, '0')
			sb.append ': '
			for (int pos2=0; pos2<BYTES_PER_ROW; pos2++) {
				if (pos+pos2>=data.length) sb.append(' ')
				else {
					char ch=data[pos+pos2]
					if (ch>=0x20 && ch<=0x7e) sb.append(ch) else sb.append('.')
				} 
			}
			sb.append '  '
			for (int pos2=0; pos2<BYTES_PER_ROW && pos+pos2<data.length; pos2++) {
				int ch=data[pos+pos2] & 0xff
				sb.append Integer.toHexString(ch).toUpperCase().padLeft(2, '0')
				sb.append ' '
			}
			pos+=BYTES_PER_ROW
		}
		sb.toString()
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
}
