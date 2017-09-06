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
package mdz.hc.itf.hm

import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

import mdz.Utilities;
import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic

@Slf4j
@CompileStatic
public class HmReinitTask {

	private final static long DEFAULT_TIMEOUT = 5*60*1000
	private final static long DEFAULT_CHECKTIME = 10*1000
	
	long timeout
	long checkTime
	
	private final ScheduledExecutorService executor
	private List<HmReinitable> interfaces=[]
	private ScheduledFuture<?> checkInterfacesFuture

	public HmReinitTask(ScheduledExecutorService executor, long timeout=DEFAULT_TIMEOUT, long checkTime=DEFAULT_CHECKTIME) {
		this.executor=executor
		this.timeout=timeout
		this.checkTime=checkTime
	} 

	public void add(HmReinitable itf) {
		synchronized(interfaces) {
			if (checkInterfacesFuture==null) {
				log.debug 'Starting re-init task'
				checkInterfacesFuture=executor.scheduleWithFixedDelay(
					this.&checkInterfaces, checkTime, checkTime, TimeUnit.MILLISECONDS
				)
			}
			interfaces << itf
		}
	}
	
	public void remove(HmReinitable itf) {
		synchronized(interfaces) {
			interfaces.remove itf
			if (!interfaces && checkInterfacesFuture!=null) {
				log.debug 'Stopping re-init task'
				checkInterfacesFuture.cancel(false)
				checkInterfacesFuture=null
			}
		}
	}
	
	private void checkInterfaces () {
		synchronized(interfaces) {
			if (checkInterfacesFuture==null) return
			Utilities.catchToLog(log) {
				log.trace 'Checking timeouts'
				Date now=[]
				Collection<HmReinitable> timedOut=interfaces.findAll { now.time-it.lastCommTime.time>timeout }
				if (timedOut.size()==interfaces.size()) {
					log.warn "Timeout on interface(s) ${timedOut*.name.join(', ')}; reinitializing all callbacks"
					interfaces.each { itf -> Utilities.catchToLog(log) { itf.init() } }
				}
			}
		}
	}
}
