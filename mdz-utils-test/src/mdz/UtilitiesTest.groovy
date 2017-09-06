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
package mdz;

import groovy.util.GroovyTestCase;

class UtilitiesTest extends GroovyTestCase {
	
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
