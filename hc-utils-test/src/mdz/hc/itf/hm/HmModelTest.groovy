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
package mdz.hc.itf.hm;

import org.junit.Test

class HmModelTest {

	@Test
	void test() {
		HmModel m=[]
		
		int iseId=0
		3.times { 
			HmModel.Device dev=[]
			dev.iseId=iseId++
			dev.displayName="My Device " + it
			dev.address="ABC" + dev.iseId
			
			3.times {
				HmModel.Channel ch=[]
				ch.iseId=iseId++
				ch.displayName="My Channel " + it
				ch.address=dev.address + ":" + it
				
				ch.device=dev
				dev.channels << ch
				
				3.times {  
					HmModel.DataPoint dp=[]
					dp.iseId=iseId++
					dp.address=ch.address + ".DP" + it
					
					dp.channel=ch
					ch.dataPoints << dp
				}
			}
			
			m.add dev
		}
		
		List<HmModel.Channel> channels=m.devices.channels.flatten()
		println channels.collect { it?.displayName }
		int chIdx=0
		2.times {
			HmModel.Room room=[]
			room.iseId=iseId++
			room.displayName="My Room " + it
			room.comment="My Room Comment " + it
			4.times {
				HmModel.Channel ch=channels[chIdx++]
				room.channels << ch
			}
			m.add room
		}
		
		String str=m as String
	
		assert str=='''Device: My Device 0 (0, ABC0)
    Channel: My Channel 0 (1, ABC0:0)
        Data point: ABC0:0.DP0 (2)
        Data point: ABC0:0.DP1 (3)
        Data point: ABC0:0.DP2 (4)
        Rooms: My Room 0 (39)
        Functions: 
    Channel: My Channel 1 (5, ABC0:1)
        Data point: ABC0:1.DP0 (6)
        Data point: ABC0:1.DP1 (7)
        Data point: ABC0:1.DP2 (8)
        Rooms: My Room 0 (39)
        Functions: 
    Channel: My Channel 2 (9, ABC0:2)
        Data point: ABC0:2.DP0 (10)
        Data point: ABC0:2.DP1 (11)
        Data point: ABC0:2.DP2 (12)
        Rooms: My Room 0 (39)
        Functions: 
Device: My Device 1 (13, ABC13)
    Channel: My Channel 0 (14, ABC13:0)
        Data point: ABC13:0.DP0 (15)
        Data point: ABC13:0.DP1 (16)
        Data point: ABC13:0.DP2 (17)
        Rooms: My Room 0 (39)
        Functions: 
    Channel: My Channel 1 (18, ABC13:1)
        Data point: ABC13:1.DP0 (19)
        Data point: ABC13:1.DP1 (20)
        Data point: ABC13:1.DP2 (21)
        Rooms: My Room 1 (40)
        Functions: 
    Channel: My Channel 2 (22, ABC13:2)
        Data point: ABC13:2.DP0 (23)
        Data point: ABC13:2.DP1 (24)
        Data point: ABC13:2.DP2 (25)
        Rooms: My Room 1 (40)
        Functions: 
Device: My Device 2 (26, ABC26)
    Channel: My Channel 0 (27, ABC26:0)
        Data point: ABC26:0.DP0 (28)
        Data point: ABC26:0.DP1 (29)
        Data point: ABC26:0.DP2 (30)
        Rooms: My Room 1 (40)
        Functions: 
    Channel: My Channel 1 (31, ABC26:1)
        Data point: ABC26:1.DP0 (32)
        Data point: ABC26:1.DP1 (33)
        Data point: ABC26:1.DP2 (34)
        Rooms: My Room 1 (40)
        Functions: 
    Channel: My Channel 2 (35, ABC26:2)
        Data point: ABC26:2.DP0 (36)
        Data point: ABC26:2.DP1 (37)
        Data point: ABC26:2.DP2 (38)
        Rooms: 
        Functions: '''
		
		HmModel.Channel ch=m.getChannelByAddress("ABC26:1")
		assert ch.iseId==31
		assert ch.displayName=="My Channel 1"
		
		assert m.rooms.every { it.channels.size()==4 && it.channels.every { it!=null } }
	}
}
