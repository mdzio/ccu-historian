package mdz.hc.timeseries.expr

import static org.junit.Assert.*

import mdz.hc.ProcessValue
import static mdz.hc.ProcessValue.*
import org.junit.Test

class AggregateFunctionsTest {

	private pv(ts, v) {
		new ProcessValue(new Date(ts), v as Double, STATE_QUALITY_GOOD)
	}

	private pv(ts, v, st) {
		new ProcessValue(new Date(ts), v as Double, st)
	}

	@Test
	public void testMinimum() {
		def ts=[
			pv(1050, 1.0),
			pv(1150, -1.0),
		]
		def inter=new Interval(new Date(1000), new Date(2000), ts.iterator())
		assert AggregateFunctions.minimum().apply(inter)==pv(1000, -1.0, STATE_QUALITY_QUESTIONABLE)
	}

	@Test
	public void testAverageNoEntries() {
		def ts=[]
		def inter=new Interval(new Date(1000), new Date(2000), ts.iterator())
		assert AggregateFunctions.average().apply(inter)==pv(1000, 0.0, STATE_QUALITY_BAD)
	}

	@Test
	public void testAverageOneEntry() {
		def ts=[pv(1000, 1.0)]
		def inter=new Interval(new Date(1000), new Date(2000), ts.iterator())
		assert AggregateFunctions.average().apply(inter)==pv(1000, 1.0, STATE_QUALITY_QUESTIONABLE)
	}

	@Test
	public void testAverageTwoOnBoundaries() {
		def ts=[
			pv(1000, 2.0),
			pv(2000, 0.0),
		]
		def inter=new Interval(new Date(1000), new Date(2000), ts.iterator())
		assert AggregateFunctions.average().apply(inter)==pv(1000, 1.0)
	}

	@Test
	public void testAverageState() {
		def ts=[
			pv(1000, 5.0),
			pv(2000, 3.0, STATE_QUALITY_QUESTIONABLE),
		]
		def inter=new Interval(new Date(1000), new Date(2000), ts.iterator())
		assert AggregateFunctions.average().apply(inter)==pv(1000, 4.0, STATE_QUALITY_QUESTIONABLE)
	}

	@Test
	public void testAverageThree() {
		def ts=[
			pv(1000, 0.0),
			pv(1500, 10.0),
			pv(2000, 0.0),
		]
		def inter=new Interval(new Date(1000), new Date(2000), ts.iterator())
		assert AggregateFunctions.average().apply(inter)==pv(1000, 5.0)
	}

	@Test
	public void testBeginNoEntries() {
		def tsit=[].iterator()
		def inter=new Interval(new Date(1000), new Date(2000), tsit)
		assert AggregateFunctions.begin().apply(inter)==pv(1000, 0.0, STATE_QUALITY_BAD)
		assert !tsit.hasNext()
	}

	@Test
	public void testBeginOnBoundary() {
		def tsit=[
			pv(1000, 10.0),
			pv(2000, 20.0),
		].iterator()
		def inter=new Interval(new Date(1000), new Date(2000), tsit)
		assert AggregateFunctions.begin().apply(inter)==pv(1000, 10.0)
		assert !tsit.hasNext()
	}

	@Test
	public void testBeginNotOnBoundary() {
		def tsit=[
			pv(1500, 10.0),
			pv(2000, 20.0),
		].iterator()
		def inter=new Interval(new Date(1000), new Date(2000), tsit)
		assert AggregateFunctions.begin().apply(inter)==pv(1000, 10.0, STATE_QUALITY_QUESTIONABLE)
		assert !tsit.hasNext()
	}
}
