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

// test connection
res=post([method:'echo', params:['test']])
assert res.result==['test']

// test unknown data point
res=post([method:'updateDataPoint', params:[
    // data point object
    [
        id:[interfaceId:'dp', address:'not', identifier:'found'],
    ],
]])
assert res.error.code==-32602
assert res.error.message=='Data point not found: dp.not.found'

// test missing custom attribute
res=post([method:'updateDataPoint', params:[
    // data point object
    [
        id:[interfaceId:'Sim', address:'Simulated_BOOL_1', identifier:'BOOL'],
    ],
]])
assert res.error.code==-32602

// set custom attribute 'custom1'
res=post([method:'updateDataPoint', params:[
    // data point object
    [
        id:[interfaceId:'Sim', address:'Simulated_BOOL_1', identifier:'BOOL'],
        attributes:[
            custom:[custom1:'value of custom1'],
        ],
    ],
]])

// set custom attribute 'custom2'
res=post([method:'updateDataPoint', params:[
    // data point object
    [
        id:[interfaceId:'Sim', address:'Simulated_BOOL_1', identifier:'BOOL'],
        attributes:[
            custom:[custom2: ['1. value of custom2','2. value of custom2']],
        ],
    ],
]])

// check custom attributes
res=post([method:'getDataPoint', params:['Sim.Simulated_BOOL_1.BOOL']])
assert res.result.attributes.custom==[
    custom1:'value of custom1',
    custom2: ['1. value of custom2','2. value of custom2'],
]

// remove custom attributes
res=post([method:'updateDataPoint', params:[
    // data point object
    [
        id:[interfaceId:'Sim', address:'Simulated_BOOL_1', identifier:'BOOL'],
        attributes:[
            custom:[
                custom1:null, custom2:null,
            ]
        ],
    ],
]])

// check custom attributes
res=post([method:'getDataPoint', params:['Sim.Simulated_BOOL_1.BOOL']])
assert !res.result.attributes.custom.containsKey('custom1')
assert !res.result.attributes.custom.containsKey('custom2')

println 'All tests succeeded'
