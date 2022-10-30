package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BinaryOperator;

import mdz.hc.ProcessValue;

class BinaryOperatorExpression extends Expression {
	private final Expression expr1;
	private final Expression expr2;
	private BinaryOperator<ProcessValue> operator;

	BinaryOperatorExpression(Expression expr1, Expression expr2, BinaryOperator<ProcessValue> operator) {
		this.expr1 = expr1;
		this.expr2 = expr2;
		this.operator = operator;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		PeekIterator[] it = { new PeekIterator(expr1.read(begin, end)), new PeekIterator(expr2.read(begin, end)) };
		return new Iterator<ProcessValue>() {
			ProcessValue calculatedNext;

			@Override
			public boolean hasNext() {
				// value already present?
				if (calculatedNext != null) {
					return true;
				}

				// additional entries available?
				if (!it[0].hasNext() && !it[1].hasNext()) {
					return false;
				}

				// find iterator with earliest next entry
				ProcessValue[] nextPV = { it[0].peekNext(), it[1].peekNext() };
				int main = 1; // index of main iterator
				if (nextPV[0] != null) {
					if (nextPV[1] != null) {
						if (nextPV[0].getTimestamp().before(nextPV[1].getTimestamp())) {
							main = 0;
						}
					} else {
						main = 0;
					}
				}
				int interp = 1 - main; // index of iterator for interpolation

				// combine timeseries entries
				Date timestamp = it[main].peekNext().getTimestamp();
				if (it[interp].peekNext() != null && it[interp].peekNext().getTimestamp().equals(timestamp)) {
					// equal timestamps
					calculatedNext = operator.apply(nextPV[0], nextPV[1]);
					// advance both iterators
					it[0].next();
					it[1].next();

				} else if (it[interp].peekPrevious() != null && it[interp].peekNext() != null) {
					// interpolate
					ProcessValue interpPV = Expression.interpolate(it[interp].peekPrevious(), it[interp].peekNext(),
							timestamp);
					if (main == 0) {
						calculatedNext = operator.apply(nextPV[0], interpPV);
					} else {
						calculatedNext = operator.apply(interpPV, nextPV[1]);
					}
					it[main].next();

				} else {
					// no interpolation possible
					calculatedNext = new ProcessValue(timestamp, 0.0D, ProcessValue.STATE_QUALITY_BAD);
					it[main].next();
				}
				return true;
			}

			@Override
			public ProcessValue next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				ProcessValue pv = calculatedNext;
				calculatedNext = null;
				return pv;
			}
		};
	}

	@Override
	public int getCharacteristics() {
		return expr1.getCharacteristics();
	}
}