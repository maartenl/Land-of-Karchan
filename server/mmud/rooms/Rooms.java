/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/rooms/Rooms.java,v 1.7 2004/10/22 22:58:31 karchan Exp $
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
import java.util.logging.Logger;
import java.util.Hashtable;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

import simkin.*;

/**
 * Collection class containing rooms used.
 */
public final class Rooms implements Executable
{
	private static Vector theRooms = new Vector();

	public static Rooms create()
	{
		return new Rooms();
	}

	/**
	 * Creation of Rooms object.
	 */
	public Rooms()
	{
	}

	/**
	 * Initialises this object with an empty list.
	 */
	public static void init()
	{
		theRooms = new Vector();
	}

	/**
	 * retrieves a room based on the roomnumber.
	 * @param aRoomNr the number of the room to retrieve
	 * @return Room object containing the room requested. Returns a null
	 * pointer if the room does not exist.
	 */
	public static Room getRoom(int aRoomNr)
	throws MudException
	{
		Room myRoom = null;
		assert theRooms != null : "theRooms vector is null";
		Logger.getLogger("mmud").finer("");
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

	public void setValue(String field_name, String attrib_name,
		Object value, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name + ", value=" + 
			value + "[" + value.getClass() + "]");
		throw new FieldNotSupportedException(field_name + " not found.");
	}

	public void setValueAt(Object array_index, 
		String attrib_name, 
		Object value, ExecutableContext ctxt)
	{
		Logger.getLogger("mmud").finer("array_index=" + array_index +
			", atttrib_name=" + attrib_name + ", value=" + 
			value);
	}

	public ExecutableIterator createIterator()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}

	public ExecutableIterator createIterator(String qualifier)
	{
		Logger.getLogger("mmud").finer("qualifier=" + qualifier);
		return createIterator();
	}

	public Hashtable getAttributes()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}

	public Hashtable getInstanceVariables()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}

	public String getSource(String location)
	{
		Logger.getLogger("mmud").finer("location=" + location);
		return null;
	}

	public Object getValue(String field_name, String
		attrib_name, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name);
		throw new FieldNotSupportedException(field_name + " not found.");
	}

	public Object getValueAt(Object array_index,
		String attrib_name, ExecutableContext ctxt)
	{
		Logger.getLogger("mmud").finer("array_index=" + array_index +
			", atttrib_name=" + attrib_name);
		return null;
	}

	public Object method(String method_name, Object[]
		arguments, ExecutableContext ctxt)
	throws MethodNotSupportedException
	{
		Logger.getLogger("mmud").finer("method_name=" + method_name +
			", arguments=" + arguments);
		if (method_name.equals("find"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof Integer))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a Integer as argument.");
				}
				try
				{
					return getRoom(((Integer) arguments[0]).intValue());
				}
				catch (MudException e)
				{
					throw new MethodNotSupportedException(method_name + 
						" error searching for room. " + e);
				}
			}
		}
		throw new MethodNotSupportedException(method_name + " not found.");
	}

}