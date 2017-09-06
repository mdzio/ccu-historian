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
import mdz.hc.itf.Manager
import mdz.hc.TestConfiguration

class HmBinRpcInterfaceTest extends GroovyTestCase {

	void test() {
		Manager mngr=new Manager()
		try {
			HmReinitTask reinit=new HmReinitTask(mngr.executor)
			HmScriptClient script=new HmScriptClient(TestConfiguration.CCU_ADDRESS)
			HmBinRpcInterface binRpcItf=new HmBinRpcInterface(
				TestConfiguration.INTERFACE_RF_NAME, TestConfiguration.INTERFACE_RF_NAME, 
				TestConfiguration.CCU_ADDRESS, TestConfiguration.INTERFACE_RF_PORT, 
				mngr.binRpcServer, script, reinit, mngr.executor, 2000 /* timeout [ms] */
			)
			binRpcItf.disableRegistration=true
			mngr.addInterface(binRpcItf)
			
			mngr.start()
			
			DataPoint dp=[id: new DataPointIdentifier(TestConfiguration.INTERFACE_RF_NAME, 'BidCoS-RF:1', 'PRESS_LONG')]
			binRpcItf.updateProperties([dp], 2000)
			assert dp.attributes.flags==1
			assert dp.attributes.type=='ACTION'
			assert dp.attributes.operations==6
			assert dp.attributes.control=='BUTTON.LONG'
			assert dp.attributes.minimum==false
			assert dp.attributes.maximum==true
			assert dp.attributes.paramSet=='VALUES'
		} finally { 	
			mngr.stop() 
		}
	}
}
