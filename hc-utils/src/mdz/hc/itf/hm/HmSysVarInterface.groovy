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

import groovy.transform.TupleConstructor
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

import mdz.Utilities
import mdz.eventprocessing.BasicProducer
import mdz.hc.itf.BrowseSupport
import mdz.hc.itf.Interface
import mdz.hc.itf.Manager
import mdz.hc.itf.SubscriptionSupport
import mdz.hc.itf.WriteSupport
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.RawEvent
import mdz.hc.ProcessValue

@Slf4j
@TupleConstructor
@CompileStatic
public class HmSysVarInterface extends BasicProducer<RawEvent> implements Interface, SubscriptionSupport, BrowseSupport, WriteSupport {

	private final static int DEFAULT_DATA_CYCLE = 30000 // ms
	private final static int INITIAL_DELAY = 5000 // ms 
	
	final String name
	final HmScriptClient scriptClient
	final Manager manager
	final int dataCycle = DEFAULT_DATA_CYCLE
	private Date propertiesCacheUpdate
	private List<DataPoint> propertiesCache
	private Map<DataPointIdentifier, RawEvent> valueCache=[:]
	private List<DataPoint> subscription
	private ScheduledFuture<?> readVariablesFuture
	private boolean writeAccess = false
	
	@Override
	public void start() {
		readVariablesFuture=manager.executor.scheduleWithFixedDelay(
			this.&readVariables, INITIAL_DELAY, dataCycle, TimeUnit.MILLISECONDS
		)
	}

	@Override
	public synchronized void stop() {
		if (readVariablesFuture!=null) {
			readVariablesFuture.cancel(false)
			readVariablesFuture=null
		}
		valueCache.clear()
	}

	public void setWriteAccess(boolean writeAccess) {
		this.writeAccess=writeAccess
	}

	private synchronized List<DataPoint> getCache(long maxCacheAge) {
		Date now=[]
		if (propertiesCacheUpdate==null || now.time-propertiesCacheUpdate.time>=maxCacheAge) {
			propertiesCache=scriptClient.getSystemVariables(name)
			propertiesCacheUpdate=now
		}
		propertiesCache
	}

	@Override
	public void updateProperties(List<DataPoint> dps, long maxCacheAge) {
		List<DataPoint> systemVariables=getCache(maxCacheAge)
		dps.each { DataPoint dp ->
			DataPoint foundDp=systemVariables.find { it.id==dp.id }
			if (foundDp) {
				dp.attributes.displayName=foundDp.attributes.displayName
				dp.attributes.maximum=foundDp.attributes.maximum
				dp.attributes.unit=foundDp.attributes.unit
				dp.attributes.minimum=foundDp.attributes.minimum
				dp.attributes.operations=foundDp.attributes.operations
				dp.attributes.type=foundDp.attributes.type
			} else
				log.warn 'Unknown system variable {}', dp.id 
		}
	}
	
	@Override
	public void setSubscription(List<DataPoint> dps) {
		DataPoint invalidDp=dps.find { DataPoint dp ->
			!dp.id?.address || !dp.attributes.type
		}
		if (invalidDp)
			throw new Exception("Required properties are not set for data point $invalidDp.id")
		subscription=dps
	}
	
	private synchronized void readVariables() {
		if (readVariablesFuture==null) return
		Utilities.catchToLog(log) {
			log.debug 'Reading system variable values'
			List<RawEvent> events=scriptClient.getSystemVariableValues(subscription)
			events.each { RawEvent event -> 
				if (event!=null) {
					RawEvent cachedEvent=valueCache[event.id]
					if (cachedEvent==null || event!=cachedEvent) {
						valueCache[event.id]=event
						// possibly replace invalid or identical/older timestamp
						if (event.pv.timestamp==null || event.pv.timestamp<=cachedEvent?.pv?.timestamp)
							event=new RawEvent(
								id: event.id, 
								pv: new ProcessValue(new Date(), event.pv.value, event.pv.state)
							) // New object so that the cache is not changed
						produce event
					}
				} 
			}
		}
	}

	@Override
	public List<DataPointIdentifier> getAllDataPoints(long maxCacheAge) {
		List<DataPoint> systemVariables=getCache(maxCacheAge)
		systemVariables.collect { DataPoint dp -> dp.id }
	}
	
	@Override
	public void writeValue(DataPoint dp, Object value) {
		if (!writeAccess)
			throw new Exception("Write access on interface $name is disabled")
		scriptClient.setSystemVariableValue(dp, value)
	}
}
