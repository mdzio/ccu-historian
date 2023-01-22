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
import java.util.NoSuchElementException;

import mdz.hc.ProcessValue;

/**
 * The CutIterator cuts out a section of a time series and presents it with its
 * own iterator. The boundary values are interpolated if the source iterator
 * provides entries before or after the time range. The source iterator is
 * consumed while the entries are within the time range (<= end).
 */
public class CutIterator implements Iterator<ProcessValue> {

	private final Date begin;
	private final Date end;
	private final PeekIterator source;
	private boolean done;
	private ProcessValue next1, next2; // small queue

	public CutIterator(Date begin, Date end, PeekIterator source) {
		this.begin = begin;
		this.end = end;
		this.source = source;
	}

	@Override
	public boolean hasNext() {
		prepare();
		return next1 != null;
	}

	@Override
	public ProcessValue next() {
		prepare();
		if (next1 == null) {
			throw new NoSuchElementException();
		}
		ProcessValue pv = next1;
		next1 = next2;
		next2 = null;
		return pv;
	}

	private void prepare() {
		// next value already prepared?
		if (next1 != null) {
			return;
		}
		// already done?
		if (done) {
			return;
		}
		// discard entries before begin
		while (source.hasNext() && source.peekNext().getTimestamp().before(begin)) {
			source.next();
		}
		// at end?
		if (!source.hasNext()) {
			done = true;
			return;
		}
		// next timestamp exactly on begin?
		if (source.peekNext().getTimestamp().equals(begin)) {
			push(source.next());
		} else {
			// next timestamp is after begin!
			// previous entry before interval begin present?
			if (source.hasPrevious() && source.peekPrevious().getTimestamp().before(begin)) {
				// interpolate at begin
				push(Expression.interpolate(source.peekPrevious(), source.peekNext(), begin));
			}
			// next timestamp within interval?
			if (source.peekNext().getTimestamp().before(end)) {
				push(source.next());
			} else {
				// next timestamp is on or after end!
				done = true;
				// next timestamp exactly on end?
				if (source.peekNext().getTimestamp().equals(end)) {
					push(source.peekNext());
				} else {
					// next timestamp is after end!
					// previous entry present?
					if (source.hasPrevious()) {
						// interpolate at end
						push(Expression.interpolate(source.peekPrevious(), source.peekNext(), end));
					}
				}
			}
		}
	}

	private void push(ProcessValue pv) {
		// two element queue
		if (next1 == null) {
			next1 = pv;
		} else {
			next2 = pv;
		}
	}
}
