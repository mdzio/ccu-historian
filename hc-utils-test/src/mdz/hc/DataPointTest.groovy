/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2017 MDZ (info@ccu-historian.de)

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
package mdz.hc

import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier

import org.junit.Test

class DataPointTest {

	@Test
	public void testDataPoint() {
		DataPoint dp=new DataPoint(
			idx:123, 
			historyTableName: 'myTableName', 
			managementFlags: DataPoint.FLAGS_HISTORY_DISABLED,
			id:new DataPointIdentifier('myIntf', 'myAddr', 'myIdent'), 
			attributes: [
				displayName: 'myDisplayName',
				comment: 'myComment', 
				paramSet: 'myParamSet', 
				tabOrder: 345, 
				maximum: 1.0, 
				minimum: 0.0,
				unit: 'myUnit', 
				control: 'myControl', 
				operations: 456, 
				flags: 567, 
				type: 'myType', 
				defaultValue: 0.5
			])
		
		assert dp.idx==123
		assert dp.id==new DataPointIdentifier('myIntf', 'myAddr', 'myIdent')
		assert !dp.historyHidden
		assert dp.historyDisabled
	}
}
