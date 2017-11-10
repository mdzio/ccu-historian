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

import java.util.logging.Level

import groovy.transform.CompileStatic
import groovy.util.logging.Log

@Log
@CompileStatic
class CmdLineConfig {

	boolean help
	String configFileName='ccu-historian.config'
	Level logLevel
	boolean recalculation
	boolean compaction
	Date clean
	String scriptFileName
	String runScriptFileName
	
	void logDebug() {
		log.fine "cmdLine.configFileName=$configFileName"
		log.fine "cmdLine.logLevel=${logLevel?:''}"
		log.fine "cmdLine.recalculation=$recalculation"
		log.fine "cmdLine.compaction=$compaction"
		log.fine "cmdLine.clean=$clean"
		log.fine "cmdLine.scriptFileName=${scriptFileName?:''}"
		log.fine "cmdLine.runScriptFileName=${runScriptFileName?:''}"
	}
}
