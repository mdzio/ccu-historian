package mdz.hc.timeseries

import static org.junit.Assert.*

import mdz.hc.DataPoint
import mdz.hc.ProcessValue
import org.junit.Test

class TimeSeriesTest {

	@Test
	public void testGetPutAt() {
		def ts=new TimeSeries(new DataPoint())
		3.times {
			ts.add(new ProcessValue(new Date(1000*it), it as Double, ProcessValue.STATE_QUALITY_GOOD))
		}
		assert ts[1]==new ProcessValue(new Date(1000), 1.0d, ProcessValue.STATE_QUALITY_GOOD)
		ts[1]=new ProcessValue(new Date(1001), 1.5d, ProcessValue.STATE_QUALITY_BAD)
		assert ts[1]==new ProcessValue(new Date(1001), 1.5d, ProcessValue.STATE_QUALITY_BAD)
	}

	@Test
	public void testRemove() {
		def ts=new TimeSeries(new DataPoint())
		4.times {
			ts.add(new ProcessValue(new Date(1000*it), it as Double, ProcessValue.STATE_QUALITY_GOOD))
		}
		assert ts.collect { it.value }==[0, 1, 2, 3]
		ts.remove(3)
		assert ts.collect { it.value }==[0, 1, 2]
		ts.remove(1)
		assert ts.collect { it.value }==[0, 2]
		ts.remove(0)
		assert ts.collect { it.value }==[2]
		ts.remove(0)
		assert ts.collect { it.value }==[]
		try {
			ts.remove(0)
			assert false
		} catch (e) {
			assert e instanceof ArrayIndexOutOfBoundsException
		}
	}
}
