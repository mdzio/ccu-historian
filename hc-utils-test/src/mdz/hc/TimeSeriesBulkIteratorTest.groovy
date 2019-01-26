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
package mdz.hc;

import java.util.Date;

import groovy.util.GroovyTestCase;
import mdz.hc.persistence.HistoryStorage
import mdz.hc.timeseries.TimeSeries
import mdz.hc.timeseries.ChunkIterator

class TimeSeriesBulkIteratorTest extends GroovyTestCase {

	public void testEmptyMiddle() {
		DataPoint dataPoint=new DataPoint()
		dataPoint.setHistoryString(false)

		TimeSeries ts1=new TimeSeries(dataPoint)
		ts1.add(0, 0.0, 0)
		ts1.add(1, 0.5, 1)

		TimeSeries ts2=new TimeSeries(dataPoint)
		// leere Zeitreihe

		TimeSeries ts3=new TimeSeries(dataPoint)
		ts3.add(2*ChunkIterator.DEFAULT_CHUNK_LENGTH, 1.0, 2)
		ts3.add(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+1, 1.5, 4)
		
		HistoryStorage hs={ DataPoint dp, Date begin, Date end ->
			switch (begin.time) {
				case 0: ts1; break
				case ChunkIterator.DEFAULT_CHUNK_LENGTH: ts2; break
				case 2*ChunkIterator.DEFAULT_CHUNK_LENGTH: ts3; break
				default: fail()
			}
		} as HistoryStorage

		ChunkIterator it=new ChunkIterator(dataPoint, hs, 
			new Date(0), new Date(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+2))
		
		assert it.hasNext()
		ProcessValue pv=it.next()
		assert pv.timestamp.time==0
		assert pv.value==0.0
		assert pv.state==0
		
		assert it.hasNext()
		pv=it.next()
		assert pv.timestamp.time==1
		assert pv.value==0.5
		assert pv.state==1
		
		assert it.hasNext()
		pv=it.next()
		assert pv.timestamp.time==2*ChunkIterator.DEFAULT_CHUNK_LENGTH
		assert pv.value==1.0
		assert pv.state==2
		
		assert it.hasNext()
		pv=it.next()
		assert pv.timestamp.time==2*ChunkIterator.DEFAULT_CHUNK_LENGTH+1
		assert pv.value==1.5
		assert pv.state==4
		
		assert !it.hasNext()
	}
	
	public void testStartWithEmpty() {
		DataPoint dataPoint=new DataPoint()
		dataPoint.setHistoryString(false)

		TimeSeries ts1=new TimeSeries(dataPoint)
		// leere Zeitreihe

		TimeSeries ts2=new TimeSeries(dataPoint)
		ts2.add(ChunkIterator.DEFAULT_CHUNK_LENGTH, 0.0, 0)
		ts2.add(ChunkIterator.DEFAULT_CHUNK_LENGTH+1, 0.5, 1)

		TimeSeries ts3=new TimeSeries(dataPoint)
		ts3.add(2*ChunkIterator.DEFAULT_CHUNK_LENGTH, 1.0, 2)
		ts3.add(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+1, 1.5, 3)
		
		HistoryStorage hs={ DataPoint dp, Date begin, Date end ->
			switch (begin.time) {
				case 0: ts1; break
				case ChunkIterator.DEFAULT_CHUNK_LENGTH: ts2; break
				case 2*ChunkIterator.DEFAULT_CHUNK_LENGTH: ts3; break
				default: fail()
			}
		} as HistoryStorage

		ChunkIterator it=new ChunkIterator(dataPoint, hs,
			new Date(0), new Date(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+2))
		assert it.hasNext()
		assert it.next().value==0.0
		assert it.hasNext()
		assert it.next().value==0.5
		assert it.hasNext()
		assert it.next().value==1.0
		assert it.hasNext()
		assert it.next().value==1.5
		assert !it.hasNext()
	}

	public void testEndWithEmpty() {
		DataPoint dataPoint=new DataPoint()
		dataPoint.setHistoryString(false)

		TimeSeries ts1=new TimeSeries(dataPoint)
		ts1.add(ChunkIterator.DEFAULT_CHUNK_LENGTH, 0.0, 0)
		ts1.add(ChunkIterator.DEFAULT_CHUNK_LENGTH+1, 0.5, 1)

		TimeSeries ts2=new TimeSeries(dataPoint)
		ts2.add(2*ChunkIterator.DEFAULT_CHUNK_LENGTH, 1.0, 2)
		ts2.add(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+1, 1.5, 3)

		TimeSeries ts3=new TimeSeries(dataPoint)
		// leere Zeitreihe
		
		HistoryStorage hs={ DataPoint dp, Date begin, Date end ->
			switch (begin.time) {
				case 0: ts1; break
				case ChunkIterator.DEFAULT_CHUNK_LENGTH: ts2; break
				case 2*ChunkIterator.DEFAULT_CHUNK_LENGTH: ts3; break
				default: fail()
			}
		} as HistoryStorage

		ChunkIterator it=new ChunkIterator(dataPoint, hs,
			new Date(0), new Date(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+2))
		assert it.hasNext()
		assert it.next().value==0.0
		assert it.hasNext()
		assert it.next().value==0.5
		assert it.hasNext()
		assert it.next().value==1.0
		assert it.hasNext()
		assert it.next().value==1.5
		assert !it.hasNext()
	}
	
	public void testAllEmpty() {
		DataPoint dataPoint=new DataPoint()
		dataPoint.setHistoryString(false)

		TimeSeries ts=new TimeSeries(dataPoint)
		
		HistoryStorage hs={ DataPoint dp, Date begin, Date end ->
			ts
		} as HistoryStorage

		ChunkIterator it=new ChunkIterator(dataPoint, hs,
			new Date(0), new Date(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+2))
		assert !it.hasNext()
	}
	
	public void testError() {
		HistoryStorage hs={ DataPoint dp, Date begin, Date end ->
			throw new Exception("Error")
		} as HistoryStorage
	
		shouldFail NoSuchElementException.class, {
			new ChunkIterator(null, hs, 
				new Date(0), new Date(2*ChunkIterator.DEFAULT_CHUNK_LENGTH+2))
		}
	}
}
