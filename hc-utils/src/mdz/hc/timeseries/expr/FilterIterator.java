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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import mdz.hc.ProcessValue;

class FilterIterator implements Iterator<ProcessValue> {
	private final Predicate<ProcessValue> predicate;
	private final Iterator<ProcessValue> srcIter;
	private ProcessValue next;

	public FilterIterator(Predicate<ProcessValue> predicate, Iterator<ProcessValue> srcIter) {
		this.predicate = predicate;
		this.srcIter = srcIter;
	}

	@Override
	public boolean hasNext() {
		if (next == null) {
			while (srcIter.hasNext()) {
				ProcessValue v = srcIter.next();
				if (predicate.test(v)) {
					next = v;
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public ProcessValue next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		ProcessValue v = next;
		next = null;
		return v;
	}
}