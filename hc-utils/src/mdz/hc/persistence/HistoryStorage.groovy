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
package mdz.hc.persistence

import mdz.eventprocessing.Consumer
import mdz.hc.DataPoint
import mdz.hc.Event
import mdz.hc.ProcessValue
import mdz.hc.timeseries.TimeSeries

public interface HistoryStorage extends Consumer<Event> {

	public Date getFirstTimestamp(DataPoint dp) throws Exception
	public ProcessValue getLast(DataPoint dp) throws Exception
	
	public Date getFirstBeforeIncl(DataPoint dp, Date ts) throws Exception
	public Date getFirstAfterIncl(DataPoint dp, Date ts) throws Exception

	public TimeSeries getTimeSeriesRaw(DataPoint dp, Date begin, Date end) throws Exception
	public TimeSeries getTimeSeries(DataPoint dp, Date begin, Date end) throws Exception

	public int getCount(DataPoint dp, Date startTime, Date endTime) throws Exception

	public int deleteTimeSeries(DataPoint dp, Date startTime, Date endTime) throws Exception
	public int copyTimeSeries(DataPoint dstDp, DataPoint srcDp, Date startTime, Date endTime, Date newStartTime) throws Exception

	public int replaceTimeSeries(DataPoint dstDp, Iterable<ProcessValue> srcSeries, Date startTime, Date endTime) throws Exception
}
