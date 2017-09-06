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

import java.util.HashMap;
import java.util.Map;

import mdz.Exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicProducer<T> implements Producer<T> {

	private final static Logger log = LoggerFactory.getLogger(BasicProducer.class);
	private Consumer<? super T> firstConsumer;
	private Map<Consumer<? super T>, ?> otherConsumers;

	private void safeProduce(Consumer<? super T> consumer, T t) {
		try {
			consumer.consume(t);
		} catch (Throwable e) {
			Exceptions.throwIfFatal(e);
			log.error("Consumer signals an error", e);
		}
	}

	public synchronized void produce(T t) {
		if (firstConsumer != null)
			safeProduce(firstConsumer, t);
		if (otherConsumers != null)
			for (Consumer<? super T> consumer : otherConsumers.keySet())
				safeProduce(consumer, t);
	}

	@Override
	public synchronized void addConsumer(Consumer<? super T> consumer) {
		if (firstConsumer == null) {
			if (otherConsumers == null || !otherConsumers.containsKey(consumer))
				firstConsumer = consumer;
		} else if (firstConsumer != consumer) {
			if (otherConsumers == null)
				otherConsumers = new HashMap<>();
			otherConsumers.put(consumer, null);
		}
	}

	@Override
	public synchronized void removeConsumer(Consumer<? super T> consumer) {
		if (firstConsumer == consumer)
			firstConsumer = null;
		else if (otherConsumers != null)
			otherConsumers.remove(consumer);
	}
}
