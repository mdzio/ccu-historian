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
import groovy.util.logging.Slf4j

@Slf4j
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
		log.debug "cmdLine.configFileName=$configFileName"
		log.debug "cmdLine.logLevel=${logLevel?:''}"
		log.debug "cmdLine.recalculation=$recalculation"
		log.debug "cmdLine.compaction=$compaction"
		log.debug "cmdLine.clean=$clean"
		log.debug "cmdLine.scriptFileName=${scriptFileName?:''}"
		log.debug "cmdLine.runScriptFileName=${runScriptFileName?:''}"
	}
}
