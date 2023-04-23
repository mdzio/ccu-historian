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

/**
 * Characteristics of a time series provided by a Reader.
 */
public final class Characteristics {
	/**
	 * The last value is hold until the next sample. This should be used for
	 * binary sensors (e.g. switches).
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
