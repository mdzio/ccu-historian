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

import static org.junit.Assert.*

import org.junit.Test

class BasicProducerTest {

	@Test
	public void testBasics() {
		BasicProducer<Integer> prod=[]
		Collector<Integer> cons1=[]
		Collector<Integer> cons2=[]
		prod.produce(1)
		prod.addConsumer(cons1)
		prod.produce(2)
		prod.addConsumer(cons2)
		prod.produce(3)
		prod.removeConsumer(cons1)
		prod.produce(4)
		prod.removeConsumer(cons2)
		prod.produce(5)
		assertEquals([2, 3], cons1.get())
		assertEquals([3, 4], cons2.get())
	}
	
	@Test
	public void testAddRemove() {
		BasicProducer<Integer> prod=[]
		Collector<Integer> cons1=[]
		Collector<Integer> cons2=[]
		Collector<Integer> cons3=[]
		prod.produce(1)
		prod.addConsumer(cons1)
		prod.addConsumer(cons1)
		prod.produce(2)
		prod.addConsumer(cons2)
		prod.addConsumer(cons2)
		prod.produce(3)
		prod.removeConsumer(cons1)
		prod.removeConsumer(cons1)
		prod.produce(4)
		prod.addConsumer(cons3)
		prod.addConsumer(cons3)
		prod.removeConsumer(cons1)
		prod.removeConsumer(cons1)
		prod.removeConsumer(cons2)
		prod.removeConsumer(cons2)
		prod.produce(5)
		prod.removeConsumer(cons1)
		prod.removeConsumer(cons1)
		prod.removeConsumer(cons2)
		prod.removeConsumer(cons2)
		prod.removeConsumer(cons3)
		prod.removeConsumer(cons3)
		prod.produce(6)
		assertEquals([2, 3], cons1.get())
		assertEquals([3, 4], cons2.get())
		assertEquals([5], cons3.get())
	}
}
