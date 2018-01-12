package mdz

import static org.junit.Assert.*

import org.junit.Test

class TextTest {

	@Test
	public void testUnescapeXml() {
		assert Text.unescapeXml(null) == ''
		assert Text.unescapeXml('') == ''
		assert Text.unescapeXml(' abc def 123 \0x03b1') == ' abc def 123 \0x03b1'
		assert Text.unescapeXml('-&amp;-') == '-&-'
		assert Text.unescapeXml('1&lt;2') == '1<2'
		assert Text.unescapeXml('a&gt;b') == 'a>b'
		assert Text.unescapeXml('&apos;') == "'"
		assert Text.unescapeXml('&quot;') == '"'
		assert Text.unescapeXml('A&#65;') == 'AA'
		assert Text.unescapeXml('&#66;B') == 'BB'
		assert Text.unescapeXml('0&#x30;') == '00'
		assert Text.unescapeXml('&#x31;1') == '11'
		assert Text.unescapeXml('&aaa;') == '&aaa;'
		assert Text.unescapeXml('&#x;') == '&#x;'
	}
	
	@Test
	public void testAsBoolean() {
		assert Text.asBoolean(true)
		assert !Text.asBoolean(false)
		assert Text.asBoolean(0.1)
		assert Text.asBoolean(1)
		assert !Text.asBoolean(0.0)
		assert Text.asBoolean("on")
		assert !Text.asBoolean("false")
		try {
			Text.asBoolean("a")
			assert false
		} catch (e) {
			assert e.message == "Can't interpret a as boolean value"
		}
	}
}
