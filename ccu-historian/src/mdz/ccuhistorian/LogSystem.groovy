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
package mdz.ccuhistorian

import java.util.logging.*
import java.text.*
import org.codehaus.groovy.runtime.StackTraceUtils
import groovy.transform.CompileStatic

@CompileStatic
public class LogSystem {
	
	LogSystemConfig config
	 
	private final ConsoleHandler consoleHandler=[]
	private FileHandler fileHandler
	private final Logger rootLogger=Logger.getLogger('')
	private final List<Logger> handlerLoggers=[]
	private final Logger log=Logger.getLogger(LogSystem.class.name)
	private final Logger binRpcLogger=Logger.getLogger('mdz.hc.itf.binrpc')
	
	private class MdzFormatter extends java.util.logging.Formatter {
		private LogSystem logSystem
		private DateFormat dateFormat
		MdzFormatter(LogSystem logSystem, String format) {
			this.logSystem=logSystem
			dateFormat=new SimpleDateFormat(format)
		}
		String format(LogRecord logRecord) {
			StringBuilder builder=[]
			builder << dateFormat.format(new Date(logRecord.millis)) << '|'
			builder << logRecord.level.toString().padRight(7) << '|'
			if (logSystem.config.showLoggerNames)
				builder << logRecord.loggerName << '|'
			builder << logRecord.message
			if (!logRecord.message.endsWith('\n'))
				builder << '\n'
			builder
		}
	}
	
	public LogSystem(LogSystemConfig config) {
		this.config=config
		consoleHandler.formatter=new MdzFormatter(this, 'yyyy-MM-dd HH:mm:ss')
		rootLogger.level=Level.WARNING
		['mdz', 'org.eclipse.jetty'].each {
			Logger logger=Logger.getLogger(it)
			logger.level=Level.ALL
			logger.addHandler consoleHandler
			logger.useParentHandlers=false
			handlerLoggers << logger
		}
		restart()
	}
	
	public void restart() {
		consoleHandler.level=config.consoleLevel
		binRpcLogger.level=config.binRpcLevel
		
		if (fileHandler) {
			handlerLoggers.each { it.removeHandler fileHandler }
			fileHandler.close()
			fileHandler=null
		}	
		if (config.fileLevel!=Level.OFF) {
			fileHandler=[config.fileName, config.fileLimit, config.fileCount, true]
			fileHandler.formatter=new MdzFormatter(this, 'yyyy-MM-dd HH:mm:ss')
			fileHandler.level=config.fileLevel
			handlerLoggers.each { it.addHandler fileHandler }
		}
	}	

	public void directMessage(msg) {
		if (config.directMessages)
			System.err.println "[DIRECT MSG] $msg"	
	}
	
	public static boolean catchToLog(Logger log, Closure cl) {
		try {
			cl()
			false
		} catch (Throwable ex) {
			String msg=ex.message
			if (!msg) msg=ex.class.name
			log.severe "Exception: $msg"
			StackTraceUtils.sanitize ex
			StackTraceUtils.sanitizeRootCause ex
			StringWriter trace=new StringWriter()
			ex.printStackTrace new PrintWriter(trace)
			log.fine trace.toString()
			true
		}
	}
}
