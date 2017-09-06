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
package mdz.hc.itf.binrpc

import groovy.transform.CompileStatic

@CompileStatic
public class BinRpcException extends Exception {
	
	public static final int GENERIC_ERROR = -1
	public static final int UNKNOWN_DEVICE_OR_CHANNEL = -2
	public static final int UNKNOWN_PARAMSET = -3
	public static final int EXPECTED_DEVICEADDRESS = -4
	public static final int UNKNOWN_PARAMETER_OR_VALUE = -5
	public static final int OPERATION_NOT_SUPPORTED_BY_PARAMETER = -6
	
	int faultCode = GENERIC_ERROR
	
	public BinRpcException(String msg) {
		super(msg)
	}
	
	public BinRpcException(int faultCode, String msg) {
		super(msg)
		this.faultCode=faultCode
	}
}
