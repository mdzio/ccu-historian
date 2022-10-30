package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

class FromReaderExpression extends Expression {
	private final Reader reader;

	public FromReaderExpression(Reader reader) {
		this.reader = reader;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		return reader.read(begin, end);
	}

	@Override
	public int getCharacteristics() {
		return reader.getCharacteristics();
	}
}