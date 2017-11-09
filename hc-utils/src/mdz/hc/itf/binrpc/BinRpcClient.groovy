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
package mdz.hc.itf.binrpc

import groovy.util.logging.Log
import groovy.transform.CompileStatic
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Executors
import mdz.hc.itf.binrpc.BinRpcDecoder.Header
import mdz.hc.itf.binrpc.BinRpcDecoder.Response
import mdz.hc.itf.binrpc.BinRpcDecoder.Fault
import mdz.Utilities
import static mdz.hc.itf.binrpc.BinRpcConstants.*

/**
 * The class BinRpcClient is used for communication with BINRPC servers.
 * 
 * The methods are not synchronized.  
 */
@Log
@CompileStatic
public class BinRpcClient {

	private final static int DEFAULT_SOCKET_TIMEOUT = 10000 // ms
	private final static int ADDITIONAL_READ_TIMEOUT = 200 // ms
	private final static long DEFAULT_CALL_PAUSE = 200 // ms
	
	String host
	int port
	int timeout = DEFAULT_SOCKET_TIMEOUT
	ScheduledExecutorService executor
	
	private Socket socket
	private BinRpcEncoder encoder=[]
	private BinRpcDecoder decoder=[]
	private long lastCallTime
	private callMutex=new Object()
	
	public void connect() {
		if (socket==null) {
			log.info "Connecting to $host:$port"
			try {
				socket=new Socket(host, port)
				socket.setSoTimeout(timeout)
			} catch (Exception e) {
				disconnect(); throw e
			}
		}
	}
	
	public void disconnect() {
		socket?.close()
		socket=null	
	}

	public void send(byte[] data) {
		connect()
		log.finest "Sending ${data.length} bytes to $host:$port:\n${Utilities.prettyPrint(data)}"
		socket.getOutputStream().write(data)
	}

	public int available() {
		connect()
		socket.getInputStream().available()
	}
	
	private enum TIMEOUT_STATE { DISABLED, ENABLED, TIMED_OUT }
	
	public void receive(byte[] data) {
		connect()
		log.finest "Receiving ${data.length} bytes from $host:$port"
		InputStream is=socket.getInputStream()
		int pos=0
		while (pos<data.length) {
			TIMEOUT_STATE toState=TIMEOUT_STATE.ENABLED
			Future<?> timeoutTask=executor.schedule(
				{ synchronized(this) {
					if (toState==TIMEOUT_STATE.ENABLED) {
						toState=TIMEOUT_STATE.TIMED_OUT
						disconnect()
					} 
				} } as Runnable, 
				timeout+ADDITIONAL_READ_TIMEOUT, TimeUnit.MILLISECONDS
			)
			
			int cnt; Throwable e
			try { cnt=is.read(data, pos, data.length-pos)
			} catch (Throwable e1) { e=e1 }

			timeoutTask.cancel(false)
			synchronized(this) {
				if (toState==TIMEOUT_STATE.TIMED_OUT) { 
					if (e!=null) throw new IOException('Read timeout', e)
					else throw new IOException('Read timeout')
				}
				if (e!=null) throw e
				toState=TIMEOUT_STATE.DISABLED
			}
			
			if (cnt==-1)
				throw new IOException('Unexpected end of stream')
			pos+=cnt
		}
		log.finest "Received data:\n${Utilities.prettyPrint(data)}"
	}
	
	public call(String methodName, List parameters) {
		synchronized(callMutex) {
			try {
				log.fine "Calling method '$methodName' with parameters $parameters"
				// pause
				if (lastCallTime!=0) {
					long elapsed=System.currentTimeMillis()-lastCallTime
					if (elapsed < DEFAULT_CALL_PAUSE) {
						long wait=DEFAULT_CALL_PAUSE-elapsed
						log.fine "Pausing call for $wait ms"
						Thread.sleep wait // allow interrupts
					}
				}
				// request
				lastCallTime=System.currentTimeMillis()
				byte[] data=encoder.encodeRequest(methodName, parameters)
				if (data.length>PACKET_SIZE_LIMIT)
					throw new Exception("Encoded packet of size $data.length exceeds packet size limit")
				send data
				data=new byte[HEADER_SIZE]
				receive data
				BinRpcDecoder.Header header=decoder.decodeHeader(data)
				if (header.payloadLength+HEADER_SIZE>PACKET_SIZE_LIMIT)
					throw new Exception("Received payload length of $header.payloadLength exceeds the packet size limit")
				data=new byte[header.payloadLength]
				receive data
				switch (header.type) {
					case HEADER_RESPONSE:
						Response response=decoder.decodeResponse(data)
						log.fine "Received response: $response.result"
						return response.result
					case HEADER_FAULT:
						Fault fault=decoder.decodeFault(data)
						throw new BinRpcException(fault.faultCode, fault.faultString)
					default:
						throw new Exception("Received invalid packet type $header.type")
				}
			} catch (Exception e) {
				disconnect(); throw e
			}
		}
	}
}
