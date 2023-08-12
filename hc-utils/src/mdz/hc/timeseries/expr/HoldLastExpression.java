package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import mdz.hc.ProcessValue;

class HoldLastExpression extends Expression {

	private Expression source;

	public HoldLastExpression(Expression source) {
		this.source = source;
	}

	@Override
	public int getCharacteristics() {
		return source.getCharacteristics();
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		Iterator<ProcessValue> srcIt = source.read(begin, end);
		return new Iterator<ProcessValue>() {
			ProcessValue previous, next;

			@Override
			public ProcessValue next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				ProcessValue pv = next;
				next = null;
				return pv;
			}

			@Override
			public boolean hasNext() {
				if (next != null) {
					return true;
				}
				if (srcIt.hasNext()) {
					next = srcIt.next();
					previous = next;
					return true;
				}
				if (previous != null && previous.getTimestamp().before(end)) {
					next = new ProcessValue(end, previous.getValue(), previous.getState());
					previous = null;
					return true;
				}
				return false;
			}
		};
	}
}
