// These tests need the sim database and the SIMULATION interface:
// database.name='sim'
// devices.device1.type=SIMULATION
// devices.device1.writeAccess=true

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2')

import groovyx.net.http.*
import groovy.transform.Field

@Field http=new HTTPBuilder('http://localhost/query/jsonrpc.gy?k=abc')

def GETwithParamsAsText(params) {
    println '-----------------------'
    println "GETwithParamsAsText: $params"
    def res=http.get(contentType: ContentType.TEXT, query: params)
    StringWriter writer=[]
    writer << res
    println "result: $writer"
    writer.toString()
}

def POSTwithTextAsText(text) {
    println '-----------------------'
    println "POSTwithTextAsText: $text"
    def res=http.post(contentType: ContentType.TEXT, requestContentType: ContentType.TEXT, body: text)
    StringWriter writer=[]
    writer << res
    println "result: $writer"
    writer.toString()
}

def POSTwithJsonAsJson(params, showResult=true) {
    println '-----------------------'
    println "POSTwithJsonAsJson: $params"
    def res=http.post(contentType: ContentType.JSON, requestContentType: ContentType.JSON, body: params)
    if (showResult) println "result: $res"
    res
}

println '#######################'
println 'TESTING: echo'

assert GETwithParamsAsText([:])=='{"id":null,"error":{"code":-32700,"message":"JSON parse error"}}'
assert GETwithParamsAsText([m:'echo'])=='{"id":null,"result":[]}'
assert GETwithParamsAsText([m:'echo',p1:123,i:456])=='{"id":"456","result":["123"]}'
assert GETwithParamsAsText([m:'echo',p1:'String',p2:123,i:456])=='{"id":"456","result":["String","123"]}'
assert GETwithParamsAsText([m:'echo',p1:'String',p2:123,p3:456,i:789])=='{"id":"789","result":["String","123","456"]}'

assert GETwithParamsAsText([j:""])=='{"id":null,"error":{"code":-32700,"message":"JSON parse error"}}'
assert GETwithParamsAsText([j:"123"])=='{"id":null,"error":{"code":-32600,"message":"Request object invalid"}}'
assert GETwithParamsAsText([j:'{"method":"","params":["String",123],"id":456}'])=='{"id":456,"error":{"code":-32601,"message":"Method not found"}}'
assert GETwithParamsAsText([j:'{"method":"echo","params":["String",123],"id":456}'])=='{"id":456,"result":["String",123]}'

assert POSTwithTextAsText('Invalid JSON')=='{"id":null,"error":{"code":-32700,"message":"JSON parse error"}}'
assert POSTwithTextAsText('123')=='{"id":null,"error":{"code":-32600,"message":"Request object invalid"}}'

def res=POSTwithJsonAsJson([])
assert res.result==null
assert res.id.equals(null) // id ist vom Typ JSONNull
assert res.error.code==-32600
assert res.error.message=='Request object invalid'

assert POSTwithJsonAsJson([:]).error.code==-32600
assert POSTwithJsonAsJson([method:123]).error.code==-32600
assert POSTwithJsonAsJson([method:'']).error.code==-32600
assert POSTwithJsonAsJson([method:'',params:123]).error.code==-32600

assert POSTwithJsonAsJson([method:"",params:[]]).error.message=='Method not found'
assert POSTwithJsonAsJson([method:"Unknown method",params:[]]).error.code==-32601

res=POSTwithJsonAsJson([method:'echo', params:[1, [1, 2], [a:1, b:'2']], id:[3,4]])
assert 1==res.result[0]
assert [1, 2]==res.result[1]
assert [a:1, b:'2']==res.result[2]
assert [3,4]==res.id

assert POSTwithJsonAsJson([method:'echo', params:[]]).id.equals(null)

println '#######################'
println 'TESTING: getDataPoint'

res=GETwithParamsAsText([m:'getDataPoint', p1:999999999, i:123])
assert res=='{"id":"123","error":{"code":-32000,"message":"Unknown data point: 999999999"}}'

res=GETwithParamsAsText([m:'getDataPoint', p1:1, i:123])
assert res=='{"id":"123","result":{"managementFlags":32,"id":{"interfaceId":"Sim","address":"Simulated_ACTION_0","identifier":"ACTION"},"displayName":"Sim.Simulated_ACTION_0.ACTION","historyHidden":false,"attributes":{"preprocType":null,"preprocParam":null,"displayName":null,"comment":null,"paramSet":null,"tabOrder":null,"maximum":1.0,"unit":"Aktion","minimum":1.0,"control":null,"operations":null,"flags":null,"type":"ACTION","defaultValue":null},"historyTableName":"D_SIM_SIMULATED_ACTION_0_ACTION","historyString":false,"idx":1,"historyDisabled":true}}'
savedGetDataPoint=res

res=POSTwithJsonAsJson([method:'getDataPoint', params:[3], id:456])
assert res.error==null
assert res.id==456
dp=res.result
assert dp.id.interfaceId=='Sim'
assert dp.id.address=='Simulated_INTEGER_2'
assert dp.id.identifier=='RAMP'
assert dp.managementFlags==32

res=POSTwithJsonAsJson([method:'getDataPoint', params:[[4, 5]], id:123])
assert res.result instanceof List
assert res.result[0].id.address=='Simulated_FLOAT_3'
assert res.result[1].id.address=='Simulated_STRING_4'

res=POSTwithJsonAsJson([method:'getDataPoint', params:[], id:123], false)
println "number of datapoints: ${res.result.size()}"
assert res.result instanceof List
res=res.result.id.address
assert 'Simulated_ACTION_0' in res
assert 'Simulated_BOOL_1' in res
assert 'Simulated_INTEGER_2' in res
assert 'Simulated_BOOL_8' in res

println '#######################'
println 'TESTING: getTimeSeriesRaw'

res=POSTwithJsonAsJson([method:'getTimeSeriesRaw', params:[9999999, 0, 0], id:123])
assert res.result==null
assert res.error.message=='Unknown data point: 9999999'

begin=Date.parse('yyyy-MM-dd HH:mm:ss', '2016-07-21 10:38:38')
end=Date.parse('yyyy-MM-dd HH:mm:ss', '2016-07-21 10:38:56')
res=POSTwithJsonAsJson([method:'getTimeSeriesRaw', params:[4, begin.time, end.time], id:123])
assert res.result!=null
assert res.result.values==[4.067366429801342, 3.090169943749475, 2.079116909201907, 1.0452846316350761]
assert res.result.timestamps[0]==Date.parse('yyyy-MM-dd HH:mm:ss.SSS', '2016-07-21 10:38:38.162').time
assert res.result.timestamps==[1469090318162, 1469090320174, 1469090322187, 1469090324199]
assert res.result.states==[2]*4

end=Date.parse('yyyy-MM-dd HH:mm:ss', '2016-07-21 10:38:44')
savedGetTimeSeriesRawBegin=begin
savedGetTimeSeriesRawEnd=end
res=POSTwithJsonAsJson([method:'getTimeSeriesRaw', params:[4, begin.time, end.time], id:123])
savedGetTimeSeriesRaw=res
assert res.result?.dataPoint?.id?.address=='Simulated_FLOAT_3'
assert res.result?.values?.size()==3
assert res.result?.timestamps?.size()==3
assert res.result?.states?.size()==3

println '#######################'
println 'TESTING: getTimeSeries'

savedGetTimeSeriesBegin=begin
savedGetTimeSeriesEnd=end
res=POSTwithJsonAsJson([method:'getTimeSeries', params:[4, begin.time, end.time], id:123])
savedGetTimeSeries=res
assert res.result!=null
assert res.result.values==[5.0000000009069, 4.067366429801342, 3.090169943749475, 2.079116909201907, 2.079116909201907]
assert res.result.timestamps[0]==Date.parse('yyyy-MM-dd HH:mm:ss.SSS', '2016-07-21 10:38:38.000').time
assert res.result.timestamps==[1469090318000, 1469090318162, 1469090320174, 1469090322187, 1469090324000]
assert res.result.states==[2]*5

println '#######################'
println 'TESTING: getValue'

res=POSTwithJsonAsJson([method:'getValue', params:[1], id:123])
savedGetValue1=res    
assert res.result?.value==1.0
assert res.result?.state==2
assert res.result?.timestamp==1469098920170

res=POSTwithJsonAsJson([method:'getValue', params:[[2, 3, 4]], id:123])
savedGetValue2=res    
assert res.result instanceof List
assert res.result.size()==3

println '#######################'
println 'TESTING: setValue'

res=POSTwithJsonAsJson([method:'setValue', params:[999999999, "Neuer Wert!"], id:123])    
assert res.error.message=='Unknown data point: 999999999'

println '#######################'
println 'TESTING: ISE identifier'

res=GETwithParamsAsText([m:'getDataPoint', p1:'Sim.Simulated_ACTION_0.ACTION', i:123])
assert res==savedGetDataPoint

res=POSTwithJsonAsJson([method:'getValue', params:['Sim.Simulated_ACTION_0.ACTION'], id:123])
assert res==savedGetValue1
res=POSTwithJsonAsJson([
    method:'getValue', 
    params:[['Sim.Simulated_BOOL_1.BOOL', 'Sim.Simulated_INTEGER_2.RAMP', 'Sim.Simulated_FLOAT_3.SIN']], 
    id:123]
)
assert res==savedGetValue2

res=POSTwithJsonAsJson([
	method:'getTimeSeriesRaw', 
	params:['Sim.Simulated_FLOAT_3.SIN', savedGetTimeSeriesRawBegin.time, savedGetTimeSeriesRawEnd.time], 
	id:123
])
assert res==savedGetTimeSeriesRaw

res=POSTwithJsonAsJson([
	method:'getTimeSeries', 
	params:['Sim.Simulated_FLOAT_3.SIN', savedGetTimeSeriesBegin.time, savedGetTimeSeriesEnd.time], 
	id:123
])
assert res==savedGetTimeSeries

res=POSTwithJsonAsJson([method:'setValue', params:['Sim.Simulated_FLOAT_3.SIN', 123.456], id:456])
assert res==[id:456, result:null]
