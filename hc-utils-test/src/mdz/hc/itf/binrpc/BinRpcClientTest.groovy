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

import java.util.concurrent.Executors

import org.junit.Test

import mdz.hc.TestConfiguration
import mdz.hc.itf.binrpc.BinRpcClient
import mdz.hc.itf.binrpc.BinRpcException

class BinRpcClientTest {

	@Test
	void testHttpConnection() {
		BinRpcClient c=new BinRpcClient()
		c.executor=Executors.newScheduledThreadPool(1)
		c.host='www.ccu-historian.de'
		c.port=80
		c.send 'GET / HTTP/1.1\r\nHost: www.ccu-historian.de\r\n\r\n'.getBytes()
		byte[] data=new byte[17]
		c.receive data
		assert new String(data)=='HTTP/1.1 200 OK\r\n'
	}
	
	@Test
	void testCcuRaw() {
		BinRpcClient c=new BinRpcClient()
		c.executor=Executors.newScheduledThreadPool(1)
		c.host=TestConfiguration.CCU_ADDRESS
		c.port=TestConfiguration.INTERFACE_RF_PORT
		byte[] data=[ 'B', 'i', 'n', 0x00, // Request Header
			0x00, 0x00, 0x00, 0x20, // Gesamtgröße (32)
			0x00, 0x00, 0x00, 0x10, // Länge Methodenname (16)
			's', 'y', 's', 't', 'e', 'm', '.', 'm', 'u', 'l', 't', 'i', 'c', 'a', 'l', 'l', // Methodenname
			0x00, 0x00, 0x00, 0x01, // Anzahl Argumente
			0x00, 0x00, 0x01, 0x00, // Typ Array
			0x00, 0x00, 0x00, 0x00  // Länge Array
		]
		c.send data
		data=new byte[16]
		c.receive data
		assert data==[ 'B', 'i', 'n', 0x01, // Response Header
			0x00, 0x00, 0x00, 0x08, // Gesamtgröße (8)
			0x00, 0x00, 0x01, 0x00, // Typ Array
			0x00, 0x00, 0x00, 0x00  // Länge Array
		]
		
		data=[ 'B', 'i', 'n', 0x00, // Request Header
			0x00, 0x00, 0x00, 0x13, // Gesamtgröße (19)
			0x00, 0x00, 0x00, 0x03, // Länge Methodenname (16)
			'x', 'y', 'z', // Methodenname
			0x00, 0x00, 0x00, 0x01, // Anzahl Argumente
			0x00, 0x00, 0x01, 0x00, // Typ Array
			0x00, 0x00, 0x00, 0x00  // Länge Array
		]
		c.send data
		data=new byte[84]
		c.receive data
		assert data==[66, 105, 110, -1, 0, 0, 0, 76, 0, 0, 1, 1, 0, 0, 0, 2, 0, 0, 0, 9, 102, 97, 117, 108, 
			116, 67, 111, 100, 101, 0, 0, 0, 1, -1, -1, -1, -1, 0, 0, 0, 11, 102, 97, 117, 108, 116, 83, 
			116, 114, 105, 110, 103, 0, 0, 0, 3, 0, 0, 0, 24, 120, 121, 122, 58, 32, 117, 110, 107, 110, 
			111, 119, 110, 32, 109, 101, 116, 104, 111, 100, 32, 110, 97, 109, 101]
	}

	@Test
	void testWithCcu() {
		BinRpcClient c=new BinRpcClient()
		c.executor=Executors.newScheduledThreadPool(1)
		c.host=TestConfiguration.CCU_ADDRESS
		c.port=TestConfiguration.INTERFACE_RF_PORT
		
		def res=c.call('system.multicall', [[]])
		assert res==[]
		
		try {
			c.call('xyz', [])
			assert 0==1
		} catch (BinRpcException e) {
			assert e.faultCode==-1
			assert e.message=='xyz: unknown method name'
		}

		res=c.call('listDevices', [])
		assert res instanceof List
		assert res[0] instanceof Map
		assert res[0].ADDRESS=='BidCoS-RF'
		
		def address='IEQ0018941:1'
		def paramSet='VALUES'
		res=c.call('getParamsetDescription', [address, paramSet])
		assert res.LEVEL.CONTROL=='BLIND.LEVEL'
	}
}
