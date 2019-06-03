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

import mdz.hc.itf.binrpc.BinRpcClient
import mdz.hc.itf.binrpc.BinRpcServer

class BinRpcServerTest {

	@Test
	void testServer() {
		BinRpcServer server=[]
		server.port=80
		server.procedures.echo = { params -> params }
		server.start()

		BinRpcClient client=[]
		client.host='localhost'
		client.port=server.port
		client.executor=Executors.newSingleThreadScheduledExecutor()
		
		def res=client.call('echo', [123, 'abc', true])
		assert res==[123, 'abc', true]
		
		res=client.call('echo', [[a:1, b:2]])
		assert res==[[a:1, b:2]]

		server.stop()
	}
	
	@Test
	void test2Clients() {
		BinRpcServer server=[]
		server.port=80
		server.procedures.echo ={ params -> params }
		server.start()

		BinRpcClient client=[]
		client.host='localhost'
		client.port=server.port
		client.executor=Executors.newSingleThreadScheduledExecutor()
		
		BinRpcClient client2=[]
		client2.host='localhost'
		client2.port=server.port
		client2.executor=Executors.newSingleThreadScheduledExecutor()
		
		def res=client.call('echo', [1, 2, 3])
		assert res==[1, 2, 3]
		res=client2.call('echo', [4, 5, 6])
		assert res==[4, 5, 6]
		
		server.stop()
	}
	
	@Test
	void testMultiCall() {
		BinRpcServer server=[]
		server.port=80
		server.procedures.echo={ params -> params }
		server.procedures.mul2={ params -> params[0]*2 }
		server.procedures.add={ params -> params[0]+params[1] }
		server.start()

		BinRpcClient client=[]
		client.host='localhost'
		client.port=server.port
		client.executor=Executors.newSingleThreadScheduledExecutor()
		
		def res=client.call('system.multicall', [[
			[methodName:'echo', params:['abc']],
			[methodName:'mul2', params:[3]],
			[methodName:'add', params:[2, 5]]
		]])
		assert res==[['abc'], 6, 7]
		
		server.stop()
	}
	
	@Test
	void testRestart() {
		BinRpcServer server=[]
		server.port=80
		server.procedures.echo={ params -> params }
		server.start()
		
		BinRpcClient client=[]
		client.host='localhost'
		client.port=server.port
		client.executor=Executors.newSingleThreadScheduledExecutor()
		assert client.call('echo', [123])==[123]
		
		server.stop()
		server.start()
		
		try { 
			client.call('echo', [123])
			assert 0 
		} catch (Exception e) { }
		assert client.call('echo', [123])==[123]
		
		server.stop()
	}
}
