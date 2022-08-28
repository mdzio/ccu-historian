package mdz.hc.persistence

import groovy.transform.CompileStatic
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.hc.timeseries.TimeSeries

@CompileStatic
public class ReadOnlyDecorator implements Storage {

	private Storage storage
	
	public ReadOnlyDecorator(Storage storage) {
		this.storage=storage
	}
	
	@Override
	public List<DataPoint> getDataPoints() throws Exception {
		storage.getDataPoints()
	}

	@Override
	public List<DataPoint> getDataPointsOfInterface(String itfName) throws Exception {
		storage.getDataPointsOfInterface(itfName)
	}

	@Override
	public DataPoint getDataPoint(int idx) throws Exception {
		storage.getDataPoint(idx)
	}

	@Override
	public DataPoint getDataPoint(DataPointIdentifier id) throws Exception {
		storage.getDataPoint(id)
	}

	@Override
	public void createDataPoint(DataPoint dp) throws Exception {
		throw new Exception('Storage is read only')
	}

	@Override
	public void updateDataPoint(DataPoint dp) throws Exception {
		throw new Exception('Storage is read only')
	}

	@Override
	public void deleteDataPoint(DataPoint dp) throws Exception {
		throw new Exception('Storage is read only')
	}

	@Override
	public void normalizeDataPoint(DataPoint dp) {
		storage.normalizeDataPoint(dp)
	}

	@Override
	public Date getFirstTimestamp(DataPoint dp) throws Exception {
		storage.getFirstTimestamp(dp)
	}

	@Override
	public ProcessValue getLast(DataPoint dp) throws Exception {
		storage.getLast(dp)
	}

	@Override
	public Date getFirstBeforeIncl(DataPoint dp, Date ts) {
		storage.getFirstBeforeIncl(dp, ts)
	}

	@Override
	public Date getFirstAfterIncl(DataPoint dp, Date ts) {
		storage.getFirstAfterIncl(dp, ts)
	}

	@Override
	public TimeSeries getTimeSeriesRaw(DataPoint dp, Date begin, Date end) throws Exception {
		storage.getTimeSeriesRaw(dp, begin, end)
	}

	@Override
	public TimeSeries getTimeSeries(DataPoint dp, Date begin, Date end) throws Exception {
		storage.getTimeSeries(dp, begin, end)
	}

	@Override
	public int getCount(DataPoint dp, Date startTime, Date endTime) throws Exception {
		storage.getCount(dp, startTime, endTime)
	}

	@Override
	public int deleteTimeSeries(DataPoint dp, Date startTime, Date endTime) throws Exception {
		throw new Exception('Storage is read only')
	}

	@Override
	public int copyTimeSeries(DataPoint dstDp, DataPoint srcDp, Date startTime, Date endTime, Date newStartTime) {
		throw new Exception('Storage is read only')
	}

	@Override
	public int replaceTimeSeries(DataPoint dstDp, Iterable<ProcessValue> srcSeries, Date startTime, Date endTime) {
		throw new Exception('Storage is read only')
	}

	@Override
	public void consume(Event t) throws Exception {
		throw new Exception('Storage is read only')
	}

	@Override void createBackup(String fileName) {
		storage.createBackup(fileName)
	}
	
	@Override
	public String getConfig(String name) {
		storage.getConfig(name)
	}

	@Override
	public void setConfig(String name, String value) {
		storage.setConfig(name, value)
	}
}
