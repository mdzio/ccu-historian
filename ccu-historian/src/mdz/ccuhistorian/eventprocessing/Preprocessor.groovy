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
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.ccuhistorian.Main
import mdz.Exceptions
import mdz.Utilities
import java.util.logging.Level

@CompileStatic
@Log
public class Preprocessor extends BasicProducer<Event> implements Processor<Event, Event> {

	public enum Type { DISABLED, DELTA_COMPR, TEMPORAL_COMPR, AVG_COMPR, MIN_COMPR, MAX_COMPR }
	
	public void consume(Event event) throws Exception {
		try {
			int typeIndex=(event.dataPoint.attributes.preprocType as Integer)?:Type.DISABLED.ordinal()
			if (typeIndex!=Type.DISABLED.ordinal()) {
				if (typeIndex<0 || typeIndex>=Type.values().length) {
					log.warning "Preprocessor: Invalid preprocessing type $typeIndex (data point: $event.dataPoint.id)"
				} else {
					Type type=Type.values()[typeIndex]
					double param=(event.dataPoint.attributes.preprocParam as Double)?:0.0D
					switch (type) {
						case Type.DELTA_COMPR: applyDelta(event, param); break
						case Type.TEMPORAL_COMPR: applyTemporal(event, param); break
						case Type.AVG_COMPR:
						case Type.MIN_COMPR:
						case Type.MAX_COMPR: applyIntervalProcessor(event, type, param); break
					}
				}
			} else {
				// forward unmodified
				produce event
			}
		} catch (Throwable t) {
			log.severe 'Preprocessor: Error'
			Exceptions.logTo(log, Level.SEVERE, t)
			Main.restart()
		}
	}
	
	public void stop() {
		log.fine 'Stopping preprocessor'
	}

	private Map<DataPointIdentifier, ProcessValue> deltaPreviousValues=[:]

	private void applyDelta(Event event, double param) {
		log.finer "Preprocessor: Applying delta compression to $event" 
		ProcessValue previousValue=deltaPreviousValues[event.dataPoint.id]
		event=applyDeltaHelper(event, previousValue, param)
		if (event!=null) {
			event.pv.state |= ProcessValue.STATE_PREPROCESSED
			deltaPreviousValues[event.dataPoint.id]=event.pv
			produce event
		}
	}
	
	private Event applyDeltaHelper(Event event, ProcessValue previousValue, double param) {
		if (previousValue==null) return event
		if (!event.pv.value.class.is(previousValue.value.class)) {
			log.finer "Preprocessor: Data type of data point $event.dataPoint.id changed"
			return event
		}
		if ((event.pv.state & ProcessValue.STATE_QUALITY_MASK) != (previousValue.state & ProcessValue.STATE_QUALITY_MASK)) {
			log.finer "Preprocessor: State of data point $event.dataPoint.id changed"
			return event
		}
		if (event.pv.value instanceof Number) {
			double last=((Number)previousValue.value).doubleValue()
			double current=((Number)event.pv.value).doubleValue()
			if (Math.abs(current-last)>=param) event 
			else {
				log.fine "Preprocessor: Value change is below delta, event discarded (event: $event)"  
				null
			}
		} else if (event.pv.value instanceof Boolean) {
			boolean last=((Boolean)previousValue.value).booleanValue()
			boolean current=((Boolean)event.pv.value).booleanValue()
			if (current!=last) event
			else {
				log.fine "Preprocessor: Value not changed, event discarded (event: $event)"  
				null
			}
		} else if (event.pv.value instanceof String) {
			String last=(String)previousValue.value
			String current=(String)event.pv.value
			if (current!=last) event
			else {
				log.fine "Preprocessor: Value not changed, event discarded (event: $event)"  
				null
			}
		} else {
			log.warning "Preprocessor: Invalid data type ${event.pv.value.class.name} for delta preprocessing (data point: $event.dataPoint.id)" 
			event
		}
	}
	
	private Map<DataPointIdentifier, Date> temporalTimestamps=[:]
	
	private void applyTemporal(Event event, double param) {
		log.finer "Preprocessor: Applying temporal compression to $event"
		Date lastTimestamp=temporalTimestamps[event.dataPoint.id]
		
		if (lastTimestamp==null || (event.pv.timestamp.time-lastTimestamp.time)>=(param*1000)) {
			event.pv.state |= ProcessValue.STATE_PREPROCESSED
			temporalTimestamps[event.dataPoint.id]=event.pv.timestamp
			produce event
		} else {
			log.fine "Preprocessor: Time not elapsed, event discarded (event: $event)"
			null
		}
	}

	private static class AvgHelper {
		long end
		double sum
	}
	
	private Map<Type, Closure> intervalFunctions=[
		(Type.AVG_COMPR): { long begin, long end, List<Event> events -> 
			AvgHelper res=events.reverse().inject(new AvgHelper(sum:0.0d, end:end)) { AvgHelper r, Event e ->
				double value
				switch (e.pv.value) {
					case Number: value=((Number)e.pv.value).doubleValue(); break
					case Boolean: value=(Boolean)e.pv.value?1.0:0.0; break
				}
				r.sum += value * (r.end - e.pv.timestamp.time)
				r.end = e.pv.timestamp.time
				r
			}
			double avg=res.sum / (end-begin)
			new Event(
				dataPoint: events[0].dataPoint,
				pv: new ProcessValue(new Date(begin), avg, events[0].pv.state)
			)
		},
		
		(Type.MIN_COMPR): { long begin, long end, List<Event> events -> 
			Event e = events.min { it.pv.value }
			new Event(
				dataPoint: e.dataPoint,
				pv: new ProcessValue(new Date(begin), e.pv.value, e.pv.state)
			)
		},
		
		(Type.MAX_COMPR): { long begin, long end, List<Event> events -> 
			Event e = events.max { it.pv.value }
			new Event(
				dataPoint: e.dataPoint,
				pv: new ProcessValue(new Date(begin), e.pv.value, e.pv.state)
			)
		}
	]
	
	private Map<DataPointIdentifier, IntervalProcessor> intervalProcessors=[:]
	
	private void applyIntervalProcessor(Event event, Type type, double param) {
		long intervalLength=(long)param
		if (intervalLength<=0) {
			log.warning "Preprocessor: Invalid interval length (data point: $event.dataPoint.id)"
			produce event
			return
		}
		if (!(event.pv.value instanceof Number) && !(event.pv.value instanceof Boolean)) {
			log.warning "Preprocessor: Invalid data type ${event.pv.value.class.name} for interval procesing (data point: $event.dataPoint.id)"
			produce event
			return
		}
		IntervalProcessor ip=intervalProcessors[event.dataPoint.id]
		if (ip==null || ip.intervalLength!=intervalLength) {
			log.fine "Preprocessor: Creating interval processor (type: $type, intervalLength: $intervalLength, data point: $event.dataPoint.id)"
			ip=[]
			ip.intervalLength=intervalLength
			ip.function=intervalFunctions[type] as IntervalProcessor.Function
			ip.addConsumer { Event e -> 
				e.pv.state |= ProcessValue.STATE_PREPROCESSED
				produce e 
			}
			intervalProcessors[event.dataPoint.id]=ip
		}
		ip.consume event
	}
}
