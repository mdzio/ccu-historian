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

import groovy.net.xmlrpc.XMLRPCServerProxy as Proxy
import groovy.util.logging.Log
import groovy.lang.Lazy

@Log
public class HmXmlRpcClient {

	String host
	int port
	
	@Lazy
	private Proxy proxy=new Proxy("http://$host:$port")
	
	public List<String> systemListMethods() {
		log.fine "Calling system.listMethods()"
		def result=getProxy().system.listMethods()
		if (!(result in List) || !result.every { it in String })
			throw new Exception("XML-RPC call system.listMethods returned invalid data structure: " + result)
		log.finer "Response: ${result.join(", ")}"
		result
	}
		
	public void init(String url, String interfaceId) {
		log.fine "Calling init($url, $interfaceId)"
		proxy.init url, interfaceId
	}

	public void deinit(String url) {
		log.fine "Calling init($url)"
		proxy.init url
	}	

	public Map<String, Map<String, Object>> getParamsetDescription(String address, String type) {
		log.fine "Calling getParamsetDescription($address, $type)"
		def result=proxy.getParamsetDescription(address, type)
		if (!(result in Map) || 
			!result.every { 
				it.key in String && it.value in Map &&
				it.value.every {
					it.key in String
					// it.value is of type Boolean, Integer, Double, String or if 
					// TYPE=='FLOAT': SPECIAL=Map<String, Double>
					// TYPE=='INTEGER': SPECIAL=Map<String, Integer>
					// TYPE=='ENUM': VALUE_LIST=List<String>
				} 
			}) throw new Exception("XML-RPC call getParamsetDescription returned invalid data structure: " + result)
		log.finer "Result: $result"
		result
	}

	public void setValue(String address, String identifier, value) {
		log.fine "Calling setValue($address, $identifier, $value)"
		proxy.setValue address, identifier, value
	}
	
	public void event(String interfaceId, String address, String key, value) {
		log.fine "Calling event($interfaceId, $address, $key, $value)"
		proxy.event interfaceId, address, key, value
	}
}
