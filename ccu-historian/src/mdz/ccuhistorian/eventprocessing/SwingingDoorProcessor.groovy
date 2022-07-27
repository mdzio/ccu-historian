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
import groovy.util.logging.Log
import mdz.eventprocessing.BasicProducer
import mdz.eventprocessing.Processor
import mdz.hc.Event
import mdz.hc.ProcessValue

@CompileStatic
@Log
class SwingingDoorProcessor extends BasicProducer<Event> implements Processor<Event, Event> {
	
	double deviation
	
	private static class Slopes {
		double upper, lower	
	}
	
	private Event first, last
	private Slopes slopes
	
	public void consume(Event e) throws Exception {
		if (first==null) {
			// store first
			produce e
			first=e
			return
		}
		if (e.pv.timestamp<=first.pv.timestamp) {
			log.warning "Swinging door compression: Discarding event with earlier timestamp: $e"
			return
		}
		// quality changed?
		if (e.pv.state!=first.pv.state) {
			// store last, if present
			if (last!=null) {
				produce last
			}
			// store first
			produce e
			// restart
			first=e
			last=null
			slopes=null
			return
		}
		// check slopes?
		if (slopes!=null) {
			Slopes slopesTmp=calculateSlopes(first, e)
			// open swinging door
			if (slopesTmp.upper>slopes.upper) {
				slopes.upper=slopesTmp.upper
			}
			if (slopesTmp.lower<slopes.lower) {
				slopes.lower=slopesTmp.lower
			} 
			// overwind?
			if (slopes.upper>slopes.lower) {
				// store last
				produce last
				// restart
				first=last
				last=e
				slopes=calculateSlopes(first, e)
				return
			} 
			// value is within slopes
			last=e
			return
		}
		// second event, calculate initial slopes
		last=e
		slopes=calculateSlopes(first, e)
	}
	
	public void close() {
		// store last, if present
		if (last!=null) {
			produce last
		}
		first=null
		last=null
		slopes=null
	}

	public void flush() {
		// store last, if present
		if (last!=null) {
			produce last
			// restart
			first=last
		}
		last=null
		slopes=null
	}
		
	private Slopes calculateSlopes(Event begin, Event end) {
		double upper=((Number)end.pv.value-((Number)begin.pv.value+deviation))/(end.pv.timestamp.time-begin.pv.timestamp.time)
		double lower=((Number)end.pv.value-((Number)begin.pv.value-deviation))/(end.pv.timestamp.time-begin.pv.timestamp.time)
		new Slopes(upper: upper, lower: lower)
	}
}
