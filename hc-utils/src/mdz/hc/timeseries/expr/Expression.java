package mdz.hc.timeseries.expr;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import mdz.hc.ProcessValue;

public abstract class Expression implements Reader {

	public static final long TIME_UNIT = 60 * 60 * 1000; // 1 hour

	/**
	 * Wraps a Reader into an Expression.
	 */
	public static Expression from(Reader reader) {
		return new FromReaderExpression(reader);
	}

	/**
	 * Wraps a constant value into an Expression. The time series has the
	 * characteristic HOLD.
	 */
	public static Expression from(double value) {
		return new FromConstantExpression(value);
	}

	/**
	 * Wraps an Iterable into an Expression. Begin and end of reads are ignored. Use
	 * sanitize() to remove out of range timestamps.
	 */
	public static Expression from(Iterable<ProcessValue> iterable, int characteristics) {
		return new FromIterableExpression(iterable, characteristics);
	}

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
		return lift(source -> new UnaryOperatorIterator(source, operator));
	}

	public Expression negative() {
		return unaryOperator(pv -> new ProcessValue(pv.getTimestamp(), -doubleValue(pv), pv.getState()));
	}

	public Expression positive() {
		return this;
	}

	/**
	 * Calls a binary operator to combine two time series. The two time series
	 * should have the characteristic LINEAR. However, time series with the
	 * characteristic HOLD are automatically converted with linear(). The new time
	 * series has entries at time points from both time series. The characteristics
	 * of the first time series are propagated with the characteristic LINEAR set
	 * and HOLD reset.
	 */
	public Expression binaryOperator(Expression other, BinaryOperator<ProcessValue> operator) {
		return new BinaryOperatorExpression(linear(), other.linear(), operator);
	}

	public Expression plus(Expression other) {
		return binaryOperator(other,
				(a, b) -> new ProcessValue(a.getTimestamp(), doubleValue(a) + doubleValue(b), combineStates(a, b)));
	}

	public Expression plus(Number constant) {
		return plus(from(constant.doubleValue()));
	}

	public Expression minus(Expression other) {
		return binaryOperator(other,
				(a, b) -> new ProcessValue(a.getTimestamp(), doubleValue(a) - doubleValue(b), combineStates(a, b)));
	}

	public Expression minus(Number constant) {
		return minus(from(constant.doubleValue()));
	}

	public Expression multiply(Expression other) {
		return binaryOperator(other,
				(a, b) -> new ProcessValue(a.getTimestamp(), doubleValue(a) * doubleValue(b), combineStates(a, b)));
	}

	public Expression multiply(Number constant) {
		return multiply(from(constant.doubleValue()));
	}

	public Expression div(Expression other) {
		return binaryOperator(other, (a, b) -> {
			double result = doubleValue(a) / doubleValue(b);
			if (Double.isNaN(result) || Double.isInfinite(result)) {
				return new ProcessValue(a.getTimestamp(), 0.0D, ProcessValue.STATE_QUALITY_BAD);
			}
			return new ProcessValue(a.getTimestamp(), result, combineStates(a, b));
		});
	}

	public Expression div(Number constant) {
		return div(from(constant.doubleValue()));
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
			BiFunction<T, ProcessValue, Iterator<ProcessValue>> function) {
		return lift(srcIter -> {
			T state = stateFactory.get();
			return new ScanManyIterator<T>(srcIter, state, function);
		});
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
			double v = doubleValue(pv);
			if (v < minimum.doubleValue() || v > maximum.doubleValue()) {
				return new ProcessValue(pv.getTimestamp(), pv.getValue(), ProcessValue.STATE_QUALITY_BAD);
			} else {
				return pv;
			}
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
	 * Converts a time series with the characteristic HOLD (and only then) into a
	 * linear interpolated one. Just before each value change an additional entry
	 * with the value of the previous one is inserted. The characteristic LINEAR is
	 * set and HOLD is reset.
	 */
	public Expression linear() {
		if (!hasCharacteristics(Characteristics.HOLD)) {
			return characteristics(Characteristics.LINEAR, 0);
		}
		class State {
			ProcessValue previous;
		}
		return scanMany(State::new, (state, pv) -> {
			if (state.previous == null) {
				state.previous = pv;
				return Collections.singleton(pv).iterator();
			}
			return Arrays.asList(new ProcessValue(new Date(pv.getTimestamp().getTime() - 1), state.previous.getValue(),
					state.previous.getState()), pv).iterator();
		}).characteristics(Characteristics.LINEAR, Characteristics.HOLD);
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
			double val = doubleValue(pv);
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
					(doubleValue(pv) - doubleValue(state.previous)) * TIME_UNIT / timeSpan,
					combineStates(pv, state.previous));
			state.previous = pv;
			return Collections.singleton(result).iterator();
		}).characteristics(Characteristics.HOLD, Characteristics.LINEAR);
	}

	static int combineStates(ProcessValue first, ProcessValue second) {
		return Math.min(first.getState() & ProcessValue.STATE_QUALITY_MASK,
				second.getState() & ProcessValue.STATE_QUALITY_MASK);
	}

	static double doubleValue(ProcessValue pv) {
		if (!(pv.getValue() instanceof Number)) {
			throw new ClassCastException("Process value does not contain a numeric value");
		}
		return ((Number) pv.getValue()).doubleValue();
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
		double v1 = doubleValue(pv1);
		double v2 = doubleValue(pv2);
		double value = (v2 - v1) * ((double) (t - t1) / (t2 - t1)) + v1;
		int state = combineStates(pv1, pv2);
		return new ProcessValue(at, value, state);
	}
}
