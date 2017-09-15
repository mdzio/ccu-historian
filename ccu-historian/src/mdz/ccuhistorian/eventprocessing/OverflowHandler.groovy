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

import java.util.Map

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import mdz.eventprocessing.BasicProducer
import mdz.eventprocessing.Processor
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.hc.persistence.HistoryStorage
import mdz.ccuhistorian.Main
import mdz.Utilities

@CompileStatic
@Slf4j
public class OverflowHandler extends BasicProducer<Event> implements Processor<Event, Event> {

	HistoryStorage historyStorage
	Map<String, Double> counters=[RAIN_COUNTER: 9666.56D, SUNSHINEDURATION: 256D]

	private Map<DataPointIdentifier, Object> previousValues=[:]
		
	public void consume(Event event) throws Exception {
		try {
			Double range=counters[event.dataPoint.id.identifier]
			if (range!=null) {
				if (!(event.pv.value instanceof Number))
					log.warn 'Overflow handler: Event for data point {} is not numeric, no overflow calculation', event.dataPoint.id
				else {
					double eventDbl=((Number)event.pv.value).doubleValue()
					def last=previousValues[event.dataPoint.id]
					if (last==null)
						last=historyStorage.getLast(event.dataPoint)?.value
					if (last!=null) {
						if (!(last instanceof Number))
							log.warn 'Overflow handler: Data point {} is not numeric, no overflow calculation', event.dataPoint.id
						else {
							double lastDbl=((Number)last).doubleValue()
							int overflows=(int)(lastDbl/range)
							double lastValue=lastDbl-overflows*range
							if (eventDbl<lastValue-range/10.0D) {
								overflows++
								log.debug 'Overflow handler: Overflow on data point {} detected', event.dataPoint.id
							}
							double adjustedValue=eventDbl+overflows*range
							log.trace 'Overflow handler: Adjusted value {} for event {}', adjustedValue, event
							event=new Event(
								dataPoint:event.dataPoint,
								pv:new ProcessValue(event.pv.timestamp, adjustedValue, event.pv.state),
								attributes:event.attributes
							)
						}
					}
					previousValues[event.dataPoint.id]=event.pv.value
				}
			}
			produce event
		} catch (Throwable t) {
			log.error 'Error handling overflows', t
			// TODO: adjust when switching to logback
			log.debug '{}', Utilities.getStackTrace(t)
			Main.restart()
		}
	}
}
