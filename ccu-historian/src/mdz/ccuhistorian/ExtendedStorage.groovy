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

import groovy.lang.Closure

import java.util.Date
import java.util.List
import java.util.concurrent.atomic.AtomicBoolean

import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.hc.persistence.Storage
import mdz.hc.timeseries.TimeSeries

class ExtendedStorage implements Storage {

	Storage storage
	
	// The onReadListener's are called first.
	HashSet<Closure> onReadListener=[]
	
	// The onRequestFlushListener's are called after the onReadListener's. 
	HashSet<Closure> onRequestFlushListener=[]
	
	public void consume(Event t) throws Exception {
		storage.consume(t)
	}

	public List<DataPoint> getDataPoints() throws Exception {
		fireOnRead()
		return storage.getDataPoints()
	}

	public List<DataPoint> getDataPointsOfInterface(String itfName) throws Exception {
		fireOnRead()
		return storage.getDataPointsOfInterface(itfName)
	}

	public DataPoint getDataPoint(int idx) throws Exception {
		fireOnRead()
		return storage.getDataPoint(idx)
	}

	public DataPoint getDataPoint(DataPointIdentifier id) throws Exception {
		fireOnRead()
		return storage.getDataPoint(id)
	}

	public void createDataPoint(DataPoint dp) throws Exception {
		storage.createDataPoint(dp)
	}

	public void updateDataPoint(DataPoint dp) throws Exception {
		storage.updateDataPoint(dp)
	}

	public void deleteDataPoint(DataPoint dp) throws Exception {
		storage.deleteDataPoint(dp)
	}

	public void normalizeDataPoint(DataPoint dp) {
		storage.normalizeDataPoint(dp)
	}

	public Date getFirstTimestamp(DataPoint dp) throws Exception {
		fireOnRead()
		return storage.getFirstTimestamp(dp)
	}

	public ProcessValue getLast(DataPoint dp) throws Exception {
		fireOnRead()
		fireOnRequestFlush(dp.id)
		return storage.getLast(dp)
	}

	public Date getFirstBeforeIncl(DataPoint dp, Date ts) {
		fireOnRead()
		return storage.getFirstBeforeIncl(dp, ts)
	}

	public Date getFirstAfterIncl(DataPoint dp, Date ts) {
		fireOnRead()
		return storage.getFirstAfterIncl(dp, ts)
	}

	public TimeSeries getTimeSeriesRaw(DataPoint dp, Date begin, Date end) throws Exception {
		fireOnRead()
		fireOnRequestFlush(dp.id)
		return storage.getTimeSeriesRaw(dp, begin, end)
	}

	public TimeSeries getTimeSeries(DataPoint dp, Date begin, Date end) throws Exception {
		fireOnRead()
		fireOnRequestFlush(dp.id)
		return storage.getTimeSeries(dp, begin, end)
	}

	public int getCount(DataPoint dp, Date startTime, Date endTime) throws Exception {
		fireOnRead()
		return storage.getCount(dp, startTime, endTime)
	}

	public int deleteTimeSeries(DataPoint dp, Date startTime, Date endTime) throws Exception {
		fireOnRead()
		return storage.deleteTimeSeries(dp, startTime, endTime)
	}

	public int insertTimeSeries(TimeSeries ts) throws Exception {
		return storage.insertTimeSeries(ts)	
	}
	
	public int copyTimeSeries(DataPoint dstDp, DataPoint srcDp, Date startTime, Date endTime, Date newStartTime) throws Exception {
		fireOnRead()
		return storage.copyTimeSeries(dstDp, srcDp, startTime, endTime, newStartTime)
	}

	public int replaceTimeSeries(DataPoint dstDp, Iterable<ProcessValue> srcSeries, Date startTime, Date endTime) throws Exception {
		fireOnRead()
		return storage.replaceTimeSeries(dstDp, srcSeries, startTime, endTime)
	}
	
	public void createBackup(String fileName) {
		fireOnRead()
		storage.createBackup(fileName)
	}
		
	String getConfig(String name) {
		return storage.getConfig(name)
	}
	
	void setConfig(String name, String value) {
		storage.setConfig(name, value)
	}

	/**
	 * Forward reading of configuration properties.
	 * @param propertyName
	 * @return
	 */
	public Object propertyMissing(String propertyName) {
		storage."$propertyName"
	}

	private fireOnRead() {
		onReadListener.each { Closure cl -> cl() }
	}
	
	private fireOnRequestFlush(DataPointIdentifier id) {
		onRequestFlushListener.each { Closure cl -> cl(id) }
	}
}
