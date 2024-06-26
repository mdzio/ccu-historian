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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

class FromConstantExpression extends Expression {
	private final double value;

	public FromConstantExpression(Number value) {
		this.value = value.doubleValue();
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		ArrayList<ProcessValue> timeSeries = new ArrayList<ProcessValue>(2);
		long duration = end.getTime() - begin.getTime();
		if (duration >= 0) {
			timeSeries.add(new ProcessValue(begin, value, ProcessValue.STATE_QUALITY_GOOD));
		}
		if (duration >= 1) {
			timeSeries.add(new ProcessValue(end, value, ProcessValue.STATE_QUALITY_GOOD));
		}
		return timeSeries.iterator();
	}

	@Override
	public int getCharacteristics() {
		return Characteristics.LINEAR;
	}
}