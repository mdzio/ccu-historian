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
package mdz.hc.itf.misc

import groovy.transform.TupleConstructor
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

import mdz.Utilities

import mdz.eventprocessing.BasicProducer
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.RawEvent
import mdz.hc.ProcessValue
import mdz.hc.itf.BrowseSupport
import mdz.hc.itf.Interface
import mdz.hc.itf.Manager
import mdz.hc.itf.SubscriptionSupport
import mdz.hc.itf.WriteSupport

@Slf4j
@TupleConstructor
@CompileStatic
class SimulationInterface extends BasicProducer<RawEvent> implements Interface, SubscriptionSupport, BrowseSupport, WriteSupport {
	
	private final static long DEFAULT_DATA_CYCLE = 2000 // ms
	private final static long INITIAL_DELAY = 1000 // ms
	private final static Random random=[]
	
	@TupleConstructor
	private static class DataPointTemplate {
		String identifier
		String type
		def minimum
		def maximum
		String unit
		Closure function
	}
	private final static List<DataPointTemplate> DATA_POINT_TEMPLATES = [
		new DataPointTemplate('ACTION', DataPoint.ATTR_TYPE_ACTION, 1.0, 1.0, 'Aktion', { int it -> (it%4)==0?1:null }),
		new DataPointTemplate('BOOL', DataPoint.ATTR_TYPE_BOOL, 0.0, 1.0, 'Ein/Aus', { int it -> (it%2)!=0 }),
		new DataPointTemplate('RAMP', DataPoint.ATTR_TYPE_INTEGER, 0.0, 9.0, '', { int it -> it%10 }),
		new DataPointTemplate('SIN', DataPoint.ATTR_TYPE_FLOAT, -10.0, 10.0, '', { int it -> Math.sin((double)it/30.0*Math.PI)*10.0 }),
		new DataPointTemplate('TEXT', DataPoint.ATTR_TYPE_STRING, null, '', '', { "Text $it" as String }),
		new DataPointTemplate('SUNSHINEDURATION', DataPoint.ATTR_TYPE_INTEGER, 0.0, 255.0, '', { int it -> (it*30)%256 }),
		new DataPointTemplate('RANDOM', DataPoint.ATTR_TYPE_FLOAT, 0.0, 20.0, '', { int it -> random.nextDouble()*20.0 }),
		new DataPointTemplate('PEAK', DataPoint.ATTR_TYPE_BOOL, 0.0, 1.0, 'Ein/Aus', { int it ->
			if ((it%10)==0) true
			else if ((it%10)==1) false
			else null 
		}),
		new DataPointTemplate('NOISY_SQUARE', DataPoint.ATTR_TYPE_BOOL, 0.0, 1.0, 'Ein/Aus', { int it ->
			int phase=it%20
			if (phase==0) false
			else if (phase>=7 && phase<10) random.nextBoolean()
			else if (phase==10) true
			else if (phase>=17) random.nextBoolean()
			else null
		})
	]
	private final static int DEFAULT_DATA_POINT_COUNT = DATA_POINT_TEMPLATES.size()
	
	final String name
	final Manager manager
	
	long dataCycle = DEFAULT_DATA_CYCLE
	int dataPointCount = DEFAULT_DATA_POINT_COUNT
	boolean writeAccess
	
	volatile private List<DataPoint> subscription
	private Map<DataPointIdentifier, DataPoint> dataPoints
	private Map<DataPointIdentifier, Closure> functions
	private ScheduledFuture<?> readVariablesFuture
	private int counter
	
	@Override
	public void start() {
		int srcIdx=0
		dataPoints=[:]
		functions=[:]
		dataPointCount.times { dstIdx ->
			DataPointTemplate tmpl=DATA_POINT_TEMPLATES[srcIdx++]
			if (srcIdx==DATA_POINT_TEMPLATES.size()) srcIdx=0
			DataPointIdentifier id=new DataPointIdentifier(
				name, 
				"Simulated_${tmpl.type}_$dstIdx" as String,
				tmpl.identifier
			)
			dataPoints << [(id): new DataPoint(
				id: id,
				attributes: [
					(DataPoint.ATTR_TYPE):tmpl.type,
					(DataPoint.ATTR_MAXIMUM):tmpl.maximum,
					(DataPoint.ATTR_UNIT):tmpl.unit,
					(DataPoint.ATTR_MINIMUM):tmpl.minimum,
				]
			)]
			functions << [(id): tmpl.function]
		}
		log.debug 'Simulated data points:'
		dataPoints.values().each { log.debug '{}', it }
		
		counter=0
		readVariablesFuture=manager.executor.scheduleWithFixedDelay(
			this.&readDataPoints, INITIAL_DELAY, dataCycle, TimeUnit.MILLISECONDS
		)
	}

	@Override
	public synchronized void stop() {
		if (readVariablesFuture!=null) {
			readVariablesFuture.cancel(false)
			readVariablesFuture=null
		}
		dataPoints=null
	}

	@Override
	public void updateProperties(List<DataPoint> dps, long maxCacheAge) {
		dps.each { DataPoint dp ->
			DataPoint foundDp=dataPoints[dp.id]
			if (foundDp) {
				dp.attributes.putAll foundDp.attributes
			} else log.warn 'Unknown data point {}', dp.id 
		}
	}
	
	@Override
	public void setSubscription(List<DataPoint> dps) {
		DataPoint invalidDp=dps.find { DataPoint dp -> !dp.id?.address }
		if (invalidDp)
			throw new Exception("Required properties are not set for data point $invalidDp.id")
		subscription=dps
	}
	
	private synchronized void readDataPoints() {
		if (readVariablesFuture==null) return
		Utilities.catchToLog(log) {
			log.debug 'Reading simulation data points'
			Date ts=new Date()
			subscription?.each { dp ->
				def value=functions[dp.id](counter)
				if (value!=null) {
					RawEvent event=new RawEvent(
						id: dp.id, 
						pv: new ProcessValue(ts, value, ProcessValue.STATE_QUALITY_NOT_SUPPORTED)
					)
					produce event
				}
			}
			counter++
		}
	}

	@Override
	public List<DataPointIdentifier> getAllDataPoints(long maxCacheAge) {
		dataPoints.keySet() as List
	}
	
	@Override
	public void writeValue(DataPoint dp, Object value) {
		if (!writeAccess)
			throw new Exception("Write access on interface $name is disabled")
		log.debug 'Writing value {} into data point {}', value, dp.id
	}
}
