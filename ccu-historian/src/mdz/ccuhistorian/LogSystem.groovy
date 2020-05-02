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
import mdz.Exceptions
import java.text.*
import org.codehaus.groovy.runtime.StackTraceUtils
import org.eclipse.jetty.util.log.JavaUtilLog
import org.eclipse.jetty.util.log.Log

public class LogSystem {
	
	private final static int SEVERITY_FIELD_LENGTH = 7
	private final static int LOGGER_NAME_FIELD_LENGTH = 36

	private final static Logger log=Logger.getLogger(LogSystem.class.name)
	
	LogSystemConfig config
	 
	private final ConsoleHandler consoleHandler=[]
	private FileHandler fileHandler
	private final Logger rootLogger=Logger.getLogger('')
	private final Logger mdzLogger=Logger.getLogger('mdz')
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
			// print timestamp
			builder << dateFormat.format(new Date(logRecord.millis)) << '|'
			
			// print log level
			builder << logRecord.level.toString().padRight(SEVERITY_FIELD_LENGTH) << '|'
			
			// print logger names?
			if (logSystem.config.showLoggerNames) {
				String ln=logRecord.loggerName
				// truncate logger names
				if (ln.size() > LOGGER_NAME_FIELD_LENGTH)
					ln='?' + ln[-(LOGGER_NAME_FIELD_LENGTH-1)..-1]
				builder << ln.padRight(LOGGER_NAME_FIELD_LENGTH) << '|'
			}
				
			// print message
			builder << logRecord.message
			if (!logRecord.message.endsWith('\n'))
				builder << '\n'

			// exception present?
			if (logRecord.thrown)
				builder << Exceptions.getStackTrace(logRecord.thrown)
			
			builder
		}
	}
	
	public LogSystem(LogSystemConfig config) {
		this.config=config
		
		// configure root logger
		rootLogger.level=Level.SEVERE
		rootLogger.handlers.each { Handler h -> rootLogger.removeHandler h }
		consoleHandler.formatter=new MdzFormatter(this, 'yyyy-MM-dd HH:mm:ss')
		rootLogger.addHandler consoleHandler
		
		// configure mdz logger
		mdzLogger.level=Level.ALL
		
		// configure jetty logging
		Log.setLog(new JavaUtilLog());

		restart()
	}
	
	public void restart() {
		consoleHandler.level=config.consoleLevel
		binRpcLogger.level=config.binRpcLevel
		
		if (fileHandler) {
			rootLogger.removeHandler fileHandler
			fileHandler.close()
			fileHandler=null
		}	
		if (config.fileLevel!=Level.OFF) {
			fileHandler=[config.fileName, config.fileLimit, config.fileCount, true]
			fileHandler.formatter=new MdzFormatter(this, 'yyyy-MM-dd HH:mm:ss')
			fileHandler.level=config.fileLevel
			rootLogger.addHandler fileHandler
		}
	}	

	public void directMessage(msg) {
		if (config.directMessages)
			System.err.println "[DIRECT MSG] $msg"	
	}
}
