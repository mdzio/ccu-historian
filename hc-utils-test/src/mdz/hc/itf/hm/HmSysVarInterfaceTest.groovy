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
import mdz.hc.DataPointIdentifier
import mdz.hc.itf.*
import mdz.hc.TestConfiguration

class HmSysVarInterfaceTest extends GroovyTestCase {
	
	void test() {
		Manager mngr=new Manager()
		addShutdownHook { mngr.stop() }
		HmScriptClient script=new HmScriptClient(TestConfiguration.CCU_ADDRESS)
		HmSysVarInterface sysVarItf=new HmSysVarInterface(
			TestConfiguration.INTERFACE_SYSVAR_NAME, script, mngr, 2000)
		mngr.addInterface(sysVarItf)

		mngr.start()
		
		List<DataPointIdentifier> dpIds=sysVarItf.getAllDataPoints(1000)
		assert new DataPointIdentifier("SysVar", "4531", "VALUE") in dpIds
		assert new DataPointIdentifier("SysVar", "10191", "VALUE") in dpIds
		
		List<DataPoint> dps=dpIds.collect { new DataPoint(id: it) }
		sysVarItf.updateProperties(dps, 1000)
		DataPoint foundDb=dps.find { DataPoint dp -> dp.id.address=='8090' }
		assert foundDb!=null
		assert foundDb.attributes.displayName=="Zisternenfüllstand"
		assert foundDb.attributes.unit=="%"
		assert foundDb.attributes.maximum==100
		assert foundDb.attributes.minimum==0
		assert foundDb.attributes.type=="FLOAT"
		
		// read current values, inital delay is 5000 ms
		sysVarItf.subscription=dps
		sleep 5500
		mngr.stop()
	}
}
