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
package mdz.hc.itf

import mdz.eventprocessing.Consumer
import mdz.eventprocessing.BasicProducer
import mdz.hc.DataPoint
import mdz.hc.RawEvent
import mdz.hc.itf.hm.HmBinRpcServer
import mdz.hc.itf.hm.HmXmlRpcServer
import mdz.hc.itf.binrpc.BinRpcServer
import groovy.util.logging.Log
import groovy.transform.CompileStatic
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Log
@CompileStatic
public class Manager extends BasicProducer<RawEvent> implements Consumer<RawEvent> {
	
	private final static long SHUTDOWN_TIMEOUT = 15000 // ms
	
	int threadPoolSize=2
	
	private HmBinRpcServer binRpcServer
	private HmXmlRpcServer xmlRpcServer
	private ScheduledExecutorService executor
	private Map<String, Interface> interfaces=[:]
	private LinkedBlockingQueue<RawEvent> eventQueue=[]
	private Thread eventSenderThread
	private boolean started
	
	public void start() {
		if (started)
			throw new IllegalStateException()
		try {
			log.info 'Starting interfaces'
			binRpcServer?.start()
			xmlRpcServer?.start()
			interfaces.each { String name, Interface itf ->
				itf.addConsumer(this) 
				itf.start() 
			}
			log.fine 'Interfaces started'
			eventSenderThread=[this.&sendEvents as Runnable, 'manager-eventsender']
			eventSenderThread.start()
			started=true
		} catch (Exception e) {
			stop(); throw e
		}
	}
	
	public void stop() {
		if (started) 
			log.info 'Stopping interfaces'
		interfaces.each { String name, Interface itf -> 
			itf.stop()
			itf.removeConsumer(this) 
		}
		xmlRpcServer?.stop()
		xmlRpcServer=null
		binRpcServer?.stop()
		binRpcServer=null
		if (executor) {
			executor.shutdownNow()
			executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS)
			executor=null
		}
		if (eventSenderThread) {
			eventSenderThread.interrupt()
			eventSenderThread.join()
			eventSenderThread=null
		}
		started=false
	}
	
	public void addInterface(Interface itf) {
		if (interfaces.containsValue(itf) || interfaces[itf.name]!=null)
			throw new Exception("Duplicate HM interface '$itf.name'")
		interfaces[itf.name]=itf
	}
	
	public Interface getAt(String name) {
		Interface itf=interfaces[name]
		if (itf==null)
			throw new Exception("Unknown HM interface '$name'")
		itf
	}
	
	public Set<String> getInterfaceNames() {
		interfaces.keySet().asImmutable()
	}
	
	public Map<String, Interface> getInterfaces() {
		interfaces.asImmutable()
	}
	
	public void updateProperties(List<DataPoint> dps, long maxCacheAge) {
		Map<String, List<DataPoint>> groupedDps=dps.groupBy([{ DataPoint dp -> dp.id.interfaceId }])
		groupedDps.each { interfaceId, List<DataPoint> itfDps ->
			Interface itf=interfaces[interfaceId]
			if (itf!=null)
				itf.updateProperties(itfDps, maxCacheAge)
			else
				log.warning "Unknown HM interface '$interfaceId': Skipping update of properties" 
		}
	}
	
	public void writeValue(DataPoint dp, value) {
		Interface itf=getAt(dp.id.interfaceId)
		if (!(itf instanceof WriteSupport))
			throw new Exception("HM interface '$itf.name' has no write support")
		((WriteSupport)itf).writeValue(dp, value)
	}
	
	public void sendEvents() {
		while (true) {
			RawEvent event
			try { event=eventQueue.take() } 
			catch (InterruptedException ex) { return }
			produce event
		}
	}
	
	public HmBinRpcServer getBinRpcServer() {
		if (binRpcServer==null)
			binRpcServer=new HmBinRpcServer()
		binRpcServer
	}

	public HmXmlRpcServer getXmlRpcServer() {
		if (xmlRpcServer==null)
			xmlRpcServer=new HmXmlRpcServer()
		xmlRpcServer
	}

	public ScheduledExecutorService getExecutor() {
		if (executor==null) {
			ScheduledThreadPoolExecutor exec=new ScheduledThreadPoolExecutor(threadPoolSize, new ThreadPoolExecutor.DiscardPolicy())
			exec.setExecuteExistingDelayedTasksAfterShutdownPolicy(false)
			executor=exec
		}
		executor
	}

	@Override
	public void consume(RawEvent event) {
		log.finer "Event received: $event"
		if (event.id.identifier=='PONG' &&
			event.id.address.startsWith('CENTRAL')) {
			log.fine 'Discarding ping response'
		} else {
			eventQueue.offer event
		}
	}
}
