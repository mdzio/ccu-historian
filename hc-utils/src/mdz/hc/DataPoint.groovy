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
package mdz.hc

import groovy.transform.EqualsAndHashCode
import groovy.transform.AutoClone

/* Note on the clone method: The objects in the attributes field are not cloned. 
 * They should therefore generally be treated as immutable.
 */
@EqualsAndHashCode
@AutoClone
public class DataPoint {
	
	// Bits 0... 3 were used for the interface type. 
	// They can still be set in existing databases (<V0.7.7)!
	
	// Data point is hidden and is not displayed by default. Set by user.
	static public final int FLAGS_HISTORY_HIDDEN   	 = 0x00000010
	
	// Data point is not recorded. Set by user.
	static public final int FLAGS_HISTORY_DISABLED 	 = 0x00000020
	
	// Data type of the history is string and not double. Set by interface and DataPointStorageUpdater.
	static public final int FLAGS_HISTORY_STRING	 = 0x00000040
	
	// Changes of the data point value are continuous. Set by interface.
	static public final int FLAGS_CONTINUOUS		 = 0x00000080
	
	// Metadata of the data point should not be synchronized. Set by user.
	static public final int FLAGS_NO_SYNCHRONIZATION = 0x00000100
	
	// Attributes of Data Preprocessing
	static public final String ATTR_PREPROC_TYPE	= 'preprocType'
	static public final String ATTR_PREPROC_PARAM	= 'preprocParam'

	// Attributes from the HomeMatic Logic Layer
	static public final String ATTR_DISPLAY_NAME	= 'displayName'
	static public final String ATTR_ROOM			= 'room'
	static public final String ATTR_FUNCTION		= 'function'
	static public final String ATTR_COMMENT 		= 'comment'
	
	// Custom attribute
	// A free to use attribute with JSON encoded content.
	static public final String ATTR_CUSTOM	 		= 'custom'
	
	// Attributes from the HomeMatic XML-RPC interface (from device)
	static public final String ATTR_PARAM_SET 		= 'paramSet'
	static public final String ATTR_TAB_ORDER 		= 'tabOrder'
	static public final String ATTR_MAXIMUM 		= 'maximum'
	static public final String ATTR_UNIT 			= 'unit'
	static public final String ATTR_MINIMUM 		= 'minimum'
	static public final String ATTR_CONTROL 		= 'control'
	static public final String ATTR_OPERATIONS 		= 'operations'
	static public final String ATTR_FLAGS 			= 'flags'
	static public final String ATTR_TYPE 			= 'type'
	static public final String ATTR_DEFAULT_VALUE 	= 'defaultValue'

	// Attribute values for ATTR_TYPE
	static public final String ATTR_TYPE_BOOL	  	= 'BOOL'
	static public final String ATTR_TYPE_ACTION    	= 'ACTION'
	static public final String ATTR_TYPE_ALARM     	= 'ALARM' // only CCU system variables
	static public final String ATTR_TYPE_INTEGER	= 'INTEGER'
	static public final String ATTR_TYPE_ENUM	  	= 'ENUM'
	static public final String ATTR_TYPE_FLOAT	  	= 'FLOAT'
	static public final String ATTR_TYPE_STRING	  	= 'STRING'
	
	// Attribute values for ATTR_OPERATIONS
	static public final int ATTR_OPERATIONS_READ   	= 1
	static public final int ATTR_OPERATIONS_WRITE  	= 2
	static public final int ATTR_OPERATIONS_EVENT  	= 4

	// Attribute values for ATTR_FLAGS
	static public final int ATTR_FLAGS_VISIBLE	  	= 1
	static public final int ATTR_FLAGS_INTERNAL	 	= 2
	static public final int ATTR_FLAGS_TRANSFORM	= 4
	static public final int ATTR_FLAGS_SERVICE	  	= 8
	static public final int ATTR_FLAGS_STICKY	  	= 16

	// unique identification
	DataPointIdentifier id
	 
	// management
	Integer idx
	String historyTableName
	int managementFlags
	
	// meta data
	Map<String, Object> attributes = [:]

	public boolean isHistoryHidden() { 
		getManagementFlag(FLAGS_HISTORY_HIDDEN)
	}
	public void setHistoryHidden(boolean isHidden) {
		setManagementFlag(FLAGS_HISTORY_HIDDEN, isHidden)
	}
	
	public boolean isHistoryDisabled() { 
		getManagementFlag(FLAGS_HISTORY_DISABLED)
	}
	public void setHistoryDisabled(boolean isDisabled) {
		setManagementFlag(FLAGS_HISTORY_DISABLED, isDisabled)
	}

	public boolean isHistoryString() {
		getManagementFlag(FLAGS_HISTORY_STRING)
	}
	public void setHistoryString(boolean isString) {
		setManagementFlag(FLAGS_HISTORY_STRING, isString)
	}

	public boolean isContinuous() { 
		getManagementFlag(FLAGS_CONTINUOUS)
	}
	public void setContinuous(boolean continuous) {
		setManagementFlag(FLAGS_CONTINUOUS, continuous)
	}

	public boolean isNoSynchronization() { 
		getManagementFlag(FLAGS_NO_SYNCHRONIZATION)
	}
	public void setNoSynchronization(boolean noSynchronization) {
		setManagementFlag(FLAGS_NO_SYNCHRONIZATION, noSynchronization)
	}

	public String getDisplayName() {
		if (isNoSynchronization() && attributes.displayName) {
			attributes.displayName
		} else {
			id.interfaceId+'.'+(attributes.displayName?:id.address)+'.'+id.identifier
		}
	}
	
	@Override
	public String toString() {
		List<String> list=[]
		if (id!=null) list << ("id: $id" as String)
		if (idx!=null) list << ("idx: $idx" as String)
		if (historyTableName!=null) list << ("historyTableName: $historyTableName" as String)
		if (managementFlags!=null) list << ("managementFlags: $managementFlags" as String)
		attributes.each { Map.Entry e ->
			if (e.value!=null) list << ("$e.key: $e.value" as String)	
		}
		list.join(', ')
	}
	
	private boolean getManagementFlag(int mask) {
		(managementFlags & mask) != 0
	}
	private void setManagementFlag(int mask, boolean value) {
		managementFlags&= ~mask
		managementFlags|= (value ? mask : 0)
	}
}
