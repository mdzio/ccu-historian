package mdz.hc.timeseries.expr

import static org.junit.Assert.*

import mdz.hc.ProcessValue
import org.junit.Test
import static mdz.hc.ProcessValue.*

class CutIteratorTest {

	private pv(ts, v) {
		new ProcessValue(new Date(ts), v as Double, STATE_QUALITY_GOOD)
	}

	private test(ts) {
		def tsit=new PeekIterator(ts.iterator())
		def out=new CutIterator(new Date(100), new Date(200), tsit).toList()
		[out, tsit.toList()]
	}

	@Test
	public void testEmpty() {
		def (out, rem)=test([])
		assert out==[]
		assert rem==[]
	}

	@Test
	public void testOneBeforeBegin() {
		def (out, rem)=test([pv(50, 0)])
		assert out==[]
		assert rem==[]
	}

	@Test
	public void testOneWithinInterval() {
		def (out, rem)=test([pv(150, 0)])
		assert out==[pv(150,0)]
		assert rem==[]
	}

	@Test
	public void testOneAfterEnd() {
		def (out, rem)=test([pv(250, 0)])
		assert out==[]
		assert rem==[pv(250, 0)]
	}
	
	@Test
	public void testInterpolateBeginEnd() {
		def (out, rem)=test([
			pv(50, 50),
			pv(250, 250),
		])
		assert out==[
			pv(100, 100),
			pv(200, 200),
		]
		assert rem==[
			pv(250, 250),
		]
	}
	
	@Test
	public void testInterpolateBeginMid() {
		def (out, rem)=test([
			pv(50, 50),
			pv(150, 150),
		])
		assert out==[
			pv(100, 100),
			pv(150, 150),
		]
		assert rem==[
		]
	}

	@Test
	public void testInterpolateMidEnd() {
		def (out, rem)=test([
			pv(150, 150),
			pv(250, 250),
		])
		assert out==[
			pv(150, 150),
			pv(200, 200),
		]
		assert rem==[
			pv(250, 250),
		]
	}

	@Test
	public void testInterpolateBeginMidEnd() {
		def (out, rem)=test([
			pv(50, 50),
			pv(150, 150),
			pv(250, 250),
		])
		assert out==[
			pv(100, 100),
			pv(150, 150),
			pv(200, 200),
		]
		assert rem==[
			pv(250, 250),
		]
	}
	
	@Test
	public void testExactBeginEnd() {
		def (out, rem)=test([
			pv(0, 0),
			pv(100, 100),
			pv(200, 200),
			pv(300, 300),
		])
		assert out==[
			pv(100, 100),
			pv(200, 200),
		]
		assert rem==[
			pv(200, 200),
			pv(300, 300),
		]
	}
}
