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

import java.util.Date

import groovy.util.logging.Log
import groovy.transform.CompileStatic
import mdz.eventprocessing.Consumer
import mdz.eventprocessing.BasicProducer
import mdz.hc.ProcessValue;
import mdz.hc.RawEvent
import mdz.hc.DataPointIdentifier
import mdz.hc.itf.binrpc.BinRpcException
import mdz.hc.itf.binrpc.BinRpcServer

@Log
@CompileStatic
public class HmBinRpcServer extends BasicProducer<RawEvent> {
	
	private final static int DEFAULT_PORT = 2099
	
	int port=DEFAULT_PORT
	String localAddress
	
	private BinRpcServer server=[]
	private boolean started
	
	HmBinRpcServer() {
		server.procedures.event=handleEvent
		server.procedures.listDevices=handleListDevices
		server.procedures.newDevices=handleNewDevices
		server.procedures.deleteDevices=handleDeleteDevices
		server.procedures.updateDevice=handleUpdateDevices
		server.procedures.replaceDevice=handleReplaceDevices
		server.procedures.readdedDevice=handleReaddedDevices
	}

	public void start() {
		if (started)
			throw new IllegalStateException()
		server.port=port
		server.start()
		started=true
	}
	
	public void stop() {
		server.stop()
		started=false
	}

	public synchronized String getLocalAddress() {
		if (localAddress==null)
			localAddress=InetAddress.localHost.hostAddress
		localAddress
	}

	public String getUrl() { 'binary://'+getLocalAddress()+':'+port }
	
	private Closure handleEvent = { List params ->
		// parameters: interfaceId, address, key, value
		if (params.size()!=4)
			throw new BinRpcException('Wrong number of parameters')
		RawEvent event=new RawEvent(
			id:new DataPointIdentifier((String)params[0], (String)params[1], (String)params[2]),
			pv:new ProcessValue(new Date(), params[3], ProcessValue.STATE_QUALITY_NOT_SUPPORTED)
		)
		produce event
		''
	}

	private Closure handleListDevices = { List params ->
		// parameters: String interfaceId; return: Array<DeviceDescription>
		[]
	}
	
	private Closure handleNewDevices = { List params ->
		// parameters: String interfaceId, Array<DeviceDescription> deviceDescriptions
		''
	}
	
	private Closure handleDeleteDevices = { List params ->
		// parameters: String interfaceId, Array<String> addresses
		''
	}
	
	private Closure handleUpdateDevices = { List params ->
		// parameters: String interfaceId, String address, int hint
		''
	}

	private Closure handleReplaceDevices = { List params ->
		// parameters: String interfaceId, String oldDeviceAddress, String newDeviceAddress
		''
	}
	
	private Closure handleReaddedDevices = { List params ->
		// parameters: String interfaceId, Array<String> addresses
		''
	}
}
