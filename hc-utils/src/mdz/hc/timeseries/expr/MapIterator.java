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
import java.util.function.UnaryOperator;

import mdz.hc.ProcessValue;

class MapIterator implements Iterator<ProcessValue> {
	private final Iterator<ProcessValue> srcIter;
	private final UnaryOperator<ProcessValue> operator;

	public MapIterator(Iterator<ProcessValue> srcIter, UnaryOperator<ProcessValue> operator) {
		this.srcIter = srcIter;
		this.operator = operator;
	}

	@Override
	public boolean hasNext() {
		return srcIter.hasNext();
	}

	@Override
	public ProcessValue next() {
		return operator.apply(srcIter.next());
	}
}