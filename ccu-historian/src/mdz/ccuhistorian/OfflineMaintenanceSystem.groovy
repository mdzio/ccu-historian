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

import groovy.util.logging.Log
import java.util.concurrent.TimeUnit
import mdz.Exceptions

@Log
class OfflineMaintenanceSystem extends BaseSystem {

	public OfflineMaintenanceSystem(Configuration config) {
		super(config)
		Closure action
		if (config.cmdLineConfig.compaction)
			action=Database.&compact.curry(config.databaseConfig)
		else if (config.cmdLineConfig.scriptFileName)
			action=Database.&dump.curry(config.databaseConfig, config.cmdLineConfig.scriptFileName)
		else if (config.cmdLineConfig.runScriptFileName)
			action=Database.&runScript.curry(config.databaseConfig, config.cmdLineConfig.runScriptFileName)
		else
			throw new Exception("No maintenance action selected")
		base.executor.schedule({
				Exceptions.catchToLog(log) { action() }
				Main.shutdown()
			}, 250, TimeUnit.MILLISECONDS)
	}
}
