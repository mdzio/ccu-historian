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

import groovy.util.GroovyTestCase

class IteratorAdaptersTest extends GroovyTestCase {

	public void testBasics() {
		def list=[1, 2, 3, 4]
		def itpro=new IteratorProducerAdapter(list)
		def itcons=new ConsumerIteratorAdapter(itpro)
		itpro.addConsumer(itcons)
		def result=itcons.collect()
		assertEquals list, result
	}
	
	public void testRequestSize() {
		def list=(0..<2000)
		def itpro=new IteratorProducerAdapter(list)
		def itcons=new ConsumerIteratorAdapter(itpro)
		itcons.setRequestSize(1)
		itpro.addConsumer(itcons)
		def result=itcons.collect()
		assertEquals list, result
	}
}
