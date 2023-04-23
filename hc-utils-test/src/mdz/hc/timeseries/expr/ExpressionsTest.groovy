package mdz.hc.timeseries.expr

import static org.junit.Assert.*
import static mdz.hc.timeseries.expr.ExpressionTest.*
import static mdz.hc.timeseries.expr.Expression.*
import static mdz.hc.timeseries.expr.Expressions.*
import org.junit.Test

class ExpressionsTest {

	@Test
	public void positiveEdge() {
		def expr=positiveEdge(new Date(100))
		assert expr.read(new Date(0), new Date(200)).toList()==[
			pv(0, 0.0),
			pv(100, 1.0),
			pv(200, 1.0),
		]
		assert expr.read(new Date(1), new Date(0)).toList()==[]
		assert expr.read(new Date(99), new Date(99)).toList()==[pv(99, 0.0),]
		assert expr.read(new Date(100), new Date(100)).toList()==[pv(100, 1.0),]
		assert expr.read(new Date(101), new Date(101)).toList()==[pv(101, 1.0),]
		assert expr.read(new Date(0), new Date(99)).toList()==[
			pv(0, 0.0),
			pv(99, 0.0),
		]
		assert expr.read(new Date(0), new Date(100)).toList()==[
			pv(0, 0.0),
			pv(100, 1.0),
		]
		assert expr.read(new Date(100), new Date(200)).toList()==[
			pv(100, 1.0),
			pv(200, 1.0),
		]
	}
}
