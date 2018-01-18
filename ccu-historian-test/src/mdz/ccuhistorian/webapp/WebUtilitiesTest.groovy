package mdz.ccuhistorian.webapp

class WebUtilitiesTest extends GroovyTestCase {

	public void testFormatString() {
		WebUtilities wu=[]
		
		assert wu.format('12345678901234567890')=='12345678901234567890'
		assert wu.format('123456789012345678901')=='12345678901234567890...'
	}
}
