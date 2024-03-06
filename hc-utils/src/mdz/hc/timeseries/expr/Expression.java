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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import mdz.hc.ProcessValue;

public abstract class Expression implements Reader {

	public static final long TIME_UNIT = 60 * 60 * 1000; // 1 hour

	/**
	 * Lifts an iterator operator to an expression.
	 */
	public Expression lift(UnaryOperator<Iterator<ProcessValue>> operator) {
		return new LiftExpression(this, operator);
	}

	/**
	 * Filters the time series entries.
	 */
	public Expression filter(Predicate<ProcessValue> predicate) {
		return lift(source -> new FilterIterator(predicate, source));
	}

	/**
	 * Calls an unary operator on each time series entry.
	 */
	public Expression unaryOperator(UnaryOperator<ProcessValue> operator) {
		return lift(source -> new MapIterator(source, operator));
	}

	/**
	 * This is a convenience function for unaryOperator. The passed function works
	 * directly on the double value. NaN, Infinity and exceptions are automatically
	 * converted into a ProcessValue with status BAD.
	 */
	public Expression map(DoubleUnaryOperator operator) {
		return unaryOperator((ProcessValue pv) -> {
			try {
				double rv = operator.applyAsDouble(pv.getDoubleValue());
				if (Double.isNaN(rv) || Double.isInfinite(rv)) {
					throw new Exception("Invalid double value");
				}
				return new ProcessValue(pv.getTimestamp(), rv, pv.getState());
			} catch (Exception e) {
				return new ProcessValue(pv.getTimestamp(), 0.0D, ProcessValue.STATE_QUALITY_BAD);
			}
		});
	}

	/**
	 * Simplification of unaryOperator() to generate a time series via a function
	 * that calculates a process value depending on the timestamp.
	 * 
	 * Example of a sine wave over a period of one day:
	 * Expressions.hourly().generate(t -> Math.sin(2 * Math.PI * t.time / 1000 / 60
	 * / 60 / 24))
	 */
	public Expression generate(Function<Date, Double> operator) {
		return unaryOperator(pv -> new ProcessValue(pv.getTimestamp(), operator.apply(pv.getTimestamp()),
				ProcessValue.STATE_QUALITY_GOOD));
	}

	public Expression negative() {
		return unaryOperator(pv -> new ProcessValue(pv.getTimestamp(), -pv.getDoubleValue(), pv.getState()));
	}

	public Expression positive() {
		return this;
	}

	/**
	 * Calls a binary operator to combine two time series. The two time series
	 * should have the characteristic LINEAR. However, time series with the
	 * characteristic HOLD are automatically converted with linear(). The new time
	 * series has entries at time points from both time series.
	 * 
	 * The characteristics of the first time series are propagated with the
	 * characteristic LINEAR set and HOLD reset.
	 */
	public Expression binaryOperator(Expression other, BinaryOperator<ProcessValue> operator) {
		return new BinaryOperatorExpression(linear(), other.linear(), operator);
	}

	/**
	 * This is a convenience function for binaryOperator. The passed function works
	 * directly on the double values. NaN, Infinity and exceptions are automatically
	 * converted into a ProcessValue with status BAD.
	 */
	public Expression combine(Expression other, DoubleBinaryOperator operator) {
		return binaryOperator(other, (a, b) -> {
			try {
				double rv = operator.applyAsDouble(a.getDoubleValue(), b.getDoubleValue());
				if (Double.isNaN(rv) || Double.isInfinite(rv)) {
					throw new Exception("Invalid double value");
				}
				return new ProcessValue(a.getTimestamp(), rv, AggregateFunctions.combineStates(a, b));
			} catch (Exception e) {
				return new ProcessValue(a.getTimestamp(), 0.0D, ProcessValue.STATE_QUALITY_BAD);
			}
		});
	}

	public Expression plus(Expression other) {
		return binaryOperator(other, (a, b) -> new ProcessValue(a.getTimestamp(),
				a.getDoubleValue() + b.getDoubleValue(), AggregateFunctions.combineStates(a, b)));
	}

	public Expression plus(Number constant) {
		return unaryOperator(
				pv -> new ProcessValue(pv.getTimestamp(), pv.getDoubleValue() + constant.doubleValue(), pv.getState()));
	}

	public Expression minus(Expression other) {
		return binaryOperator(other, (a, b) -> new ProcessValue(a.getTimestamp(),
				a.getDoubleValue() - b.getDoubleValue(), AggregateFunctions.combineStates(a, b)));
	}

	public Expression minus(Number constant) {
		return unaryOperator(
				pv -> new ProcessValue(pv.getTimestamp(), pv.getDoubleValue() - constant.doubleValue(), pv.getState()));
	}

	public Expression multiply(Expression other) {
		return binaryOperator(other, (a, b) -> new ProcessValue(a.getTimestamp(),
				a.getDoubleValue() * b.getDoubleValue(), AggregateFunctions.combineStates(a, b)));
	}

	public Expression multiply(Number constant) {
		return unaryOperator(
				pv -> new ProcessValue(pv.getTimestamp(), pv.getDoubleValue() * constant.doubleValue(), pv.getState()));
	}

	public Expression div(Expression other) {
		return binaryOperator(other, (a, b) -> {
			double result = a.getDoubleValue() / b.getDoubleValue();
			if (Double.isNaN(result) || Double.isInfinite(result)) {
				return new ProcessValue(a.getTimestamp(), 0.0D, ProcessValue.STATE_QUALITY_BAD);
			}
			return new ProcessValue(a.getTimestamp(), result, AggregateFunctions.combineStates(a, b));
		});
	}

	public Expression div(Number constant) {
		return unaryOperator(pv -> {
			double result = pv.getDoubleValue() / constant.doubleValue();
			if (Double.isNaN(result) || Double.isInfinite(result)) {
				return new ProcessValue(pv.getTimestamp(), 0.0D, ProcessValue.STATE_QUALITY_BAD);
			}
			return new ProcessValue(pv.getTimestamp(), result, pv.getState());
		});
	}

	/**
	 * The result time series has the value 1.0 if the value of this time series is
	 * greater than zero, otherwise the value 0.0. The status is copied.
	 */
	public Expression greaterThanZero() {
		if (hasCharacteristics(Characteristics.HOLD)) {
			return unaryOperator(pv -> {
				if ((pv.getState() & ProcessValue.STATE_QUALITY_MASK) == ProcessValue.STATE_QUALITY_BAD) {
					return new ProcessValue(pv.getTimestamp(), 0.0D, ProcessValue.STATE_QUALITY_BAD);
				}
				return new ProcessValue(pv.getTimestamp(), pv.getDoubleValue() > 0.0 ? 1.0D : 0.0D, pv.getState());
			});
		} else {
			class State {
				ProcessValue prevPV;
			}
			return linear().scanMany(State::new, (state, pv) -> {
				ArrayList<ProcessValue> res = new ArrayList<>(2);
				double v = pv.getDoubleValue();
				// not the first entry?
				if (state.prevPV != null) {
					double vp = state.prevPV.getDoubleValue();
					if ((vp > 0.0 && v < 0.0) || (vp < 0.0 && v > 0.0)) {
						// zero crossing
						ProcessValue zpv = zeroCrossing(state.prevPV, pv);
						if (zpv != null) {
							zpv.setValue(v > 0.0 ? 1.0D : 0.0D);
							res.add(zpv);
						}
					} else if (vp == 0.0 && v > 0.0) {
						// special case: increasing from zero
						Date ti = new Date(state.prevPV.getTimestamp().getTime() + 1);
						if (ti.before(pv.getTimestamp())) {
							ProcessValue zpv = new ProcessValue(ti, 1.0D,
									AggregateFunctions.combineStates(state.prevPV, pv));
							res.add(zpv);
						}
					}
				}
				// add always entry for current PV
				res.add(new ProcessValue(pv.getTimestamp(), v > 0.0 ? 1.0D : 0.0D, pv.getState()));
				state.prevPV = pv;
				return res.iterator();
			}).characteristics(Characteristics.HOLD, Characteristics.LINEAR);
		}
	}

	/**
	 * The result time series has the value 1.0 if the value of this time series is
	 * greater than the value of the specified time series, otherwise the value 0.0.
	 * The status is combined.
	 */
	public Expression greaterThan(Expression other) {
		return minus(other).greaterThanZero();
	}

	/**
	 * The result time series has the value 1.0 if the value of this time series is
	 * greater than the specified constant value, otherwise the value 0.0. The
	 * status is copied.
	 */
	public Expression greaterThan(Number constant) {
		return minus(constant).greaterThanZero();
	}

	/**
	 * The result time series has the value 1.0 if the value of this time series is
	 * less than zero, otherwise the value 0.0. The status is copied.
	 */
	public Expression lessThanZero() {
		return negative().greaterThanZero();
	}

	/**
	 * The result time series has the value 1.0 if the value of this time series is
	 * less than the value of the specified time series, otherwise the value 0.0.
	 * The status is combined.
	 */
	public Expression lessThan(Expression other) {
		return minus(other).lessThanZero();
	}

	/**
	 * The result time series has the value 1.0 if the value of this time series is
	 * less than the specified constant value, otherwise the value 0.0. The status
	 * is copied.
	 */
	public Expression lessThan(Number constant) {
		return minus(constant).lessThanZero();
	}

	/**
	 * All negative values of the time series are clipped at zero.
	 * 
	 * The time series should have the characteristic LINEAR. However, time series
	 * with the characteristic HOLD are automatically converted with linear(). The
	 * characteristics of the time series are propagated with the characteristic
	 * LINEAR set and HOLD reset.
	 */
	public Expression clipZero() {
		class State {
			ProcessValue prevPV;
		}
		return linear().scanMany(State::new, (state, pv) -> {
			ArrayList<ProcessValue> res = new ArrayList<>(2);
			double v = pv.getDoubleValue();
			// not the first entry?
			if (state.prevPV != null) {
				double vp = state.prevPV.getDoubleValue();
				// crossing zero?
				if ((vp > 0.0 && v < 0.0) || (vp < 0.0 && v > 0.0)) {
					// add entry at zero
					ProcessValue zpv = zeroCrossing(state.prevPV, pv);
					if (zpv != null) {
						res.add(zpv);
					}
				}
			}
			// add clipped value
			if (v < 0.0) {
				res.add(new ProcessValue(pv.getTimestamp(), 0.0D, pv.getState()));
			} else {
				res.add(pv);
			}
			state.prevPV = pv;
			return res.iterator();
		});
	}

	/**
	 * The values of the time series are limited to the specified range.
	 * 
	 * The time series should have the characteristic LINEAR. However, time series
	 * with the characteristic HOLD are automatically converted with linear(). The
	 * characteristics of the time series are propagated with the characteristic
	 * LINEAR set and HOLD reset.
	 */
	public Expression clip(Number limitLow, Number limitHigh) {
		return this.minus(limitLow).clipZero().plus(limitLow.doubleValue() - limitHigh.doubleValue()).negative()
				.clipZero().negative().plus(limitHigh);
	}

	/**
	 * The time series is divided into intervals. The time stamps for the intervals
	 * are taken from the time series 'intervals'. For each interval, a function is
	 * called that aggregates the time series within the interval to a process
	 * value. Before processing, linear() is called on this time series. The
	 * characteristic HOLD is set and LINEAR is unset on the resulting time series.
	 */
	public Expression aggregate(Expression intervals, Function<Interval, ProcessValue> function) {
		return new AggregateExpression(this, intervals, function);
	}

	/**
	 * Calculates the minimum of each interval. Short cut for aggregate(intervals,
	 * AggregateFunctions.minimum()).
	 */
	public Expression minimum(Expression intervals) {
		return new AggregateExpression(this, intervals, AggregateFunctions.minimum());
	}

	/**
	 * Calculates the minimum over the entire requested time range. Short cut for
	 * minimum(Expressions.entire()).
	 */
	public Expression minimum() {
		return new AggregateExpression(this, Expressions.entire(), AggregateFunctions.minimum());
	}

	/**
	 * Calculates the maximum of each interval. Short cut for aggregate(intervals,
	 * AggregateFunctions.maximum()).
	 */
	public Expression maximum(Expression intervals) {
		return new AggregateExpression(this, intervals, AggregateFunctions.maximum());
	}

	/**
	 * Calculates the maximum over the entire requested time range. Short cut for
	 * maximum(Expressions.entire()).
	 */
	public Expression maximum() {
		return new AggregateExpression(this, Expressions.entire(), AggregateFunctions.maximum());
	}

	/**
	 * Calculates the average of each interval. Short cut for aggregate(intervals,
	 * AggregateFunctions.average()).
	 */
	public Expression average(Expression intervals) {
		return new AggregateExpression(this, intervals, AggregateFunctions.average());
	}

	/**
	 * Calculates the average over the entire requested time range. Short cut for
	 * average(Expressions.entire()).
	 */
	public Expression average() {
		return new AggregateExpression(this, Expressions.entire(), AggregateFunctions.average());
	}

	/**
	 * Calculates the value at the beginning of each interval. Short cut for
	 * aggregate(intervals, AggregateFunctions.begin()).
	 */
	public Expression resample(Expression intervals) {
		return new AggregateExpression(this, intervals, AggregateFunctions.begin());
	}

	/**
	 * Calculates the number of time series entries within each interval. Short cut
	 * for aggregate(intervals, AggregateFunctions.count()).
	 */
	public Expression count(Expression intervals) {
		return new AggregateExpression(this, intervals, AggregateFunctions.count());
	}

	/**
	 * Calculates the number of time series entries within the entire requested time
	 * range. Short cut for count(Expressions.entire()).
	 */
	public Expression count() {
		return new AggregateExpression(this, Expressions.entire(), AggregateFunctions.count());
	}

	/**
	 * Returns the derivative of the time series. The time unit is one hour. The
	 * returned time series has one entry less. Characteristic HOLD is set and
	 * LINEAR is reset.
	 */
	public Expression differentiate() {
		class State {
			ProcessValue previous;
		}
		return scanMany(State::new, (state, pv) -> {
			if (state.previous == null) {
				state.previous = pv;
				return Collections.emptyIterator();
			}
			long timeSpan = pv.getTimestamp().getTime() - state.previous.getTimestamp().getTime();
			if (timeSpan <= 0) {
				return Collections.singleton(new ProcessValue(pv.getTimestamp(), 0.0, ProcessValue.STATE_QUALITY_BAD))
						.iterator();
			}
			ProcessValue result = new ProcessValue(state.previous.getTimestamp(),
					(pv.getDoubleValue() - state.previous.getDoubleValue()) * TIME_UNIT / timeSpan,
					AggregateFunctions.combineStates(pv, state.previous));
			state.previous = pv;
			return Collections.singleton(result).iterator();
		}).characteristics(Characteristics.HOLD, Characteristics.LINEAR);
	}

	/**
	 * Returns the integral of the time series. The time unit is one hour. The time
	 * series should have the characteristic LINEAR. However, a time series with the
	 * characteristic HOLD is automatically converted with linear(). Characteristic
	 * LINEAR is set and HOLD is reset.
	 */
	public Expression integrate() {
		class State {
			ProcessValue prev;
			double sumVal;
			int sumStat;
		}
		return linear().scan(State::new, (st, pv) -> {
			if (st.prev == null) {
				st.sumVal = 0.0;
				st.sumStat = pv.getState();
			} else {
				st.sumStat = AggregateFunctions.combineStates(st.sumStat, pv.getState());
				// Once the status is BAD, it will remain BAD!
				if ((st.sumStat & ProcessValue.STATE_QUALITY_MASK) == ProcessValue.STATE_QUALITY_BAD) {
					st.sumVal = 0.0;
				} else {
					long timeSpan = pv.getTimestamp().getTime() - st.prev.getTimestamp().getTime();
					st.sumVal += (st.prev.getDoubleValue() + pv.getDoubleValue()) / 2.0 * timeSpan / TIME_UNIT;
				}
			}
			st.prev = pv;
			return new ProcessValue(pv.getTimestamp(), st.sumVal, st.sumStat);
		});
	}

	/**
	 * The time series is shifted in time. If the time duration is positive, an
	 * older time range is requested and the timestamps are shifted to the current
	 * time range. (The curve is shifted to the right in a trend display, for
	 * example). The time duration is indicated in hours.
	 */
	public Expression shift(Number duration) {
		Expression src = this;
		return new Expression() {
			@Override
			public Iterator<ProcessValue> read(Date begin, Date end) {
				long durationMs = (long) (duration.doubleValue() * 60.0 * 60.0 * 1000.0);
				Iterator<ProcessValue> srcIt = src.read(new Date(begin.getTime() - durationMs),
						new Date(end.getTime() - durationMs));

				return new Iterator<ProcessValue>() {
					@Override
					public boolean hasNext() {
						return srcIt.hasNext();
					}

					@Override
					public ProcessValue next() {
						ProcessValue pv = srcIt.next();
						return new ProcessValue(new Date(pv.getTimestamp().getTime() + durationMs), pv.getValue(),
								pv.getState());
					}
				};
			}

			@Override
			public int getCharacteristics() {
				return src.getCharacteristics();
			}
		};
	}

	/**
	 * Heating degree day (HDD) is a measurement designed to quantify the demand for
	 * energy needed to heat a building. This function should be used with the
	 * outside air temperature. A value is calculated for each day. The heating
	 * limit must be specified (e.g. 15°C).
	 */
	public Expression hdd(Number heatingLimit) {
		return average(Expressions.daily()).unaryOperator(pv -> {
			double v = heatingLimit.doubleValue() - pv.getDoubleValue();
			return v >= 0 ? new ProcessValue(pv.getTimestamp(), v, pv.getState())
					: new ProcessValue(pv.getTimestamp(), 0.0D, pv.getState());
		});
	}

	/**
	 * Heating degree day (HDD) with a heating limit of 15°C.
	 */
	public Expression hdd() {
		return hdd(15);
	}

	/**
	 * Corrects counter resets on meters. The characteristic COUNTER is set.
	 */
	public Expression counter() {
		class State {
			boolean first = true;
			double accu, base, previous;
		}
		return scan(State::new, (state, pv) -> {
			double val = pv.getDoubleValue();
			if (state.first) {
				state.first = false;
				state.base = val;
				state.previous = val;
				return new ProcessValue(pv.getTimestamp(), 0.0d, pv.getState());
			}
			// overflow?
			if (val - state.previous < 0) {
				state.accu += state.previous - state.base;
				state.base = val;
			}
			state.previous = val;
			return new ProcessValue(pv.getTimestamp(), val - state.base + state.accu, pv.getState());
		}).characteristics(Characteristics.COUNTER, 0);
	}

	/**
	 * Converts a time series with the characteristic HOLD (and only then) into a
	 * linear interpolated one. Just before each value change and at the end of the
	 * requested time range an additional entry with the value of the previous one
	 * is inserted. The characteristic LINEAR is set and HOLD is reset.
	 */
	public Expression linear() {
		if (!hasCharacteristics(Characteristics.HOLD)) {
			return characteristics(Characteristics.LINEAR, 0);
		}
		return new LinearExpression(this);
	}

	/**
	 * The last process value is repeated at the end of the query interval.
	 */
	public Expression holdLast() {
		return new HoldLastExpression(this);
	}

	/**
	 * Cleans invalid timestamps. The timestamps must be within the queried time
	 * range and strictly monotonically ascending. All others are removed.
	 */
	public Expression sanitize() {
		return new SanitizeExpression(this);
	}

	/**
	 * Sets the quality of values outside the specified range to BAD.
	 */
	public Expression validate(Number minimum, Number maximum) {
		return unaryOperator(pv -> {
			double v = pv.getDoubleValue();
			if (v < minimum.doubleValue() || v > maximum.doubleValue()) {
				return new ProcessValue(pv.getTimestamp(), pv.getValue(), ProcessValue.STATE_QUALITY_BAD);
			} else {
				return pv;
			}
		});
	}

	/**
	 * The state of the time series is set to BAD depending on a second time series.
	 * If the second time series has the state BAD or a value greater than 0, then
	 * the state in the original time series is also set to BAD. The value is
	 * changed to 0.0, if the resulting state is BAD.
	 */
	public Expression badIf(Expression condition) {
		return binaryOperator(condition, (src, cond) -> {
			int state = src.getState();
			Object value = src.getValue();
			if ((cond.getState() & ProcessValue.STATE_QUALITY_MASK) == ProcessValue.STATE_QUALITY_BAD
					|| cond.getDoubleValue() > 0.0) {
				state = (state & ~ProcessValue.STATE_QUALITY_MASK) | ProcessValue.STATE_QUALITY_BAD;
				value = 0.0D;
			}
			return new ProcessValue(src.getTimestamp(), value, state);
		});
	}

	/**
	 * Replaces process values with quality BAD to the specified replacement value
	 * and quality QUESTIONABLE.
	 */
	public Expression replaceBad(Number replacement) {
		return unaryOperator(pv -> {
			if ((pv.getState() & ProcessValue.STATE_QUALITY_MASK) == ProcessValue.STATE_QUALITY_BAD) {
				pv = new ProcessValue(pv.getTimestamp(), replacement, ProcessValue.STATE_QUALITY_QUESTIONABLE);
			}
			return pv;
		});
	}

	/**
	 * Removes process values with quality BAD from the time series.
	 */
	public Expression removeBad() {
		return scanMany(() -> null, (notUsed, pv) -> {
			if ((pv.getState() & ProcessValue.STATE_QUALITY_MASK) == ProcessValue.STATE_QUALITY_BAD) {
				return Collections.emptyIterator();
			}
			return Collections.singleton(pv).iterator();
		});
	}

	/**
	 * Changes the characteristics of a time series.
	 */
	public Expression characteristics(int setMask, int resetMask) {
		return new CharacteristicsExpression(this, resetMask, setMask);
	}

	/**
	 * A function is called for each time series entry with a modifiable state. The
	 * returned values form the new time series.
	 */
	public <T> Expression scan(Supplier<T> stateFactory, BiFunction<T, ProcessValue, ProcessValue> function) {
		return lift(srcIter -> {
			T state = stateFactory.get();
			return new ScanIterator<T>(srcIter, function, state);
		});
	}

	/**
	 * A function is called for each time series entry with a modifiable state. The
	 * concatenation of the returned iterators form the new time series.
	 */
	public <T> Expression scanMany(Supplier<T> stateFactory,
			BiFunction<T, ProcessValue, Iterator<ProcessValue>> mapFunc) {
		return lift(srcIter -> {
			T state = stateFactory.get();
			return new MapManyIterator(srcIter, pv -> mapFunc.apply(state, pv), null);
		});
	}

	/**
	 * onEntry is called for each time series entry with a modifiable state. If
	 * there are no more entries, endFunc is called. The concatenation of the
	 * returned iterators form the new time series.
	 */
	public <T> Expression scanMany(Supplier<T> stateFactory,
			BiFunction<T, ProcessValue, Iterator<ProcessValue>> mapFunc, Function<T, Iterator<ProcessValue>> endFunc) {
		return lift(srcIter -> {
			T state = stateFactory.get();
			return new MapManyIterator(srcIter, pv -> mapFunc.apply(state, pv), () -> endFunc.apply(state));
		});
	}

	static ProcessValue interpolate(ProcessValue pv1, ProcessValue pv2, Date at) {
		long t1 = pv1.getTimestamp().getTime();
		long t2 = pv2.getTimestamp().getTime();
		long t = at.getTime();
		if (t < t1 || t > t2) {
			throw new IllegalArgumentException("Timestamp out of range");
		}
		if (t1 == t2) {
			return pv1;
		}
		double v1 = pv1.getDoubleValue();
		double v2 = pv2.getDoubleValue();
		double value = (v2 - v1) * ((double) (t - t1) / (t2 - t1)) + v1;
		int state = AggregateFunctions.combineStates(pv1, pv2);
		return new ProcessValue(at, value, state);
	}

	static ProcessValue zeroCrossing(ProcessValue pv1, ProcessValue pv2) {
		long t1 = pv1.getTimestamp().getTime();
		long t2 = pv2.getTimestamp().getTime();
		double v1 = pv1.getDoubleValue();
		double v2 = pv2.getDoubleValue();
		double _t = t1 - v1 * (t2 - t1) / (v2 - v1);
		// no zero crossing?
		if (!Double.isFinite(_t)) {
			return null;
		}
		long t = (long) _t;
		// timestamp not between pv1 and pv2?
		if (t <= t1 || t >= t2) {
			return null;
		}
		return new ProcessValue(new Date(t), 0.0D, AggregateFunctions.combineStates(pv1, pv2));
	}
}
