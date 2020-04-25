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

import groovy.util.logging.Log
import groovy.transform.CompileStatic

import java.text.SimpleDateFormat
import java.text.ParseException
import mdz.Exceptions
import mdz.hc.RawEvent
import mdz.hc.ProcessValue
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier

@Log
@CompileStatic
public class HmScriptClient {

	private static String VALUE_TERMINATOR = '\1'
	private static long FUTURE_TIMESTAMP_LIMIT = 30*60*1000 // 30 minutes
	private final static String DEFAULT_IDENTIFIER = "VALUE"; // like device data points
	private final static int DEFAULT_CONNECT_TIMEOUT = 10000 // ms
	private final static long DEFAULT_SCRIPT_PAUSE = 200 // ms

	final String address
		
	private final URL url
	private long lastScriptExec
	private HmModel model
	private long modelLastScan
	private Object modelMutex=[]
	private String auth
	
	public HmScriptClient(String address, String username, String password) {
		this.address=address
		if (username) {
			auth='Basic '+(username+':'+password?:'').bytes.encodeBase64() 
		}
		String host="http://$address:8181/tclrega.exe"
		log.info "Creating HM script client for $host"
		url=[host]
	}
	
	public List<DataPoint> getSystemVariables(String interfaceIdFiller) {
		log.finer 'Getting list of system variables'
		List<String> response=execute('''string id; foreach(id, dom.GetObject(ID_SYSTEM_VARIABLES).EnumIDs()) {
			var sv=dom.GetObject(id);
			var vt=sv.ValueType(); var st=sv.ValueSubType();
			var outvt="";
			if ((vt==ivtBinary) && (st==istBool)) { outvt="BOOL"; }
			if ((vt==ivtBinary) && (st==istAlarm)) { outvt="ALARM"; }
			if ((vt==ivtInteger) && (st==istEnum)) { outvt="ENUM"; }
			if ((vt==ivtFloat) && (st==istGeneric)) { outvt="FLOAT"; }
			if ((vt==ivtString) && (st==istChar8859)) { outvt="STRING"; }
			if (outvt!="") { WriteLine(id # "\t" # sv.Name() # "\t" # sv.ValueMax() # "\t" # sv.ValueUnit() # "\t" # 
				sv.ValueMin() # "\t" # sv.Operations() # "\t" # outvt); }
		}''')
		List<DataPoint> dataPoints=[]
		response.each { String line ->
			String[] items=line.split(/\t/, -1)
			if (items.length==7) {
				def min, max
				switch (items[6]) {
				case 'BOOL': min=0.0; max=1.0; break
				case 'ALARM': min=0.0; max=1.0; break
				case 'FLOAT': 
					Exceptions.catchToLog(log) { max=items[2].toDouble() }
					Exceptions.catchToLog(log) { min=items[4].toDouble() }
					break
				}
				dataPoints << new DataPoint(
					id: new DataPointIdentifier(interfaceIdFiller, items[0], DEFAULT_IDENTIFIER),
					attributes: [
						displayName: items[1],
						maximum: max,
						unit: items[3],
						minimum: min,
						operations: items[5].toInteger(),
						type: items[6]
					] as Map<String, Object>
				)
			} else
				log.warning "Invalid response: $line"
		}
		dataPoints
	}

	public List<RawEvent> getSystemVariableValues(List<DataPoint> dataPoints) {
		log.finer 'Getting values of system variables'
		if (!dataPoints) return []
		String sysVarIds=dataPoints.id*.address.join('\t')
		List<String> response=execute('''string id; foreach(id, "''' + sysVarIds + '''") {
			var sv=dom.GetObject(id); 
			if (sv) {
				if (sv.IsTypeOf(OT_VARDP) || sv.IsTypeOf(OT_ALARMDP)) { 
					WriteLine("0"); WriteLine(sv.Timestamp());
					Write(sv.Value()); WriteLine("''' + VALUE_TERMINATOR + '''");
				} else {
					WriteLine("2"); 
				}
			} else { 
				WriteLine("1"); 
			}
		}''')
		SimpleDateFormat df=new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
		int lineIdx=0
		Date maxTimestamp=[]
		maxTimestamp.time+=FUTURE_TIMESTAMP_LIMIT
		dataPoints.collect { DataPoint dp ->
			if (response[lineIdx++]=="0") {
				def strval=response[lineIdx++]
				// manchmal werden ungï¿½ltige Zeitstempel von der CCU angeliefert
				Date timestamp
				try {
					timestamp=df.parse(strval)
					if (timestamp.time==0 || timestamp>maxTimestamp) {
						log.fine "Timestamp $strval of system variable $dp.attributes.displayName is out of range"
						timestamp=null
					}
				} catch (ParseException e) {
					log.fine "Can't parse $strval as timestamp"
				}
				strval=response[lineIdx++]
				while (!strval.endsWith(VALUE_TERMINATOR)) { strval+='\n'+response[lineIdx++] }
				strval=strval.substring(0, strval.length()-1)
				def value
				try {
					switch (dp.attributes.type) {
					case 'BOOL': case 'ALARM': value=strval.toBoolean(); break
					case 'ENUM': value=strval.toInteger(); break
					case 'FLOAT': value=strval.toDouble(); break
					case 'STRING': value=strval; break
					default:
						log.warning "Unknown data type: $dp.attributes.type"
					}
				} catch (NumberFormatException e) {
					log.warning "Value $strval is not valid for data type $dp.attributes.type of data point $dp.id"
				}
				if (value!=null) new RawEvent(
					id: dp.id, 
					pv: new ProcessValue(timestamp, value, ProcessValue.STATE_QUALITY_NOT_SUPPORTED)
				)
				else null
			} else {
				log.warning "Error reading system variable $dp.attributes.displayName"
				null
			}
		}
	}
	
	public void setSystemVariableValue(DataPoint dp, value) {
		if (value instanceof String) {
			// Attention: The characters " or \ in a string are not handled correctly by the CCU!
			if (value.contains('"') || value.contains('\\'))
				throw new Exception("HM script client: Value $value contains invalid character for data point $dp.id")
			value='"'+value+'"'
		} else value=value.toString()
		List<String> response=execute('''var sv=dom.GetObject('''+dp.id.address+''');
			if (sv) {
				sv.State('''+value+''');
				WriteLine("0");
			} else { 
				WriteLine("1"); 
			}
		}''')
		if (response.size()!=1 || response[0]!='0')
			throw new Exception("HM script client: Setting data point $dp.id to value $value failed")
	}
	
	private void retrieveChannels(HmModel.Device dev) {
		List<String> res=execute('''var dev=dom.GetObject(''' + dev.iseId + ''');
			if (dev && dev.Type()==OT_DEVICE) {
				string chId; foreach(chId, dev.Channels()) {
					var ch=dom.GetObject(chId);
					Write(chId # "\t" # ch.Name() # "\t" # ch.Address());
					string dpId; foreach(dpId, ch.DPs()) {
        				var dp=dom.GetObject(dpId);
        				Write("\t" # dpId # "\t" # dp.Name());
					}
					WriteLine("");
				}
			}''')
		res.each { String line ->
			String[] items=line.split(/\t/, -1)
			if (items.length<3 || ((items.length-3)&1)!=0)
				throw new Exception("HM script client: Invalid response line (wrong number of fields): $line");
			if (!items[0].isInteger())
				throw new Exception("HM script client: Invalid response line (invalid ISE ID): $line");
			HmModel.Channel ch=new HmModel.Channel(iseId:items[0] as int, displayName:items[1], address:items[2], device:dev)
			if (items.length>3) {
				items[3..-1].collate(2).each {
					if (!it[0].isInteger())
						throw new Exception("HM script client: Invalid response line (invalid ISE ID): $line");
					ch.dataPoints << new HmModel.DataPoint(iseId:it[0] as int, address:it[1], channel:ch)
				}	
			}
			dev.channels << ch
		}
	}
	
	private void retrieveDevices(HmModel model) {
		List<String> res=execute('''string id; foreach(id, root.Devices().EnumIDs()) {
			var device=dom.GetObject(id);
			if (device.ReadyConfig()==true && device.Name()!='Gateway') {
				WriteLine(id # "\t" # device.Name() # "\t" # device.Address());
			}
		}''')
		res.each { String line ->
			String[] items=line.split(/\t/, -1)
			if (items.length<3)
				throw new Exception("HM script client: Invalid response line (missing fields): $line");
			if (!items[0].isInteger())
				throw new Exception("HM script client: Invalid response line (invalid ISE ID): $line");
			HmModel.Device dev=new HmModel.Device(iseId:items[0] as int, displayName:items[1], address:items[2])
			retrieveChannels dev
			model.add dev
		}
	}
	
	private void retrieveRooms(HmModel model) {
		List<String> res=execute('''string roomId; foreach(roomId, dom.GetObject(ID_ROOMS).EnumUsedIDs()) {
		    var room = dom.GetObject(roomId);
		    if (room) {
		        Write(room.ID() # "\t" # room.Name() # "\t" # room.EnumInfo());
		        string chId; foreach(chId, room.EnumUsedIDs()) {
		            Write("\t" # chId);
		        }
		        WriteLine("");
		    }
		}''')
		res.each { String line ->
			String[] items=line.split(/\t/, -1)
			if (items.length<3)
				throw new Exception("HM script client: Invalid response line (missing fields): $line");
			if (!items[0].isInteger())
				throw new Exception("HM script client: Invalid response line (invalid ISE ID): $line");
			HmModel.Room room=new HmModel.Room(iseId:items[0] as int, displayName:items[1], comment:items[2])
			if (items.length>3) {
				items[3..-1].each {
					if (!it.isInteger())
						throw new Exception("HM script client: Invalid response line (invalid ISE ID): $line");
					HmModel.Channel ch=model.getChannelByIseId(it as int)
					if (ch!=null)
						room.channels << ch
					else
						log.warning "HM script client: Channel with ISE-ID $it not exists"
				}
			}
			model.add room
		}
	}

	private void retrieveFunctions(HmModel model) {
		List<String> res=execute('''string funcId; foreach(funcId, dom.GetObject(ID_FUNCTIONS).EnumUsedIDs()) {
		    var func = dom.GetObject(funcId);
		    if (func) {
		        Write(func.ID() # "\t" # func.Name() # "\t" # func.EnumInfo());
		        string chId; foreach(chId, func.EnumUsedIDs()) {
		            Write("\t" # chId);
		        }
		        WriteLine("");
		    }
		}''')
		res.each { String line ->
			String[] items=line.split(/\t/, -1)
			if (items.length<3)
				throw new Exception("HM script client: Invalid response line (missing fields): $line");
			if (!items[0].isInteger())
				throw new Exception("HM script client: Invalid response line (invalid ISE ID): $line");
			HmModel.Function func=new HmModel.Function(iseId:items[0] as int, displayName:items[1], comment:items[2])
			if (items.length>3) {
				items[3..-1].each {
					if (!it.isInteger())
						throw new Exception("HM script client: Invalid response line (invalid ISE ID): $line");
					HmModel.Channel ch=model.getChannelByIseId(it as int)
					if (ch!=null)
						func.channels << ch
					else
						log.warning "HM script client: Channel with ISE-ID $it not exists"
				}
			}
			model.add func
		}
	}

	public HmModel getModel(long maxCacheAge) {
		synchronized (modelMutex) {
			if (modelLastScan==0 || (System.currentTimeMillis()-modelLastScan)>maxCacheAge) {
				log.finer "Retrieving model from CCU"
				model=[]
				retrieveDevices model
				retrieveRooms model
				retrieveFunctions model
				modelLastScan=System.currentTimeMillis()
				log.finer "Retrieved CCU model:\n$model"
			}
			model
		}
	}
	
	public Date getSystemDate() {
		List<String> res=execute('WriteLine(system.Date("%F %T"));')
		if (res.size()!=1)
			throw new Exception("HM script client: Expected a response with exactly one line");
		Date t;
		try {
			t=Date.parse('yyyy-MM-dd HH:mm:ss', res[0])
		} catch (e) {
			throw new Exception("HM script client: Invalid date format: ${res[0]}");
		}
		t
	}
	
	public void executeProgram(String program) {
		List<String> res=execute($/
			var prg=dom.GetObject(ID_PROGRAMS).Get("$program");
			if (prg) { prg.ProgramExecute(); WriteLine("0"); } 
			else { WriteLine("1"); }/$)
		if (res.size()!=1 || res[0]!='0') 
			throw new Exception("Execution of HM program failed: $program")
	}
	
	public synchronized List<String> execute(String script) {
		log.finer "Executing script: $script"
		// pause
		if (lastScriptExec!=0) {
			long elapsed=System.currentTimeMillis()-lastScriptExec
			if (elapsed < DEFAULT_SCRIPT_PAUSE) {
				long wait=DEFAULT_SCRIPT_PAUSE-elapsed
				log.finer "Pausing script execution for $wait ms"
				Thread.sleep wait // allow interrupts
			}
		}
		// request
		lastScriptExec=System.currentTimeMillis()
		HttpURLConnection con=(HttpURLConnection)url.openConnection()
		con.connectTimeout=DEFAULT_CONNECT_TIMEOUT
		con.requestMethod='POST'
		con.doOutput=true
		if (auth) {
			con.setRequestProperty("Authorization", auth);
		}
		con.outputStream.write script.getBytes('ISO-8859-1')
		con.outputStream.close()
		List<String> response=[]
		con.inputStream.withReader('ISO-8859-1') { Reader reader ->
			reader.eachLine { String line ->
				if (!line.startsWith('<xml><exec>')) response << line
			}
		}
		con.disconnect()
		log.finer "Response: ${response.join('\n')}"
		response
	}
}
