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

import groovy.util.GroovyTestCase
import mdz.eventprocessing.Collector
import mdz.hc.RawEvent
import uk.co.wilson.net.xmlrpc.XMLRPCFailException

class HmXmlRpcServerTest extends GroovyTestCase {

	void testStandalone() {
		HmXmlRpcServer svr=[]
		Collector<RawEvent> col=[]
		svr.addConsumer(col)
		svr.start()

		try {
			HmXmlRpcClient clnt=[host:'localhost', port:svr.port]
			
			List<String> methods=clnt.systemListMethods()
			assert methods.containsAll(['event', 'listDevices', 'newDevices', 'deleteDevices', 
				'updateDevice', 'system.multicall', 'system.listMethods'])
			
			assert col.size()==0
			clnt.event('itf', 'addr', 'key', 123)
			assert col.size()==1
			RawEvent e=col.get()[0]
			assert e.id.interfaceId=='itf'
			assert e.id.address=='addr'
			assert e.id.identifier=='key'
			assert e.pv.value==123
		} finally {
			svr.stop()
		}
	}
}
