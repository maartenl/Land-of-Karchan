/*-------------------------------------------------------------------------
cvsinfo: $Header$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/
package mmud.rooms;

import java.util.Vector;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

public final class Rooms 
{
	private static Vector theRooms = new Vector();

	public Rooms()
	{
		theRooms = new Vector();
	}

	public static void init()
	{
		theRooms = new Vector();
	}

	public static Room getRoom(int aRoomNr)
	{
		Room myRoom = null;
		assert theRooms != null : "theRooms vector is null";
		if (Constants.logging)
		{
			System.err.println("Rooms.getRoom: " + aRoomNr);
		}
		if (aRoomNr == 0) 
		{
			return null;
		}
		for (int i=0;i < theRooms.size(); i++)
		{
			myRoom = (Room) theRooms.elementAt(i);
			if ((myRoom != null) && (myRoom.getId() == aRoomNr))
			{
				return myRoom;
			}
		}
		myRoom = Database.getRoom(aRoomNr);
		if (myRoom != null)
		{
			theRooms.addElement(myRoom);
		}
		return myRoom;
	}

}