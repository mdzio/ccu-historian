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
import javax.servlet.http.HttpServletRequest

@CompileStatic 
public class TimeRange {

	Date begin
	String beginText
	Date end
	String endText
	
	public TimeRange(HttpServletRequest request) {
		this(request.getParameter('b'), request.getParameter('e'))
	}
	
	public TimeRange(String beginText, String endText) {
		this.beginText=beginText
		this.endText=endText
		Date now=new Date()
		
		if (!beginText && !endText) {
			begin=new Date(now.time-24l*60*60*1000)
			end=now
			return	
		}
		
		begin=TextFormat.parseDate(now, beginText)
		if (begin==null) {
			throw new IllegalArgumentException("Invalid begin time for time range: $beginText")
		}
		
		if (!endText) {
			end=now
		} else {
			end=TextFormat.parseDate(begin, endText)
			if (end==null)
				throw new IllegalArgumentException("Invalid end time for time range: $endText")
		}
		
		if (end==begin) {
			throw new IllegalArgumentException("End time is equal to begin time: $beginText, $endText")
		}
		if (end<begin) {
			throw new IllegalArgumentException("End time is before begin time: $beginText, $endText")
		}
	}
	
	public Map<String, String[]> getParameters() {
		Map<String, String[]> params=[:]
		if (beginText!=null) {
			params.b=[beginText]
		}
		if (endText!=null) {
			params.e=[endText]
		}
		params
	}
}
