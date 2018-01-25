package mdz.ccuhistorian.webapp

import groovy.util.GroovyTestCase
import javax.servlet.http.HttpServletRequest
import mdz.ccuhistorian.TrendDesign
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.persistence.DataPointStorage

class TrendParametersTest extends GroovyTestCase {

	private requestStub(params) {
		[
			getParameter: { String name ->
				params[name]?params[name][0]:null
			},
			getParameterValues: { String name ->
				params[name] as String[]
			},
		] as HttpServletRequest
	}
	
	private storageStub() {
		{ 	id -> 
			new DataPoint(
				idx: id, 
				id:new DataPointIdentifier("ITF", "N$id", "V")
			) 
		} as DataPointStorage
	}

	public void test() {
		def tp=new TrendParameters(
			requestStub([
				w:['320'],
				h:['240'],
				i:['42', '24', '1'], 
				b:['2018'], e:['2019'],
				g:['1', '2', '1'],
				gh:['3', '4'],
				t:['t42']
			]), 
			storageStub(), 
			[
				't42':new TrendDesign(identifier:'design t42')
			]
		)
		
		assert tp.width==320
		assert tp.height==240
		assert tp.timeRange.begin==Date.parse('yyyy', '2018')
		assert tp.timeRange.end==Date.parse('yyyy', '2019')
		assert tp.trendDesign.identifier=='design t42'
		assert tp.groups.size()==2
		assert tp.groups[1].height==3
		assert tp.groups[1].dataPoints.idx==[42, 1]
		assert tp.groups[2].height==4
		assert tp.groups[2].dataPoints.idx==[24]
		
		def params=tp.parameters
		assert params.size()==8
		assert params.w==['320']
		assert params.h==['240']
		assert params.b==['2018']
		assert params.e==['2019']
		assert params.t==['design t42']
		assert params.i==['42', '1', '24']
		assert params.g==['1', '1', '2']
		assert params.gh==['3', '4']
	}
}
