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
package mdz.eventprocessing;

import static org.junit.Assert.*;

import org.junit.Test;

class CollectorTest {

	@Test
	public void test() {
		Collector<Integer> cs=[]
		assertEquals([], cs.get())
		assertEquals(0, cs.size())
		cs.consume(1)
		assertEquals([1], cs.get())
		cs.consume(2)
		assertEquals([2], cs.get())
		cs.consume(3)
		cs.consume(4)
		assertEquals(2, cs.size())
		assertEquals([3, 4], cs.get())
		assertEquals([], cs.get())
	}
}
