package mdz.hc.timeseries.expr;

import java.util.Iterator;
import java.util.function.BiFunction;

import mdz.hc.ProcessValue;

class ScanIterator<T> implements Iterator<ProcessValue> {
	private final Iterator<ProcessValue> srcIter;
	private final BiFunction<T, ProcessValue, ProcessValue> function;
	private final T state;

	ScanIterator(Iterator<ProcessValue> srcIter, BiFunction<T, ProcessValue, ProcessValue> function, T state) {
		this.srcIter = srcIter;
		this.function = function;
		this.state = state;
	}

	@Override
	public boolean hasNext() {
		return srcIter.hasNext();
	}

	@Override
	public ProcessValue next() {
		return function.apply(state, srcIter.next());
	}
}