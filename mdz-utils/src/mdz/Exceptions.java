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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.groovy.runtime.StackTraceUtils;

import groovy.lang.Closure;

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

	public static String getStackTrace(Throwable ex) {
		sanitize(ex);
		StringWriter trace = new StringWriter();
		ex.printStackTrace(new PrintWriter(trace));
		return trace.toString();
	}

	public static void logTo(Logger log, Level level, Throwable e) {
		if (log.isLoggable(level)) {
			String msg = e.getMessage();
			if (msg == null)
				msg = e.getClass().getName();
			log.log(level, "Exception: " + msg);
			log.log(level, "Detail: " + getStackTrace(e));
		}
	}

	@FunctionalInterface
	public interface RunnableCanThrow {
		void run() throws Throwable;
	}

	public static Throwable catchToLog(Logger log, RunnableCanThrow runnable) {
		try {
			runnable.run();
			return null;
		} catch (Throwable e) {
			throwIfFatal(e);
			logTo(log, Level.SEVERE, e);
			return e;
		}
	}

	public static <V> Throwable catchToLog(Logger log, Closure<V> closure) {
		return catchToLog(log, () -> closure.call());
	}
}
