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
package mdz.ccuhistorian

import mdz.ccuhistorian.eventprocessing.Preprocessor
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.ProcessValue
import mdz.hc.persistence.Storage
import mdz.hc.timeseries.expr.Expression
import mdz.hc.timeseries.expr.Expressions
import mdz.hc.timeseries.expr.Reader
import mdz.hc.timeseries.expr.Characteristics

public class DatabaseExpressionAdapter {

	Storage storage

	Expression dataPoint(DataPoint dp) {
		if (dp.historyString) {
			throw new Exception("Data point $dp.displayName is not numeric")
		}

		// determine characteristics
		def action=(dp.attributes.type=='ACTION') || (dp.id.identifier=='PRESS_SHORT') || (dp.id.identifier=='PRESS_LONG')
		def continuous=dp.continuous
		def preprocType=Preprocessor.Type.ofDataPoint(dp)
		int characteristics
		if (action) {
			characteristics=Characteristics.EVENT
		} else if (continuous && !preprocType.clearsContinuous()) {
			characteristics=Characteristics.LINEAR
		} else {
			characteristics=Characteristics.HOLD
		}

		// wrap database.getTimeSeries
		Expressions.from(new Reader() {
			public int getCharacteristics() {
				characteristics
			}
			public Iterator<ProcessValue> read(Date begin, Date end) {
				storage.getTimeSeries(dp, begin, end).iterator()
			}
		})
	}

	Expression dataPoint(int idx) {
		def p=storage.getDataPoint(idx)
		if (p==null) {
			throw new Exception("Data point with index $idx does not exist")
		}
		dataPoint(p)
	}

	Expression dataPoint(String itfAddrIdent) {
		def p=storage.getDataPoint(new DataPointIdentifier(itfAddrIdent))
		if (p==null) {
			throw new Exception("Data point $itfAddrIdent does not exist")
		}
		dataPoint(p)
	}
}
