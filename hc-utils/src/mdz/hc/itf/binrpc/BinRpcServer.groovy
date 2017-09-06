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

import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import mdz.Utilities
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
@Slf4j
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
		// Sockets schließen, damit die Threads beendet werden
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
		Utilities.catchToLog(log) {
			try {
				while (true) {
					Socket socket=serverSocket.accept()
					clientSockets << socket
					pool.execute {
						String client="$socket.inetAddress.hostAddress:$socket.port"
						log.debug "Client $client has connected"
						try {
							handleClient(socket)
						} finally { 
							log.trace "Client $client disconnected"
							clientSockets.remove(socket)
							socket.close()
						}
					}
				}
			} catch (SocketException e) {
				// serverSocket.close() wurde z.B. aufgerufen
			}
		}
	}
	
	private void send(Socket socket, byte[] data) {
		log.trace "Sending ${data.length} bytes:\n${Utilities.prettyPrint(data)}"
		socket.getOutputStream().write(data)
	}

	private void receive(Socket socket, byte[] data) {
		log.trace "Receiving ${data.length} bytes"
		InputStream is=socket.getInputStream()
		int pos=0
		while (pos<data.length) {
			int cnt=is.read(data, pos, data.length-pos)
			if (cnt==-1)
				throw new IOException('Unexpected end of stream')
			pos+=cnt
		}
		log.trace "Received data:\n${Utilities.prettyPrint(data)}"
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
		log.debug "Call of method '$methodName' received with parameters $parameters"
		Closure proc=procedures[methodName]
		if (proc==null)
			throw new BinRpcException("Unknown method: $methodName")
		def result=proc(parameters)
		if (result==null) result=''
		log.debug "Sending method result: $result"
		result
	}
	
	private void handleClient(Socket socket) {
		Utilities.catchToLog (log) {
			BinRpcEncoder encoder=[]
			BinRpcDecoder decoder=[]
		
			while (true) {
				// BIN-RPC-Kopf einlesen und dekodieren
				byte[] data=new byte[HEADER_SIZE]
				try {
					receive socket, data
				} catch (IOException e) {
					// Client hat z.B. die Verbindung geschlossen
					break
				}
				BinRpcDecoder.Header header=decoder.decodeHeader(data)
				if (header.payloadLength+HEADER_SIZE>PACKET_SIZE_LIMIT)
					throw new Exception("Received payload length of $header.payloadLength exceeds the packet size limit")
				if (header.type!=HEADER_REQUEST)
					throw new Exception("Received invalid packet type $header.type")
				// BIN-RPC-Request einlesen und dekodieren
				data=new byte[header.payloadLength]
				receive socket, data
				BinRpcDecoder.Request request=decoder.decodeRequest(data)

				// Prozedur aufrufen
				try {
					def result=handleCall(request.methodName, request.parameters)
					data=encoder.encodeResponse(result)
				} catch (BinRpcException e) {
					String msg=e.message?:e.class.name
					log.warn "Exception: $msg"
					// TODO: Bei Umstellung auf LogBack anpassen (u.a. Exceptions.sanitize() verwenden)
					log.debug Utilities.getStackTrace(e)
					log.debug "Sending fault: code $e.faultCode, message '$msg'"
					data=encoder.encodeFault(e.faultCode, msg)
				} catch (Exception e) {
					String msg=e.message?:e.class.name
					log.warn "Exception: $msg"
					// TODO: Bei Umstellung auf LogBack anpassen (u.a. Exceptions.sanitize() verwenden)
					log.debug Utilities.getStackTrace(e)
					log.debug "Sending fault: code $BinRpcException.GENERIC_ERROR, message '$msg'"
					data=encoder.encodeFault(BinRpcException.GENERIC_ERROR, msg)
				}
				
				// BIN-RPC Antwort schicken
				if (data.length>PACKET_SIZE_LIMIT)
					throw new Exception("Encoded packet of size $data.length exceeds packet size limit")
				send socket, data
			}
		}
	}
}
