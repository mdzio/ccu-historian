package mdz.hc.timeseries.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

public class Expressions {

	/**
	 * Wraps a Reader into an Expression.
	 */
	public static Expression from(Reader reader) {
		return new FromReaderExpression(reader);
	}

	/**
	 * Wraps a constant value into an Expression. The time series has the
	 * characteristic HOLD.
	 */
	public static Expression from(Number value) {
		return new FromConstantExpression(value);
	}

	/**
	 * Wraps an Iterable into an Expression. Begin and end of reads are ignored. Use
	 * sanitize() to remove out of range timestamps.
	 */
	public static Expression from(Iterable<ProcessValue> iterable, int characteristics) {
		return new FromIterableExpression(iterable, characteristics);
	}

	public static Expression step(Number firstValue, Number secondValue, Date stepTimestamp) {
		return new Expression() {
			@Override
			public int getCharacteristics() {
				return Characteristics.HOLD;
			}

			@Override
			public Iterator<ProcessValue> read(Date begin, Date end) {
				if (end.before(begin)) {
					return Collections.emptyIterator();
				}
				ArrayList<ProcessValue> timeSeries = new ArrayList<ProcessValue>(3);
				if (!stepTimestamp.after(begin)) {
					timeSeries.add(new ProcessValue(begin, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					if (end.after(begin)) {
						timeSeries.add(new ProcessValue(end, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					}
				} else if (!stepTimestamp.after(end)) {
					timeSeries.add(new ProcessValue(begin, firstValue, ProcessValue.STATE_QUALITY_GOOD));
					timeSeries.add(new ProcessValue(stepTimestamp, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					if (stepTimestamp.before(end)) {
						timeSeries.add(new ProcessValue(end, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					}
				} else {
					timeSeries.add(new ProcessValue(begin, firstValue, ProcessValue.STATE_QUALITY_GOOD));
					if (end.after(begin)) {
						timeSeries.add(new ProcessValue(end, firstValue, ProcessValue.STATE_QUALITY_GOOD));
					}
				}
				return timeSeries.iterator();
			}
		};
	}

	public static Expression positiveEdge(Date edgeTimestamp) {
		return step(0.0, 1.0, edgeTimestamp);
	}

	public static Expression negativeEdge(Date edgeTimestamp) {
		return step(1.0, 0.0, edgeTimestamp);
	}
}
