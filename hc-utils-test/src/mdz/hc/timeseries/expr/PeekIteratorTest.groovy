package mdz.hc.timeseries.expr

import static org.junit.Assert.*

import mdz.hc.ProcessValue
import org.junit.Test
import static mdz.hc.ProcessValue.*

class PeekIteratorTest {

	private pv(ts, v) {
		new ProcessValue(new Date(ts), v as Double, STATE_QUALITY_GOOD)
	}

	@Test
	public void testEmpty() {
		def it=new PeekIterator([].iterator())
		assert it.peekNext()==null
		assert it.peekPrevious()==null
		
		assert !it.hasNext()
	}

	@Test
	public void testOne() {
		def it=new PeekIterator([pv(0, 0)].iterator())
		assert it.peekNext()==pv(0, 0)
		assert it.peekPrevious()==null
		
		assert it.hasNext()
		assert it.next()==pv(0, 0)
		assert it.peekNext()==null
		assert it.peekPrevious()==pv(0, 0)
		
		assert !it.hasNext()
	}
	
	@Test
	public void testTwo() {
		def it=new PeekIterator([pv(0, 0), pv(1, 1)].iterator())
		assert it.peekNext()==pv(0, 0)
		assert it.peekPrevious()==null
		
		assert it.hasNext()
		assert it.next()==pv(0, 0)
		assert it.peekNext()==pv(1, 1)
		assert it.peekPrevious()==pv(0, 0)

		assert it.hasNext()
		assert it.next()==pv(1, 1)
		assert it.peekNext()==null
		assert it.peekPrevious()==pv(1, 1)

		assert !it.hasNext()
	}
}
