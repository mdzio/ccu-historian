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
package mdz.ccuhistorian

import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic

@Slf4j
@CompileStatic
public class DatabaseSystem extends BaseSystem {

	ExtendedStorage extendedStorage
	
	private Database internalDatabase
	
	public DatabaseSystem(Configuration config) {
		super(config)
		internalDatabase=new Database(config.databaseConfig, base)
		extendedStorage=new ExtendedStorage(storage:internalDatabase)
	}

	public synchronized void stop()  {
		internalDatabase.stop()
		super.stop()
	}
	
	public ExtendedStorage getDatabase() { extendedStorage }
}
