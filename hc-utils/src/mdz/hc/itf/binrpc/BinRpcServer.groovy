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

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import mdz.Exceptions
import mdz.Text
import mdz.hc.itf.binrpc.BinRpcDecoder.Header
import mdz.hc.itf.binrpc.BinRpcDecoder.Response
import mdz.hc.itf.binrpc.BinRpcDecoder.Fault
import static mdz.hc.itf.binrpc.BinRpcConstants.*

/**
 * The class BinRpcServer is a server that accepts remote method 
 * calls according to the BIN-RPC protocol.
 * 
 * The methods start, stop are not synchronized.
 * Closures can be published as RPC methods using the property procedures. 
 * These Closures must be thread-safe implemented. 
 */
@Log
@CompileStatic
public class BinRpcServer {

	private final static int DEFAULT_PORT = 2099
	private final static int SHUTDOWN_TIMEOUT = 2000 // ms
	
	int port = DEFAULT_PORT
	Map<String, Closure> procedures=[:]
	
	private ExecutorService pool
	private ServerSocket serverSocket
	private Set<Socket> clientSockets=new HashSet().asSynchronized()
	private Thread acceptThread
	
	BinRpcServer() {
		procedures.'system.multicall'=multicallHandler
		procedures.'system.listMethods'=listMethodsHandler
	}
	
	public void start() {
		if (pool!=null) 
			throw new IllegalStateException()
		try{
			log.info "Starting BIN-RPC server on port $port"
			pool=Executors.newCachedThreadPool()
			serverSocket=[port]
			acceptThread=Thread.start('binrpc-server') { acceptClients() }
		} catch (Exception e) {
			stop(); throw e
		}
	}
	
	public void stop() {
		if (acceptThread!=null)
			log.info "Stopping BIN-RPC server on port $port"
		// close sockets to end threads
		serverSocket?.close()
		acceptThread?.join()
		acceptThread=null
		serverSocket=null
		synchronized(clientSockets) {
			clientSockets.each { it.close() }
		}
		pool?.shutdown()
		pool?.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS)
		pool=null
	}
	
	private void acceptClients() {
		Exceptions.catchToLog(log) {
			try {
				while (true) {
					Socket socket=serverSocket.accept()
					clientSockets << socket
					pool.execute {
						String client="$socket.inetAddress.hostAddress:$socket.port"
						log.fine "Client $client has connected"
						try {
							handleClient(socket)
						} finally { 
							log.finer "Client $client disconnected"
							clientSockets.remove(socket)
							socket.close()
						}
					}
				}
			} catch (SocketException e) {
				// e.g. serverSocket.close() is called
			}
		}
	}
	
	private void send(Socket socket, byte[] data) {
		log.finest "Sending ${data.length} bytes:\n${Text.prettyPrint(data)}"
		socket.getOutputStream().write(data)
	}

	private void receive(Socket socket, byte[] data) {
		log.finest "Receiving ${data.length} bytes"
		InputStream is=socket.getInputStream()
		int pos=0
		while (pos<data.length) {
			int cnt=is.read(data, pos, data.length-pos)
			if (cnt==-1)
				throw new IOException('Unexpected end of stream')
			pos+=cnt
		}
		log.finest "Received data:\n${Text.prettyPrint(data)}"
	}

	private Closure listMethodsHandler = { params ->
		procedures.collect { it.key } 
	}

	private Closure multicallHandler = { List params ->
		params[0].collect { Map oneCall ->
			handleCall((String)oneCall.methodName, (List)oneCall.params)
		}
	}

	private def handleCall(String methodName, List parameters) {
		log.fine "Call of method '$methodName' received with parameters $parameters"
		Closure proc=procedures[methodName]
		if (proc==null)
			throw new BinRpcException("Unknown method: $methodName")
		def result=proc(parameters)
		if (result==null) result=''
		log.fine "Sending method result: $result"
		result
	}
	
	private void handleClient(Socket socket) {
		Exceptions.catchToLog (log) {
			BinRpcEncoder encoder=[]
			BinRpcDecoder decoder=[]
		
			while (true) {
				// read and decode BIN-RPC header
				byte[] data=new byte[HEADER_SIZE]
				try {
					receive socket, data
				} catch (IOException e) {
					// e.g. client closed connection
					break
				}
				BinRpcDecoder.Header header=decoder.decodeHeader(data)
				if (header.payloadLength+HEADER_SIZE>PACKET_SIZE_LIMIT)
					throw new Exception("Received payload length of $header.payloadLength exceeds the packet size limit")
				if (header.type!=HEADER_REQUEST)
					throw new Exception("Received invalid packet type $header.type")
				// read and decode BIN-RPC request
				data=new byte[header.payloadLength]
				receive socket, data
				BinRpcDecoder.Request request=decoder.decodeRequest(data)

				// call procedure
				try {
					def result=handleCall(request.methodName, request.parameters)
					data=encoder.encodeResponse(result)
				} catch (BinRpcException e) {
					Exceptions.logTo(log, Level.WARNING, e)
					String msg=e.message?:e.class.name
					log.fine "Sending fault: code $e.faultCode, message '$msg'"
					data=encoder.encodeFault(e.faultCode, msg)
				} catch (Exception e) {
					Exceptions.logTo(log, Level.WARNING, e)
					String msg=e.message?:e.class.name
					log.fine "Sending fault: code $BinRpcException.GENERIC_ERROR, message '$msg'"
					data=encoder.encodeFault(BinRpcException.GENERIC_ERROR, msg)
				}
				
				// send BIN-RPC response
				if (data.length>PACKET_SIZE_LIMIT)
					throw new Exception("Encoded packet of size $data.length exceeds packet size limit")
				send socket, data
			}
		}
	}
}
