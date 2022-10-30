package mdz.hc.timeseries.expr;

import java.util.Iterator;
import java.util.function.UnaryOperator;

import mdz.hc.ProcessValue;

class UnaryOperatorIterator implements Iterator<ProcessValue> {
	private final Iterator<ProcessValue> srcIter;
	private final UnaryOperator<ProcessValue> operator;

	public UnaryOperatorIterator(Iterator<ProcessValue> srcIter, UnaryOperator<ProcessValue> operator) {
		this.srcIter = srcIter;
		this.operator = operator;
	}

	@Override
	public boolean hasNext() {
		return srcIter.hasNext();
	}

	@Override
	public ProcessValue next() {
		return operator.apply(srcIter.next());
	}
}