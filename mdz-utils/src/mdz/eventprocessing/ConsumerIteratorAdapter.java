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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConsumerIteratorAdapter<T> implements Consumer<T>, Iterator<T>, Iterable<T> {

	private static final int DEFAULT_REQUEST_SIZE = 1000;

	private final Requestable requestable;
	private final Queue<T> queue = new ConcurrentLinkedQueue<>();
	private int requestSize = DEFAULT_REQUEST_SIZE;

	public ConsumerIteratorAdapter(Requestable requestable) {
		this.requestable = requestable;
	}

	public void setRequestSize(int requestSize) {
		this.requestSize = requestSize;
	}

	@Override
	public synchronized void consume(T t) {
		queue.add(t);
	}

	@Override
	public boolean hasNext() {
		while (queue.peek() == null && requestable.request(requestSize))
			;
		return queue.peek() != null;
	}

	@Override
	public T next() {
		return queue.remove();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}
}
