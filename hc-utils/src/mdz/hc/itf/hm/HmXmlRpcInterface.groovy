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

import java.util.List
import java.util.concurrent.ScheduledExecutorService
import java.util.logging.Level
import groovy.util.logging.Log
import groovy.transform.TupleConstructor
import groovy.transform.EqualsAndHashCode
import groovy.transform.CompileStatic
import mdz.Exceptions
import mdz.Text
import mdz.eventprocessing.Consumer
import mdz.eventprocessing.BasicProducer
import mdz.hc.RawEvent
import mdz.hc.itf.Interface
import mdz.hc.itf.WriteSupport
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier

/**
 * HmXmlRpcInterface communicates with a HomeMatic interface process over XML-RPC.
 * 
 * Methods start, stop are not synchronized.
 */
@Log
@TupleConstructor
@CompileStatic
public class HmXmlRpcInterface extends BasicProducer<RawEvent> implements Interface, WriteSupport, Consumer<RawEvent>, HmReinitable {

	public static final String PARAMSET_VALUES = 'VALUES'
	public static final String UNKNOWN_DEVICE_ERROR_TEXT1 = 'Unknown instance'
	public static final String UNKNOWN_DEVICE_ERROR_TEXT2 = 'Invalid device'
	 
	final String name
	// name of the interface in the logic layer of the CCU
	final String logicName
	final String host
	final int port
	final HmXmlRpcServer server
	final HmScriptClient scriptClient
	final HmReinitTask reinitTask
	final ScheduledExecutorService executor
	
	private HmXmlRpcClient client=[]
	private Date lastCommTime
	private boolean disableRegistration = false
	private boolean writeAccess = false
	private boolean registered
	
	@Override
	public void start() {
		try {
			server.addConsumer(this)
			client.host=host
			client.port=port
			if (!disableRegistration) {
				init()
				reinitTask.add this
			}
		} catch (Exception e) {
			stop(); throw e
		}
	}
	
	@Override
	public void stop() {
		if (!disableRegistration) {
			reinitTask.remove this
			if (registered)
				Exceptions.catchToLog(log) {	deinit() }
		}
		server.removeConsumer(this)
		devicePropCache.clear()
		lastCommTime=null
	}
	
	@Override
	public void consume(RawEvent event) {
		if (event.id.interfaceId==name) {
			lastCommTime=event.pv.timestamp
			produce event
		}
	}

	@Override
	public Date getLastCommTime() { lastCommTime }
	
	public void setDisableRegistration(boolean disableRegistration) {
		this.disableRegistration=disableRegistration
	}
	
	public void setWriteAccess(boolean writeAccess) {
		this.writeAccess=writeAccess
	}

	@Override
	public synchronized void init() {
		client.init server.url, name
		lastCommTime=[]
		registered=true
	}
		
	private synchronized void deinit() {
		client.deinit(server.url)
		registered=false
	}

	private void updateLogicProperties(List<DataPoint> dps, long maxCacheAge) {
		if (!logicName || scriptClient==null) return
		HmModel model=scriptClient.getModel(maxCacheAge)
		dps.each { DataPoint dp ->
			HmModel.Channel ch=model.getChannelByAddress(dp.id.address)
			if (ch?.displayName!=null) 
				dp.attributes.displayName=ch.displayName
			if (ch?.rooms)
				dp.attributes.room=ch.rooms.displayName.toSorted().join(", ")
			if (ch?.functions)
				dp.attributes.function=ch.functions.displayName.toSorted().join(", ")
		}
	}
		
	@TupleConstructor
	@EqualsAndHashCode
	private static class DevicePropCacheValue {
		String paramSet
		Integer tabOrder
		def maximum
		String unit
		def minimum
		String control
		Integer operations
		Integer flags
		String type
		def defaultValue
	}

	private final Map<DataPointIdentifier, DevicePropCacheValue> devicePropCache=[:]
	
	private void updateDeviceProperties(List<DataPoint> dps, long maxCacheAge) {
		dps.each { DataPoint dp ->
			// detect a continuous data point
			dp.continuous=dp.id.identifier in [
				'ACTUAL_HUMIDITY',
				'ACTUAL_TEMPERATURE',
				'AIR_PRESSURE',
				'BRIGHTNESS',
				'CURRENT',
				'ENERGY_COUNTER',
				'FREQUENCY',
				'HUMIDITY',
				'ILLUMINATION',
				'LUX',
				'POWER',
				'RAIN_COUNTER',
				'SUNSHINEDURATION',
				'TEMPERATURE',
				'VOLTAGE',
				'WIND_SPEED',
			]
			
			// device properties never changes
			if (dp.attributes.type!=null) return
			DevicePropCacheValue cachedProperties=devicePropCache[dp.id]
			if (cachedProperties==null) {
				try {
					client.getParamsetDescription(dp.id.address, PARAMSET_VALUES).each { String notUsed, Map meta ->
						DataPointIdentifier dpId=new DataPointIdentifier(name, dp.id.address, (String)meta.ID)
						devicePropCache[dpId]=new DevicePropCacheValue(
							PARAMSET_VALUES,
							(Integer)meta.TAB_ORDER, 
							meta.MAX,
							// incorrectly encoded by CCU
							meta.UNIT!=null?Text.unescapeXml((String)meta.UNIT):null,
							meta.MIN, 
							(String)meta.CONTROL,
							(Integer)meta.OPERATIONS, 
							(Integer)meta.FLAGS,
							(String)meta.TYPE,
							meta.DEFAULT
						)
					}
					cachedProperties=devicePropCache[dp.id]
				} catch (Exception e) {
					if (e.getMessage()!=null && 
						(e.getMessage().equals(UNKNOWN_DEVICE_ERROR_TEXT1) || 
						 e.getMessage().equals(UNKNOWN_DEVICE_ERROR_TEXT2)) ) {
						log.warning "Device $dp.id does not exist"
					} else {
						log.warning "Communication error"
						Exceptions.logTo(log, Level.WARNING, e)
					}
				} catch (IOException e) {
					log.warning "Communication error"
					Exceptions.logTo(log, Level.WARNING, e)
				}
			}
			if (cachedProperties!=null) {
				dp.attributes.paramSet=cachedProperties.paramSet
				dp.attributes.tabOrder=cachedProperties.tabOrder
				dp.attributes.maximum=cachedProperties.maximum
				dp.attributes.unit=cachedProperties.unit
				dp.attributes.minimum=cachedProperties.minimum
				dp.attributes.control=cachedProperties.control
				dp.attributes.operations=cachedProperties.operations
				dp.attributes.flags=cachedProperties.flags
				dp.attributes.type=cachedProperties.type
				dp.attributes.defaultValue=cachedProperties.defaultValue
			}
		}
	}
	
	@Override
	public synchronized void updateProperties(List<DataPoint> dps, long maxCacheAge) {
		updateLogicProperties(dps, maxCacheAge)
		updateDeviceProperties(dps, maxCacheAge)
	}

	@Override
	public synchronized void writeValue(DataPoint dp, value) {
		if (!writeAccess)
			throw new Exception("Write access on interface $name is disabled")
		// convert data type
		// (only system variables can be of type ALARM)
		switch (dp.attributes.type) {
		case DataPoint.ATTR_TYPE_ACTION:
			// action is triggered also with false
			value=true
			break 
		case DataPoint.ATTR_TYPE_BOOL: 
			value=Text.asBoolean(value)
			break
		case DataPoint.ATTR_TYPE_FLOAT: 
			value=value as double
			break
		// TODO: following data types are not tested
		case DataPoint.ATTR_TYPE_INTEGER: 
			// like ENUM, fallthrough
		case DataPoint.ATTR_TYPE_ENUM: 
			value=value as int
			break
		case DataPoint.ATTR_TYPE_STRING:	
			value=value as String
			break
		default:
			throw new Exception("Type $dp.attributes.type of data point $dp.id is not supported")
		}
		client.setValue(dp.id.address, dp.id.identifier, value)
	}
}
