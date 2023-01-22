/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2022 MDZ (info@ccu-historian.de)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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
	 * Reads a time series by time range. Boundary values on the start and end
	 * timestamps should also be returned, if possible. The iterator must not return
	 * null elements.
	 */
	Iterator<ProcessValue> read(Date begin, Date end);
}
