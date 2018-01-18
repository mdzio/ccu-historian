package mdz.ccuhistorian.webapp

import groovy.util.GroovyTestCase

class TimeRangeTest extends GroovyTestCase {

	public void testEmpty() {
		def now=new Date()
		def r=new TimeRange(null, null)
		assert r.end.time-r.begin.time==24*60*60*1000
		assert Math.abs(r.end.time-now.time)<1000
		
		now=new Date()
		r=new TimeRange('', '')
		assert r.end.time-r.begin.time==24*60*60*1000
		assert Math.abs(r.end.time-now.time)<1000
	}
	
	public void testRelativeBegin() {
		def now=new Date()
		def r=new TimeRange('-24h', '')
		assert r.end.time-r.begin.time==24*60*60*1000
		assert Math.abs(r.end.time-now.time)<1000
	}

	public void testRelativeBeginRelativeEnd() {
		def now=new Date()
		def r=new TimeRange('-24h', '12h')
		assert r.end.time-r.begin.time==12*60*60*1000
		assert Math.abs(now.time-24*60*60*1000-r.begin.time)<1000
	}

	public void testAbsoluteBegin() {
		def now=new Date()
		def r=new TimeRange('2016', '')
		assert r.begin==Date.parse('yyyy-MM-dd', '2016-01-01')
		assert Math.abs(r.end.time-now.time)<1000
	}
	
	public void testAbsoluteBeginEnd() {
		def r=new TimeRange('1.4.2016', '9.11.2017')
		assert r.begin==Date.parse('yyyy-MM-dd', '2016-04-01')
		assert r.end==Date.parse('yyyy-MM-dd', '2017-11-09')
	}

	public void testAbsoluteBeginRelativeEnd() {
		def r=new TimeRange('1.4.2016 1:2:3', '1M -3D +2h -3m +11s')
		assert r.begin==Date.parse('yyyy-MM-dd HH:mm:ss', '2016-04-01 01:02:03')
		assert r.end==Date.parse('yyyy-MM-dd HH:mm:ss', '2016-04-28 02:59:14')
	}
}
