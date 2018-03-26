package mdz.ccuhistorian.webapp

import java.util.Map
import javax.servlet.http.HttpServletRequest
import groovy.transform.CompileStatic
import mdz.ccuhistorian.TrendDesign
import mdz.hc.DataPoint
import mdz.hc.persistence.DataPointStorage

@CompileStatic
public class TrendParameters {

	public static int DEFAULT_WIDTH = 1000
	public static int DEFAULT_HEIGHT = 600
	
	public static class Group {
		int height=1
		List<DataPoint> dataPoints=[]
		
		@Override
		public String toString() {
			"(height: $height, dataPoints: $dataPoints.displayName)"
		}
	}
	
	int width, height
	TimeRange timeRange
	TrendDesign trendDesign
	Map<Integer /* group id */, Group> groups=new TreeMap<Integer, Group>().withDefault { new Group() }

	public TrendParameters(HttpServletRequest request, DataPointStorage storage, Map<String, TrendDesign> trendDesigns) {
		// width and height of the graphics
		try {
			width=request.getParameter('w')?.toInteger()?:DEFAULT_WIDTH
			height=request.getParameter('h')?.toInteger()?:DEFAULT_HEIGHT
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException('Parameter w (width) or h (height) is invalid')
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
			groups[groupId].dataPoints << dp
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
	
	public Map<String, String[]> getParameters() {
		Map<String, String[]> params=[:]
		if (width!=DEFAULT_WIDTH) {
			params.w=[width]
		}
		if (height!=DEFAULT_HEIGHT) {
			params.h=[height]
		}
		params << timeRange.parameters
		if (trendDesign.identifier!='default') {
			params.t=[trendDesign.identifier]
		}
		int dpIdx=1
		groups.each { Integer groupId, Group group ->
			params['gh' + groupId]=[group.height] as String[]
			group.dataPoints.each { DataPoint dp ->
				params['dp' + dpIdx]=[dp.idx] as String[]
				params['g' + dpIdx]=[groupId] as String[]
				dpIdx++
			} 
		}
		params
	}
	
	@Override
	public String toString() {
		"width: $width, height: $height, groups: $groups, begin: $timeRange.begin, end: $timeRange.end"
	}
}
