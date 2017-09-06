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

import static mdz.hc.itf.binrpc.BinRpcConstants.*
import groovy.transform.CompileStatic

class BinRpcEncoder {

	private ByteArrayOutputStream os=[]

	@CompileStatic
	private static byte[] raw(int i) {
		byte[] data=new byte[4]
		data[0]=(byte)(i>>24)
		data[1]=(byte)(i>>16)
		data[2]=(byte)(i>>8)
		data[3]=(byte)(i)
		data
	}

	@CompileStatic
	private write(int i) {
		os.write raw(i)
	}
	
	@CompileStatic
	private write(byte[] bin) {
		write bin.length
		os.write bin
	}

	@CompileStatic
	private write(String str) {
		write str.getBytes('ISO-8859-1')
	}

	@CompileStatic
	private void encode(int i) { 
		write TYPE_INT
		write i 
	}
	
	@CompileStatic
	private void encode(boolean b) { 
		write TYPE_BOOLEAN
		os.write b?1:0 
	}

	@CompileStatic
	private void encode(String str) {	
		write TYPE_STRING
		write str 
	}
	
	@CompileStatic
	private void encode(double v) {
		write TYPE_DOUBLE
		if (v==0.0) {
			write 0; write 0
		} else {
			// v=mant*2^exp mit 0.5<=mant<1.0 
			int exp=(int)(Math.log(Math.abs(v))/Math.log(2.0))+1
			int mant=(int)(v/(2**exp)*MANTISSA_MULTIPLICATOR)
			write mant; write exp
		}
	}
	
	@CompileStatic
	private void encode(Date d) {
		 write TYPE_DATE
		 write ((int)(d.getTime()/1000L))
	}
	
	@CompileStatic
	private void encode(byte[] bin) {
		 write TYPE_BINARY
		 write bin
	}

	private void encode(List l) {
		write TYPE_ARRAY
		write l.size()
		l.each { encode it } 
	}
	
	private void encode(Map m) {
		write TYPE_STRUCT
		write m.size()
		m.each {
			write it.key.toString()
			encode it.value
		}
	}
	
	@CompileStatic
	private byte[] buildPacket(byte type) {
		byte[] payload=os.toByteArray()
		byte[] packet=new byte[8+payload.length]
		
		// head
		System.arraycopy HEADER_START, 0, packet, 0, 3
		// type
		packet[3]=type
		// length of payload
		byte[] lengthRaw=raw(payload.length)
		System.arraycopy lengthRaw, 0, packet, 4, 4
		// payload
		System.arraycopy payload, 0, packet, 8, payload.length 
		packet
	}
	
	public byte[] encodeRequest(String methodName, List params) {
		os.reset()
		write methodName
		write params.size()
		params.each { encode it	}
		buildPacket HEADER_REQUEST
	}
	
	public byte[] encodeResponse(response) {
		os.reset()
		encode response
		buildPacket HEADER_RESPONSE
	}
	
	@CompileStatic
	public byte[] encodeFault(int faultCode, String faultString) {
		os.reset()
		encode([
			faultCode: faultCode,
			faultString: faultString
		])
		buildPacket HEADER_FAULT
	}
}
