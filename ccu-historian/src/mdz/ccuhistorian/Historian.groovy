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

import java.util.logging.Logger
import java.util.concurrent.TimeUnit
import java.util.concurrent.ScheduledFuture
import groovy.util.logging.Log
import mdz.eventprocessing.Buffer
import mdz.eventprocessing.Consumer
import mdz.hc.Event
import mdz.hc.RawEvent
import mdz.hc.DataPoint
import mdz.hc.itf.BrowseSupport
import mdz.hc.itf.Interface
import mdz.hc.itf.SubscriptionSupport
import mdz.hc.itf.Manager
import mdz.hc.persistence.Storage
import mdz.hc.timeseries.TimeSeries
import mdz.Exceptions
import mdz.ccuhistorian.eventprocessing.DataPointStorageUpdater
import mdz.ccuhistorian.eventprocessing.FirstArchived
import mdz.ccuhistorian.eventprocessing.HistoryDisabledFilter
import mdz.ccuhistorian.eventprocessing.OverflowHandler
import mdz.ccuhistorian.eventprocessing.Preprocessor

@Log
class Historian implements Runnable {

	private static final long DEFAULT_START_DELAY = 2000 // ms
	private static final long DEFAULT_RETRY_DELAY = 30000 // ms
	
	HistorianConfig config
	Base base
	ExtendedStorage database
	Manager interfaceManager
	
	private Buffer buffer
	private DataPointStorageUpdater dataPointStorageUpdater
	private HistoryDisabledFilter historyDisabledFilter
	private OverflowHandler overflowHandler
	private Preprocessor preprocessor
	private FirstArchived firstArchived
	
	Historian(HistorianConfig config, Base base, ExtendedStorage database, Manager interfaceManager) {
		log.info 'Starting historian'
		this.config=config
		this.base=base
		this.database=database
		this.interfaceManager=interfaceManager
		config.logDebug()
		
		firstArchived=[]
		preprocessor=[]
		overflowHandler=[]
		overflowHandler.historyStorage=database
		if (config.counters!=null) {
			overflowHandler.counters=config.counters
		}
		historyDisabledFilter=[]
		dataPointStorageUpdater=[]
		dataPointStorageUpdater.storage=database
		dataPointStorageUpdater.defaultDisabled=config.defaultDisabled
		dataPointStorageUpdater.defaultHidden=config.defaultHidden
		buffer=[]
		buffer.countLimit=config.bufferCount
		buffer.timeLimit=config.bufferTime

		firstArchived.addConsumer database
		preprocessor.addConsumer firstArchived
		database.onRequestFlushListener << { id -> preprocessor.flush(id) }
		overflowHandler.addConsumer preprocessor
		historyDisabledFilter.addConsumer overflowHandler
		dataPointStorageUpdater.addConsumer historyDisabledFilter
		buffer.addConsumer dataPointStorageUpdater  
		database.onReadListener << { buffer.purge() }
		interfaceManager.addConsumer buffer 
		
		base.executor.schedule this, DEFAULT_START_DELAY, TimeUnit.MILLISECONDS
	}
	
	synchronized void stop() {
		buffer.purge()
		preprocessor.stop()
	}

	@Override
	public void run() {
		def err=false
		interfaceManager.interfaces.each { name, itf ->
			def ex=Exceptions.catchToLog(log) {
				if (itf instanceof BrowseSupport) {
					browse itf
				}
				update itf
				if (itf instanceof SubscriptionSupport) {
					subscribe itf
				}
			}
			if (ex!=null) {
				err=true
			}
		}
		base.executor.schedule this, err?DEFAULT_RETRY_DELAY:config.metaCycle, TimeUnit.MILLISECONDS
	}

	private void browse(Interface itf) {
		log.finer "Historian: Browsing interface: $itf.name"
		List<DataPoint> itfDps=((BrowseSupport)itf).getAllDataPoints(config.metaCycle-1).collect {	new DataPoint(id: it) }
		List<DataPoint> dbDps=database.getDataPointsOfInterface(itf.name)
		
		// create new data points
		itfDps.each { DataPoint itfDp ->
			DataPoint dbDp=dbDps.find { it.id==itfDp.id }
			if (dbDp==null) {
				log.info "Historian: Creating data point $itfDp.id"
				itfDp.historyDisabled=config.defaultDisabled
				itfDp.historyHidden=config.defaultHidden
				database.createDataPoint itfDp
			}
		}

		// deactivate missing data points
		dbDps.findAll { !it.historyDisabled }.each { DataPoint dbDp ->
			DataPoint itfDp=itfDps.find { dbDp.id==it.id }
			if (itfDp==null) {
				log.info "Historian: Disabling data point $dbDp.id"
				dbDp.historyDisabled=true
				database.updateDataPoint dbDp
			}
		}
	}

	private void update(Interface itf) {
		log.finer "Historian: Updating data points of interface: $itf.name"
		
		List<DataPoint> dataPoints=database.getDataPointsOfInterface(itf.name).findAll { 
			!it.noSynchronization && (!it.historyDisabled || !it.synced)
		}
		List<DataPoint> oldDataPoints=dataPoints.collect { (DataPoint)it.clone() }
		interfaceManager.updateProperties(dataPoints, config.metaCycle-1)
		dataPoints.eachWithIndex { DataPoint dp, int index ->
			dp.synced=true
			if (dp!=oldDataPoints[index]) {
				database.updateDataPoint dp
			}
		}
	}

	private void subscribe(Interface itf) {
		log.finer "Historian: Subscribing data points of interface: $itf.name"
		
		((SubscriptionSupport)itf).setSubscription(
			database.getDataPointsOfInterface(itf.name).findAll { !it.historyDisabled	}
		)
	}
}
