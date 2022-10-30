package mdz.hc.timeseries.expr;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import mdz.hc.ProcessValue;

class FilterIterator implements Iterator<ProcessValue> {
	private final Predicate<ProcessValue> predicate;
	private final Iterator<ProcessValue> srcIter;
	private ProcessValue next;

	public FilterIterator(Predicate<ProcessValue> predicate, Iterator<ProcessValue> srcIter) {
		this.predicate = predicate;
		this.srcIter = srcIter;
	}

	@Override
	public boolean hasNext() {
		if (next == null) {
			while (srcIter.hasNext()) {
				ProcessValue v = srcIter.next();
				if (predicate.test(v)) {
					next = v;
					return true;
				}
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
}