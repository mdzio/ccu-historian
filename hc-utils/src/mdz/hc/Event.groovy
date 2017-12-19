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

import java.util.Date
import java.util.Map

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor
import groovy.transform.CompileStatic

@EqualsAndHashCode
@TupleConstructor
@CompileStatic
class Event {

	DataPoint dataPoint
	ProcessValue pv
	
	@Override
	public String toString() {
		List<String> list=[]
		list << dataPoint.id.toString()
		list << pv.timestamp.toString()
		list << pv.value.toString()
		list << pv.state.toString()
		list.join(', ')
	}
}
