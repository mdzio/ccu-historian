package mdz.hc.timeseries.expr

import static org.junit.Assert.*
import static mdz.hc.timeseries.expr.ExpressionTest.*
import static mdz.hc.timeseries.expr.Expressions.*
import org.junit.Test

class ExpressionCategoryTest {

	@Test
	public void testCategory() {
		def ce=from(5.0)
		use(ExpressionCategory) {
			Expression re=2+ce
			def r=re.read(new Date(1000), new Date(2000)).toList()
			assert r==[
				pv(1000, 7.0),
				pv(2000, 7.0),
			]
		}
	}
}
