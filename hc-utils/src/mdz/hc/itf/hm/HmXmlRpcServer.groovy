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
package mdz.hc.itf.hm

import java.net.ServerSocket
import groovy.net.xmlrpc.XMLRPCServer
import groovy.util.logging.Slf4j
import mdz.Utilities
import mdz.eventprocessing.BasicProducer
import mdz.hc.RawEvent
import mdz.hc.ProcessValue
import mdz.hc.DataPointIdentifier

@Slf4j
public class HmXmlRpcServer extends BasicProducer<RawEvent> {

	private final static int DEFAULT_PORT = 2098

	String localAddress
	int port = DEFAULT_PORT
	
	final Map rpcMethods=[
		event: { interfaceId, address, key, value ->
			log.debug "event($interfaceId, $address, $key, $value) call received"
			RawEvent event=new RawEvent(
				id:new DataPointIdentifier((String)interfaceId, (String)address, (String)key),
				pv:new ProcessValue(new Date(), value, ProcessValue.STATE_QUALITY_NOT_SUPPORTED)
			)
			produce event
			''
		},
		listDevices: { interfaceId ->
			log.debug "listDevices($interfaceId) call received"
			[]
		},
		newDevices: { interfaceId, deviceDescriptions ->
			log.debug "newDevices($interfaceId) call received"
			if (log.debugEnabled)
				deviceDescriptions.each {
					log.debug "newDevices($interfaceId): "+it
				}
			''
		},
		deleteDevices: { interfaceId, addresses ->
			log.debug "deleteDevices($interfaceId, $addresses) call received"
			''
		},
		updateDevice: { interfaceId, address, hint ->
			log.debug "updateDevice($interfaceId, $address, $hint) call received"
			''
		}
	]
	
	private XMLRPCServer server

	synchronized void start() {
		log.info "Starting XML-RPC server on port $port"
		
		server=new XMLRPCServer()
		rpcMethods.each { name, method -> server."$name"=method }
		
		server.system.multicall={ multiCall ->
			log.debug "system.multicall() with $multiCall.size item(s) received"
			multiCall.collect { oneCall ->
				String name=oneCall.methodName
				Closure method=rpcMethods[name]
				if (!method) returnFault "Unknown method $name called", -1
				method(oneCall.params)
			}
		}
		server.system.listMethods={  
			log.debug "system.listMethods($it) received"
			List<String> methods=rpcMethods.collect { it.key } + 'system.multicall' + 'system.listMethods'
			log.trace "Response: {}", methods
			methods
		}
		
		def defaultMethod={ name, params ->
			log.warn "Unknown method $name called"
			returnFault "Unknown method $name called", -1
		}
		defaultMethod.delegate=server
		server.setupDefaultMethod defaultMethod
		 
		server.setupFaultMethod { String message, int code ->
			 log.warn "Returning error message '$message', $code"
		}
		
		ServerSocket serverSocket = new ServerSocket(port)
		serverSocket.reuseAddress=true
		server.startServer serverSocket
		
		log.debug "XML-RPC server address is $url"
	}
	
	String getUrl() { 'http://'+getLocalAddress()+':'+port }
	
	String getLocalAddress() {
		if (localAddress==null)
			localAddress=InetAddress.localHost.hostAddress
		localAddress
	}

	synchronized void stop() {
		if (server) { 
			log.debug 'Stopping XML-RPC server'
			Utilities.catchToLog(log) { server.stopServer(); } 
			server=null 
		}
	}
}
