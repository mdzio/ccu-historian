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
package mdz.hc.itf.hm

import groovy.transform.EqualsAndHashCode

public class HmModel {

	@EqualsAndHashCode
	public static class HmObject {
		int iseId
		String address
		String displayName
		String comment
	}
	
	@EqualsAndHashCode
	public static class Device extends HmObject {
		List<Channel> channels=[]
	}
	
	@EqualsAndHashCode
	public static class Channel extends HmObject {
		List<DataPoint> dataPoints=[]
		Device device
		List<Room> rooms=[]
		List<Function> functions=[]
	}

	@EqualsAndHashCode
	public static class DataPoint extends HmObject {
		Channel channel
	}

	@EqualsAndHashCode
	public static class Room extends HmObject {
		List<Channel> channels=[]
	}

	@EqualsAndHashCode
	public static class Function extends HmObject {
		List<Channel> channels=[]
	}

	private List<Device> devices=[]
	private List<Room> rooms=[]
	private List<Function> functions=[]
	private Map<String, Channel> addressToChannel=[:]
	private Map<Integer, Channel> chIseIdToChannel=[:]
	
	public void add(Device dev) {
		devices << dev
		dev.channels.each { 
			addressToChannel[it.address]=it
			chIseIdToChannel[it.iseId]=it 
		}
	}

	public void add(Room room) {
		rooms << room
		room.channels.each {
			it.rooms << room	
		}
	}

	public void add(Function func) {
		functions << func
		func.channels.each {
			it.functions << func	
		}
	}

	public List<Device> getDevices() {
		devices
	}
	
	public List<Room> getRooms() {
		rooms
	}
	
	public List<Function> getFunctions() {
		functions
	}

	public Channel getChannelByAddress(String address) {
		addressToChannel[address]
	}

	public Channel getChannelByIseId(int iseId) {
		chIseIdToChannel[iseId]
	}

	@Override
	String toString() {
		StringBuilder sb=[]
		boolean first=true
		devices.each { Device dev ->
			if (first) first=false; else sb << "\n"
			sb << "Device: " << dev.displayName << " (" << dev.iseId << ", " << dev.address << ")"
			dev.channels.each { Channel ch ->
				sb << "\n    Channel: " << ch.displayName << " (" << ch.iseId << ", " << ch.address << ")"
				ch.dataPoints.each { DataPoint dp ->
					sb << "\n        Data point: " << dp.address << " (" << dp.iseId << ")"
				}
				sb << "\n        Rooms: " << ch.rooms.collect { it.displayName + " (" + it.iseId + ")" }.join(", ")
				sb << "\n        Functions: " << ch.functions.collect { it.displayName + " (" + it.iseId + ")" }.join(", ")
			}
		}
		sb.toString()
	}
}
