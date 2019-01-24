package mdz.hc.timeseries;

import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

import mdz.hc.ProcessValue;

/**
 * CombineLatestIterator combines multiple time series iterators into a single
 * iterator. At every time stamp of every time series an entry is generated. The
 * ProcessValue's of all iterators is then aggregated into a single ProcessValue
 * through a combiner function.
 */
public class CombineLatestIterator implements Iterator<ProcessValue> {

	private Iterator<ProcessValue>[] iterators;
	private Function<ProcessValue[], ProcessValue> combiner;
	private ProcessValue[] iteratorValues;
	private ProcessValue[] currentValues;
	private boolean consumed;

	/**
	 * Create a CombineLatestIterator with the specified combiner.
	 * 
	 * @param iterators
	 * @param combiner
	 */
	public CombineLatestIterator(Iterator<ProcessValue>[] iterators, Function<ProcessValue[], ProcessValue> combiner) {
		Objects.requireNonNull(iterators, "CombineLatestIterator: iterators must not be null");
		Objects.requireNonNull(combiner, "CombineLatestIterator: combiner must not be null");
		this.iterators = iterators;
		this.combiner = combiner;
		iteratorValues = new ProcessValue[iterators.length];
		currentValues = new ProcessValue[iterators.length];

		// fill with start values
		for (int i = 0; i < iterators.length; i++) {
			if (iterators[i].hasNext()) {
				iteratorValues[i] = iterators[i].next();
				Objects.requireNonNull(iteratorValues[i], "CombineLatestIterator: null elements not allowed");
			}
		}

		updateCurrentValues();
	}

	/**
	 * Create a CombineLatestIterator with a holding value combiner. The created
	 * iterator returns ProcessValue's with an Object[] as value, which contains the
	 * current hold values of the source iterators.
	 * 
	 * @param iterators
	 */
	public CombineLatestIterator(Iterator<ProcessValue>[] iterators) {
		this(iterators, processValues -> {
			long tsAggr = Long.MIN_VALUE;
			Object[] valueAggr = new Object[processValues.length];
			int qualityAggr = ProcessValue.STATE_QUALITY_GOOD;
			for (int i = 0; i < processValues.length; i++) {
				if (processValues[i] != null) {
					// latest timestamp
					long ts = processValues[i].getTimestamp().getTime();
					if (ts > tsAggr) {
						tsAggr = ts;
					}
					// worst quality
					int quality = processValues[i].getState() & ProcessValue.STATE_QUALITY_MASK;
					if (quality < qualityAggr) {
						qualityAggr = quality;
					}
					// values as array
					valueAggr[i] = processValues[i].getValue();
				}
			}
			return new ProcessValue(new Date(tsAggr), valueAggr, qualityAggr);
		});
	}

	@Override
	public boolean hasNext() {
		return !consumed;
	}

	@Override
	public ProcessValue next() {
		if (consumed) {
			throw new NoSuchElementException();
		}
		ProcessValue pv = combiner.apply(currentValues);
		updateCurrentValues();
		return pv;
	}

	private void updateCurrentValues() {
		// find earliest timestamp
		long earliestTimestamp = Long.MAX_VALUE;
		for (int i = 0; i < iteratorValues.length; i++) {
			if (iteratorValues[i] != null) {
				long ts = iteratorValues[i].getTimestamp().getTime();
				if (ts < earliestTimestamp) {
					earliestTimestamp = ts;
				}
			}
		}

		// not found?
		if (earliestTimestamp == Long.MAX_VALUE) {
			consumed = true;
			return;
		}

		// update current values and advance iterators
		for (int i = 0; i < iteratorValues.length; i++) {
			if (iteratorValues[i] != null) {
				long ts = iteratorValues[i].getTimestamp().getTime();
				if (ts == earliestTimestamp) {
					currentValues[i] = iteratorValues[i];
					if (iterators[i].hasNext()) {
						iteratorValues[i] = iterators[i].next();
						Objects.requireNonNull(iteratorValues[i], "CombineLatestIterator: null elements not allowed");
					} else {
						iteratorValues[i] = null;
					}
				}
			}
		}
		consumed = false;
	}
}
