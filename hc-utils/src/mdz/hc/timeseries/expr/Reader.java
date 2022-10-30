package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

public interface Reader {

	/**
	 * Return the characteristics of this Reader's time series.
	 */
	int getCharacteristics();

	/**
	 * Returns true if this Reader's getCharacteristics() contain all of the given
	 * characteristics.
	 */
	default boolean hasCharacteristics(int characteristics) {
		return (getCharacteristics() & characteristics) == characteristics;
	}

	/**
	 * Reads a time series by time range. The iterator does not return null
	 * elements.
	 */
	Iterator<ProcessValue> read(Date begin, Date end);
}
