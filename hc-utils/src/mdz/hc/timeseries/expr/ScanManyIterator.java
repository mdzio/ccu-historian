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

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

import mdz.hc.ProcessValue;

class ScanManyIterator<T> implements Iterator<ProcessValue> {
	private final Iterator<ProcessValue> srcIter;
	private final T state;
	private final BiFunction<T, ProcessValue, Iterator<ProcessValue>> onEntry;
	private Function<T, Iterator<ProcessValue>> onCompleted;
	private Iterator<ProcessValue> resultIter;

	ScanManyIterator(Iterator<ProcessValue> srcIter, T state,
			BiFunction<T, ProcessValue, Iterator<ProcessValue>> onEntry,
			Function<T, Iterator<ProcessValue>> onCompleted) {
		this.srcIter = srcIter;
		this.state = state;
		this.onEntry = onEntry;
		this.onCompleted = onCompleted;
		resultIter = Collections.emptyIterator();
	}

	@Override
	public boolean hasNext() {
		// current iterator depleted?
		while (!resultIter.hasNext()) {
			// more iterators available?
			if (srcIter.hasNext()) {
				// get next iterator
				resultIter = onEntry.apply(state, srcIter.next());
			} else {
				// onCompleted available?
				if (onCompleted != null) {
					// get last iterator
					resultIter = onCompleted.apply(state);
					onCompleted = null;
				} else {
					// no more iterators
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ProcessValue next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return resultIter.next();
	}
}