package mdz.hc.timeseries.expr

import static org.junit.Assert.*

import mdz.hc.ProcessValue
import mdz.hc.ProcessValuePair

import static mdz.hc.ProcessValue.*
import static mdz.hc.timeseries.expr.Expression.*
import static mdz.hc.timeseries.expr.Characteristics.*
import org.junit.Test

class ExpressionTest {

	private pv(ts, v) {
		new ProcessValue(new Date(ts), v as Double, STATE_QUALITY_GOOD)
	}

	private pv(ts, v, st) {
		new ProcessValue(new Date(ts), v as Double, st)
	}

	private pvp(first, second) {
		def r=new ProcessValuePair()
		r.first=first
		r.second=second
		r
	}

	private pvEquals(ProcessValue first, ProcessValue second) {
		first.timestamp==second.timestamp && first.state==second.state &&
				Math.abs(first.value-second.value) < 0.001
	}

	@Test
	public void testFromDouble() {
		def e=from(123.456)
		def ts=e.read(new Date(1000), new Date(3000))
		assert ts.collect()==[
			pv(1000, 123.456, STATE_QUALITY_GOOD),
			pv(2999, 123.456, STATE_QUALITY_GOOD),
		]
	}

	@Test
	public void testFromIterableAndSanitize() {
		def l=(0..<5).collect { pv(it*1000, it) }
		def e=from(l, 0).sanitize()
		def ts=e.read(new Date(1000), new Date(3000))
		assert ts.collect()==[pv(1000, 1), pv(2000, 2)]
	}

	@Test
	public void testFilter() {
		def l=(0..<5).collect { pv(it*1000, it) }
		def e=from(l, 0).filter { pv -> pv.value==3 }
		def ts=e.read(new Date(0), new Date(5000))
		assert ts.collect()==[pv(3000, 3)]
	}

	@Test
	public void testUnaryOperator() {
		def l=(0..<5).collect {
			pv(it*1000, it)
		}
		def e=from(l, 0).unaryOperator { pv ->
			pv.value*=2;  pv
		}
		def ts=e.read(new Date(0), new Date(5000))
		assert ts.collect()==(0..<5).collect { pv(it*1000, it*2) }
	}

	@Test
	public void testScanMany() {
		def tsin=[
			pv(1000, 10),
			pv(5000, 5)
		]
		def tsout=[
			pv(1000, 10),
			pv(2000, 11),
			pv(5000, 5),
			pv(7000, 7)
		]
		def e=from(tsin, 0).scanMany({ [0] }, { s, v ->
			s[0]++
			[
				v,
				pv(v.timestamp.time+s[0]*1000, v.value+s[0])
			].iterator()
		})
		assert e.read(new Date(0), new Date(10000)).collect()==tsout
		// 2nd try because of state
		assert e.read(new Date(0), new Date(10000)).collect()==tsout
	}

	@Test
	public void testSanitize() {
		def tsin=[
			pv(1000, 10),
			pv(2000, 11),
			pv(3000, 1),
			pv(3000, 5),
			pv(5000, 0),
			pv(6000, 3)
		]
		def tsout=[
			pv(2000, 11),
			pv(3000, 1),
			pv(5000, 0),
		]
		def e=from(tsin, 0).sanitize()
		assert e.read(new Date(2000), new Date(6000)).collect()==tsout
	}

	@Test
	public void testLinear() {
		def tsin=[
			pv(1000, 10, STATE_QUALITY_QUESTIONABLE),
			pv(2000, 11),
		]
		def tsout=[
			pv(1000, 10, STATE_QUALITY_QUESTIONABLE),
			pv(1999, 10, STATE_QUALITY_QUESTIONABLE),
			pv(2000, 11)
		]
		def e=from(tsin, HOLD).linear()
		assert e.read(new Date(0), new Date(10000)).collect()==tsout
	}

	@Test
	public void testCounter() {
		def tsin=[
			pv(1000, 10),
			pv(2000, 11),
			pv(3000, 1),
			pv(4000, 5),
			pv(5000, 0),
			pv(6000, 3)
		]
		def tsout=[
			pv(1000, 0),
			pv(2000, 1),
			pv(3000, 1),
			pv(4000, 5),
			pv(5000, 5),
			pv(6000, 8)
		]
		def e=from(tsin, 0).counter()
		assert e.read(new Date(0), new Date(10000)).collect()==tsout
	}

	@Test
	public void testDifferentiate() {
		def tu=TIME_UNIT
		def tsin=[
			pv(0*tu, 1),
			pv(3*tu, 4),
			pv(4*tu, 1),
			pv(5*tu, 10),
			pv(5*tu, 10),
			pv(6*tu, 12),
			pv(8*tu, 7, STATE_QUALITY_QUESTIONABLE),
		]
		def tsout=[
			pv(0*tu, 1),
			pv(3*tu, -3),
			pv(4*tu, 9),
			pv(5*tu, 0, STATE_QUALITY_BAD),
			pv(5*tu, 2),
			pv(6*tu, -2.5, STATE_QUALITY_QUESTIONABLE)
		]
		def e=from(tsin, 0).differentiate()
		assert e.read(new Date(0), new Date(10*tu)).collect()==tsout
	}

	@Test
	public void testBinaryOperator() {
		def expr1=from([
			pv(0, 5),
			pv(10, 10),
		], HOLD)
		def expr2=from([
			pv(0, 0),
			pv(10, 10),
		], LINEAR)
		def expr=expr1.binaryOperator(expr2, { ProcessValue pv1, ProcessValue pv2 ->
			new ProcessValue(pv1.timestamp, pv1.value+pv2.value, combineStates(pv1, pv2))
		})
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(0, 5),
			pv(9, 14),
			pv(10, 20),
		]
	}

	@Test
	public void testMixed() {
		def a=from([
			pv(0, 0),
			pv(5, 5),
		], LINEAR)
		def b=from([
			pv(0, 2),
			pv(1, 1),
			pv(2, 2),
			pv(3, 1),
			pv(4, 2),
			pv(5, 1),
		], LINEAR)
		def c=from([
			pv(0, 5),
			pv(5, 0),
		], LINEAR)
		def expr=((-a*b)*2-c-3.0)/b/2.0+2.0+a
		def r=expr.read(new Date(0), new Date(6)).toList()
		def w=[
			pv(0, 0),
			pv(1, -1.5),
			pv(2, 0.5),
			pv(3, -0.5),
			pv(4, 1),
			pv(5, 0.5),
		]
		assert r==w
	}
}
