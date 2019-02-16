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
package mdz.hc.timeseries;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;

import mdz.hc.DataPoint;
import mdz.hc.ProcessValue;
import mdz.hc.persistence.HistoryStorage;

public class ChunkIterator implements Iterator<ProcessValue> {

	private static final long DEFAULT_CHUNK_LENGTH = 30l * 24 * 60 * 60 * 1000; // 30 days in milliseconds

	private final DataPoint dataPoint;
	private final HistoryStorage historyStorage;
	private final Date end;
	private Date begin;
	private Iterator<ProcessValue> chunkIterator;
	private long chunkLength = DEFAULT_CHUNK_LENGTH;

	public ChunkIterator(DataPoint dataPoint, HistoryStorage historyStorage, Date begin, Date end)
			throws Exception {
		this.dataPoint = dataPoint;
		this.historyStorage = historyStorage;
		this.begin = begin;
		this.end = end;
		nextChunk();
	}

	public void setChunkLength(long chunkLength) {
		this.chunkLength = chunkLength;
	}

	private void nextChunk() {
		Date chunkEnd = new Date(begin.getTime() + chunkLength);
		if (chunkEnd.after(end))
			chunkEnd = end;
		try {
			chunkIterator = historyStorage.getTimeSeriesRaw(dataPoint, begin, chunkEnd).iterator();
		} catch (Exception e) {
			throw new NoSuchElementException("Retrieving of time series failed"
					+ (e.getMessage() != null ? ": " + e.getMessage() : ""));
		}
		begin = chunkEnd;
	}

	private Iterator<ProcessValue> currentOrNextIterator() {
		while (chunkIterator.hasNext() == false && begin.before(end))
			nextChunk();
		return chunkIterator;
	}

	@Override
	public boolean hasNext() {
		return currentOrNextIterator().hasNext();
	}

	@Override
	public ProcessValue next() {
		return currentOrNextIterator().next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
