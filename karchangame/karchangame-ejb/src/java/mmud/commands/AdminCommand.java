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
import java.util.StringTokenizer;
import java.util.logging.Logger;

import mmud.Constants;
import mmud.MemoryManager;
import mmud.exceptions.MmudException;
import mmud.characters.GuildFactory;
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
import mmud.database.Database;
import mmud.items.ItemDefs;
import mmud.rooms.Rooms;

/**
 * Admin command. Necessary for resetting several caching stuff like:
 * <ul>
 * <li>rooms
 * <li>characters
 * <li>itemdefs
 * </ul>
 */
public class AdminCommand extends NormalCommand
{
	public AdminCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * Parses the string given, and tries to run the event from the Database.
	 * 
	 * @param aStringToParse
	 *            the string containing the eventid and either the roomnumber or
	 *            the name or nothing at all.
	 * @throws MmudException
	 *             whenever something cannot be parsed or the event throws an
	 *             error.
	 */
	public void runEvent(String aStringToParse) throws MmudException
	{
		StringTokenizer stuff = new StringTokenizer(aStringToParse, " ");
		String name = null;
		int eventid = 0;
		int room = 0;

		if (!stuff.hasMoreTokens())
		{
			throw new MmudException("Empty runevent admin '" + aStringToParse
					+ "' command.");
		}
		try
		{
			eventid = Integer.parseInt(stuff.nextToken());
		} catch (NumberFormatException e)
		{
			throw new MmudException(
					"Admincommand runevent could not be parsed. Missing eventid. String was '"
							+ aStringToParse + "'");
		}
		if (stuff.hasMoreTokens())
		{
			name = stuff.nextToken();
			try
			{
				room = Integer.parseInt(name);
				name = null;
			} catch (NumberFormatException e)
			{
				// seems like things have worked out.
				// could not parse into number, so probably a name.
			}
		}

		// run the event
		Database.runEvent(eventid, name, room);

	}

	/**
	 * This is used for issuing certain commands required for the proper
	 * administration of the server.
	 */
	@Override
	public boolean run(Player aPlayer) throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return
		// this
		if (!aPlayer.isGod())
		{
			aPlayer.writeMessage("You are not an administrator.<BR>\r\n");
			return true;
		}
		if (getCommand().toLowerCase().startsWith("admin status"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'status' executed");
			aPlayer.writeMessage(MemoryManager.MEMORY_MANAGER + ""
					+ Constants.getObjectCount()
					+ Constants.returnThreadStatus()
					+ Constants.returnSettings());
			return true;
		}
		if (getCommand().toLowerCase().startsWith("admin kick "))
		{
			Person myPerson = Persons
					.retrievePerson(getCommand().substring(11));
			if ((myPerson == null) || (!(myPerson instanceof Player)))
			{
				aPlayer.writeMessage("Person not found.<BR>\r\n");
				return true;
			}
			Persons.deactivatePlayer((Player) myPerson);
			Database.writeLog(aPlayer.getName(),
					"admin command 'kick' executed on " + myPerson.getName());
			Persons.sendMessage(aPlayer, myPerson,
					"%SNAME boot%VERB2 %TNAME from the game.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset rooms"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'reset rooms' executed");
			Rooms.init();
			Persons.init();
			aPlayer.writeMessage("Rooms have been reset.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset guilds"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'reset guilds' executed");
			GuildFactory.init();
			aPlayer.writeMessage("Guilds have been reset.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset characters"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'reset characters' executed");
			Persons.init();
			aPlayer
					.writeMessage("Persons have been reset, active persons reloaded.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset commands"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'reset commands' executed");
			Constants.setPlayerCommands(Database.getPlayerCommands());
			aPlayer
					.writeMessage("Player commands have been reloaded from database.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reset itemdefs"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'reset itemdefs' executed");
			ItemDefs.init();
			aPlayer.writeMessage("Item Definitions have been reset.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin shutdown"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'shutdown' executed");
			Constants.shutdown = true;
			aPlayer.writeMessage("Shutdown started.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin events off"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'events off' executed");
			Constants.events_active = false;
			aPlayer.writeMessage("Events have been turned off.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin events on"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'events on' executed");
			Constants.events_active = true;
			aPlayer.writeMessage("Events have been turned on.<BR>\r\n");
			return true;
		}
		if (getCommand().startsWith("admin wall"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'wall' executed ("
							+ getCommand().substring(11) + ")");
			Persons.sendWall(getCommand().substring(11));
			aPlayer.writeMessage("Wall message sent.<BR>\r\n");
			return true;
		}
		if (getCommand().startsWith("admin runevent"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'runevent' executed ("
							+ getCommand().substring(15) + ")");
			runEvent(getCommand().substring(15));
			aPlayer.writeMessage("Event " + getCommand().substring(15)
					+ " started.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin reload"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'reload' executed");
			Constants.loadInfo();
			aPlayer.writeMessage("Reload done.<BR>\r\n");
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin uptime"))
		{
			Database.writeLog(aPlayer.getName(),
					"admin command 'uptime' executed");
			Calendar time = Calendar.getInstance();
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
					DateFormat.FULL);
			Calendar oldtime = Constants.theGameStartupTime;
			long checkem = time.getTimeInMillis() - oldtime.getTimeInMillis();
			String uptime = "Game has been online for " + checkem / 86400000
					+ " days, " + checkem % 86400000 / 3600000 + " hours, "
					+ checkem % 3600000 / 60000 + " minutes, " + checkem
					% 60000 / 1000 + " seconds, " + checkem % 1000
					+ " milliseconds.<BR>\r\n";
			aPlayer.writeMessage("Game started on "
					+ df.format(oldtime.getTime())
					+ ".<BR>\r\nCurrent time is " + df.format(time.getTime())
					+ ".<BR>\r\n" + uptime);
			return true;
		}
		if (getCommand().equalsIgnoreCase("admin help"))
		{
			Database.writeLog(aPlayer.getName(), "admin command 'help' executed");
			aPlayer
					.writeMessage("Possible commands are:<DL>"
							+ "<DT>admin help<DD>this help text"
							+ "<DT>admin status<DD>shows a view of the status of the program."
							+ "<DT>admin kick &lt;character name&gt;<DD>kicks the character off the game immediately."
							+ "<DT>admin reset characters<DD>reset the cached characters, required every time you make a change to a character. "
							+ "Already active characters are reloaded into the cache from the database."
							+ "<DT>admin reset itemdefs<DD>reset the cached item definitions, required every time you make a change to an itemdefinition"
							+ "<DT>admin reset rooms<DD>reset the cached rooms, required every time"
							+ " you make a change to a room. Also implicitly runs "
							+ "<DT>admin reset guilds<DD>reset the cached guilds, required every time"
							+ " you make a change to a guild."
							+ "<DT>admin reset commands<DD>reset the cached <I>special</I> commands. "
							+ "Necessary if a command has been deleted, added or changed."
							+ "<DT>admin runevent eventid<DD>runs an event, in order to see if it works."
							+ "<DT>admin events on|off<DD>turns off or on the triggering of events."
							+ "<DT>admin reload<DD>reloads the settings from the config"
							+ "file. Used when the config file has changed. Database connection changes "
							+ "require a reboot."
							+ "<DT>admin shutdown<DD>Shuts down the game. Carefull! The game is not automatically restarted!"
							+ "<DT>admin wall &lt;message&gt;<DD>pages all users with the message entered"
							+ "<DT>admin uptime<DD>show the datetime when the game was started and the current datetime "
							+ "and the elapsed time period in between."
							+ "</DL>\r\n");
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new AdminCommand(getRegExpr());
	}

}
