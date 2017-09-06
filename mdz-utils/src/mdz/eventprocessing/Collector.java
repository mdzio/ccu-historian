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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Collector<T> implements Consumer<T> {

	protected List<T> items;

	@Override
	public synchronized void consume(T t) {
		if (items == null)
			items = new ArrayList<T>();
		items.add(t);
	}

	public synchronized List<T> get() {
		if (items == null)
			return Collections.emptyList();
		else {
			List<T> res = items;
			items = null;
			return res;
		}
	}

	public synchronized int size() {
		if (items == null)
			return 0;
		return items.size();
	}
}
