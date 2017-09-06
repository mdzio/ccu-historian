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
package mdz.hc.itf

import java.util.List
import mdz.hc.DataPointIdentifier

public interface BrowseSupport {
	/**
	 * All data points are listed. To save queries to the hardware, the list may be cached internally.
	 * @param maxCacheAge Maximum age of cached entries in milliseconds
	 * @return list of data points
	 */
	public List<DataPointIdentifier> getAllDataPoints(long maxCacheAge)
}
