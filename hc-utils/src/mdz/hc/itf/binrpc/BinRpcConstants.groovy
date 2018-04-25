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

class BinRpcConstants {

	public static final byte[] HEADER_START 	= ['B', 'i', 'n']
	public static final byte HEADER_REQUEST 	= 0x00
	public static final byte HEADER_RESPONSE 	= 0x01
	public static final byte HEADER_FAULT 		= (byte)0xFF
	public static final byte HEADER_SIZE 		= 8
	
	public static final int TYPE_INT 			= 0x00000001
	public static final int TYPE_BOOLEAN 		= 0x00000002
	public static final int TYPE_STRING 		= 0x00000003
	public static final int TYPE_DOUBLE 		= 0x00000004
	public static final int TYPE_DATE 			= 0x00000005
	public static final int TYPE_BINARY 		= 0x00000006
	
	public static final int TYPE_ARRAY 			= 0x00000100
	public static final int TYPE_STRUCT 		= 0x00000101
	
	public static final double MANTISSA_MULTIPLICATOR = 0x40000000
	
	public static final int PACKET_SIZE_LIMIT = 10*1024*1024 // 10 MB
}
