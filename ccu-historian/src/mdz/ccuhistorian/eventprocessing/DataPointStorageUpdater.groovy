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
import java.util.logging.Level
import mdz.eventprocessing.BasicProducer
import mdz.eventprocessing.Processor
import mdz.hc.DataPoint
import mdz.hc.Event
import mdz.hc.RawEvent
import mdz.hc.persistence.DataPointStorage
import mdz.hc.timeseries.TimeSeries
import mdz.ccuhistorian.Main
import mdz.Exceptions

@CompileStatic
@Log
public class DataPointStorageUpdater extends BasicProducer<Event> implements Processor<RawEvent, Event> {

	DataPointStorage storage
	
	public void consume(RawEvent rawEvent) throws Exception {
		try {
			boolean isValueString=TimeSeries.isNormalizedTypeString(rawEvent.pv.value)
			DataPoint dataPoint
			dataPoint=storage.getDataPoint(rawEvent.id)
			if (dataPoint) {
				if (isValueString!=dataPoint.historyString) {
					dataPoint.historyString=isValueString
					storage.updateDataPoint dataPoint
				}
			} else {
				dataPoint=new DataPoint([
					id: rawEvent.id,
					managementFlags: (isValueString?DataPoint.FLAGS_HISTORY_STRING:0),
					attributes: [paramSet:'VALUES'] as Map<String, Object>
				])
				storage.createDataPoint dataPoint
			}
			produce new Event(
				dataPoint: dataPoint,
				pv: rawEvent.pv 
			)
		} catch (Throwable t) {
			log.severe 'Error updating data point storage'
			Exceptions.logTo(log, Level.SEVERE, t)
		}
	}
}
