package mdz.hc.timeseries.expr;

/**
 * Characteristics of a time series provided by a Reader.
 */
public final class Characteristics {
	/**
	 * The last value is hold until the next sample. This should be used for
	 * discrete sensors (e.g. switches).
	 */
	public static final int HOLD = 0x0001;

	/**
	 * The value is linear interpolated between the samples. This should be used for
	 * continuous sensors (e.g. temperature).
	 */
	public static final int LINEAR = 0x0002;

	/**
	 * Indicates an event. The timestamp is of primary interest.
	 */
	public static final int EVENT = 0x0004;

	/**
	 * The value is a steadily increasing count (e.g. energy meter, rain meter).
	 */
	public static final int COUNTER = 0x0008;

	/**
	 * User specific characteristics start at bit 16.
	 */
	public static final int USER_MASK = 0xffff0000;
}
