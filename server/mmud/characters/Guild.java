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
import java.util.TreeMap;
import java.util.Iterator;

import mmud.MudException;
import mmud.database.MudDatabaseException;
import mmud.database.Database;
import mmud.Attribute;

/**
 * This class contains the properties of a guild.
 * @see User
 */
public class Guild
{

	private String theName;
	private int theMaxDaysGuildDeath;
	private int theDaysGuildDeath;
	private int theMinGuildMembers;
	private String theBossName;

	private String theTitle;
	private int theMinGuildLevel;
	private String theGuildDescription;
	private String theGuildUrl;
	private String theLogonMessage;

	private boolean theActive;
	
	private TreeMap theRanks = new TreeMap();

	/**
	 * Constructor, to be used for newly created guilds.
	 */
	public Guild(String aName,
		int aMaxDaysGuildDeath,
		int aDaysGuildDeath,
		int aMinGuildMembers,
		String aBossName,
		boolean aActive)
	throws MudException
	{
		if ((aName == null) || (aBossName == null))
		{
			throw new MudException("guildname or bossname unknown!");
		}
		theName = aName;
		theMaxDaysGuildDeath = aMaxDaysGuildDeath;
		theDaysGuildDeath = aDaysGuildDeath;
		theMinGuildMembers = aMinGuildMembers;
		theBossName = aBossName;
		theActive = aActive;
	}
		

	/**
	 * Constructor. Create a guild.
	 * @param aName the name of the guild, a unique identifier. Usually 
	 * just one word.
	 * @param aMaxDaysGuildDeath the TTL (time-to-live) for a guild
	 * once it gets below minimum expected guild members.
	 * @param aDaysGuildDeath apparently if this is not 0, it means
	 * the amount of members have fallen below the minimum and this
	 * guild will be purged when the <I>aDaysGuildDeath</I> equals
	 * <I>aMaxDaysGuildDeath</I>.
	 * @param aMinGuildMembers the minimum required amount of guildmembers.
	 * @param aBossName the name of the character that <I>started</I> the guild.
	 * @param aTitle the full title of the guild.
	 * @param aMinGuildLevel the minimum level before you are allowed to enter the guild.
	 * @param aGuildDescription a description of the guild.
	 * @param aGuildUrl the Universal Resource Locator (URL) or the 
	 * webaddress of the guild homepage.
	 : @param aActive boolean, true if it is an active guild, false if it is 
	 * a new guild that has not yet had the required number of guild members.
	 * @see Guild(String,int,int,int,String,boolean)
	 * @param aLogonMessage a logon message that is made visible
	 * when a member of the guild logs onto the game.
	 */
	public Guild(String aName,
		int aMaxDaysGuildDeath,
		int aDaysGuildDeath,
		int aMinGuildMembers,
		String aBossName,
		String aTitle,
		int aMinGuildLevel,
		String aGuildDescription,
		String aGuildUrl,
		String aLogonMessage,
		boolean aActive)
	throws MudException
	{
		this(aName, aMaxDaysGuildDeath, aDaysGuildDeath, aMinGuildMembers, aBossName, aActive);
		Logger.getLogger("mmud").finer("");
		theTitle = aTitle;
		theMinGuildLevel = aMinGuildLevel;
		theGuildDescription = aGuildDescription;
		theGuildUrl = aGuildUrl;
		theLogonMessage = aLogonMessage;
	}

	public void setTitle(String aTitle)
	throws MudException
	{
		theTitle = aTitle;
		Database.setGuild(this);
	}

	public void setLogonMessage(String aLogonMessage)
	throws MudException
	{
		theLogonMessage = aLogonMessage;
		Database.setGuild(this);
	}

	public void setMinGuildLevel(int aLevel)
	throws MudException
	{
		theMinGuildLevel = aLevel;
		Database.setGuild(this);
	}
	
	public void setDescription(String aDescription)
	throws MudException
	{
		theGuildDescription = aDescription;
		Database.setGuild(this);
	}
	
	public void setGuildUrl(String aUrl)
	throws MudException
	{
		theGuildUrl = aUrl;
		Database.setGuild(this);
	}
	
	public String getName()
	{
		return theName;
	}
	
	public int getDaysGuildDeath()
	{
		return theDaysGuildDeath;
	}
	
	public int getMaxDaysGuildDeath()
	{
		return theMaxDaysGuildDeath;
	}

	public int getMinGuildMembers()
	{
		return theMinGuildMembers;
	}

	public String getBossName()
	{
		return theBossName;
	}

	public String getTitle()
	{
		return theTitle;
	}
	
	public String getLogonMessage()
	{
		return theLogonMessage;
	}
	
	public int getMinGuildLevel()
	{
		return theMinGuildLevel;
	}
	
	public String getDescription()
	{
		return theGuildDescription;
	}
	
	public String getGuildUrl()
	{
		return theGuildUrl;
	}

	public boolean isActive()
	{
		return theActive;
	}

	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Guild))
		{
			return false;
		}
		Guild aGuild = (Guild) obj;
		if (aGuild == null)
		{
			return false;
		}
		return getName().equals(aGuild.getName());
	}
	
	public String toString()
	{
		return super.toString() + ":" + getName();
	}

	/**
	 * This is primarily used for displaying the current status of the guild
	 * to a member of the guild.
	 */
	public String getGuildDetails()
	throws MudException
	{
		Vector list = Database.getGuildMembers(this);
		StringBuffer result = new StringBuffer();
		if (theLogonMessage != null)
		{
			result.append("<B>Logonmessage:</B>" + theLogonMessage + "<P>");
		}
		result.append("<B>Members</B><P><TABLE>");
		for (int i=0;i<list.size();i++)
		{
			Person character = (Person) list.get(i);
			result.append("<TR><TD>");
			if (character.isActive())
			{
				result.append("<B>");
			}
			result.append(character.getName());
			result.append("</TD><TD>");
			if (character.isAttribute("guildrank"))
			{
				Attribute attrib = character.getAttribute("guildrank");
				int title = Integer.parseInt(attrib.getValue());
				GuildRank rank = getRank(title);
				if (rank == null)
				{
					result.append(" Initiate");
				}
				else
				{
					result.append(" " + rank.getTitle());
				}
			}
			else
			{
				result.append(" Initiate");
			}
			if (character.isActive())
			{
				result.append("</B>");
			}
			result.append("</TD></TR>");
		}
		result.append("</TABLE><P><B>Hopefuls</B><BR>");
		list = Database.getGuildHopefuls(this);
		for (int i=0;i<list.size();i++)
		{
			result.append((String) list.get(i));
			result.append(" ");
		}
		result.append("<P><B>Guildranks</B><BR>");
		Iterator ranks = theRanks.values().iterator();
		while (ranks.hasNext())
		{
			GuildRank rank = (GuildRank) ranks.next();
			result.append(rank.getTitle() + "(" + rank.getId() + ")<BR>");
		}
		return "<H1><IMG SRC=\"/images/gif/money.gif\">" + getTitle() + "</H1>You"
			+ " are a member of the <I>" + getTitle() + "</I> (" + getName()
			+ ").<BR>The current guildmaster is <I>" + getBossName() 
			+ "</I>.<BR>The guild is <I>" 
			+ (isActive() ? "active" : "inactive")
			+ "</I>.<P>" + result + "<P>";

	}
	
	/**
	 * Removes all guildranks and starts with a clean slate.
	 */
	public void setRanks(TreeMap aRanks)
	{
		if (aRanks == null)
		{
			throw new RuntimeException("ranks was null.");
		}
		theRanks = aRanks;
	}
	
	/**
	 * Returns the rank based on the rank id.
	 * In case the rank id does not exist, an
	 * Exception will be thrown.
	 */
	public GuildRank getRank(int id)
	{
		return (GuildRank) theRanks.get(new Integer(id));
	}
	
	public void addRank(GuildRank aRank)
	throws MudException
	{
		theRanks.put(new Integer(aRank.getId()), aRank);
		Database.addGuildRank(this, aRank);
	}

	public void removeRank(GuildRank aRank)
	throws MudException
	{
		theRanks.remove(new Integer(aRank.getId()));
		Database.removeGuildRank(this, aRank);
	}
}
