package mdz.ccuhistorian

import static org.junit.Assert.*

import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier

import static mdz.hc.ProcessValue.*
import mdz.hc.ProcessValue
import mdz.hc.persistence.Storage
import mdz.hc.timeseries.TimeSeries
import mdz.hc.timeseries.expr.Reader

import org.junit.Before
import org.junit.Test

class ExpressionParserTest {

	ExpressionParser exprParser

	static pv(ts, v) {
		new ProcessValue(new Date(ts*1000), v as Double, STATE_QUALITY_GOOD)
	}

	@Before
	public void setUp() {
		def storage=[
			getDataPoint: { int idx ->
				if (idx!=1) {
					throw new Exception("invalid data point idx")
				}
				def dp=new DataPoint(idx: idx, id:new DataPointIdentifier('X', 'X', 'X'))
				dp.attributes.type='FLOAT'
				dp.continuous=true
				dp
			},
			getTimeSeries: { DataPoint dp, Date begin, Date end ->
				if (dp.idx!=1) {
					throw new Exception("invalid data point idx")
				}
				def ts=new TimeSeries(dp)
				ts.add pv(1, 1.0)
				ts.add pv(2, 2.0)
				ts.add pv(4, 0.0)
				ts.add pv(8, 3.0)
				ts
			}
		] as Storage
		exprParser=[storage]
	}

	@Test
	public void testIntegration() {
		Reader rdr=exprParser.parse('dataPoint(1)')
		def ts=rdr.read(new Date(0), new Date(10000))
		assert ts.collect() == [
			pv(1, 1.0),
			pv(2, 2.0),
			pv(4, 0.0),
			pv(8, 3.0)
		]
	}

	@Test
	public void testCategory() {
		Reader rdr=exprParser.parse('1+dataPoint(1)')
		def ts=rdr.read(new Date(1000), new Date(8000))
		assert ts.collect() == [
			pv(1, 2.0),
			pv(2, 3.0),
			pv(4, 1.0),
			pv(8, 4.0)
		]
	}
}
