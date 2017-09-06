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
import mdz.hc.DataPoint
import mdz.hc.TestConfiguration

class HmXmlRpcClientTest extends GroovyTestCase {

	private HmXmlRpcClient createClient() {
		[host:TestConfiguration.CCU_ADDRESS, port:TestConfiguration.INTERFACE_RF_PORT]
	}
	
	void testSystemListMethods() {
		HmXmlRpcClient clnt=createClient()
		List<String> res=clnt.systemListMethods()
		assert res.containsAll([
			"getParamsetDescription", "init", "ping", "setValue", 
			"system.listMethods", "system.multicall"
		])
	}
	
	void testGetParameterDescr() {
		HmXmlRpcClient clnt=createClient()
		Map<String, Map<String, Object>> params=clnt.getParamsetDescription("IEQ0018941:1", "VALUES")
		assert params.LEVEL.MIN==0
		assert params.LEVEL.MAX==1
		assert params.LEVEL.UNIT=="100%"
	}
}
