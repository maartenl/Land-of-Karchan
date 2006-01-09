/*-------------------------------------------------------------------------
svninfo: $Id: Bot.java 1005 2005-10-30 13:21:36Z maartenl $
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

package mmud.characters;

import java.util.TreeMap;

import mmud.MudException;
import mmud.database.Database;

/**
 * This class contains the properties of a guild.
 * @see User
 */
public class GuildFactory
{

	private static TreeMap theGuilds = new TreeMap();

	/**
	 * Default Constructor privatised.
	 */
	public GuildFactory()
	{
		// disable default constructor
	}

	/**
	 * Returns a guild based on the guildname. If the guildname is null
	 * it will automatically return null.
	 * @param aGuildName the name of the guild
	 * @return the guild itself.
	 */
	public static Guild createGuild(String aGuildName)
	throws MudException
	{
		if (aGuildName == null)
		{
			return null;
		}
		Guild guild = (Guild) theGuilds.get(aGuildName);
		if (guild == null)
		{
			guild = Database.getGuild(aGuildName);
			if (guild == null)
			{
				throw new MudException("Guild not found!");
			}
			theGuilds.put(guild.getName(), guild);
		}
		if (guild == null)
		{
			throw new MudException("Guild not found!");
		}
		return guild;
	}

	/**
	 * Re-initialise the known guilds to an empty list. This is a good
	 * thing to do if the guild information in the database has changed.
	 */
	public static void init()
	{
		theGuilds = new TreeMap();
	}
}
