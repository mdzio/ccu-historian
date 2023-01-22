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

import mdz.hc.ProcessValue;

public class PeekIterator implements Iterator<ProcessValue> {

	private Iterator<ProcessValue> source;
	private boolean initialized;
	private ProcessValue previous, next;

	public PeekIterator(Iterator<ProcessValue> source) {
		this.source = source;
	}

	@Override
	public boolean hasNext() {
		init();
		return next != null;
	}

	@Override
	public ProcessValue next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		ProcessValue pv = next;
		advance();
		return pv;
	}

	public ProcessValue peekNext() {
		init();
		return next;
	}

	public boolean hasPrevious() {
		init();
		return previous != null;
	}

	public ProcessValue peekPrevious() {
		init();
		return previous;
	}

	private void init() {
		if (!initialized) {
			advance();
			initialized = true;
		}
	}

	private void advance() {
		previous = next;
		if (source.hasNext()) {
			next = source.next();
		} else {
			next = null;
		}
	}
}
