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

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class DatabaseConfig {

	String dir='./data', name='history'
	String user='sa', password='ccu-historian'
	
	boolean webEnable=true, tcpEnable, pgEnable
	boolean webAllowOthers, tcpAllowOthers, pgAllowOthers
	int webPort=8082, tcpPort=9092, pgPort=5435
	String backup=''

	void logDebug() {
		log.debug "database.dir='$dir'"
		log.debug "database.name='$name'"
		log.debug "database.user='$user'"
		log.debug "database.webEnable=$webEnable"
		log.debug "database.webPort=$webPort"
		log.debug "database.webAllowOthers=$webAllowOthers"
		log.debug "database.tcpEnable=$tcpEnable"
		log.debug "database.tcpPort=$tcpPort"
		log.debug "database.tcpAllowOthers=$tcpAllowOthers"
		log.debug "database.pgEnable=$pgEnable"
		log.debug "database.pgPort=$pgPort"
		log.debug "database.pgAllowOthers=$pgAllowOthers"
		log.debug "database.backup='$backup'"
	}
}
