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

import groovy.util.logging.Log
import mdz.eventprocessing.BasicProducer
import mdz.eventprocessing.Processor
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.ccuhistorian.Main
import mdz.Exceptions
import java.util.logging.Level

@Log
public class Preprocessor extends BasicProducer<Event> implements Processor<Event, Event> {

	public enum Type { 
		DISABLED(false), 
		DELTA_COMPR(true), 
		TEMPORAL_COMPR(false), 
		AVG_COMPR(true), 
		MIN_COMPR(true), 
		MAX_COMPR(true), 
		SWD_COMPR(false);
		 
		private boolean clrCont;
		Type(boolean clearsContinuous) {
			clrCont=clearsContinuous	
		}
		public boolean clearsContinuous() {
			clrCont
		}
		public static Type ofDataPoint(DataPoint dataPoint) {
			int typeIndex=(dataPoint.attributes.preprocType as Integer)?:Type.DISABLED.ordinal()
			if (typeIndex<0 || typeIndex>=Type.values().length) {
				log.warning "Preprocessor: Invalid preprocessing type (data point: $dataPoint.id)"
				DISABLED
			} else {
				Type.values()[typeIndex] 
			}
		} 
	}
	
	public void consume(Event event) throws Exception {
		try {
			Type type=Type.ofDataPoint(event.dataPoint)
			if (type!=Type.DISABLED) {
				double param=(event.dataPoint.attributes.preprocParam as Double)?:0.0D
				switch (type) {
					case Type.DELTA_COMPR: applyDelta(event, param); break
					case Type.TEMPORAL_COMPR: applyTemporal(event, param); break
					case Type.AVG_COMPR:
					case Type.MIN_COMPR:
					case Type.MAX_COMPR: applyIntervalProcessor(event, type, param); break
					case Type.SWD_COMPR: applySwingingDoor(event, type, param); break
				}
			} else {
				// forward unmodified
				produce event
			}
		} catch (Throwable t) {
			log.severe 'Preprocessor: Error'
			Exceptions.logTo(log, Level.SEVERE, t)
		}
	}
	
	public void flush(DataPointIdentifier id) {
		// flush swinging door processor
		def p=swingingDoorProcessors[id]
		if (p!=null) {
			p.flush()
		}
	}
	
	public void stop() {
		log.fine 'Stopping preprocessor'
		// close swinging door processors
		swingingDoorProcessors.values().each { it.close() }
		swingingDoorProcessors.clear()
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

	private static int minQuality(int first, int second) {
		// get minimum quality
		int q = Math.min(first & ProcessValue.STATE_QUALITY_MASK, second & ProcessValue.STATE_QUALITY_MASK) 
		// replace quality bits, keep other bits
		(first & ~ProcessValue.STATE_QUALITY_MASK) | q
	}
	
	private static int stateComprFunction(long begin, List<Event> events) {
		int state = events[0].pv.state
		// incomplete interval?
		if (events[0].pv.timestamp.time > begin)
			state = minQuality(state, ProcessValue.STATE_QUALITY_QUESTIONABLE)
		// compress state
		state = events.inject(state) { int s, Event e -> Preprocessor.minQuality(s, e.pv.state) }
		// return preprocessed
		state | ProcessValue.STATE_PREPROCESSED
	}
	
	private static class AvgComprSum {
		long end
		double sum
	}
	
	private static Event avgComprFunction(long begin, long end, List<Event> events) {
		AvgComprSum res=events.reverse().inject(new AvgComprSum(sum:0.0d, end:end)) { AvgComprSum r, Event e ->
			double value
			switch (e.pv.value) {
				case Number: value=((Number)e.pv.value).doubleValue(); break
				case Boolean: value=(Boolean)e.pv.value?1.0:0.0; break
			}
			r.sum += value * (r.end - e.pv.timestamp.time)
			r.end = e.pv.timestamp.time
			r
		}
		double avg = res.sum / (end - events[0].pv.timestamp.time)
		int state = stateComprFunction(begin, events)
		new Event(
			dataPoint: events[0].dataPoint,
			pv: new ProcessValue(new Date(begin), avg, state)
		)
	}

	private static Event minComprFunction(long begin, long end, List<Event> events) {
		Event e = events.min { it.pv.value }
		int state = stateComprFunction(begin, events)
		new Event(
			dataPoint: e.dataPoint,
			pv: new ProcessValue(new Date(begin), e.pv.value, state)
		)
	}

	private static Event maxComprFunction(long begin, long end, List<Event> events) {
		Event e = events.max { it.pv.value }
		int state = stateComprFunction(begin, events)
		new Event(
			dataPoint: e.dataPoint,
			pv: new ProcessValue(new Date(begin), e.pv.value, state)
		)
	}
	
	private Map<DataPointIdentifier, IntervalProcessor> intervalProcessors=[:]
	
	private void applyIntervalProcessor(Event event, Type type, double param) {
		long intervalLength=(long)(param*1000)
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
			switch (type) {
				case Type.AVG_COMPR: 
					ip.function = Preprocessor.&avgComprFunction as IntervalFunction
					break 
				case Type.MIN_COMPR: 
					ip.function = Preprocessor.&minComprFunction as IntervalFunction
					break 
				case Type.MAX_COMPR: 
					ip.function = Preprocessor.&maxComprFunction as IntervalFunction
					break 
			}
			ip.addConsumer { Event e -> produce e }
			intervalProcessors[event.dataPoint.id]=ip
		}
		ip.consume event
	}

	private Map<DataPointIdentifier, SwingingDoorProcessor> swingingDoorProcessors=[:]
	
	private void applySwingingDoor(Event event, Type type, double deviation) {
		log.finer "Preprocessor: Applying swinging door compression to $event" 
		if (deviation<0) {
			log.warning "Preprocessor: Invalid swinging door parameter (data point: $event.dataPoint.id)"
			produce event
			return
		}
		if (!(event.pv.value instanceof Number)) {
			log.warning "Preprocessor: Invalid data type ${event.pv.value.class.name} for swinging door compression (data point: $event.dataPoint.id)"
			produce event
			return
		}
		SwingingDoorProcessor swdp=swingingDoorProcessors[event.dataPoint.id]
		if (swdp==null || swdp.deviation!=deviation) {
			// replace previous processor?
			if (swdp!=null) {
				swdp.close()
			}
			// create new processor
			swdp=[]
			swdp.deviation=deviation
			swdp.addConsumer { Event e -> produce e }
			swingingDoorProcessors[event.dataPoint.id]=swdp
		}
		swdp.consume event
	}
}
