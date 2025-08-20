package mdz.hc.timeseries.expr;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import mdz.hc.ProcessValue;

class LinearExpression extends Expression {

	private Expression source;

	public LinearExpression(Expression source) {
		this.source = source;
	}

	@Override
	public int getCharacteristics() {
		return (source.getCharacteristics() | Characteristics.LINEAR) & ~Characteristics.HOLD;
	}

	@Override
	public Iterator<ProcessValue> read(Date begin, Date end) {
		class State {
			ProcessValue previous;
		}
		return new ScanManyIterator<State>(source.read(begin, end), new State(), (state, pv) -> {
			Iterator<ProcessValue> result = null;
			if (state.previous == null) {
				result = Collections.singleton(pv).iterator();
			} else {
				long directlyBefore = pv.getTimestamp().getTime() - 1;
				if (state.previous.getTimestamp().getTime() == directlyBefore) {
					result = Collections.singleton(pv).iterator();
				} else {
					result = Arrays.asList(new ProcessValue(new Date(directlyBefore), state.previous.getValue(),
							state.previous.getState()), pv).iterator();
				}
			}
			state.previous = pv;
			return result;
		}, state -> {
			if (state.previous != null && state.previous.getTimestamp().before(end)) {
				return Collections
						.singleton(new ProcessValue(end, state.previous.getValue(), state.previous.getState()))
						.iterator();
			} else {
				return Collections.emptyIterator();
			}
		});
	}

}
