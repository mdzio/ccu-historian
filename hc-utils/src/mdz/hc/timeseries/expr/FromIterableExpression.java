package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

class FromIterableExpression extends Expression {
	private final Iterable<ProcessValue> iterable;
	private final int characteristics;

	public FromIterableExpression(Iterable<ProcessValue> iterable, int characteristics) {
		this.iterable = iterable;
		this.characteristics = characteristics;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		return iterable.iterator();
	}

	@Override
	public int getCharacteristics() {
		return characteristics;
	}
}