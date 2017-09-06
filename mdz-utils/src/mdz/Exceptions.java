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
package mdz;

import org.codehaus.groovy.runtime.StackTraceUtils;

public class Exceptions {

	private Exceptions() {
	}

	public static void throwIfFatal(Throwable t) {
		if (t instanceof StackOverflowError)
			throw (StackOverflowError) t;
		else if (t instanceof VirtualMachineError)
			throw (VirtualMachineError) t;
		else if (t instanceof ThreadDeath)
			throw (ThreadDeath) t;
		else if (t instanceof LinkageError)
			throw (LinkageError) t;
	}

	public static void sanitize(Throwable t) {
		StackTraceUtils.sanitize(t);
		StackTraceUtils.sanitizeRootCause(t);
	}
}
