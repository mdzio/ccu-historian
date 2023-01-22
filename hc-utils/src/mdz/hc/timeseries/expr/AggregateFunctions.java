/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2022 MDZ (info@ccu-historian.de)

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
package mdz.hc.timeseries.expr;

import java.util.Date;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

import mdz.hc.ProcessValue;

public class AggregateFunctions {

	static int combineStates(int first, int second) {
		return Math.min(first & ProcessValue.STATE_QUALITY_MASK, second & ProcessValue.STATE_QUALITY_MASK);
	}

	static int combineStates(ProcessValue first, ProcessValue second) {
		return combineStates(first.getState(), second.getState());
	}

	private static class StateAggregator {
		private final Interval interval;
		private Date first, last;
		private int state;

		public StateAggregator(Interval interval) {
			this.interval = interval;
			state = ProcessValue.STATE_QUALITY_GOOD;
		}

		public void update(ProcessValue pv) {
			if (first == null) {
				first = pv.getTimestamp();
			}
			last = pv.getTimestamp();
			state = combineStates(state, pv.getState());
		}

		public int determineState() {
			// no entries?
			if (first == null) {
				return ProcessValue.STATE_QUALITY_BAD;
			}
			// missing entries at begin or end?
			if ((first != null && !first.equals(interval.getBegin()))
					|| (last != null && !last.equals(interval.getEnd()))) {
				return combineStates(state, ProcessValue.STATE_QUALITY_QUESTIONABLE);
			}
			// state solely based on entries
			return state;
		}

		public Date getFirst() {
			return first;
		}

		public Date getLast() {
			return last;
		}
	}

	private static Function<Interval, ProcessValue> scan(double start, DoubleBinaryOperator op) {
		return interval -> {
			StateAggregator sa = new StateAggregator(interval);
			double aggr = start;
			for (ProcessValue pv : interval) {
				sa.update(pv);
				aggr = op.applyAsDouble(aggr, pv.getDoubleValue());
			}
			int state = sa.determineState();
			if (state == ProcessValue.STATE_QUALITY_BAD) {
				aggr = 0.0d;
			}
			return new ProcessValue(interval.getBegin(), aggr, state);
		};
	}

	public static Function<Interval, ProcessValue> minimum() {
		return scan(Double.MAX_VALUE, Math::min);
	}

	public static Function<Interval, ProcessValue> maximum() {
		return scan(Double.MIN_VALUE, Math::max);
	}

	public static Function<Interval, ProcessValue> average() {
		return interval -> {
			StateAggregator sa = new StateAggregator(interval);
			double aggr = 0.0, lastValue = 0.0;
			long lastTimestamp = 0;
			boolean first = true;
			for (ProcessValue pv : interval) {
				sa.update(pv);
				long ts = pv.getTimestamp().getTime();
				double v = pv.getDoubleValue();
				if (first) {
					first = false;
				} else {
					aggr += (lastValue + v) / 2.0 * (ts - lastTimestamp);
				}
				lastTimestamp = ts;
				lastValue = v;
			}
			int state = sa.determineState();
			// no entries or at least one bad values?
			if (state == ProcessValue.STATE_QUALITY_BAD) {
				return new ProcessValue(interval.getBegin(), 0.0, state);
			}
			long duration = sa.getLast().getTime() - sa.getFirst().getTime();
			// only one entry?
			if (duration == 0) {
				// state will be STATE_QUALITY_QUESTIONABLE
				return new ProcessValue(interval.getBegin(), lastValue, state);
			}
			return new ProcessValue(interval.getBegin(), aggr / duration, state);
		};
	}

	public static Function<Interval, ProcessValue> begin() {
		return interval -> {
			// no entries?
			if (!interval.iterator().hasNext()) {
				return new ProcessValue(interval.getBegin(), 0.0, ProcessValue.STATE_QUALITY_BAD);
			}
			// get first
			ProcessValue pv = interval.iterator().next();
			// purge iterator
			while (interval.iterator().hasNext()) {
				interval.iterator().next();
			}
			// first entry at interval begin?
			if (pv.getTimestamp().equals(interval.getBegin())) {
				return pv;
			}
			// not at interval begin, reduce quality
			return new ProcessValue(interval.getBegin(), pv.getValue(),
					combineStates(pv.getState(), ProcessValue.STATE_QUALITY_QUESTIONABLE));
		};
	}
}
