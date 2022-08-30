package mdz.ccuhistorian.webapp

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

import mdz.ccuhistorian.Main

class JsonRpcTest {

	Main main;
	
	def post(params) {
		HttpURLConnection post = new URL("http://localhost/query/jsonrpc.gy").openConnection();
		// TODO
		def message = '{"message":"this is a message"}'
		post.setRequestMethod("POST")
		post.setDoOutput(true)
		post.setRequestProperty("Content-Type", "application/json")
		post.getOutputStream().write(message.getBytes("UTF-8"));
		def postRC = post.getResponseCode();
		println(postRC);
		if (postRC.equals(200)) {
			println(post.getInputStream().getText());
		}
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
