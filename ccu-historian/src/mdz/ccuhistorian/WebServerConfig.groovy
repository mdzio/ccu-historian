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

import java.util.Map
import java.util.logging.Level
import groovy.transform.CompileStatic
import groovy.util.logging.Log

@Log
@CompileStatic
class WebServerConfig {
	
	private static final class Link {
		String text
		String address
	}
	
	Level logLevel=Level.WARNING
	int port=80
	String dir='./webapp'
	String historianAddress=''
	Map<String, TrendDesign> trendDesigns=[:].withDefault { new TrendDesign(identifier: (String)it, displayName: (String)it) }
	Map<String, Link> menuLinks=new TreeMap().withDefault { new Link() }
	String[] apiKeys

	void logDebug() {
		log.fine "webServer.port=$port"
		log.fine "webServer.dir='$dir'"
		log.fine "webServer.logLevel=Level.$logLevel"
		log.fine "webServer.historianAddress='${getHistorianAddress()}'"
		log.fine "webServer.trendDesigns=[${trendDesigns.collect { it.key }.join(', ')}]"
		log.fine "webServer.apiKeys=[${apiKeys.collect { "'$it'" }.join(', ')}]"
		log.fine "webServer.menuLinks=[${menuLinks.collect { it.value.text }.join(', ')}]"
	}
}
