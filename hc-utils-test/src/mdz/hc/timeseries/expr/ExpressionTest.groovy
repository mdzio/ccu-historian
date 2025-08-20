package mdz.hc.timeseries.expr

import static org.junit.Assert.*

import mdz.hc.ProcessValue
import mdz.hc.ProcessValuePair

import static mdz.hc.ProcessValue.*
import static mdz.hc.timeseries.expr.Expressions.*
import static mdz.hc.timeseries.expr.Characteristics.*
import org.junit.Test

class ExpressionTest {

	static pv(ts, v) {
		new ProcessValue(new Date(ts), v as Double, STATE_QUALITY_GOOD)
	}

	static pv(ts, v, st) {
		new ProcessValue(new Date(ts), v as Double, st)
	}

	static pvp(first, second) {
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
			pv(3000, 123.456, STATE_QUALITY_GOOD),
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
	public void testScanMany2() {
		def tsin=[
			pv(1000, 10),
			pv(5000, 5)
		]
		def tsout=[
			pv(1000, 10),
			pv(5000, 5)
		]
		def e=from(tsin, 0).scanMany({ [] }, { s, pv -> s << pv; [].iterator() }, { s -> s.iterator() })
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
		assert from([], HOLD).linear().read(new Date(0), new Date(10000)).collect().empty
		
		def tsin=[
			pv(1000, 10, STATE_QUALITY_QUESTIONABLE),
			pv(2000, 11),
		]
		def tsout=[
			pv(1000, 10, STATE_QUALITY_QUESTIONABLE),
			pv(1999, 10, STATE_QUALITY_QUESTIONABLE),
			pv(2000, 11),
			pv(10000, 11),
		]
		def e=from(tsin, HOLD).linear()
		assert e.read(new Date(0), new Date(10000)).collect()==tsout

		tsin=[
			pv(1000, 10),
			pv(1001, 11),
			pv(1002, 12),
			pv(1010, 13),
		]
		tsout=[
			pv(1000, 10),
			pv(1001, 11),
			pv(1002, 12),
			pv(1009, 12),
			pv(1010, 13),
			pv(10000, 13),
		]
		e=from(tsin, HOLD).linear()
		assert e.read(new Date(0), new Date(10000)).collect()==tsout

		tsin=[pv(10000, 10),]
		tsout=[pv(10000, 10),]
		e=from(tsin, HOLD).linear()
		assert e.read(new Date(0), new Date(10000)).collect()==tsout

		tsin=[
			pv(9999, 3),
			pv(10000, 3),
		]
		tsout=[
			pv(9999, 3),
			pv(10000, 3),
		]
		e=from(tsin, HOLD).linear()
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
		def tu=Expression.TIME_UNIT
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
			new ProcessValue(pv1.timestamp, pv1.value+pv2.value, AggregateFunctions.combineStates(pv1, pv2))
		})
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(0, 5),
			pv(9, 14),
			pv(10, 20),
		]
	}

	@Test
	public void testAdd() {
		def expr1=from([
			pv(2, 2),
			pv(10, 10),
		], LINEAR)
		def expr2=from([
			pv(0, 0),
			pv(8, 8),
		], LINEAR)
		def expr=expr1+expr2
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(0, 0, STATE_QUALITY_BAD),
			pv(2, 4),
			pv(8, 16),
			pv(10, 0, STATE_QUALITY_BAD),
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
		def r=expr.read(new Date(0), new Date(5)).toList()
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

	@Test
	public void testAggregate() {
		def src=from([
			pv(1000, 1.0),
			pv(3000, 3.0),
			pv(8000, 3.0),
			pv(10000, 5.0),
		], LINEAR)

		def r=src.aggregate(from([], 0), null).read(new Date(0), new Date(10000)).toList()
		assert r.isEmpty()

		r=src.aggregate(from([pv(5000, 0.0)], 0), null).read(new Date(0), new Date(10000)).toList()
		assert r.isEmpty()

		def ts=from([
			pv(2000, 0.0),
			pv(5000, 0.0),
		], 0)
		r=src.aggregate(ts, AggregateFunctions.minimum()).read(new Date(0), new Date(10000)).toList()
		assert r==[pv(2000, 2.0),]

		ts=from([
			pv(0, 0.0),
			pv(500, 0.0),
			pv(1000, 0.0),
			pv(2000, 0.0),
			pv(3000, 0.0),
			pv(4000, 0.0),
			pv(7000, 0.0),
			pv(8000, 0.0),
			pv(9000, 0.0),
			pv(10000, 0.0),
			pv(11000, 0.0),
		], 0)
		r=src.aggregate(ts, AggregateFunctions.maximum()).read(new Date(0), new Date(12000)).toList()
		assert r==[
			pv(0, 0.0, STATE_QUALITY_BAD),
			pv(500, 1.0, STATE_QUALITY_QUESTIONABLE),
			pv(1000, 2.0),
			pv(2000, 3.0),
			pv(3000, 3.0),
			pv(4000, 3.0),
			pv(7000, 3.0),
			pv(8000, 4.0),
			pv(9000, 5.0),
			pv(10000, 5.0, STATE_QUALITY_QUESTIONABLE),
		]
	}

	@Test
	public void testClipZero() {
		def ts=from([
			pv(0, -1.0),
			pv(2000, 1.0),
			pv(4000, -1.0),
			pv(5000, 0.0),
			pv(6000, 1.0),
			pv(8000, -1.0),
			pv(10000, 0.0),
		], 0)
		def r=ts.clipZero().read(new Date(0), new Date(10000)).toList()
		assert r==[
			pv(0, 0.0),
			pv(1000, 0.0),
			pv(2000, 1.0),
			pv(3000, 0.0),
			pv(4000, 0.0),
			pv(5000, 0.0),
			pv(6000, 1.0),
			pv(7000, 0.0),
			pv(8000, 0.0),
			pv(10000, 0.0)
		]
	}

	@Test
	public void testClip() {
		def ts=from([
			pv(0, 10.0),
			pv(20000, -10.0),
			pv(30000, -10.0),
			pv(50000, 10.0),
		], 0)
		def r=ts.clip(1, 5).read(new Date(0), new Date(50000)).toList()
		assert r==[
			pv(0, 5.0),
			pv(5000, 5.0),
			pv(9000, 1.0),
			pv(20000, 1.0),
			pv(30000, 1.0),
			pv(41000, 1.0),
			pv(45000, 5.0),
			pv(50000, 5.0),
		]
	}

	@Test
	public void testIntegrate() {
		def TU=Expression.TIME_UNIT
		def ts=from([
			pv(0, 10.0),
			pv(1*TU, 11.0),
			pv(2*TU, 10.0),
			pv(3*TU, 9.0),
			pv(4*TU, 0.0),
			pv(5*TU, -9.0),
		], 0)
		def r=ts.integrate().read(new Date(0), new Date(10000)).toList()
		assert r==[
			pv(0, 0.0),
			pv(1*TU, 10.5),
			pv(2*TU, 21.0),
			pv(3*TU, 30.5),
			pv(4*TU, 35.0),
			pv(5*TU, 30.5)
		]
	}

	@Test
	public void testGreaterThan() {
		def TU=Expression.TIME_UNIT

		def ts=from([
			pv(0, 0.0),
			pv(1*TU, 1.0),
			pv(2*TU, 11.0),
			pv(3*TU, 9.0),
			pv(4*TU, 20.0),
			pv(5*TU, -9.0),
		], Characteristics.HOLD)
		def r=ts.greaterThan(10).read(new Date(0), new Date(10*TU)).toList()
		assert r==[
			pv(0, 0.0),
			pv(1*TU, 0.0),
			pv(2*TU, 1.0),
			pv(3*TU, 0.0),
			pv(4*TU, 1.0),
			pv(5*TU, 0.0)
		]

		def ts1=from([
			pv(0, 0.0),
			pv(2*TU, 3.0),
			pv(6*TU, 3.0),
			pv(10*TU, 7.0),
			pv(11*TU, 6.5),
			pv(12*TU, 7.0),
			pv(14*TU, 9.0),
		], Characteristics.LINEAR)
		def ts2=from([
			pv(0, 1.0),
			pv(14*TU, 8.0),
		], Characteristics.LINEAR)
		r=ts1.greaterThan(ts2).read(new Date(0), new Date(14*TU)).toList()
		assert r==[
			pv(0, 0.0),
			pv(1*TU, 1.0),
			pv(2*TU, 1.0),
			pv(4*TU, 0.0),
			pv(6*TU, 0.0),
			pv(8*TU, 1.0),
			pv(10*TU, 1.0),
			pv(11*TU, 0.0),
			pv(12*TU, 0.0),
			pv(12*TU+1, 1.0),
			pv(14*TU, 1.0)
		]
	}

	@Test
	public void testLessThan() {
		def TU=Expression.TIME_UNIT
		def ts1=from([
			pv(0, 0.0),
			pv(2*TU, 2.0),
		], Characteristics.LINEAR)
		def ts2=from([
			pv(0, 2.0),
			pv(2*TU, 0.0),
		], Characteristics.LINEAR)
		def r=ts1.lessThan(ts2).read(new Date(0), new Date(2*TU)).toList()
		assert r==[
			pv(0, 1.0),
			pv(1*TU, 0.0),
			pv(2*TU, 0.0),
		]
	}

	@Test
	public void testBadIf() {
		def TU=Expression.TIME_UNIT
		def ts1=from([
			pv(0, 0.0),
			pv(3*TU, 3.0),
		], Characteristics.LINEAR)
		def ts2=from([
			pv(0, 0.0),
			pv(1*TU, 1.0),
			pv(2*TU, 0.0, ProcessValue.STATE_QUALITY_BAD),
			pv(3*TU, 0.0),
		], Characteristics.LINEAR)
		def r=ts1.badIf(ts2).read(new Date(0), new Date(3*TU)).toList()
		assert r==[
			pv(0, 0.0),
			pv(1*TU, 0.0, ProcessValue.STATE_QUALITY_BAD),
			pv(2*TU, 0.0, ProcessValue.STATE_QUALITY_BAD),
			pv(3*TU, 3.0),
		]
	}

	@Test
	public void testShift() {
		def TU=Expression.TIME_UNIT
		def ts=from([
			pv(0, 0.0),
			pv(3*TU, 3.0),
		], Characteristics.LINEAR)
		def r=ts.shift(1).read(new Date(1*TU), new Date(4*TU)).toList()
		assert r==[
			pv(1*TU, 0.0),
			pv(4*TU, 3.0),
		]
	}

	@Test
	public void testHoldLast() {
		def TU=Expression.TIME_UNIT
		def ts=from([
			pv(0, 0.0),
			pv(3*TU, 3.0),
		], 0)
		def r=ts.holdLast().read(new Date(0*TU), new Date(4*TU)).toList()
		assert r==[
			pv(0, 0.0),
			pv(3*TU, 3.0),
			pv(4*TU, 3.0),
		]

		ts=from([
			pv(0, 0.0),
			pv(3*TU, 3.0),
		], 0)
		r=ts.holdLast().read(new Date(0*TU), new Date(3*TU)).toList()
		assert r==[
			pv(0, 0.0),
			pv(3*TU, 3.0),
		]

		ts=from([], 0)
		r=ts.holdLast().read(new Date(0*TU), new Date(3*TU)).toList()
		assert r==[]
	}
}
