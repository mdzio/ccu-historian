// These tests need the sim database.
// database.name='sim'

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2')

import groovyx.net.http.*
import groovy.transform.Field

@Field http=new HTTPBuilder('http://localhost/query/jsonrpc.gy?k=abc')

def post(params) {
    println "request : $params"
    def res=http.post(contentType: ContentType.JSON, requestContentType: ContentType.JSON, body: params)
    println "response: $res\n"
    res
}

def assertResult(res, val) {
    assert !res.containsKey('error')
    assert res.containsKey('result')
    assert res.result==val
}

// test connection
res=post([method:'echo', params:['test']])
assert res.result==['test']

// test unknown config
res=post([
	method:'getConfig', 
	params:['test.unknown']
])
assertResult(res, null)

// set config
res=post([
    method:'setConfig',
    params:['test.name1','value1'],
])
assertResult(res, null)
res=post([
    method:'setConfig',
    params:['test.name2','value2'],
])
assertResult(res, null)

// get config
res=post([
	method:'getConfig', 
	params:['test.name1']
])
assertResult(res, 'value1')
res=post([
	method:'getConfig', 
	params:['test.name2']
])
assertResult(res, 'value2')

// delete config
res=post([
    method:'setConfig',
    params:['test.name1',null],
])
assertResult(res, null)
res=post([
    method:'setConfig',
    params:['test.name2',null],
])
assertResult(res, null)

// get config
res=post([
	method:'getConfig', 
	params:['test.name1']
])
assertResult(res, null)
res=post([
	method:'getConfig', 
	params:['test.name2']
])
assertResult(res, null)

println 'All tests succeeded'
