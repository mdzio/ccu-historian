/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2018 MDZ (info@ccu-historian.de)

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

import groovy.time.*
import javax.servlet.http.HttpServletRequest
import mdz.ccuhistorian.TrendDesign
import mdz.ccuhistorian.webapp.TrendParameters.Group
import mdz.hc.DataPoint
import mdz.hc.persistence.DataPointStorage

class TrendParametersV1 extends TrendParameters {
	
	protected TrendParametersV1(HttpServletRequest request, DataPointStorage storage, Map<String, TrendDesign> trendDesigns) {
		// height and width of the graphics
		try {
			width=(request.getParameter('w')?:'640') as Integer
			height=(request.getParameter('h')?:'260') as Integer
		} catch (NumberFormatException e) {
			throw new Exception('Page parameter w (width) or h (height) is invalid')
		}
		
		// datapoint IDs
		List<DataPoint> dataPoints=WebUtilities.getDataPoints(request.getParameterValues('i') as List, storage)
		if (!dataPoints) {
			throw new Exception('Page parameter i (data point ID/s) not set)')
		}
	
		// data point groups
		List<Integer> dpGroups
		if (!request.getParameter('g')) {
			dpGroups=[0]*dataPoints.size()
		} else {
			try {
				dpGroups=request.getParameterValues('g').collect { it as Integer }
			} catch (NumberFormatException e) {
				throw new Exception('Page parameter g (data point group/s) is invalid (not a number)')
			}
		}
		if (dataPoints.size()!=dpGroups.size()) {
			throw new Exception('Page parameter g (data point group/s) is invalid (wrong count)')
		}
		
		// build groups
		[dataPoints, dpGroups].transpose().each { DataPoint dp, int groupId ->
			Group grp=groups[groupId]
			if (grp==null) {
				grp=new Group()
				groups[groupId]=grp
			}
			grp.dataPoints << dp
		}

		// group heights
		List<Integer> groupHeights
		if (!request.getParameter('gh')) {
			groupHeights=[1]*dpGroups.unique().size()
		} else {
			try {
				groupHeights=request.getParameterValues('gh').collect { it as Integer }
			} catch (NumberFormatException e) {
				throw new Exception('Page parameter gh (group height/s) is invalid (not a number)')
			}
		}
		if (groups.size()!=groupHeights.size())
			throw new Exception('Page parameter gh (group height/s) is invalid (wrong count)')

		[groups.values().asList(), groupHeights].transpose().each { Group group, int height ->
			group.height=height
		}

		// time range
		def begin=TextFormat.parseDate(request.getParameter('b'))
		def end=TextFormat.parseDate(request.getParameter('e'))
		def duration=parseDuration(request.getParameter('d'))
		use(TimeCategory) {
			if (!begin) {
				if (!end) {
					end=new Date()
				}
				if (!duration) {
					begin=end-1
				} else {
					begin=end-duration
				}
			} else {
				if (!end) {
					if (!duration) {
						end=new Date()
					} else {
						end=begin+duration
					}
				}	
			}	
		}
		timeRange=TimeRange.fromDates(begin, end)
		
		// trend design
		if (request.getParameter('t') && request.getParameter('t')!='default') {
			trendDesign=trendDesigns[request.getParameter('t')]
			if (!trendDesign) {
				throw new Exception("Page parameter t (trend design) is invalid")
			}
		} else {
			trendDesign=trendDesigns['default']?:new TrendDesign()
		}
	}
		
	private static BaseDuration parseDuration(str) {
		if (!str || !(str==~/\s*(((-|\+)?\d+)([YMDWhms])\s*)+/)) {
			null
		} else {
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
					}	
				}	
			}
			dur
		}
	}
}
