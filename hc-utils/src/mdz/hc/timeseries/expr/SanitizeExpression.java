package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import mdz.hc.ProcessValue;

class SanitizeExpression extends Expression {
	private Expression expression;

	public SanitizeExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		Iterator<ProcessValue> srcIter = expression.read(begin, end);
		return new Iterator<ProcessValue>() {
			private ProcessValue next, previous;

			@Override
			public boolean hasNext() {
				if (next == null) {
					while (srcIter.hasNext()) {
						ProcessValue v = srcIter.next();
						if (previous != null) {
							if (!v.getTimestamp().after(previous.getTimestamp())) {
								continue;
							}
						} else {
							if (v.getTimestamp().before(begin)) {
								continue;
							}
						}
						if (!v.getTimestamp().before(end)) {
							continue;
						}
						previous = v;
						next = v;
						return true;
					}
					return false;
				} else {
					return true;
				}
			}

			@Override
			public ProcessValue next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				ProcessValue v = next;
				next = null;
				return v;
			}
		};
	}

	@Override
	public int getCharacteristics() {
		return expression.getCharacteristics();
	}
}