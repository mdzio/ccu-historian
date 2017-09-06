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
package mdz.hc.itf.hm;

import groovy.util.GroovyTestCase
import groovy.util.logging.Slf4j
import mdz.hc.TestConfiguration

@Slf4j(value='l')
class HmScriptClientTest extends GroovyTestCase {
	
	void testExecute() {
		HmScriptClient script=new HmScriptClient(TestConfiguration.CCU_ADDRESS)
		
		long t=System.currentTimeMillis()
		assert ['test1']==script.execute('WriteLine("test1");')
		assert ['test2']==script.execute('WriteLine("test2");')
		t=System.currentTimeMillis()-t
		l.debug "Script delay: {} ms", t
		assert t>=script.DEFAULT_SCRIPT_PAUSE
	}
	
	void testGetSystemDate() {
		HmScriptClient script=new HmScriptClient(TestConfiguration.CCU_ADDRESS)
		Date tccu=script.getSystemDate()
		Date thost=new Date()
		long d=tccu.time-thost.time
		l.debug "Host time: $thost, CCU time: $tccu, difference: $d ms"
		// difference should be below 2 minutes
		assert Math.abs(d)<120000
	}
	
	void testHmModel() {
		HmScriptClient script=new HmScriptClient(TestConfiguration.CCU_ADDRESS)
		
		HmModel m=script.getModel(10000)
		
		HmModel.Device cent=m.devices.find { it.displayName=='Automatisierungszentrale' }
		assert cent?.iseId==1389
		assert cent.address=='System'
		
		HmModel.Channel ch=cent.channels.find { it.displayName=='Zentralennetzteil' }
		assert ch?.iseId==1390
		assert ch.address=='System:1'
		assert ch.device==cent
		
		HmModel.DataPoint dp=ch.dataPoints.find { it.address=='System.System:1.LOWBAT' }
		assert dp?.iseId==1393
		
		HmModel.Room room=m.rooms.find { it.displayName=='HWR' }
		assert room?.iseId==1256
		assert m.getChannelByIseId(11491) in room.channels
		
		HmModel.Function func=m.functions.find { it.displayName=='Licht' }
		assert func?.iseId==1238
		assert m.getChannelByIseId(2182) in func.channels
		
		// test caching
		long t=System.currentTimeMillis()
		m=script.getModel(1000)
		t=System.currentTimeMillis()-t
		assert t<1000
	}
}
