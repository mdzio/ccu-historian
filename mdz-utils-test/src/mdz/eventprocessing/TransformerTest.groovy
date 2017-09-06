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
package mdz.eventprocessing

import static org.junit.Assert.*

import java.util.function.Function
import org.junit.Test;

class TransformerTest {

	@Test
	public void test() {
		Transformer<Integer, String> f=[
			{ "($it)" as String } as Function<Integer, String>
		]
		Collector<Integer> c=[]
		f.addConsumer(c)
		5.times { f.consume(it) }
		assertEquals(['(0)', '(1)', '(2)', '(3)', '(4)'], c.get())
	}
}
