package mdz.ccuhistorian.webapp

import javax.servlet.http.HttpServletRequest

import org.junit.Test

import mdz.ccuhistorian.TrendDesign
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.persistence.DataPointStorage

class TrendParametersTest {

	private requestStub(params) {
		[
			getParameter: { String name ->
				params[name]?params[name][0]:null
			},
			getParameterValues: { String name ->
				params[name] as String[]
			},
			getParameterNames: { -> 
				Collections.enumeration(params.keySet())
			}
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

	@Test
	public void testCompleteParameters() {
		def tp=TrendParameters.from(
			requestStub([
				w:['320'],
				h:['240'],
				dp1:['42'],
				dp2:['24'],
				dp3:['1'],
				b:['2018'], e:['2019'],
				g1:['1'],
				g2:['2'],
				g3:['1'],
				gh1:['3'],
				gh2:['4'],
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
		assert params.size()==13
		assert params.w==[320]
		assert params.h==[240]
		assert params.b==['2018']
		assert params.e==['2019']
		assert params.t==['design t42']
		assert [params.dp1, params.dp2, params.dp3]==[['42'], ['1'], ['24']]
		assert [params.g1, params.g2, params.g3]==[['1'], ['1'], ['2']]
		assert [params.gh1, params.gh2]==[['3'], ['4']]
	}
	
	@Test
	public void testPartialParameters() {
		def tp=TrendParameters.from(
			requestStub([
				dp1:['42'],
				dp3:['1'],
				b:['2017'], e:['2018'],
				g3:['3'],
				gh1:['2'],
				t:['my']
			]),
			storageStub(),
			[
				'my':new TrendDesign(identifier:'my design')
			]
		)
		
		assert tp.width==1000
		assert tp.height==600
		assert tp.timeRange.begin==Date.parse('yyyy', '2017')
		assert tp.timeRange.end==Date.parse('yyyy', '2018')
		assert tp.trendDesign.identifier=='my design'
		assert tp.groups.size()==2
		assert tp.groups[1].height==2
		assert tp.groups[1].dataPoints.idx==[42]
		assert tp.groups[2].height==1
		assert tp.groups[2].dataPoints.idx==[1]
		
		def params=tp.parameters
		assert params.size()==9
		assert params.b==['2017']
		assert params.e==['2018']
		assert params.t==['my design']
		assert [params.dp1, params.dp2]==[['42'], ['1']]
		assert [params.g1, params.g2]==[['1'], ['2']]
		assert [params.gh1, params.gh2]==[['2'], ['1']]
	}
	
	@Test
	public void testParametersV1() {
		def tp=TrendParameters.from(
			requestStub([
				i:['42', '1'],
				b:['2017'], e:['2018'],
				g:['1', '3'],
				gh:['5', '2'],
				t:['my']
			]),
			storageStub(),
			[
				'my':new TrendDesign(identifier:'my design')
			]
		)
		
		assert tp.width==640
		assert tp.height==260
		assert tp.timeRange.begin==Date.parse('yyyy', '2017')
		assert tp.timeRange.end==Date.parse('yyyy', '2018')
		assert tp.trendDesign.identifier=='my design'
		assert tp.groups.size()==2
		assert tp.groups[1].height==5
		assert tp.groups[1].dataPoints.idx==[42]
		assert tp.groups[3].height==2
		assert tp.groups[3].dataPoints.idx==[1]
	}
	
	@Test
	public void testParametersV1BeginAndDuration() {
		def tp=TrendParameters.from(
			requestStub([
				i:['42'],
				b:['2017'], d:['1D  2h'],
			]),
			storageStub(),
			[:]
		)
		assert tp.timeRange.begin==Date.parse('yyyy', '2017')
		assert tp.timeRange.end==Date.parse('yyyy-MM-dd hh:mm:ss', '2017-01-02 02:00:00')
	}

	@Test
	public void testParametersV1Duration() {
		def now=new Date()
		def tp=TrendParameters.from(
			requestStub([
				i:['42'],
				d:['1W'],
			]),
			storageStub(),
			[:]
		)
		assert Math.abs(now.time-tp.timeRange.end.time)<1000
		assert tp.timeRange.end.time-tp.timeRange.begin.time==7*24*60*60*1000
	}
}
