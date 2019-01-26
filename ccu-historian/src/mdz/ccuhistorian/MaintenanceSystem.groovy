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

import groovy.util.logging.Log
import groovy.transform.CompileStatic
import java.util.concurrent.TimeUnit
import mdz.Exceptions
import mdz.ccuhistorian.eventprocessing.Preprocessor
import mdz.ccuhistorian.eventprocessing.Preprocessor.Type
import mdz.eventprocessing.ConsumerIteratorAdapter
import mdz.eventprocessing.IteratorProducerAdapter
import mdz.eventprocessing.Processor
import mdz.eventprocessing.Producer
import mdz.eventprocessing.Transformer
import mdz.hc.DataPoint
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.hc.timeseries.TimeSeries
import mdz.hc.timeseries.ChunkIterator

@Log
@CompileStatic
class MaintenanceSystem extends DatabaseSystem {

	public MaintenanceSystem(Configuration config) {
		super(config)
		Closure action
		if (config.cmdLineConfig.recalculation)
			action=this.&recalculate
		else if (config.cmdLineConfig.clean!=null)
			action=this.&clean
		else
			throw new Exception("No maintenance action selected")
		base.executor.schedule({
				Exceptions.catchToLog(log) { action() }
				Main.shutdown()
			}, 250, TimeUnit.MILLISECONDS)
	}
	
	private void recalculate() {
		log.info 'Starting recalculation of compressed data points'
		
		Collection<DataPoint> dps=database.dataPoints.findAll { DataPoint dp ->
			boolean found=false
			int typeIndex=(dp.attributes.preprocType as Integer)?:Type.DISABLED.ordinal()
			if (typeIndex!=Type.DISABLED.ordinal()) {
				if (typeIndex<0 || typeIndex>=Type.values().length)
					log.warning "Invalid preprocessing type $typeIndex (data point: $dp.id)"
				else {
					Type type=Type.values()[typeIndex]
					switch (type) {
						case Type.DELTA_COMPR: found=true;break
						case Type.TEMPORAL_COMPR: found=true; break
					}
				}
			}
			found
		}
		
		long totalCurNumOfEntries=0
		long totalNewNumOfEntries=0
		long totalTimeTaken=0
		dps.each { DataPoint dp ->
			log.info "Recalculating compressed data point $dp.displayName" 
			
			// statistics
			long timeTaken=System.currentTimeMillis()
			int curNumOfEntries=database.getCount(dp, null, null)
			totalCurNumOfEntries+=curNumOfEntries
			
			// build up processing chain
			Date beginTime=database.getFirstTimestamp(dp)
			if (beginTime!=null) {
				Date endTime=new Date(database.getLast(dp).timestamp.time+1)
				Iterator<ProcessValue> srcIterator=new ChunkIterator(dp, database, beginTime, endTime)
				Producer<ProcessValue> producer=new IteratorProducerAdapter<ProcessValue>(srcIterator)
				Processor<ProcessValue, Event> transformPv=new Transformer<ProcessValue, Event>({ 
					ProcessValue pv -> new Event(dataPoint: dp, pv:pv) 
				})
				producer.addConsumer(transformPv)
				Processor<Event, Event> preprocessor=new Preprocessor()
				transformPv.addConsumer(preprocessor)
				Processor<Event, ProcessValue> transformEvent=new Transformer<Event, ProcessValue>({
					Event e -> e.pv
				})
				preprocessor.addConsumer(transformEvent)
				Iterator<ProcessValue> preprocIterator=new ConsumerIteratorAdapter<ProcessValue>(producer)
				transformEvent.addConsumer(preprocIterator)
				
				// compress time series
				database.replaceTimeSeries(dp, preprocIterator, beginTime, endTime)
			}
				
			// statistics
			int newNumOfEntries=database.getCount(dp, null, null)
			totalNewNumOfEntries+=newNumOfEntries
			timeTaken=System.currentTimeMillis()-timeTaken
			float percentDropped=curNumOfEntries?(curNumOfEntries-newNumOfEntries)/curNumOfEntries*100:0.0
			log.info "${curNumOfEntries-newNumOfEntries} entries from $curNumOfEntries dropped" +
				" ($percentDropped %); ${timeTaken/1000} seconds" 
			totalTimeTaken+=timeTaken
			
			if (Thread.interrupted()) 
				throw new Exception('Shutdown request')
		}
		
		// statistics
		float percentDropped=totalCurNumOfEntries?(totalCurNumOfEntries-totalNewNumOfEntries)/
			totalCurNumOfEntries*100:0.0
		log.info "Summary: ${totalCurNumOfEntries-totalNewNumOfEntries} entries from $totalCurNumOfEntries" +
			" dropped ($percentDropped %); ${totalTimeTaken/1000} seconds" 
		log.info 'Recalculation completed' 
	}
	
	private void clean() {
		log.info "Starting cleaning of time series date before $config.cmdLineConfig.clean" 

		long totalCurNumOfEntries=0
		long totalNewNumOfEntries=0
		long totalTimeTaken=0
		database.dataPoints.each { DataPoint dp ->
			log.info "Cleaning data point $dp.displayName"
			
			// statistics
			long timeTaken=System.currentTimeMillis()
			int curNumOfEntries=database.getCount(dp, null, null)
			totalCurNumOfEntries+=curNumOfEntries

			// delete time series
			database.deleteTimeSeries(dp, null, config.cmdLineConfig.clean)
			
			// statistics
			int newNumOfEntries=database.getCount(dp, null, null)
			totalNewNumOfEntries+=newNumOfEntries
			timeTaken=System.currentTimeMillis()-timeTaken
			float percentDropped=curNumOfEntries?(curNumOfEntries-newNumOfEntries)/curNumOfEntries*100:0.0
			log.info "${curNumOfEntries-newNumOfEntries} entries from $curNumOfEntries dropped" + 
				" ($percentDropped %); ${timeTaken/1000} seconds"
			totalTimeTaken+=timeTaken
			
			if (Thread.interrupted())
				throw new Exception('Shutdown request')
		}
		
		// statistics
		float percentDropped=totalCurNumOfEntries?(totalCurNumOfEntries-totalNewNumOfEntries)/
			totalCurNumOfEntries*100:0.0
		log.info "Summary: ${totalCurNumOfEntries-totalNewNumOfEntries} entries from $totalCurNumOfEntries" +
			" dropped ($percentDropped %); ${totalTimeTaken/1000} seconds" 
		log.info 'Cleaning completed'
	}
}
