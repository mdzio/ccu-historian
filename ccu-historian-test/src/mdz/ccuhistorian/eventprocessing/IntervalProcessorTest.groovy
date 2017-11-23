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
package mdz.ccuhistorian.eventprocessing

import groovy.util.GroovyTestCase
import mdz.eventprocessing.Collector
import mdz.hc.Event
import mdz.hc.ProcessValue

class IntervalProcessorTest extends GroovyTestCase {

	Event createEvent(long ts, value) {
		new Event(pv:new ProcessValue(
			new Date(ts), value, ProcessValue.STATE_QUALITY_GOOD
		))
	}
	
	public void test() {
		Collector c = []
		IntervalProcessor ip = [
			intervalLength: 10,
			function: { long begin, long end, List<Event> events -> 
				Event e = events.max { it.pv.value } 
				new Event(
					dataPoint: e.dataPoint,
					pv: new ProcessValue(new Date(begin), e.pv.value, e.pv.state)
				)
			}
		]
		ip.addConsumer c
		
		ip.consume createEvent(0, 0.0)
		List<Event> r = c.get()
		assert r.size() == 0

		ip.consume createEvent(1, 0.0)
		r = c.get()
		assert r.size() == 0

		ip.consume createEvent(10, 1.0)
		r = c.get()
		assert r.size() == 1
		assert r[0].pv.timestamp.time == 0
		assert r[0].pv.value == 0.0
		assert r[0].pv.state == ProcessValue.STATE_QUALITY_GOOD

		ip.consume createEvent(20, 2.0)
		r = c.get()
		assert r.size() == 1
		assert r[0].pv.timestamp.time == 10
		assert r[0].pv.value == 1.0
		assert r[0].pv.state == ProcessValue.STATE_QUALITY_GOOD

		ip.consume createEvent(21, 3.0)
		ip.consume createEvent(22, 4.0)
		ip.consume createEvent(23, 1.0)
		r = c.get()
		assert r.size() == 0
		
		ip.consume createEvent(30, -1.0)
		r = c.get()
		assert r.size() == 1
		assert r[0].pv.timestamp.time == 20
		assert r[0].pv.value == 4.0
		assert r[0].pv.state == ProcessValue.STATE_QUALITY_GOOD
		
		ip.consume createEvent(139, 2.0)
		r = c.get()
		assert r.size() == 10
		assert r.every { it.pv.value == -1.0 }
		
		ip.consume createEvent(150, -2.0)
		r = c.get()
		assert r.size() == 2
		assert r.every { it.pv.value == 2.0 }
	}
}
