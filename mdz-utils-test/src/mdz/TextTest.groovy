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
package mdz

import org.junit.Test

class TextTest {

	@Test
	public void testForEachMatch() {
		def m = []
		Text.forEachMatch('abc123def', ~/\d+/, { g -> m << g })
		assert m == [['123']]
		
		m = []
		Text.forEachMatch('abc123def678ghi', ~/\d+/, { g -> m << g })
		assert m == [['123'], ['678']]
		
		m = []
		Text.forEachMatch('abc123def678ghi901', ~/(\d+)(\p{Lower}+)/, { g -> m << g })
		assert m == [['123def', '123', 'def'], ['678ghi', '678', 'ghi']]
	}
	
	@Test
	public void testUnescapeXml() {
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
	public void testEscapeXml() {
		assert Text.escapeXml('') == ''
		assert Text.escapeXml('abc def 123 +~#ä') == 'abc def 123 +~#ä'
		assert Text.escapeXml('-&-') == '-&amp;-'
		assert Text.escapeXml(' < ') == ' &lt; '
		assert Text.escapeXml('a>b') == 'a&gt;b'
		assert Text.escapeXml('"') == '&quot;'
		assert Text.escapeXml("'") == '&apos;'
		assert Text.escapeXml("'<&>\"") == '&apos;&lt;&amp;&gt;&quot;'
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
			assert e.message == "Can't interpret a (type: java.lang.String) as boolean value"
		}
	}
	
	@Test
	public void testPrettyPrint() {
		assert Text.prettyPrint([0, 1, (byte)'1', (byte)'a'] as byte[]) == 
			'0000: ..1a                              00 01 31 61 '
		assert Text.prettyPrint((0..63) as byte[]) == '0000: ................................  00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 10 11 12 13 14 15 16 17 18 19 1A 1B 1C 1D 1E 1F \n' +
			'0020:  !"#$%&\'()*+,-./0123456789:;<=>?  20 21 22 23 24 25 26 27 28 29 2A 2B 2C 2D 2E 2F 30 31 32 33 34 35 36 37 38 39 3A 3B 3C 3D 3E 3F '
	}
}
