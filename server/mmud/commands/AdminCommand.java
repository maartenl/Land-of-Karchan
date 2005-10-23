/*-------------------------------------------------------------------------
svninfo: $Id$
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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import mmud.Constants;
import mmud.MudException;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.database.Database;
import mmud.items.ItemDefs;
import mmud.rooms.Rooms;

/**
 * Admin command. Necessary for resetting several caching stuff like:
 * <ul><li>rooms
 * <li>characters
 * <li>itemdefs
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
		if (getCommand().toLowerCase().startsWith("admin kick "))
		{
			Person myPerson = 
				Persons.retrievePerson(getCommand().substring(11));
			if ((myPerson == null) || (!(myPerson instanceof User)))
			{
				aUser.writeMessage("Person not found.<BR>\r\n");
				return true;
			}
			Persons.deactivateUser((User) myPerson);
			Database.writeLog(aUser.getName(), "admin command 'kick' executed on " + myPerson.getName());
			Persons.sendMessage(aUser, myPerson, "%SNAME boot%VERB2 %TNAME from the game.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset rooms"))
		{
			Database.writeLog(aUser.getName(), "admin command 'reset rooms' executed");
			Rooms.init();
			Persons.init();
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
		if (getCommand().equalsIgnoreCase("admin reset commands"))
		{
			Database.writeLog(aUser.getName(), "admin command 'reset commands' executed");
			Constants.setUserCommands(Database.getUserCommands());
			aUser.writeMessage("User commands have been reloaded from database.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset itemdefs"))
		{
			Database.writeLog(aUser.getName(), "admin command 'reset itemdefs' executed");
			ItemDefs.init();
			aUser.writeMessage("Item Definitions have been reset.<BR>\r\n");
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
		if (getCommand().equalsIgnoreCase("admin reload"))
		{
			Database.writeLog(aUser.getName(), "admin command 'reload' executed");
			Constants.loadInfo();
			aUser.writeMessage("Reload done.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin uptime"))
		{
			Database.writeLog(aUser.getName(), "admin command 'uptime' executed");
			Calendar time = Calendar.getInstance();
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
			Calendar oldtime = Constants.theGameStartupTime;
			long checkem = time.getTimeInMillis() - oldtime.getTimeInMillis();
			String uptime = "Game has been online for " + 
				checkem / 86400000 + " days, " +
				checkem % 86400000 / 3600000  + " hours, " +
				checkem % 3600000 / 60000 + " minutes, " +
				checkem % 60000 / 1000 + " seconds, " +
				checkem % 1000 + " milliseconds.<BR>\r\n";
			aUser.writeMessage("Game started on " + 
				df.format(oldtime.getTime()) +
				".<BR>\r\nCurrent time is " +
				df.format(time.getTime()) + 
				".<BR>\r\n" + uptime);
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin help"))
		{
			Database.writeLog(aUser.getName(), "admin command 'help' executed");
			aUser.writeMessage("Possible commands are:<DL>" +
				"<DT>admin help<DD>this help text" +
				"<DT>admin kick &lt;character name&gt;<DD>kicks the character off the game immediately." +
				"<DT>admin reset characters<DD>reset the cached characters, required every time you make a change to a character. " +
				"Already active characters are reloaded into the cache from the database." +
				"<DT>admin reset itemdefs<DD>reset the cached item definitions, required every time you make a change to an itemdefinition" +
				"<DT>admin reset rooms<DD>reset the cached rooms, required every time"
				+ " you make a change to a room. Also implicitly runs " +
				"<I>admin reset characters</I>." +
				"<DT>admin reset commands<DD>reset the cached <I>special</I> commands. " +
				"Necessary if a command has been deleted, added or changed." +
				"<DT>admin reload<DD>reloads the settings from the config" +
				"file. Used when the config file has changed. Database connection changes " +
				"require a reboot." +
				"<DT>admin shutdown<DD>Shuts down the game. Carefull! The game is not automatically restarted!" +
				"<DT>admin wall &lt;message&gt;<DD>pages all users with the message entered" +
				"<DT>admin uptime<DD>show the datetime when the game was started and the current datetime " +
				"and the elapsed time period in between." +
				"</DL>\r\n");
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new AdminCommand(getRegExpr());
	}
	
}
