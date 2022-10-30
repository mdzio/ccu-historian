package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

class CharacteristicsExpression extends Expression {
	private final int resetMask;
	private final int setMask;
	private Expression source;

	CharacteristicsExpression(Expression source, int resetMask, int setMask) {
		this.source = source;
		this.resetMask = resetMask;
		this.setMask = setMask;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		return source.read(begin, end);
	}

	@Override
	public int getCharacteristics() {
		return (source.getCharacteristics() | setMask) & ~resetMask;
	}
}