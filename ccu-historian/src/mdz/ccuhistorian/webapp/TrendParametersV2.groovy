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

import javax.servlet.http.HttpServletRequest
import mdz.ccuhistorian.TrendDesign
import mdz.ccuhistorian.webapp.TrendParameters.Group
import mdz.hc.DataPoint
import mdz.hc.persistence.DataPointStorage

class TrendParametersV2 extends TrendParameters {

	protected TrendParametersV2(HttpServletRequest request, DataPointStorage storage, Map<String, TrendDesign> trendDesigns) {
		// width and height of the graphics
		String widthText=request.getParameter('w')
		if (widthText) {
			if (!widthText.isInteger()) {
				throw new IllegalArgumentException('Parameter w (width) is invalid')
			}
			width=widthText.toInteger()
		}
		
		String heightText=request.getParameter('h')
		if (heightText) {
			if (!heightText.isInteger()) {
				throw new IllegalArgumentException('Parameter h (height) is invalid')
			}
			height=heightText.toInteger()
		}
		
		// data points
		List<DataPoint> dataPoints=[]
		List<Integer> groupIds=[]
		Collection<String> rowsTxt=request.parameterNames.findAll { String name -> 
			name.startsWith('dp')
		}
		Collection<Integer> rowsIdx=rowsTxt.collect { String name ->
			try {
				name.substring(2).toInteger()
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException('Parameter name dp... (data point) ends not with a number: ' + name)
			}
		}.sort()
		rowsIdx.each { Integer rowIdx ->
			// data point
			dataPoints << WebUtilities.getDataPoint(request.getParameter('dp' + rowIdx), storage)
			// related group parameter
			String groupTxt=request.getParameter('g' + rowIdx)
			if (groupTxt==null) {
				// use default group
				groupIds << 1
			} else {
				try {
					groupIds << groupTxt.toInteger()
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException('Parameter g' + rowIdx + 
						' (data point group) is invalid (not a number): ' + groupTxt)
				}
			}
		}
		
		// build groups
		[dataPoints, groupIds].transpose().each { DataPoint dp, int groupId ->
			Group grp=groups[groupId]
			if (grp==null) {
				grp=new Group()
				groups[groupId]=grp
			}
			grp.dataPoints << dp
		}

		// group heights
		groups.each { Integer groupId, Group group ->
			String ghTxt=request.getParameter('gh' + groupId)
			if (ghTxt!=null) {
				try {
					group.height=ghTxt.toInteger()
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException('Parameter gh' + groupId + 
						' (group height) is invalid (not a number):' + ghTxt)
				}
			}
		}
		
		// normalize group indices
		if (groups && (groups.firstKey()!=1 || groups.lastKey()!=groups.size())) {
			TreeMap<Integer, Group> tmp=new TreeMap<Integer, Group>()
			int idx=1
			groups.each { k, v -> tmp[idx++]=v }
			groups=tmp
		}
		
		// time range
		timeRange=new TimeRange(request.getParameter('b'), request.getParameter('e'))
		
		// trend design
		String tParam=request.getParameter('t')
		if (tParam!=null && tParam!='default') {
			trendDesign=trendDesigns[tParam]
			if (!trendDesign)
				throw new IllegalArgumentException("Parameter t (trend design) is invalid")
		} else {
			trendDesign=trendDesigns['default']?:new TrendDesign(identifier:'default')
		}
	}
}
