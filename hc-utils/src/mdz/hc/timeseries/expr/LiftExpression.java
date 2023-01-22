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
import java.util.function.UnaryOperator;

import mdz.hc.ProcessValue;

class LiftExpression extends Expression {
	private final UnaryOperator<Iterator<ProcessValue>> operator;
	private Expression source;

	public LiftExpression(Expression source, UnaryOperator<Iterator<ProcessValue>> operator) {
		this.source = source;
		this.operator = operator;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		Iterator<ProcessValue> srcIter = source.read(begin, end);
		return operator.apply(srcIter);
	}

	@Override
	public int getCharacteristics() {
		return source.getCharacteristics();
	}
}