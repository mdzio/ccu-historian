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
import groovy.util.logging.Log

@Log
@CompileStatic
class DatabaseConfig {

	String dir='./data', name='history'
	String user='sa', password='ccu-historian'
	
	boolean webEnable=true, tcpEnable, pgEnable
	boolean webAllowOthers, tcpAllowOthers, pgAllowOthers
	int webPort=8082, tcpPort=9092, pgPort=5435
	String backup=''

	void logDebug() {
		log.fine "database.dir='$dir'"
		log.fine "database.name='$name'"
		log.fine "database.user='$user'"
		log.fine "database.webEnable=$webEnable"
		log.fine "database.webPort=$webPort"
		log.fine "database.webAllowOthers=$webAllowOthers"
		log.fine "database.tcpEnable=$tcpEnable"
		log.fine "database.tcpPort=$tcpPort"
		log.fine "database.tcpAllowOthers=$tcpAllowOthers"
		log.fine "database.pgEnable=$pgEnable"
		log.fine "database.pgPort=$pgPort"
		log.fine "database.pgAllowOthers=$pgAllowOthers"
		log.fine "database.backup='$backup'"
	}
}
