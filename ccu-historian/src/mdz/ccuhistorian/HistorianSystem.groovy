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

import mdz.ccuhistorian.webapp.WebServer
import mdz.hc.itf.Manager
import groovy.util.logging.Log
import groovy.transform.CompileStatic

@Log
@CompileStatic
class HistorianSystem extends DatabaseSystem {

	Manager interfaceManager
	Historian historian
	WebServer webServer

	public HistorianSystem(Configuration config) {
		super(config)
		try {
			interfaceManager=new Manager()
			log.fine 'Configuring interfaces'
			new ManagerConfigurator().configure(interfaceManager, config.deviceConfigs)
			interfaceManager.start()
			historian=new Historian(config.historianConfig, base, extendedStorage, interfaceManager)
			webServer=new WebServer(config.webServerConfig, extendedStorage, interfaceManager)
		} catch (Throwable t) {
			stopScheduler();
			stop();
			throw t;
		}
	}
	
	@Override
	public synchronized void stop()  {
		if (webServer) { webServer.stop(); webServer=null }
		if (historian) { historian.stop(); historian=null }
		if (interfaceManager) { interfaceManager.stop(); interfaceManager=null }
		super.stop();
	}
}
