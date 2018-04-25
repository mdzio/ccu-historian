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

import groovy.transform.CompileStatic
import mdz.eventprocessing.BasicProducer
import mdz.eventprocessing.Processor
import mdz.hc.Event
import mdz.hc.ProcessValue

@CompileStatic
class IntervalProcessor extends BasicProducer<Event> implements Processor<Event, Event> {
	
	long intervalLength // milliseconds
	IntervalFunction function 
	
	private List<Event> events = []
	private Long intervalEnd
	
	public void consume(Event e) throws Exception {
		long ets = e.pv.timestamp.time
		// initial event?
		if (intervalEnd == null) {
			// setup interval
			events << e
			intervalEnd = getIntervalEnd(ets)
		} else {
			// time stamp within current interval?
			if (ets < intervalEnd) {
				events << e				
			} else {
				// interval completed
				while (ets >= intervalEnd) {
					// apply function and produce event
					produce function.apply(intervalEnd - intervalLength, intervalEnd, events)
					// new event is not exactly on interval begin?
					Event first
					if (ets > intervalEnd) {
						// copy last event to begin of next interval
						Event last = events[-1]
						first = new Event(
							dataPoint: last.dataPoint,
							pv: new ProcessValue(new Date(intervalEnd), last.pv.value, last.pv.state)
						)
					} else {
						// new event is exactly on interval begin
						first = e
					}
					// setup next interval
					events.clear()
					events << first
					intervalEnd += intervalLength
				}
				events << e
			}
		}
	}
	
	private long getIntervalEnd(long t) {
		((long)t.intdiv(intervalLength) + 1) * intervalLength
	}
}
