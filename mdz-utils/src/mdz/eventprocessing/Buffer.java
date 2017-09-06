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

import java.util.LinkedList;
import java.util.Queue;

public class Buffer<T> extends BasicProducer<T> implements Consumer<T> {

	// final Logger log = LoggerFactory.getLogger(Buffer.class);

	private int countLimit;
	private long timeLimit;
	private long timeOfOldest;
	private Queue<T> queue = new LinkedList<>();
	private boolean purging;

	public Buffer() {
		this.countLimit = Integer.MAX_VALUE;
		this.timeLimit = Long.MAX_VALUE;
	}

	public Buffer(int countLimit, long timeLimit) {
		this.countLimit = countLimit;
		this.timeLimit = timeLimit;
	}

	public int getCountLimit() {
		return countLimit;
	}

	public void setCountLimit(int countLimit) {
		this.countLimit = countLimit;
	}

	public long getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}

	@Override
	public synchronized void consume(T t) throws Exception {
		if (queue.size() == 0)
			timeOfOldest = System.currentTimeMillis();
		queue.add(t);
		if (queue.size() >= countLimit || (System.currentTimeMillis() - timeOfOldest >= timeLimit))
			purge();
	}

	public synchronized void purge() {
		if (purging)
			return;
		purging = true;
		T t;
		while ((t = queue.poll()) != null)
			produce(t);
		purging = false;
	}

	public synchronized int size() {
		return queue.size();
	}
}
