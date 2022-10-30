package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;
import java.util.function.UnaryOperator;

import mdz.hc.ProcessValue;

class LiftExpression extends Expression {
	private final UnaryOperator<Iterator<ProcessValue>> operator;
	private Expression source;

	public LiftExpression(Expression source, UnaryOperator<Iterator<ProcessValue>> operator) {
		this.source = source;
		this.operator = operator;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		Iterator<ProcessValue> srcIter = source.read(begin, end);
		return operator.apply(srcIter);
	}

	@Override
	public int getCharacteristics() {
		return source.getCharacteristics();
	}
}