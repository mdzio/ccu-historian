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

class BufferTest {

	@Test
	public void test() {
		Buffer<Integer> buffer=[5, 50]
		Collector<Integer> collector=[]
		buffer.addConsumer(collector)
		assertEquals(0, collector.size())

		4.times { buffer.consume(it) }
		assertEquals(4, buffer.size())
		assertEquals(0, collector.size())
		buffer.purge()
		assertEquals([0, 1, 2, 3], collector.get())
		assertEquals(0, buffer.size())

		4.times { buffer.consume(it) }
		assertEquals(4, buffer.size())
		assertEquals(0, collector.size())
		buffer.consume(4)
		assertEquals(0, buffer.size())
		assertEquals([0, 1, 2, 3, 4], collector.get())

		3.times { buffer.consume(it) }
		assertEquals(3, buffer.size())
		assertEquals(0, collector.size())
		sleep(100)
		buffer.consume(3)
		assertEquals(0, buffer.size())
		assertEquals([0, 1, 2, 3], collector.get())
	}
}
