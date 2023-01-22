package mdz.hc.timeseries.expr

import static org.junit.Assert.*

import mdz.hc.ProcessValue
import org.junit.Test

class IntervalExpressionsTest {

	private date(String str) {
		Date.parse('yyyy-MM-dd HH:mm', str)
	}
	
	private pv(ts) {
		new ProcessValue(date(ts), 1.0D, ProcessValue.STATE_QUALITY_GOOD)
	}
	
	@Test
	public void testHourly() {
		def expr=IntervalExpressions.hourly()
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
		def expr=IntervalExpressions.hourly()
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
		def expr=IntervalExpressions.cron('0 10 */2 * * ?')
		def b=date('2023-01-01 12:01')
		def e=date('2023-01-01 15:59')
		def res=expr.read(b, e).toList()
		assert res==[
			pv('2023-01-01 12:10'),
			pv('2023-01-01 14:10'),
		]
	}
}
