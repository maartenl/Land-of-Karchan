/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/UserCommandInfo.java,v 1.2 2005/09/28 04:33:50 karn Exp $
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

package mmud;

import mmud.database.Database;

/**
 * The definition of a user command. Bear in mind that commands that
 * are executed often do not make very good global user commands,
 * as the load on the database might be a little too much.
 */ 
public class UserCommandInfo 
{
	private int theCommandId;
	
	private String theCommand;

	private String theMethodName;

	private Integer theRoom;

	/**
	 * constructor for creating a user command for a specific room.
	 * @param aCommand the regular expression for determining
	 * the command is equal to the command entered.
	 * @param aMethodName the name of the method to call.
	 * @param aRoom the room in which this command is active. Can be
	 * a null pointer if the command is active in all rooms.
	 * @param aCommandId the identification number for the command
	 */
	public UserCommandInfo(int aCommandId, String aCommand, 
		String aMethodName, Integer aRoom)
	{
		assert aCommand != null : "aCommand is null";
		assert aMethodName != null : "aMethodName is null";
		theCommandId = aCommandId;
		theCommand = aCommand;
		theMethodName = aMethodName;
		theRoom = aRoom;
	}

	/**
	 * constructor for creating a user command for all rooms.
	 * @param aCommand the regular expression for determining
	 * the command is equal to the command entered.
	 * @param aMethodName the name of the method to call.
	 * @param aCommandId the identification number for the command
	 */
	public UserCommandInfo(int aCommandId, String aCommand, 
		String aMethodName)
	{
		assert aCommand != null : "aCommand is null";
		assert aMethodName != null : "aMethodName is null";
		theCommandId = aCommandId;
		theCommand = aCommand;
		theMethodName = aMethodName;
	}

	public String getCommand()
	{
		return theCommand;
	}

	public String getMethodName()
	{
		return theMethodName;
	}

	public Integer getRoom()
	{
		return theRoom;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof UserCommandInfo))
		{
			return false;
		}
		UserCommandInfo myUC = (UserCommandInfo) o;
		boolean p = myUC.getCommand().equals(getCommand());
		p = p && myUC.getMethodName().equals(getMethodName());
		if (getRoom() == null)
		{
			p = p && (myUC.getRoom() == null);
		}
		else
		{
			p = p && myUC.getRoom().equals(getRoom());
		}
		return p;
	}

	public String toString()
	{
		return theCommandId + ":" + getCommand() + ":" + getMethodName() + ":" + getRoom();
	}

	/**
	 * Use this method when the command that had to be run, throws
	 * an exception.
	 */
	public void deactivateCommand()
		throws MudException
	{
		Constants.removeUserCommand(this);
		Database.deactivateCommand(theCommandId);
	}

}
