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
package mdz.ccuhistorian.test

import groovy.util.GroovyTestCase
import mdz.ccuhistorian.WebUtilities
import mdz.Utilities

class TestWebUtilities extends GroovyTestCase {

	public void testEscapeHtml() {
		WebUtilities wu=new WebUtilities()

		assert wu.escapeHtml(null)==''
		assert wu.escapeHtml('')==''
		assert wu.escapeHtml(' abc def 123 \0x03b1')==' abc def 123 \0x03b1'
		assert wu.escapeHtml('bvszugtszugkwzrgbwrbszgylwityigzbtyzubrgvykezbrgvkygtbytwubfrcwuzgenoqztrbkuzwbrkzuyr')==
			'bvszugtszugkwzrgbwrbszgylwityigzbtyzubrgvykezbrgvkygtbytwubfrcwuzgenoqztrbkuzwbrkzuyr'
		assert wu.escapeHtml('-&-')=='-&amp;-'
		assert wu.escapeHtml(' < ')==' &lt; '
		assert wu.escapeHtml('a>b')=='a&gt;b'
		assert wu.escapeHtml('"')=='&quot;'
		assert wu.escapeHtml(' ü')==' &uuml;'
		assert wu.escapeHtml('Ü ')=='&Uuml; '
		assert wu.escapeHtml('öö')=='&ouml;&ouml;'
		assert wu.escapeHtml('ÖÖÖ')=='&Ouml;&Ouml;&Ouml;'
		assert wu.escapeHtml('ä')=='&auml;'
		assert wu.escapeHtml('Ä')=='&Auml;'
		assert wu.escapeHtml('ß')=='&szlig;'
		assert wu.escapeHtml('&<>"üÜöÖäÄß')=='&amp;&lt;&gt;&quot;&uuml;&Uuml;&ouml;&Ouml;&auml;&Auml;&szlig;'
		assert wu.escapeHtml('üÜöÖäÄß<>"&')=='&uuml;&Uuml;&ouml;&Ouml;&auml;&Auml;&szlig;&lt;&gt;&quot;&amp;'
	}

	public void testUnescapeHtml() {
		assert Utilities.unescapeXml(null)==''
		assert Utilities.unescapeXml('')==''
		assert Utilities.unescapeXml(' abc def 123 \0x03b1')==' abc def 123 \0x03b1'
		assert Utilities.unescapeXml('bvszugtszugkwzrgbwrbszgylwityigzbtyzubrgvykezbrgvkygtbytwubfrcwuzgenoqztrbkuzwbrkzuyr')==
			'bvszugtszugkwzrgbwrbszgylwityigzbtyzubrgvykezbrgvkygtbytwubfrcwuzgenoqztrbkuzwbrkzuyr'
		assert Utilities.unescapeXml('-&amp;-')=='-&-'
		assert Utilities.unescapeXml(' &lt; ')==' < '
		assert Utilities.unescapeXml('a&gt;b')=='a>b'
		assert Utilities.unescapeXml('&apos;')=="'"
		assert Utilities.unescapeXml('&quot;')=='"'
		assert Utilities.unescapeXml('&#65;')=='A'
		assert Utilities.unescapeXml('&#66;')=='B'
		assert Utilities.unescapeXml('&#x30;')=='0'
		assert Utilities.unescapeXml('&#x31;')=='1'
	}
}
