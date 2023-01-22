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

class SanitizeExpression extends Expression {
	private Expression expression;

	public SanitizeExpression(Expression expression) {
		this.expression = expression;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		Iterator<ProcessValue> srcIter = expression.read(begin, end);
		return new Iterator<ProcessValue>() {
			private ProcessValue next, previous;

			@Override
			public boolean hasNext() {
				if (next == null) {
					while (srcIter.hasNext()) {
						ProcessValue v = srcIter.next();
						if (previous != null) {
							if (!v.getTimestamp().after(previous.getTimestamp())) {
								continue;
							}
						} else {
							if (v.getTimestamp().before(begin)) {
								continue;
							}
						}
						if (!v.getTimestamp().before(end)) {
							continue;
						}
						previous = v;
						next = v;
						return true;
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
		};
	}

	@Override
	public int getCharacteristics() {
		return expression.getCharacteristics();
	}
}