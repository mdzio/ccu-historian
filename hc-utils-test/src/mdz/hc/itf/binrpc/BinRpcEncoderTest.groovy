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

import org.junit.Test

import mdz.hc.itf.binrpc.BinRpcEncoder;

class BinRpcEncoderTest {

	@Test
	void testRaw() {
		BinRpcEncoder enc=[]
		
		assert enc.raw(0)==[0x00, 0x00, 0x00, 0x00] as byte[]
		assert enc.raw(1)==[0x00, 0x00, 0x00, 0x01] as byte[]
		assert enc.raw(-1)==[0xFF, 0xFF, 0xFF, 0xFF] as byte[]
		assert enc.raw(Integer.MAX_VALUE)==[0x7F, 0xFF, 0xFF, 0xFF] as byte[]
		assert enc.raw(Integer.MIN_VALUE)==[0x80, 0x00, 0x00, 0x00] as byte[]
	}
	
	@Test
	void testWrite() {
		BinRpcEncoder enc=[]
		
		enc.write 65536
		enc.write 'faultCode'
		assert enc.os.toByteArray()==[
			0x00, 0x01, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x09,
			'f', 'a', 'u', 'l', 't', 'C', 'o', 'd', 'e'
		]
	}
	
	@Test
	void testEncodeIntBoolStr() {
		BinRpcEncoder enc=[]
		
		enc.encode 256
		enc.encode true
		enc.encode false
		enc.encode 'xyz2'
		
		assert (enc.os.toByteArray() as List)==[
			0x00, 0x00, 0x00, 0x01,
			0x00, 0x00, 0x01, 0x00,
			0x00, 0x00, 0x00, 0x02,
			0x01, 
			0x00, 0x00, 0x00, 0x02,
			0x00,
			0x00, 0x00, 0x00, 0x03,
			0x00, 0x00, 0x00, 0x04,
			'x', 'y', 'z', '2'
		]
	}
	
	@Test
	void testEncodeDouble() {
		BinRpcEncoder enc=[]
		
		enc.encode 0.0
		enc.encode 1234.0
		enc.encode(-1.0)
		
		assert enc.os.toByteArray()==[
			0x00, 0x00, 0x00, 0x04,
			0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x04,
			0x26, 0x90, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x0B,
			0x00, 0x00, 0x00, 0x04,
			0xe0, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x01
		] as byte[]
	}
	
	@Test
	void testEncodeDate() {
		BinRpcEncoder enc=[]
		
		enc.encode new Date(0)
		enc.encode new Date(1000L)
		
		assert enc.os.toByteArray()==[
			0x00, 0x00, 0x00, 0x05,
			0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x05,
			0x00, 0x00, 0x00, 0x01
		]
	}

	@Test
	void testEncodeBinary() {
		BinRpcEncoder enc=[]
		
		enc.encode ([] as byte[]) 
		enc.encode ([0, 1, (byte)0x80, (byte)0xff] as byte[])
		
		assert enc.os.toByteArray()==[
			0x00, 0x00, 0x00, 0x06,
			0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x06,
			0x00, 0x00, 0x00, 0x04,
			0x00, 0x01, (byte)0x80, (byte)0xff
		]
	}

	@Test
	void testEncodeList() {
		BinRpcEncoder enc=[]
		
		enc.encode([
			4, true, 'abc' 
		])
		
		assert enc.os.toByteArray()==[
			0x00, 0x00, 0x01, 0x00,
			0x00, 0x00, 0x00, 0x03,
			0x00, 0x00, 0x00, 0x01,
			0x00, 0x00, 0x00, 0x04,
			0x00, 0x00, 0x00, 0x02,
			0x01,
			0x00, 0x00, 0x00, 0x03,
			0x00, 0x00, 0x00, 0x03,
			'a', 'b', 'c'
		]
	}
	
	@Test
	void testEncodeMap() {
		BinRpcEncoder enc=[]
		
		enc.encode([
			a: false,
			b: 'defg'
		])
		
		assert enc.os.toByteArray()==[
			0x00, 0x00, 0x01, 0x01,
			0x00, 0x00, 0x00, 0x02,
			0x00, 0x00, 0x00, 0x01,
			'a',
			0x00, 0x00, 0x00, 0x02,
			0x00,
			0x00, 0x00, 0x00, 0x01,
			'b',
			0x00, 0x00, 0x00, 0x03,
			0x00, 0x00, 0x00, 0x04,
			'd', 'e', 'f', 'g'
		]
	}
	
	@Test
	void testEncodeComplex() {
		BinRpcEncoder enc=[]
		
		enc.encode([
			a: [[b:false], true]
		])
		
		assert enc.os.toByteArray()==[
			0x00, 0x00, 0x01, 0x01,
			0x00, 0x00, 0x00, 0x01,
			0x00, 0x00, 0x00, 0x01,
			'a',
			0x00, 0x00, 0x01, 0x00,
			0x00, 0x00, 0x00, 0x02,
			0x00, 0x00, 0x01, 0x01,
			0x00, 0x00, 0x00, 0x01,
			0x00, 0x00, 0x00, 0x01,
			'b',
			0x00, 0x00, 0x00, 0x02,
			0x00,
			0x00, 0x00, 0x00, 0x02,
			0x01
		]
	}
	
	@Test
	void testRequest() {
		BinRpcEncoder enc=[]
		byte[] res=enc.encodeRequest('system.multicall', [[]])
		byte[] data=[ 'B', 'i', 'n', 0x00, // Request Header
			0x00, 0x00, 0x00, 0x20, // Gesamtgröße (32)
			0x00, 0x00, 0x00, 0x10, // Länge Methodenname (16)
			's', 'y', 's', 't', 'e', 'm', '.', 'm', 'u', 'l', 't', 'i', 'c', 'a', 'l', 'l', // Methodenname
			0x00, 0x00, 0x00, 0x01, // Anzahl Argumente
			0x00, 0x00, 0x01, 0x00, // Typ Array
			0x00, 0x00, 0x00, 0x00  // Länge Array
		]
		assert res==data
	}
	
	@Test
	void testResponse() {
		BinRpcEncoder enc=[]
		byte[] res=enc.encodeResponse([])
		byte[] data=[ 'B', 'i', 'n', 0x01, // Response Header
			0x00, 0x00, 0x00, 0x08, // Gesamtgröße (8)
			0x00, 0x00, 0x01, 0x00, // Typ Array
			0x00, 0x00, 0x00, 0x00  // Länge Array
		]
		assert res==data
	}
	
	@Test
	void testFault() {
		BinRpcEncoder enc=[]
		byte[] res=enc.encodeFault(-1, 'xyz: unknown method name')
		byte[] data=[66, 105, 110, -1, 0, 0, 0, 76, 0, 0, 1, 1, 0, 0, 0, 2, 0, 0, 0, 9, 102, 97, 117, 108, 
			116, 67, 111, 100, 101, 0, 0, 0, 1, -1, -1, -1, -1, 0, 0, 0, 11, 102, 97, 117, 108, 116, 83, 
			116, 114, 105, 110, 103, 0, 0, 0, 3, 0, 0, 0, 24, 120, 121, 122, 58, 32, 117, 110, 107, 110, 
			111, 119, 110, 32, 109, 101, 116, 104, 111, 100, 32, 110, 97, 109, 101]
		assert res==data
	}
}
