package mdz.ccuhistorian.eventprocessing

import mdz.eventprocessing.Collector
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue

import org.junit.Before
import org.junit.Test

class SwingingDoorTest {

	private final boolean logFailedToStdOut = true
	
	DataPoint dataPoint

	@Before
	void before() {
		dataPoint=new DataPoint(id:new DataPointIdentifier("", "", "Test"))
		dataPoint.attributes.(DataPoint.ATTR_PREPROC_TYPE)=6 // swinging door
		dataPoint.attributes.(DataPoint.ATTR_PREPROC_PARAM)=0.1 // deviation
	}

	private Event event(long ts, value, int state = ProcessValue.STATE_QUALITY_GOOD) {
		new Event(dataPoint: dataPoint,	pv:new ProcessValue(new Date(ts), value, state))
	}

	@Test
	public void testEmpty() {
		helper([], [])
	}

	@Test
	public void testOne() {
		helper([
			event(100, 1.23)
		], [
			event(100, 1.23)
		])
	}

	@Test
	public void testTwo() {
		helper([
			event(100, 1.23),
			event(1100, 21.23)
		], [
			event(100, 1.23),
			event(1100, 21.23)
		])
	}
	
	@Test
	public void testTwoWithoutStop() {
		helper([
			event(100, 1.23),
			event(1100, 21.23)
		], [
			event(100, 1.23)
		], false)
	}

	@Test
	public void testLinear() {
		helper([
			event(0, 0.0),
			event(1000, 1.0),
			event(2000, 2.0),
			event(3000, 3.0),
			event(4000, 4.0),
		], [
			event(0, 0.0),
			event(4000, 4.0)
		])
	}

	@Test
	public void testTwoLinear() {
		helper([
			event(0, 0.0),
			event(1000, 1.0),
			event(2000, 2.0),
			event(3000, 3.0),
			event(4000, 4.0),
			event(5000, 6.0),
			event(6000, 5.0),
			event(7000, 4.0),
		], [
			event(0, 0.0),
			event(4000, 4.0),
			event(5000, 6.0),
			event(7000, 4.0),
		])
	}

	@Test
	public void testSawTooth() {
		helper([
			event(0, 0.0),
			event(1000, 1.0),
			event(2000, 2.0),
			event(3000, 0.0),
			event(4000, 1.0),
			event(5000, 2.0),
			event(6000, 0.0),
			event(7000, 1.0),
			event(8000, 2.0),
		], [
			event(0, 0.0),
			event(2000, 2.0),
			event(3000, 0.0),
			event(5000, 2.0),
			event(6000, 0.0),
			event(8000, 2.0),
		])
	}

	@Test
	public void testTrapezoid() {
		helper([
			event(0, 0.0),
			event(1, -1.0),
			event(2, -2.0),
			event(3, -2.0),
			event(4, -2.0),
			event(5, -1.0),
			event(6, 0.0),
			event(7, 1.0),
			event(8, 2.0),
			event(9, 2.0),
			event(10, 2.0),
			event(11, 1.0),
			event(12, 0.0),
		], [
			event(0, 0.0),
			event(2, -2.0),
			event(4, -2.0),
			event(8, 2.0),
			event(10, 2.0),
			event(12, 0.0),
		])
	}

	@Test
	public void testWithinDeviation() {
		helper([
			event(0, 0.0),
			event(1000, -1.1), // in band
			event(1000000, -1000.0),
		], [
			event(0, 0.0),
			event(1000000, -1000.0),
		])
	}
	
	@Test
	public void testDeviationExceeded() {
		helper([
			event(0, 0.0),
			event(1000, -1.11), // out of band
			event(1000000, -1000.0),
		], [
			event(0, 0.0),
			event(1000, -1.11), // out of band
			event(1000000, -1000.0),
		])
	}

	private helper(List<Event> input, List<Event> expected, boolean stop=true) {
		Collector c=[]
		Preprocessor pp=[]
		pp.addConsumer c
		input.each { pp.consume it }
		if (stop) {
			pp.stop()
		}		
		List<Event> got=c.get()
		if (logFailedToStdOut && expected!=got) {
			println "Failed test:"
			println "  Expected:"
			expected.each { println "    $it.pv.timestamp.time, $it.pv.value" }
			println "  Got:"
			got.each { println "    $it.pv.timestamp.time, $it.pv.value" }
		}
		assert expected==got
	}
}
