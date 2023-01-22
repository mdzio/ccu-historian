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
import java.util.function.Function;

import mdz.hc.ProcessValue;

class AggregateExpression extends Expression {

	private Expression source;
	private Expression intervals;
	private Function<Interval, ProcessValue> function;

	public AggregateExpression(Expression source, Expression intervals, Function<Interval, ProcessValue> function) {
		this.source = source.linear();
		this.intervals = intervals;
		this.function = function;
	}

	@Override
	public int getCharacteristics() {
		return (source.getCharacteristics() | Characteristics.HOLD) & ~Characteristics.LINEAR;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		PeekIterator sourceIter = new PeekIterator(source.read(begin, end));
		Iterator<ProcessValue> intervalIter = intervals.read(begin, end);

		return new Iterator<ProcessValue>() {
			ProcessValue next;
			Date end;

			@Override
			public boolean hasNext() {
				prepare();
				return next != null;
			}

			@Override
			public ProcessValue next() {
				prepare();
				if (next == null) {
					throw new NoSuchElementException();
				}
				ProcessValue pv = next;
				next = null;
				return pv;
			}

			void prepare() {
				// next value already prepared?
				if (next != null) {
					return;
				}
				// first interval?
				if (end == null) {
					if (!intervalIter.hasNext()) {
						return;
					}
					end = intervalIter.next().getTimestamp();
				}
				// load next interval
				if (!intervalIter.hasNext()) {
					return;
				}
				Date begin = end;
				end = intervalIter.next().getTimestamp();
				// aggregate interval
				Interval interval = new Interval(begin, end, new CutIterator(begin, end, sourceIter));
				next = function.apply(interval);
			}
		};
	}
}
