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
package mmud.commands;  

import java.util.logging.Logger;
import java.util.Vector;
import java.util.Calendar;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Admin command. Necessary for resetting several caching stuff like:
 * <ul><li>rooms
 * <li>characters
 * <li>itemdefs
 * </ul>
 * The following commands are possible:
 * <ul><li>admin reset rooms
 * <li>admin reset characters
 * <li>admin reset itemdefs
 * <li>admin wall &lt;message&gt;
 * <li>admin shutdown
 * </ul>
 */
public class AdminCommand extends NormalCommand
{
	private String name;
	private String adject1;
	private String adject2;
	private String adject3;
	private int amount;

	public AdminCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * This is used for issuing certain commands required
	 * for the proper administration of the server.
	 */
	public boolean run(User aUser)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}
		// initialise string, important otherwise previous instances will return this
		if (!aUser.isGod())
		{
			aUser.writeMessage("You are not an administrator.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset rooms"))
		{
			Database.writeLog(aUser.getName(), "admin command 'reset rooms' executed");
			Rooms.init();
			aUser.writeMessage("Rooms have been reset.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset characters"))
		{
			Database.writeLog(aUser.getName(), "admin command 'reset characters' executed");
			Persons.init();
			aUser.writeMessage("Persons have been reset, active persons reloaded.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin shutdown"))
		{
			Database.writeLog(aUser.getName(), "admin command 'shutdown' executed");
			Constants.shutdown = true;
			aUser.writeMessage("Shutdown started.<BR>\r\n");
			return true;
		}
		if (getCommand().startsWith("admin wall"))
		{
			Database.writeLog(aUser.getName(), "admin command 'wall' executed (" + getCommand().substring(11) + ")");
			Persons.sendWall(getCommand().substring(11));
			aUser.writeMessage("Wall message sent.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin uptime"))
		{
			Database.writeLog(aUser.getName(), "admin command 'uptime' executed (" +
				Constants.theGameStartupTime + ")");
			Calendar time = Calendar.getInstance();
			Calendar oldtime = Constants.theGameStartupTime;
			aUser.writeMessage("Game started on " + 
				oldtime +
				".<BR>\r\nCurrent time is " + time + 
				".<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin help"))
		{
			Database.writeLog(aUser.getName(), "admin command 'help' executed");
			aUser.writeMessage("Possible commands are:<DL>" +
				"<DT>admin help<DD>this help text" +
				"<DT>admin reset characters<DD>reset the cached rooms, required every time you make a change to a room" +
				"<DT>admin reset rooms<DD>reset the cached characters, required every time you make a change to a character." +
				"Already active characters are reloaded into the cache from the database." +
				"<DT>admin wall &lt;message&gt;<DD>pages all users with the message entered" +
				"<DT>admin uptime<DD>show the datetime when the game was started and the current datetime " +
				"and the elapsed time period in between." +
				"</DL>\r\n");
			return true;
		}
		return false;
	}

}
