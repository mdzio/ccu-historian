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
		// height and width of the graphics
		try {
			width=request.getParameter('w')?.toInteger()?:DEFAULT_WIDTH
			height=request.getParameter('h')?.toInteger()?:DEFAULT_HEIGHT
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException('Parameter w (width) or h (height) is invalid')
		}
		
		// data point IDs
		def dpParam=request.getParameterValues('i')
		if (dpParam==null) {
			// continue with no data points
			dpParam=[] as String[]
		}
		def dataPoints=WebUtilities.getDataPoints(dpParam.toList(), storage)

		// data point groups
		def groupParam=request.getParameterValues('g')
		def groupIds
		if (groupParam==null) {
			groupIds=[0]*dataPoints.size()
		} else {
			try {
				groupIds=groupParam.collect { it as Integer }
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException('Parameter g (data point group/s) is invalid (not a number)')
			}
		}
		if (dataPoints.size()!=groupIds.size())
			throw new IllegalArgumentException('Parameter g (data point group/s) is invalid (wrong count)')

		// build groups
		[dataPoints, groupIds].transpose().each { DataPoint dp, int groupId ->
			groups[groupId].dataPoints << dp
		}

		// group heights
		def ghParam=request.getParameterValues('gh')
		def groupHeights
		if (ghParam==null) {
			groupHeights=[1]*groupIds.unique().size()
		} else {
			try {
				groupHeights=ghParam.collect { it as Integer }
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException('Parameter gh (group height/s) is invalid (not a number)')
			}
		}
		if (groups.size()!=groupHeights.size())
			throw new IllegalArgumentException('Parameter gh (group height/s) is invalid (wrong count)')

		[groups.values() as List, groupHeights].transpose().each { Group group, Integer height ->
			group.height=height
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
		params.i=groups.values().collectMany { Group g -> g.dataPoints.idx }
		params.g=groups.collectMany { Integer id, Group group ->  [id]*group.dataPoints.size() }
		params.gh=groups.values()*.height
		params
	}
	
	@Override
	public String toString() {
		"width: $width, height: $height, groups: $groups, begin: $timeRange.begin, end: $timeRange.end"
	}
}
