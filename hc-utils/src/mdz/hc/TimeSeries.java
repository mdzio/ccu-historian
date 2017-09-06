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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import mdz.hc.DataPoint;

/**
 * The TimeSeries class was implemented in Java for performance reasons.
 */
public class TimeSeries implements Iterable<ProcessValue> {

	/**
	 * Version optimized for serialization (for example to JSON).
	 */
	public static class CompactTimeSeries {
		private long[] timestamps;
		private Object values; // double[] oder String[]
		private int[] states;
		private DataPoint dataPoint;

		CompactTimeSeries(TimeSeries timeSeries) {
			dataPoint = timeSeries.getDataPoint();
			final int size = timeSeries.size;
			timestamps = Arrays.copyOf(timeSeries.timestamps, size);
			if (timeSeries.isNumeric())
				values = Arrays.copyOf((double[]) timeSeries.values, size);
			else
				values = Arrays.copyOf((String[]) timeSeries.values, size);
			states = Arrays.copyOf(timeSeries.states, size);
		}

		public long[] getTimestamps() {
			return timestamps;
		}

		public Object getValues() {
			return values;
		}

		public int[] getStates() {
			return states;
		}

		public DataPoint getDataPoint() {
			return dataPoint;
		}
	}

	private class TimeSeriesIterator implements Iterator<ProcessValue>, Iterable<ProcessValue> {
		int idx;

		@Override
		public boolean hasNext() {
			return idx < size;
		}

		@Override
		public ProcessValue next() {
			if (idx >= size)
				throw new NoSuchElementException();
			return getAt(idx++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<ProcessValue> iterator() {
			return this;
		}
	}

	private static final int INITIAL_CAPACITY = 1000;

	private long[] timestamps;
	private Object values; // double[] or String[]
	private int[] states;
	private int size; // actual number of entries
	private DataPoint dataPoint;

	/**
	 * The property historyString must return something useful for the passed data point.
	 */
	public TimeSeries(DataPoint dataPoint) {
		this.dataPoint = dataPoint;
		timestamps = new long[INITIAL_CAPACITY];
		values = dataPoint.isHistoryString() ? new String[INITIAL_CAPACITY] : new double[INITIAL_CAPACITY];
		states = new int[INITIAL_CAPACITY];
	}

	public DataPoint getDataPoint() {
		return dataPoint;
	}

	public boolean isNumeric() {
		return values instanceof double[];
	}

	public long[] getTimestamps() {
		return timestamps;
	}

	public Object getValues() {
		return values;
	}

	public int[] getStates() {
		return states;
	}

	public int getSize() {
		return size;
	}

	public ProcessValue getAt(int idx) {
		if (idx >= size)
			throw new ArrayIndexOutOfBoundsException();
		if (isNumeric())
			return new ProcessValue(new Date(timestamps[idx]), ((double[]) values)[idx], states[idx]);
		else
			return new ProcessValue(new Date(timestamps[idx]), ((String[]) values)[idx], states[idx]);
	}

	@Override
	public Iterator<ProcessValue> iterator() {
		return new TimeSeriesIterator();
	}

	public void add(long timestamp, double value, int state) {
		ensureCapacity(size + 1);
		timestamps[size] = timestamp;
		((double[]) values)[size] = value;
		states[size++] = state;
	}

	public void add(long timestamp, String value, int state) {
		ensureCapacity(size + 1);
		timestamps[size] = timestamp;
		((String[]) values)[size] = value;
		states[size++] = state;
	}

	public void add(long timestamp, Object value, int state) {
		ensureCapacity(size + 1);
		timestamps[size] = timestamp;
		if (isNumeric())
			((double[]) values)[size] = (Double) getNormalizedValue(value);
		else
			((String[]) values)[size] = (String) getNormalizedValue(value);
		states[size++] = state;
	}

	public void add(ProcessValue entry) {
		add(entry.getTimestamp().getTime(), entry.getValue(), entry.getState());
	}

	public void add(ResultSet resultSet) throws SQLException {
		if (isNumeric())
			while (resultSet.next())
				add(resultSet.getTimestamp(1).getTime(), resultSet.getDouble(2), resultSet.getInt(3));
		else
			while (resultSet.next())
				add(resultSet.getTimestamp(1).getTime(), resultSet.getString(2), resultSet.getInt(3));
	}

	public void ensureCapacity(int minCapacity) {
		if (minCapacity > timestamps.length) {
			int capacity = timestamps.length;
			capacity += capacity; // Array-Größen verdoppeln
			if (capacity < minCapacity)
				capacity = minCapacity;
			timestamps = Arrays.copyOf(timestamps, capacity);
			if (isNumeric())
				values = Arrays.copyOf((double[]) values, capacity);
			else
				values = Arrays.copyOf((String[]) values, capacity);
			states = Arrays.copyOf(states, capacity);
		}
	}

	/**
	 * A double is returned for numeric data types and booleans, otherwise a string.
	 */
	static public Object getNormalizedValue(Object value) {
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? 1.0D : 0.0D;
		else if (value instanceof Number)
			return ((Number) value).doubleValue();
		else
			return value.toString();
	}

	static public boolean isNormalizedTypeString(Object value) {
		if (value instanceof Boolean || value instanceof Number)
			return false;
		else
			return true;
	}
}
