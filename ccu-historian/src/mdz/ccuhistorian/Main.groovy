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

import java.util.logging.Logger
import mdz.Exceptions
import mdz.hc.itf.Manager
import mdz.hc.persistence.Storage
import groovy.transform.CompileStatic

@CompileStatic
class Main {

	public static String version='1.2.0'
	
	private static final Logger log=Logger.getLogger(Main.class.name)
	private static Main main
	
	private final LogSystem logSystem=new LogSystem(new LogSystemConfig())
	private final Configuration config=[]
	private volatile boolean restart
	private BaseSystem system

	public static void main(String[] args) {
		main=new Main()
		main.run(args)
	}
	
	private void run(String[] args) {
		if (Exceptions.catchToLog(log) {
			config.readCommandLine(args)
		}) return
		if (config.cmdLineConfig.help)
			return
		if (config.cmdLineConfig.logLevel!=null) {
			logSystem.config.consoleLevel=config.cmdLineConfig.logLevel
			logSystem.restart()
		}
		log.info 'CCU-Historian V'+version
		log.info '(C)MDZ (info@ccu-historian.de)'
		log.fine 'Command line options:'
		config.cmdLineConfig.logDebug()

		addShutdownHook {
			logSystem.directMessage 'Shutdown hook called' 
			stop() 
			logSystem.directMessage 'Shutdown hook completed' 
		}
		
		while (true) {
			Exceptions.catchToLog(log) {
				if (config.fileModified)
					restart=true
			}
			if (restart) {
				restart=false
				stop()
				if (Exceptions.catchToLog(log) { start() })
					restart=true
			}
			log.finest 'Sleeping'
			sleep 30000
		}
	}

	private void start() {
		config.readFile()
		logSystem.config=config.logSystemConfig
		logSystem.restart()
		log.fine 'Log system configuration:'
		logSystem.config.logDebug()

		if (config.cmdLineConfig.recalculation ||
			config.cmdLineConfig.clean!=null)
			system=new MaintenanceSystem(config)
		else if (config.cmdLineConfig.compaction ||
			config.cmdLineConfig.scriptFileName ||
			config.cmdLineConfig.runScriptFileName)
			system=new OfflineMaintenanceSystem(config)
		else
			system=new HistorianSystem(config)
	}
	
	private void stop() {
		if (system) { 
			system.stopScheduler()
			system.stop()
			system=null 
		}
	}
	
	public static shutdown() {
		Thread.start { System.exit(0) }
	}

	public static void restart() {	main.restart=true }
}