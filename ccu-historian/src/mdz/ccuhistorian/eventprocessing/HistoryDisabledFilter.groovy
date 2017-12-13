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
import mdz.hc.Event
import mdz.ccuhistorian.Main
import mdz.Exceptions

@CompileStatic
@Log
public class HistoryDisabledFilter extends BasicProducer<Event> implements Processor<Event, Event> {

	public void consume(Event event) throws Exception {
		try {
			if (!event.dataPoint.historyDisabled)
				produce event
		} catch (Throwable t) {
			log.severe 'Error filtering disabled histories'
			Exceptions.logTo(log, Level.SEVERE, t)
		}
	}
}
