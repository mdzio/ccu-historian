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

import groovy.transform.CompileStatic
import static mdz.hc.itf.binrpc.BinRpcConstants.*

@CompileStatic
class BinRpcDecoder {

	public static class Header {
		byte type
		int payloadLength
	}
	
	public static class Request {
		String methodName
		List parameters
	}

	public static class Response {
		def result
	}
	 
	public static class Fault {
		int faultCode
		String faultString
	} 
	
	private ByteArrayInputStream is
	
	public Header decodeHeader(byte[] data) {
		if (data.length!=HEADER_SIZE)
			throw new Exception('Invalid BIN-RPC header size')
		is=[data]
		byte[] header=new byte[3]
		is.read header
		if (header!=HEADER_START)
			throw new Exception("Invalid BIN-RPC header start: $header")
		byte type=(byte)is.read()
		switch (type) {
			case HEADER_REQUEST:
			case HEADER_RESPONSE:
			case HEADER_FAULT:
				break
			default:
				throw new Exception("Invalid BIN-RPC packet type: $type")
		}
		int payloadLength=decodeInt()
		new Header(type: type, payloadLength: payloadLength)	
	}
	
	private Request decodeRequest(byte[] data) {
		is=[data]
		Request req=[]
		req.methodName=decodeString()
		req.parameters=decodeArray()
		req	
	}
	
	public Response decodeResponse(byte[] data) {
		is=[data]
		Response resp=[]
		resp.result=decodeObject()
		resp	
	}

	public Fault decodeFault(byte[] data) {
		is=[data]
		Fault fault=[]
		int type=decodeInt()
		if (type!=TYPE_STRUCT)
			throw new Exception('Invalid root object type in BIN-RPC FAULT packet')
		Map result=decodeStruct()
		if (!(result.faultCode in Integer))
			throw new Exception('Invalid faultCode object type in BIN-RPC FAULT packet')
		fault.faultCode=(Integer)result.faultCode
		if (!(result.faultString in String))
			throw new Exception('Invalid faultString object type in BIN-RPC FAULT packet')
		fault.faultString=(String)result.faultString
		fault
	}
	
	private byte[] read(int count) {
		byte[] data=new byte[count]
		if (count!=0) {
			int readCount=is.read(data)
			if (readCount!=data.length)
				throw new Exception('Unexpected end of BIN-RPC packet')
		}
		data
	}
	
	private int decodeInt() {
		byte[] data=read(4)
		((int)data[0] << 24) + ((int)data[1] << 16 & 0xFF0000) + ((int)data[2] << 8 & 0xFF00) + ((int)data[3] & 0xFF)
	}
	
	private boolean decodeBoolean() {
		byte[] data=read(1)
		data[0]!=0
	}

	private String decodeString() {
		byte[] data=read(decodeInt())
		new String(data, 'ISO-8859-1')
	}
	
	private double decodeDouble() {
		double mant=(double)decodeInt()/MANTISSA_MULTIPLICATOR
		double exp=(double)decodeInt()
		mant*(2**exp)
	}

	private byte[] decodeBinary() {
		read(decodeInt())
	}

	private Date decodeDate() {
		new Date((long)decodeInt()*1000L)
	}

	private List decodeArray() {
		int length=decodeInt()
		List ret=[]
		length.times { ret << decodeObject() }
		ret
	}

	private Map decodeStruct() {
		int length=decodeInt()
		Map ret=[:]
		length.times {
			String key=decodeString()
			def value=decodeObject()
			ret << new MapEntry(key, value)
		}
		ret
	}

	private def decodeObject() {
		int type=decodeInt()
		def ret
		switch (type) {			
			case TYPE_INT: ret=decodeInt(); break 
			case TYPE_BOOLEAN: ret=decodeBoolean(); break
			case TYPE_STRING: ret=decodeString(); break
			case TYPE_DOUBLE: ret=decodeDouble(); break
			case TYPE_DATE: ret=decodeDate(); break
			case TYPE_BINARY: ret=decodeBinary(); break
			case TYPE_ARRAY: ret=decodeArray(); break
			case TYPE_STRUCT: ret=decodeStruct(); break
			default:
				throw new Exception("Invalid object type in BIN-RPC packet: $type")
		}
		ret
	}
}
