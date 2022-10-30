package mdz.hc.timeseries.expr;

import java.util.Iterator;
import java.util.NoSuchElementException;

import mdz.hc.ProcessValue;

public class PeekIterator implements Iterator<ProcessValue> {

	private Iterator<ProcessValue> source;
	private boolean once = true;
	private ProcessValue previous, next;

	public PeekIterator(Iterator<ProcessValue> source) {
		this.source = source;
	}

	@Override
	public boolean hasNext() {
		init();
		return next != null;
	}

	@Override
	public ProcessValue next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		ProcessValue pv = next;
		advance();
		return pv;
	}

	public ProcessValue peekNext() {
		init();
		return next;
	}

	public ProcessValue peekPrevious() {
		init();
		return previous;
	}

	private void init() {
		if (once) {
			advance();
			once = false;
		}
	}

	private void advance() {
		previous = next;
		if (source.hasNext()) {
			next = source.next();
		} else {
			next = null;
		}
	}
}
