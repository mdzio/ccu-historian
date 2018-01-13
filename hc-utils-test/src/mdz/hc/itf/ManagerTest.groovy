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
package mdz.hc.itf;

import mdz.hc.TestConfiguration
import mdz.hc.itf.Manager
import mdz.hc.itf.hm.HmBinRpcInterface
import mdz.hc.itf.hm.HmReinitTask
import mdz.hc.itf.hm.HmScriptClient
import mdz.hc.itf.hm.HmSysVarInterface
import groovy.util.GroovyTestCase

class ManagerTest extends GroovyTestCase {

	void test() {
		Manager mngr=new Manager()
		addShutdownHook { mngr.stop() }
		HmScriptClient script=new HmScriptClient(TestConfiguration.CCU_ADDRESS)
		HmSysVarInterface sysVarItf=new HmSysVarInterface(
			TestConfiguration.INTERFACE_SYSVAR_NAME, script, mngr, 5000)
		mngr.addInterface(sysVarItf)
		mngr.start()
		((HmSysVarInterface)mngr[TestConfiguration.INTERFACE_SYSVAR_NAME]).getAllDataPoints(0)
		mngr.stop()
	}
}
