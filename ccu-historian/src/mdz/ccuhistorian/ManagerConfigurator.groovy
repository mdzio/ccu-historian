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
package mdz.ccuhistorian

import groovy.util.logging.Log
import mdz.hc.itf.Manager
import mdz.hc.itf.hm.HmBinRpcInterface
import mdz.hc.itf.hm.HmXmlRpcInterface
import mdz.hc.itf.hm.HmScriptClient
import mdz.hc.itf.hm.HmReinitTask
import mdz.hc.itf.hm.HmSysVarInterface
import mdz.hc.itf.misc.SimulationInterface

@Log
class ManagerConfigurator {

	private static final int MAX_NUM_DEVICES = 10
	private static final int MAX_NUM_PLUGINS = 10
	
	private final static String INTERFACE_WIRED_NAME = 'BidCos-Wired'
	private final static int INTERFACE_WIRED_PORT = 2000
	
	private final static String INTERFACE_RF_NAME = 'BidCos-RF'
	private final static int INTERFACE_RF_PORT = 2001
	
	private final static String INTERFACE_SYSTEM_NAME = 'System'
	private final static int INTERFACE_SYSTEM_PORT = 2002

	private final static String INTERFACE_HMIP_RF_NAME = 'HmIP-RF'
	private final static int INTERFACE_HMIP_RF_PORT = 2010

	private final static String INTERFACE_CUXD_NAME = 'CUxD'
	private final static int INTERFACE_CUXD_PORT = 8701

	private final static String INTERFACE_SYSVAR_NAME = 'SysVar'
	
	private final static String INTERFACE_SIMULATION_NAME = 'Sim'
	
	public static enum DeviceTypes {
		CCU1, CCU2, BINRPC, XMLRPC, SIMULATION, CUSTOM_CCU 
	}
	
	public static enum PlugInTypes {
		HMWLGW, CUXD, BIDCOS_WIRED /* alias for HMWLGW */, BIDCOS_RF, SYSTEM, HMIP_RF
	}

	private def getOption(ConfigObject devCfg, String name, Class clazz, String prefix='', boolean required=true) {
		def value=devCfg."$name"
		if (value instanceof ConfigObject) value=null
		log.fine "${prefix}Reading configuration option '$name': ${value?:''}" 
		if (value==null)
			if (required)
				throw new Exception("${prefix}Configuration option '$name' is not set")
			else
				return null
		try {
			return value.asType(clazz)
		} catch (Exception e) {
			throw new Exception(prefix+"Configuration option '$name' is invalid", e)
		}
	}
	
	void configure(Manager manager, ConfigObject config) {
		// BIN-RPC port
		Integer binRpcPort=getOption(config, 'historianBinRpcPort', Integer, 'All devices: ', false)
		if (binRpcPort!=null)
			manager.binRpcServer.port=binRpcPort

		// XML-RPC port
		Integer xmlRpcPort=getOption(config, 'historianXmlRpcPort', Integer, 'All devices: ', false)
		if (xmlRpcPort!=null)
			manager.xmlRpcServer.port=xmlRpcPort
	
		// local address
		String historianAddress=getOption(config, 'historianAddress', String, 'All devices: ', false)
		if (historianAddress==null) {
			historianAddress=InetAddress.localHost.hostAddress
			log.info "Auto detected local address (please check): $historianAddress" 
		}
		manager.binRpcServer.localAddress=historianAddress
		manager.xmlRpcServer.localAddress=historianAddress
	
		(1..MAX_NUM_DEVICES).each { idx ->
		 	def cfg=config."device$idx"
			if (cfg) {
				log.info "Setting up device $idx"
				
				DeviceTypes type=getOption(cfg, 'type', DeviceTypes, "Device $idx: ") 

				if (type==DeviceTypes.CCU1 || type==DeviceTypes.CCU2) {
					String address=getOption(cfg, 'address', String, "Device $idx: ") 
					Long reinitTimeout=getOption(cfg, 'reinitTimeout', Long, "Device $idx: ", false) 
					String prefix=getOption(cfg, 'prefix', String, "Device $idx: ", false) 
					if (prefix==null) prefix=''
					Boolean writeAccess=getOption(cfg, 'writeAccess', Boolean, "Device $idx: ", false)
					Integer sysVarDataCycle=getOption(cfg, 'sysVarDataCycle', Integer, "Device $idx: ", false)
					Integer timeout=getOption(cfg, 'timeout', Integer, "Device $idx: ", false)
					
					HmReinitTask reinitTask=new HmReinitTask(manager.executor)
					if (reinitTimeout!=null)
						reinitTask.timeout=reinitTimeout
					HmScriptClient scriptClient=new HmScriptClient(address)

					if (type==DeviceTypes.CCU1) {
						HmBinRpcInterface binRpcItfWired=new HmBinRpcInterface(
							prefix+INTERFACE_WIRED_NAME, INTERFACE_WIRED_NAME, address, INTERFACE_WIRED_PORT,
							manager.binRpcServer, scriptClient, reinitTask, manager.executor, timeout
						)
						if (writeAccess!=null) 
							binRpcItfWired.writeAccess=writeAccess
						manager.addInterface(binRpcItfWired)
					}
					
					HmBinRpcInterface binRpcItfRf=new HmBinRpcInterface(
						prefix+INTERFACE_RF_NAME, INTERFACE_RF_NAME, address, INTERFACE_RF_PORT,
						manager.binRpcServer, scriptClient, reinitTask, manager.executor, timeout
					)
					if (writeAccess!=null) 
						binRpcItfRf.writeAccess=writeAccess
					manager.addInterface(binRpcItfRf)
					
					if (type==DeviceTypes.CCU1) {
						HmBinRpcInterface binRpcItfSys=new HmBinRpcInterface(
							prefix+INTERFACE_SYSTEM_NAME, INTERFACE_SYSTEM_NAME, address, INTERFACE_SYSTEM_PORT,
							manager.binRpcServer, scriptClient, reinitTask, manager.executor, timeout
						)
						if (writeAccess!=null) 
							binRpcItfSys.writeAccess=writeAccess
						manager.addInterface(binRpcItfSys)
					}
					
					if (type==DeviceTypes.CCU2) {
						HmXmlRpcInterface hmIpItf=new HmXmlRpcInterface(
							prefix+INTERFACE_HMIP_RF_NAME, INTERFACE_HMIP_RF_NAME, address, INTERFACE_HMIP_RF_PORT,
							manager.xmlRpcServer, scriptClient, reinitTask, manager.executor
						)
						if (writeAccess!=null)
							hmIpItf.writeAccess=writeAccess
						manager.addInterface(hmIpItf)
					}
					
					HmSysVarInterface sysVarItf
					if (sysVarDataCycle!=null)
						sysVarItf=new HmSysVarInterface(prefix+INTERFACE_SYSVAR_NAME, scriptClient, manager, sysVarDataCycle)
					else
						sysVarItf=new HmSysVarInterface(prefix+INTERFACE_SYSVAR_NAME, scriptClient, manager)
					manager.addInterface(sysVarItf)
					
					(1..MAX_NUM_PLUGINS).each { piIdx ->
						def piCfg=cfg."plugin$piIdx"
						if (piCfg) {
							log.info "Setting up plug-in $piIdx"
							PlugInTypes piType=getOption(piCfg, 'type', PlugInTypes, "Plug-in $piIdx: ")
							
							if (type==DeviceTypes.CCU1 && piType==PlugInTypes.HMWLGW)
								throw new Exception('Plug-in HMWLGW can not be used with a CCU1')

							String name
							int port
							switch (piType) {
								case PlugInTypes.CUXD:
									name=INTERFACE_CUXD_NAME
									port=INTERFACE_CUXD_PORT
									break
								case PlugInTypes.HMWLGW:
									name=INTERFACE_WIRED_NAME
									port=INTERFACE_WIRED_PORT
									break
								default:
									throw new Exception('Plug-in not supported: '+piType)
							}
														
							HmBinRpcInterface binRpcItfPi=new HmBinRpcInterface(
								prefix+name, name, address, port,
								manager.binRpcServer, scriptClient, reinitTask, manager.executor,
								timeout
							)
							if (writeAccess!=null)
								binRpcItfPi.writeAccess=writeAccess
							manager.addInterface(binRpcItfPi)
						}
					}
					
					// watchdog
					String watchdogProgram=getOption(cfg, 'watchdogProgram', String, "Device $idx: ", false)
					Long watchdogCycle=getOption(cfg, 'watchdogCycle', Long, "Device $idx: ", false)
					if (watchdogProgram && watchdogCycle)
						new Watchdog(watchdogProgram, watchdogCycle, manager.executor, scriptClient)
		   
				} else if (type==DeviceTypes.BINRPC) {
					String address=getOption(cfg, 'address', String, "Device $idx: ") 
					String name=getOption(cfg, 'name', String, "Device $idx: ")
					int port=getOption(cfg, 'port', Integer, "Device $idx: ")
					Long reinitTimeout=getOption(cfg, 'reinitTimeout', Long, "Device $idx: ", false)
					Boolean writeAccess=getOption(cfg, 'writeAccess', Boolean, "Device $idx: ", false)
					Integer timeout=getOption(cfg, 'timeout', Integer, "Device $idx: ", false)
					
					HmReinitTask reinitTask=new HmReinitTask(manager.executor)
					if (reinitTimeout!=null)
						reinitTask.timeout=reinitTimeout

					HmBinRpcInterface binRpcItf=new HmBinRpcInterface(
						name, null, address, port, manager.binRpcServer, null, reinitTask, manager.executor, timeout
					)
					if (writeAccess!=null)
						binRpcItf.writeAccess=writeAccess
					manager.addInterface(binRpcItf)
					
				} else if (type==DeviceTypes.XMLRPC) {
					String address=getOption(cfg, 'address', String, "Device $idx: ")
					String name=getOption(cfg, 'name', String, "Device $idx: ")
					int port=getOption(cfg, 'port', Integer, "Device $idx: ")
					Long reinitTimeout=getOption(cfg, 'reinitTimeout', Long, "Device $idx: ", false)
					Boolean writeAccess=getOption(cfg, 'writeAccess', Boolean, "Device $idx: ", false)
					Integer timeout=getOption(cfg, 'timeout', Integer, "Device $idx: ", false)
					
					HmReinitTask reinitTask=new HmReinitTask(manager.executor)
					if (reinitTimeout!=null)
						reinitTask.timeout=reinitTimeout

					HmXmlRpcInterface xmlRpcItf=new HmXmlRpcInterface(
						name, null, address, port, manager.xmlRpcServer, null, reinitTask, manager.executor
					)
					if (writeAccess!=null)
						xmlRpcItf.writeAccess=writeAccess
					manager.addInterface(xmlRpcItf)

				} else if (type==DeviceTypes.SIMULATION) {
					String name=getOption(cfg, 'name', String, "Device $idx: ", false)
					if (name==null) name=INTERFACE_SIMULATION_NAME
					Long dataCycle=getOption(cfg, 'dataCycle', Long, "Device $idx: ", false)
					Integer dataPointCount=getOption(cfg, 'dataPointCount', Integer, "Device $idx: ", false)
					Boolean writeAccess=getOption(cfg, 'writeAccess', Boolean, "Device $idx: ", false)
					
					SimulationInterface itf=new SimulationInterface(name, manager)
					if (dataCycle!=null) itf.dataCycle=dataCycle
					if (dataPointCount!=null) itf.dataPointCount=dataPointCount 
					if (writeAccess!=null) itf.writeAccess=writeAccess
					manager.addInterface(itf)
					
				} else if (type==DeviceTypes.CUSTOM_CCU) {
					String address=getOption(cfg, 'address', String, "Device $idx: ")
					Long reinitTimeout=getOption(cfg, 'reinitTimeout', Long, "Device $idx: ", false)
					String prefix=getOption(cfg, 'prefix', String, "Device $idx: ", false)
					if (prefix==null) prefix=''
					Boolean writeAccess=getOption(cfg, 'writeAccess', Boolean, "Device $idx: ", false)
					Integer sysVarDataCycle=getOption(cfg, 'sysVarDataCycle', Integer, "Device $idx: ", false)
					Integer timeout=getOption(cfg, 'timeout', Integer, "Device $idx: ", false)
					
					HmReinitTask reinitTask=new HmReinitTask(manager.executor)
					if (reinitTimeout!=null)
						reinitTask.timeout=reinitTimeout
					HmScriptClient scriptClient=new HmScriptClient(address)

					HmSysVarInterface sysVarItf
					if (sysVarDataCycle!=null)
						sysVarItf=new HmSysVarInterface(prefix+INTERFACE_SYSVAR_NAME, scriptClient, manager, sysVarDataCycle)
					else
						sysVarItf=new HmSysVarInterface(prefix+INTERFACE_SYSVAR_NAME, scriptClient, manager)
					manager.addInterface(sysVarItf)

					Set<PlugInTypes> seen=[]
					(1..MAX_NUM_PLUGINS).each { piIdx ->
						def piCfg=cfg."plugin$piIdx"
						if (piCfg) {
							log.info "Setting up plug-in $piIdx"
							PlugInTypes piType=getOption(piCfg, 'type', PlugInTypes, "Plug-in $piIdx: ")
							
							if (piType in seen)
								throw new Exception('Duplicate plug-in: '+piType)
							seen << piType
							
							String name
							int port
							switch (piType) {
								case PlugInTypes.HMWLGW:
								case PlugInTypes.BIDCOS_WIRED:
									name=INTERFACE_WIRED_NAME
									port=INTERFACE_WIRED_PORT
									break;
								case PlugInTypes.CUXD:
									name=INTERFACE_CUXD_NAME
									port=INTERFACE_CUXD_PORT
									break
								case PlugInTypes.BIDCOS_RF:
									name=INTERFACE_RF_NAME
									port=INTERFACE_RF_PORT
									break
								case PlugInTypes.SYSTEM:
									name=INTERFACE_SYSTEM_NAME
									port=INTERFACE_SYSTEM_PORT
									break
								case PlugInTypes.HMIP_RF:
									name=INTERFACE_HMIP_RF_NAME
									port=INTERFACE_HMIP_RF_PORT
									break
								default:
									throw new Exception('Plug-in not supported: '+piType)
							}

							if (piType!=PlugInTypes.HMIP_RF) {				
								HmBinRpcInterface binRpcItfPi=new HmBinRpcInterface(
									prefix+name, name, address, port,
									manager.binRpcServer, scriptClient, reinitTask, manager.executor,
									timeout
								)
								if (writeAccess!=null)
									binRpcItfPi.writeAccess=writeAccess
								manager.addInterface(binRpcItfPi)
							} else {
								HmXmlRpcInterface hmIpItf=new HmXmlRpcInterface(
									prefix+name, name, address, port,
									manager.xmlRpcServer, scriptClient, reinitTask, manager.executor
								)
								if (writeAccess!=null)
									hmIpItf.writeAccess=writeAccess
								manager.addInterface(hmIpItf)
							}
						}
					}
					
					// watchdog
					String watchdogProgram=getOption(cfg, 'watchdogProgram', String, "Device $idx: ", false)
					Long watchdogCycle=getOption(cfg, 'watchdogCycle', Long, "Device $idx: ", false)
					if (watchdogProgram && watchdogCycle)
						new Watchdog(watchdogProgram, watchdogCycle, manager.executor, scriptClient)
				}
			}
		}
		log.info "Configured following interfaces: ${manager.getInterfaceNames().join(', ')}" 
	}
}
