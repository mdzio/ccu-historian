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
import java.util.logging.Level
import mdz.Exceptions
import mdz.hc.itf.Manager;

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext

import groovy.transform.CompileStatic

@CompileStatic
public class WebServer {

	static private WebServer instance // required for the servlets
	
	WebUtilities webUtilities
	WebServerConfig config
	ExtendedStorage database // required for the servlets
	Manager interfaceManager // required for the servlets

	private final Logger log=Logger.getLogger(WebServer.class.name)
	private final Logger webServerLog=Logger.getLogger('org.eclipse.jetty')
	private Server server

	public WebServer(WebServerConfig config, ExtendedStorage database, Manager interfaceManager) {
		this.config=config
		this.database=database
		this.interfaceManager=interfaceManager
		
		log.info 'Starting web server'
		config.logDebug()
		
		webUtilities=new WebUtilities()
		instance=this
		
		webServerLog.level=config.logLevel
		
		WebAppContext context=new WebAppContext()
		context.resourceBase=config.dir
		context.contextPath='/'
		context.parentLoaderPriority=true
		
		server=new Server(config.port)
		server.setHandler context
		server.start()
		
		config.trendDesigns.each { String id, TrendDesign design ->
			if (design.identifier==null) design.identifier=id
			if (design.displayName==null) design.displayName=id
		}
		if (!config.trendDesigns.containsKey('default'))
			config.trendDesigns['default']=new TrendDesign(identifier: 'default', displayName: 'Standard')
			
		log.info "Web server port: $config.port"
	}
		
	public synchronized stop() {
		if (server) {
			log.info 'Stopping web server'
			Exceptions.catchToLog(log) { server.stop() }
			server=null
		}
		webUtilities=null
		instance=null
	}	
	
	public String getHistorianAddress() {
		if (config.historianAddress=='')
			config.historianAddress=InetAddress.localHost.hostAddress
		config.historianAddress
	}

	// required for the servlets
	public static WebServer getInstance() { instance }
}
