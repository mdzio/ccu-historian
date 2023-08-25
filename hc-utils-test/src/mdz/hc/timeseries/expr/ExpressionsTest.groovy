package mdz.hc.timeseries.expr

import static org.junit.Assert.*

import mdz.hc.ProcessValue

import static mdz.hc.timeseries.expr.ExpressionTest.*
import static mdz.hc.timeseries.expr.Expression.*
import static mdz.hc.timeseries.expr.Expressions.*
import org.junit.Test

class ExpressionsTest {

	static date(String str) {
		Date.parse('yyyy-MM-dd HH:mm', str)
	}

	static pv(ts) {
		new ProcessValue(date(ts), 1.0D, ProcessValue.STATE_QUALITY_GOOD)
	}

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

	@Test
	public void testHourly() {
		def expr=Expressions.hourly()
		def b=date('2023-01-01 12:01')
		def e=date('2023-01-01 15:59')
		def res=expr.read(b, e).toList()
		assert res==[
			pv('2023-01-01 13:00'),
			pv('2023-01-01 14:00'),
			pv('2023-01-01 15:00')
		]
	}

	@Test
	public void testHourlyOnBoundaries() {
		def expr=Expressions.hourly()
		def b=date('2023-01-01 13:00')
		def e=date('2023-01-01 15:00')
		def res=expr.read(b, e).toList()
		assert res==[
			pv('2023-01-01 13:00'),
			pv('2023-01-01 14:00'),
			pv('2023-01-01 15:00')
		]
	}

	@Test
	public void testCron() {
		def expr=Expressions.cron('0 10 */2 * * ?')
		def b=date('2023-01-01 12:01')
		def e=date('2023-01-01 15:59')
		def res=expr.read(b, e).toList()
		assert res==[
			pv('2023-01-01 12:10'),
			pv('2023-01-01 14:10'),
		]
	}
}
