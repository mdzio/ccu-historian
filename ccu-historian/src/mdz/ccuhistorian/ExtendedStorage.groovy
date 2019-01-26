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
import groovy.transform.CompileStatic

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
	HashSet<Closure> onReadListener=[]

	@CompileStatic
	public void consume(Event t) throws Exception {
		storage.consume(t)
	}

	@CompileStatic
	public List<DataPoint> getDataPoints() throws Exception {
		fireOnRead()
		return storage.getDataPoints()
	}

	@CompileStatic
	public List<DataPoint> getDataPointsOfInterface(String itfName) throws Exception {
		fireOnRead()
		return storage.getDataPointsOfInterface(itfName)
	}

	@CompileStatic
	public DataPoint getDataPoint(int idx) throws Exception {
		fireOnRead()
		return storage.getDataPoint(idx)
	}

	@CompileStatic
	public DataPoint getDataPoint(DataPointIdentifier id) throws Exception {
		fireOnRead()
		return storage.getDataPoint(id)
	}

	@CompileStatic
	public void createDataPoint(DataPoint dp) throws Exception {
		storage.createDataPoint(dp)
	}

	@CompileStatic
	public void updateDataPoint(DataPoint dp) throws Exception {
		storage.updateDataPoint(dp)
	}

	@CompileStatic
	public void deleteDataPoint(DataPoint dp) throws Exception {
		storage.deleteDataPoint(dp)
	}

	@CompileStatic
	public void normalizeDataPoint(DataPoint dp) {
		storage.normalizeDataPoint(dp)
	}

	@CompileStatic
	public Date getFirstTimestamp(DataPoint dp) throws Exception {
		fireOnRead()
		return storage.getFirstTimestamp(dp)
	}

	@CompileStatic
	public ProcessValue getLast(DataPoint dp) throws Exception {
		fireOnRead()
		return storage.getLast(dp)
	}

	@CompileStatic
	public TimeSeries getTimeSeriesRaw(DataPoint dp, Date begin, Date end) throws Exception {
		fireOnRead()
		return storage.getTimeSeriesRaw(dp, begin, end)
	}

	@CompileStatic
	public TimeSeries getTimeSeries(DataPoint dp, Date begin, Date end) throws Exception {
		fireOnRead()
		return storage.getTimeSeries(dp, begin, end)
	}

	@CompileStatic
	public int getCount(DataPoint dp, Date startTime, Date endTime) throws Exception {
		fireOnRead()
		return storage.getCount(dp, startTime, endTime)
	}

	@CompileStatic
	public int deleteTimeSeries(DataPoint dp, Date startTime, Date endTime) throws Exception {
		fireOnRead()
		return storage.deleteTimeSeries(dp, startTime, endTime)
	}

	@CompileStatic
	public int copyTimeSeries(DataPoint dstDp, DataPoint srcDp, Date startTime, Date endTime, Date newStartTime) throws Exception {
		fireOnRead()
		return storage.copyTimeSeries(dstDp, srcDp, startTime, endTime, newStartTime)
	}

	@CompileStatic
	public int replaceTimeSeries(DataPoint dstDp, Iterable<ProcessValue> srcSeries, Date startTime, Date endTime) throws Exception {
		fireOnRead()
		return storage.replaceTimeSeries(dstDp, srcSeries, startTime, endTime)
	}
	
	@CompileStatic
	public Object transactional(Closure cl) throws Exception {
		fireOnRead()
		return storage.with(cl)
	}
	
	@CompileStatic
	String getConfig(String name) {
		return storage.getConfig(name)
	}
	
	@CompileStatic
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

	@CompileStatic
	private fireOnRead() {
		onReadListener.each { Closure cl -> cl() }
	}
}
