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

import java.util.logging.Logger;
import java.util.Vector;

import mmud.MudException;
import mmud.database.MudDatabaseException;
import mmud.database.Database;

/**
 * This class contains a possible rank in a guild.
 * @see Guild
 */
public class GuildRank
{

	private int theId;
	private String theTitle;

	/**
	 * Constructor, to be used for newly created guilds.
	 */
	public GuildRank(int aId,
		String aDesc)
	throws MudException
	{
		if ((aId < 0) || (aDesc == null))
		{
			throw new MudException("illegal id or rankdescription unknown!");
		}
		theId = aId;
		theTitle = aDesc;
	}
	
	public int getId()
	{
		return theId;
	}
	
	public String getTitle()
	{
		return theTitle;
	}

	public String toString()
	{
		return theId + ":" + theTitle;
	}	
}
