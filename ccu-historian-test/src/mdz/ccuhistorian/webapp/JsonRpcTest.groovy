package mdz.ccuhistorian.webapp

import static org.junit.Assert.*

import javax.net.ssl.HttpsURLConnection
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import mdz.ccuhistorian.Main
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class JsonRpcTest {

	static Main main;
	
	def post(params) {
		def reqBody=JsonOutput.toJson(params)
		HttpURLConnection post=new URL("http://localhost/query/jsonrpc.gy").openConnection()
		post.requestMethod="POST"
		post.doOutput=true
		post.setRequestProperty("Content-Type", "application/json")
		post.outputStream.write(reqBody.getBytes("UTF-8"))
		def status=post.responseCode
		if (status!=HttpURLConnection.HTTP_OK || status!=HttpURLConnection.HTTP_OK) {
			throw new Exception("HTTP post failed with status code: "+status)
		} else {
		    new JsonSlurper().parse(post.inputStream)	
		}
	}
	
	@BeforeClass
	static public void setUp() {
		main=new Main()
		main.config.readCommandLine(["-config", "test.config"] as String[]);
		main.start()
	}

	@AfterClass
	static public void tearDown() {
		main.stop()
		new File("test.mv.db").delete()
	}

	@Test
	public void testInvalidRequest() {
		def resp=post([:])
		assert resp==[id:null, error:[code:-32600, message:"Request object invalid"]]
	}
	
	@Test
	public void testEcho() {
		def resp=post([method:"echo",params:[1, 2, 3]])
		assert resp==[id:null, result:[1, 2, 3]]
	}
	
	@Test
	public void testManageDataPoint() {
		def resp=post([method:"createDataPoint", params:[[
			id: [interfaceId: "User", address:"1", identifier:"TEST"],
			managementFlags: 0,	
			attributes: [
				displayName: "Testdatenpunkt",
				room: "Virtuell",
				function: "Test",
				comment: "Kommentar",
				custom: [info:"p1"],
				maximum: 90,
				unit: "-",
				minimum: 0,
				type: "ENUM",
			]
		]]])
		assert resp==[id:null, result:null]
		
		resp=post([method:"getDataPoint", params:[[id: [interfaceId: "User", address:"1", identifier:"TEST"]]]])
		resp.result.idx=0
	    assert resp==[id:null, result:[
			managementFlags:0, 
			id:[interfaceId:"User", address:"1", identifier:"TEST"], 
			continuous:false, 
			displayName:"Testdatenpunkt.TEST", 
			historyHidden:false, 
			noSynchronization:false, 
			attributes:[
				preprocType:null, 
				preprocParam:null, 
				displayName:"Testdatenpunkt", 
				room:"Virtuell", 
				function:"Test", 
				comment:"Kommentar", 
				custom:[info:"p1"], 
				paramSet:"VALUES", 
				tabOrder:null, 
				maximum:90.0, 
				unit:"-", 
				minimum:0.0, 
				control:null, 
				operations:null, 
				flags:null, 
				type:"ENUM",
				defaultValue:null
			], 
			synced:false, 
			historyString:false, 
			historyTableName:"D_USER_1_TEST", 
			idx:0,
			historyDisabled:false
		]]
		
		resp=post([method:"updateDataPoint", params:[[
			id: [interfaceId: "User", address:"1", identifier:"TEST"],
			managementFlags: 0x80,
			attributes: [
				displayName: "Testdatenpunkt Neu",
				room: "Virtuell Neu",
				function: "Test Neu",
				comment: "Kommentar Neu",
				custom: [info:"p2"],
				maximum: 100,
				unit: "%",
				minimum: 0,
				type: "FLOAT",
			]
		]]])
		assert resp==[id:null, result:null]
		
		resp=post([method:"getDataPoint", params:[[id: [interfaceId: "User", address:"1", identifier:"TEST"]]]])
		resp.result.idx=0
		assert resp==[id:null, result:[
			managementFlags:128,
			id:[interfaceId:"User", address:"1", identifier:"TEST"],
			continuous:true,
			displayName:"Testdatenpunkt Neu.TEST",
			historyHidden:false,
			noSynchronization:false,
			attributes:[
				preprocType:null,
				preprocParam:null,
				displayName:"Testdatenpunkt Neu",
				room:"Virtuell Neu",
				function:"Test Neu",
				comment:"Kommentar Neu",
				custom:[info:"p2"],
				paramSet:"VALUES",
				tabOrder:null,
				maximum:100.0,
				unit:"%",
				minimum:0.0,
				control:null,
				operations:null,
				flags:null,
				type:"FLOAT",
				defaultValue:null
			],
			synced:false,
			historyString:false,
			historyTableName:"D_USER_1_TEST",
			idx:0,
			historyDisabled:false
		]]
	}
	
	@Test
	public void testManageTimeSeries() {
		def resp=post([method:"createDataPoint", params:[[id: [interfaceId: "User", address:"2", identifier:"TEST"]]]])
		assert resp==[id:null, result:null]
		
		resp=post([method:"insertTimeSeries", params:[
			"User.2.TEST",
			[
				timestamps: [1000, 2000, 3000],
				values:[1.1, 2.2, 3.3],
				states:[3, 3, 3]
			]
		]])
		assert resp.result==3
		
		resp=post(method:"getTimeSeriesRaw", params:["User.2.TEST", 1000, 4000])
		assert resp.result.dataPoint.id==[interfaceId: "User", address:"2", identifier:"TEST"]
		assert resp.result.states==[3, 3, 3]
		assert resp.result.values==[1.1, 2.2, 3.3]
		assert resp.result.timestamps==[1000, 2000, 3000]
		
		resp=post(method:"deleteTimeSeries", params:["User.2.TEST", 2000, 3000])
		assert resp.result==1
		
		resp=post(method:"getTimeSeriesRaw", params:["User.2.TEST", 1000, 4000])
		assert resp.result.dataPoint.id==[interfaceId: "User", address:"2", identifier:"TEST"]
		assert resp.result.states==[3, 3]
		assert resp.result.values==[1.1, 3.3]
		assert resp.result.timestamps==[1000, 3000]
	}
}
