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

import java.util.Date;

public class ProcessValue {

	public final static int STATE_QUALITY_MASK = 0x00000003;
	public final static int STATE_QUALITY_BAD = 0x00000000;
	public final static int STATE_QUALITY_QUESTIONABLE = 0x00000001;
	public final static int STATE_QUALITY_NOT_SUPPORTED = 0x00000002;
	public final static int STATE_QUALITY_GOOD = 0x00000003;

	public final static int STATE_PREPROCESSED = 0x00000004;
	
	private Date timestamp;
	private Object value;
	private int state;

	public ProcessValue(Date timestamp, Object value, int state) {
		this.timestamp = timestamp;
		this.value = value;
		this.state = state;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Object getValue() {
		return value;
	}

	void setValue(Object value) {
		this.value = value;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + state;
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessValue other = (ProcessValue) obj;
		if (state != other.state)
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return timestamp + ", " + value + ", " + state;
	}
}
