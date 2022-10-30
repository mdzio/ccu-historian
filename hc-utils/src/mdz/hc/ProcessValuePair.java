package mdz.hc;

import java.util.Objects;

public class ProcessValuePair {
	private ProcessValue first;
	private ProcessValue second;

	public ProcessValuePair() {
	}

	public ProcessValuePair(ProcessValuePair other) {
		this.first = other.first;
		this.second = other.second;
	}

	public ProcessValuePair(ProcessValue first, ProcessValue second) {
		this.first = first;
		this.second = second;
	}

	public ProcessValue getFirst() {
		return first;
	}

	public void setFirst(ProcessValue first) {
		this.first = first;
	}

	public ProcessValue getSecond() {
		return second;
	}

	public void setSecond(ProcessValue second) {
		this.second = second;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessValuePair other = (ProcessValuePair) obj;
		return Objects.equals(first, other.first) && Objects.equals(second, other.second);
	}

	@Override
	public String toString() {
		return "first=" + first + ", second=" + second;
	}
}