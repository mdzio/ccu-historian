package mdz.hc.timeseries.expr;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import mdz.hc.ProcessValue;

class ScanManyIterator<T> implements Iterator<ProcessValue> {
	private final Iterator<ProcessValue> srcIter;
	private final T state;
	private final BiFunction<T, ProcessValue, Iterator<ProcessValue>> function;
	private Iterator<ProcessValue> resultIter;

	ScanManyIterator(Iterator<ProcessValue> srcIter, T state,
			BiFunction<T, ProcessValue, Iterator<ProcessValue>> function) {
		this.srcIter = srcIter;
		this.state = state;
		this.function = function;
	}

	@Override
	public boolean hasNext() {
		// switch iterator
		while (resultIter == null && srcIter.hasNext()) {
			resultIter = function.apply(state, srcIter.next());
			// iterator at end?
			if (!resultIter.hasNext()) {
				resultIter = null;
			}
		}
		return resultIter != null && resultIter.hasNext();
	}

	@Override
	public ProcessValue next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		ProcessValue v = resultIter.next();
		// iterator at end?
		if (!resultIter.hasNext()) {
			resultIter = null;
		}
		return v;
	}
}