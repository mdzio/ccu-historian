package mdz.hc.timeseries.expr

import static mdz.hc.ProcessValue.*
import static mdz.hc.timeseries.expr.Expressions.*

import java.util.function.BinaryOperator

import org.junit.Test

import mdz.hc.ProcessValue

class BinaryOperatorExpressionTest {

	private pv(ts, v) {
		new ProcessValue(new Date(ts), v as Double, STATE_QUALITY_GOOD)
	}

	private pv(ts, v, st) {
		new ProcessValue(new Date(ts), v as Double, st)
	}

	BinaryOperator<ProcessValue> adder = { ProcessValue pv1, ProcessValue pv2 ->
		new ProcessValue(pv1.timestamp, pv1.value+pv2.value, AggregateFunctions.combineStates(pv1, pv2))
	}

	@Test
	public void testBothEmpty() {
		def expr1=from([], 0)
		def expr2=from([], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList().empty
	}

	@Test
	public void testFirstEmpty() {
		def expr1=from([], 0)
		def expr2=from([
			pv(1, 5),
			pv(2, 7),
		], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(1, 0, STATE_QUALITY_BAD),
			pv(2, 0, STATE_QUALITY_BAD),
		]
	}

	@Test
	public void testSecondEmpty() {
		def expr1=from([
			pv(5, 5),
			pv(7, 7),
		], 0)
		def expr2=from([], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(5, 0, STATE_QUALITY_BAD),
			pv(7, 0, STATE_QUALITY_BAD),
		]
	}

	@Test
	public void testEqualTimestamps() {
		def expr1=from([
			pv(2, 2),
			pv(7, 7),
		], 0)
		def expr2=from([
			pv(2, 3),
			pv(7, 1),
		], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(2, 5),
			pv(7, 8),
		]
	}

	@Test
	public void testEqualTimestampsWithOverlap() {
		def expr1=from([
			pv(2, 2),
			pv(7, 7),
		], 0)
		def expr2=from([
			pv(7, 1),
			pv(8, 3),
		], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(2, 0, STATE_QUALITY_BAD),
			pv(7, 8),
			pv(8, 0, STATE_QUALITY_BAD),
		]
	}

	@Test
	public void testInterpolation() {
		def expr1=from([
			pv(0, 0),
			pv(2, 2),
			pv(3, 3),
			pv(4, 4),
		], 0)
		def expr2=from([
			pv(0, 1),
			pv(1, 3),
			pv(2, 5),
			pv(4, 7),
		], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(0, 1),
			pv(1, 4),
			pv(2, 7),
			pv(3, 9),
			pv(4, 11),
		]
	}

	@Test
	public void testMixed() {
		def expr1=from([
			pv(0, 0),
			pv(2, 2),
			pv(4, 6),
		], 0)
		def expr2=from([
			pv(1, 3),
			pv(3, 5),
			pv(4, 6),
			pv(5, 1),
		], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(0, 0, STATE_QUALITY_BAD),
			pv(1, 4),
			pv(2, 6),
			pv(3, 9),
			pv(4, 12),
			pv(5, 0, STATE_QUALITY_BAD),
		]
	}

	@Test
	public void testState() {
		def expr1=from([
			pv(0, 0, STATE_QUALITY_QUESTIONABLE),
			pv(2, 2),
			pv(4, 4),
		], 0)
		def expr2=from([
			pv(0, 1),
			pv(1, 5),
			pv(2, 6),
			pv(3, 1),
			pv(4, 2, STATE_QUALITY_QUESTIONABLE),
		], 0)
		def expr=new BinaryOperatorExpression(expr1, expr2, adder)
		def ts=expr.read(new Date(0), new Date(10))
		assert ts.toList()==[
			pv(0, 1, STATE_QUALITY_QUESTIONABLE),
			pv(1, 6, STATE_QUALITY_QUESTIONABLE),
			pv(2, 8),
			pv(3, 4),
			pv(4, 6, STATE_QUALITY_QUESTIONABLE),
		]
	}
}
