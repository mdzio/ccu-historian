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
package mdz.ccuhistorian

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import mdz.Exceptions
import mdz.hc.itf.hm.HmScriptClient

@CompileStatic
@Log
public class Watchdog implements Runnable {

	private static final long INITIAL_DELAY = 5000; // [ms]
	
	String program
	Long cycle
	ScheduledExecutorService executor
	HmScriptClient scriptClient

	public Watchdog(String program, Long cycle, ScheduledExecutorService executor, HmScriptClient scriptClient) {
		this.program=program
		this.cycle=cycle
		this.executor=executor
		this.scriptClient=scriptClient
		log.fine "Starting watchdog (program: $program, cycle: $cycle" 
		executor.scheduleAtFixedRate this, INITIAL_DELAY, cycle, TimeUnit.MILLISECONDS
	}

	@Override
	public void run() {
		Exceptions.catchToLog(log) {
			log.finer 'Watchdog is triggered'
			scriptClient.executeProgram program
		}
	}
}
