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

import java.util.Iterator;

public class IteratorProducerAdapter<T> extends BasicProducer<T> implements Requestable {

	private final Iterator<T> iterator;

	public IteratorProducerAdapter(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	public IteratorProducerAdapter(Iterable<T> iterable) {
		this.iterator = iterable.iterator();
	}

	public boolean request(int numberOfItems) {
		while (numberOfItems > 0 && iterator.hasNext())
			produce(iterator.next());
		return iterator.hasNext();
	}
}
