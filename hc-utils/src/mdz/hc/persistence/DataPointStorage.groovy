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
package mdz.hc.persistence

import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier

public interface DataPointStorage {

	public List<DataPoint> getDataPoints() throws Exception
	public List<DataPoint> getDataPointsOfInterface(String itfName) throws Exception

	// The return value is null if the data point was not found.	
	public DataPoint getDataPoint(int idx) throws Exception
	
	// The return value is null if the data point was not found.
	public DataPoint getDataPoint(DataPointIdentifier id) throws Exception
	
	// At least the properties id and historyString must be set from
	// the data point. The data point is sometimes changed, for example, 
	// the table name can be generated.
	public void createDataPoint(DataPoint dp) throws Exception
	
	public void updateDataPoint(DataPoint dp) throws Exception
	
	public void deleteDataPoint(DataPoint dp) throws Exception
	
	public void normalizeDataPoint(DataPoint dp)
}
