package mdz.ccuhistorian.webapp

import javax.servlet.http.HttpServletRequest

import org.junit.Test

class TimeRangeTest {

	@Test	
	public void testEmpty() {
		def now=new Date()
		def r=new TimeRange('', '')
		assert r.end.time-r.begin.time==24*60*60*1000
		assert Math.abs(r.end.time-now.time)<1000
	}
	
	@Test	
	public void testRelativeBegin() {
		def now=new Date()
		def r=new TimeRange('-24h', '')
		assert r.end.time-r.begin.time==24*60*60*1000
		assert Math.abs(r.end.time-now.time)<1000
	}

	@Test	
	public void testRelativeBeginRelativeEnd() {
		def now=new Date()
		def r=new TimeRange('-24h', '12h')
		assert r.end.time-r.begin.time==12*60*60*1000
		assert Math.abs(now.time-24*60*60*1000-r.begin.time)<1000
	}

	@Test	
	public void testAbsoluteBegin() {
		def now=new Date()
		def r=new TimeRange('2016', '')
		assert r.begin==Date.parse('yyyy-MM-dd', '2016-01-01')
		assert Math.abs(r.end.time-now.time)<1000
	}
	
	@Test	
	public void testAbsoluteBeginEnd() {
		def r=new TimeRange('1.4.2016', '9.11.2017')
		assert r.begin==Date.parse('yyyy-MM-dd', '2016-04-01')
		assert r.end==Date.parse('yyyy-MM-dd', '2017-11-09')
	}

	@Test	
	public void testAbsoluteBeginRelativeEnd() {
		def r=new TimeRange('1.4.2016 1:2:3', '1M -3D +2h -3m +11s')
		assert r.begin==Date.parse('yyyy-MM-dd HH:mm:ss', '2016-04-01 01:02:03')
		assert r.end==Date.parse('yyyy-MM-dd HH:mm:ss', '2016-04-28 02:59:14')
	}
	
	private HttpServletRequest buildRequest(b, e) {
		{ name -> name=='b'?b:(name=='e'?e:null) } as HttpServletRequest	
	}
	
	@Test	
	public void testRequestParameters() {
		def r=new TimeRange(buildRequest(null, null))
		assert r.beginText==null
		assert r.endText==null

		r=new TimeRange(buildRequest('1.1.2018', '1W'))
		assert r.beginText=='1.1.2018'
		assert r.endText=='1W'
		assert r.begin==Date.parse('yyyy-MM-dd HH:mm:ss', '2018-01-01 00:00:00')
		assert r.end==Date.parse('yyyy-MM-dd HH:mm:ss', '2018-01-08 00:00:00')
	}
	
	@Test	
	public void testAddParameters() {
		def r=new TimeRange(buildRequest(null, null))
		def p=r.parameters
		assert !p
		
		r=new TimeRange(buildRequest('   1.1.2018', '1h   '))
		p=r.parameters
		assert p==[b:['   1.1.2018'], e:['1h   ']]
		assert p.b instanceof String[]
	}
}
