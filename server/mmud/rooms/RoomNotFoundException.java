/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/rooms/RoomNotFoundException.java,v 1.2 2004/10/03 00:12:51 karchan Exp $
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

import java.util.logging.Logger;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Exception thrown when the user is not found (either in the entire
 * database, or in the list of active users.
 */
public class RoomNotFoundException extends RoomException
{

	/**
	 * constructor for creating an exception with a default message.
	 */
	public RoomNotFoundException()
	{
		super(Constants.ROOMNOTFOUNDERROR);
	}

	/**
	 * constructor for creating a exception with a message.
	 * @param aMessage the string containing the message
	 */
	public RoomNotFoundException(String aMessage)
	{
		super(Constants.ROOMNOTFOUNDERROR + " " + aMessage);
	}

}
