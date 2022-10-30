package mdz.hc.timeseries.expr;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

class FromConstantExpression extends Expression {
	private final double value;

	public FromConstantExpression(double value) {
		this.value = value;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		ArrayList<ProcessValue> timeSeries = new ArrayList<ProcessValue>(2);
		long duration = end.getTime() - begin.getTime();
		if (duration > 0) {
			timeSeries.add(new ProcessValue(begin, value, ProcessValue.STATE_QUALITY_GOOD));
		}
		if (duration > 1) {
			timeSeries.add(new ProcessValue(new Date(end.getTime() - 1), value, ProcessValue.STATE_QUALITY_GOOD));
		}
		return timeSeries.iterator();
	}

	@Override
	public int getCharacteristics() {
		return Characteristics.HOLD;
	}
}