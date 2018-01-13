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
package mdz.hc.itf.binrpc;

import groovy.util.GroovyTestCase
import mdz.hc.itf.binrpc.BinRpcDecoder
import mdz.hc.itf.binrpc.BinRpcEncoder
import static mdz.hc.itf.binrpc.BinRpcConstants.*

class BinRpcDecoderTest extends GroovyTestCase {

	void testDecodeInt() {
		BinRpcDecoder dec=[]
		dec.is=new ByteArrayInputStream([0, 0, 0, 0] as byte[])
		assert dec.decodeInt()==0
		dec.is=new ByteArrayInputStream([1, 2, 3, 4] as byte[])
		assert dec.decodeInt()==16909060
		dec.is=new ByteArrayInputStream([0x7F, 0xFF, 0xFF, 0xFF] as byte[])
		assert dec.decodeInt()==Integer.MAX_VALUE
		dec.is=new ByteArrayInputStream([0x80, 0, 0, 0] as byte[])
		assert dec.decodeInt()==Integer.MIN_VALUE
	}
	
	void testDecodeBoolean() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]
		
		enc.encode false
		enc.encode true
		dec.is=new ByteArrayInputStream(enc.os.toByteArray())
		assert dec.decodeInt()==TYPE_BOOLEAN
		assert dec.decodeBoolean()==false
		assert dec.decodeInt()==TYPE_BOOLEAN
		assert dec.decodeBoolean()==true
	}
	
	void testDecodeString() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]
		
		enc.encode ''
		enc.encode 'Test 123 abc ABC üöäÜÖÄß'
		dec.is=new ByteArrayInputStream(enc.os.toByteArray())
		assert dec.decodeInt()==TYPE_STRING
		assert dec.decodeString()==''
		assert dec.decodeInt()==TYPE_STRING
		assert dec.decodeString()=='Test 123 abc ABC üöäÜÖÄß'
	}
	
	void testDecodeDouble() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]
		
		// DOUBLE-Repräsentationen (von der CCU):
		// 0.0: 00 00 00 00 00 00 00 00
		// 1.0: 20 00 00 00 00 00 00 01
		
		enc.encode 0.0
		enc.encode 1.0
		enc.encode (-1.0)
		enc.encode (1.2345e100)
		
		byte[] data=enc.os.toByteArray()
		// 0.0
		assert data[4..11]==[0, 0, 0, 0, 0, 0, 0, 0]
		// 1.0
		assert data[16..23]==[0x20, 0, 0, 0, 0, 0, 0, 0x01]
		
		dec.is=new ByteArrayInputStream(data)
		assert dec.decodeInt()==TYPE_DOUBLE
		assert dec.decodeDouble()==0.0
		assert dec.decodeInt()==TYPE_DOUBLE
		assert dec.decodeDouble()==1.0
		assert dec.decodeInt()==TYPE_DOUBLE
		assert dec.decodeDouble()==-1.0
		assert dec.decodeInt()==TYPE_DOUBLE
		assert Math.abs(dec.decodeDouble()-1.2345e100)<0.0001e100
	}

	void testDecodeDate() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]
		
		enc.encode new Date(0)
		enc.encode new Date(10000L)
		dec.is=new ByteArrayInputStream(enc.os.toByteArray())
		assert dec.decodeInt()==TYPE_DATE
		assert dec.decodeDate()==new Date(0)
		assert dec.decodeInt()==TYPE_DATE
		assert dec.decodeDate()==new Date(10000L)
	}

	void testDecodeBinary() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]
		
		enc.encode ([] as byte[]) 
		enc.encode ([0, 1, (byte)0x80, (byte)0xff] as byte[])
		
		dec.is=new ByteArrayInputStream(enc.os.toByteArray())
		assert dec.decodeInt()==TYPE_BINARY
		assert dec.decodeBinary()==[] as byte[]
		assert dec.decodeInt()==TYPE_BINARY
		assert dec.decodeBinary()==[0, 1, (byte)0x80, (byte)0xff] as byte[]
	}

	void testDecodeArray() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]

		List test=[0, 'a', new Date(1000), false, 0.25]
		enc.encode test
		dec.is=new ByteArrayInputStream(enc.os.toByteArray())
		assert dec.decodeInt()==TYPE_ARRAY
		assert dec.decodeArray()==test
	}

	void testDecodeStruct() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]

		Map test=[a:0, b:'a', c:new Date(1000), d:false, e:0.25]
		enc.encode test
		dec.is=new ByteArrayInputStream(enc.os.toByteArray())
		assert dec.decodeInt()==TYPE_STRUCT
		assert dec.decodeStruct()==test
	}
	
	void testDecodeComplex() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]

		def test=[a:[1, false, [b:2]]]
		enc.encode test
		dec.is=new ByteArrayInputStream(enc.os.toByteArray())
		assert dec.decodeObject()==test
	}

	void testDecodeRequest() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]

		byte[] data=enc.encodeRequest('xyz', [1, 2, 3])
		BinRpcDecoder.Request req=dec.decodeRequest(data[8..data.length-1] as byte[])
		assert req.methodName=='xyz'
		assert req.parameters==[1, 2, 3]
	}
	
	void testDecodeResponse() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]

		byte[] data=enc.encodeResponse([true, 'a'])
		BinRpcDecoder.Response resp=dec.decodeResponse(data[8..data.length-1] as byte[])
		assert resp.result==[true, 'a']
	}
	
	void testDecodeFault() {
		BinRpcDecoder dec=[]
		BinRpcEncoder enc=[]

		byte[] data=enc.encodeFault(123, 'Text abc ABC üöäÜÖÄß')
		BinRpcDecoder.Fault flt=dec.decodeFault(data[8..data.length-1] as byte[])
		assert flt.faultCode==123
		assert flt.faultString=='Text abc ABC üöäÜÖÄß'
	}
	
	void testHeader() {
		BinRpcDecoder dec=[]

		try {
			dec.decodeHeader([0, 1, 2, 3, 4, 5, 6] as byte[])
			assert 0==1
		} catch (Exception e) {
			assert e.message=='Invalid BIN-RPC header size'
		}
		
		try {
			dec.decodeHeader([0, 1, 2, 3, 4, 5, 6, 7] as byte[])
			assert 0==1
		} catch (Exception e) {
			assert e.message=='Invalid BIN-RPC header start: [0, 1, 2]'
		}
		
		try {
			dec.decodeHeader([0x42, 0x69, 0x6E, 0x80, 0, 0, 0, 0] as byte[])
			assert 0==1
		} catch (Exception e) {
			assert e.message=='Invalid BIN-RPC packet type: -128'
		}
		
		BinRpcDecoder.Header header=dec.decodeHeader([0x42, 0x69, 0x6E, HEADER_RESPONSE, 0, 0, 1, 0] as byte[])
		assert header.type==HEADER_RESPONSE
		assert header.payloadLength==0x100
	}
}
