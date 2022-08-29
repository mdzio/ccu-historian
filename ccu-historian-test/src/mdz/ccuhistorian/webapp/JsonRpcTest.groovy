package mdz.ccuhistorian.webapp

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import groovy.json.JsonOutput

import mdz.ccuhistorian.Main

class JsonRpcTest {

	Main main;
	
	def post(params) {
		// TODO
		// HTTP-POST http://localhost/query/jsonrpc.gy
	}
	
	@Before
	public void setUp() {
		main=new Main()
		main.config.readCommandLine(["-config", "test.config"] as String[]);
		main.start()
	}

	@After
	public void tearDown() {
		main.stop()
		new File("test.mv.db").delete()
	}

	@Test
	public void test() {
		// TODO
		post([:])
	}
}
