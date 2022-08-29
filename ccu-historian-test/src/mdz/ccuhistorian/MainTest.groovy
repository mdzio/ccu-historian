package mdz.ccuhistorian

import static org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

class MainTest {

	Main main;
	
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
		// only setUp and tearDown
	}
}
