/*-------------------------------------------------------------------------
svninfo: $Id: PkillCommand.java 994 2005-10-23 10:19:20Z maartenl $
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
package mmud.commands.guilds;

import java.util.logging.Logger;

import mmud.MudException;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.commands.Command;
import mmud.database.Database;

/**
 * Makes you, as guildmaster, accept a new member to the guild.
 * There are some requirements to follow:
 * <UL><LI>the user must exist and be a normal player
 * <LI>the user must have a <I>guildwish</I>
 * <LI>the user must not already be a member of a guild
 * </UL>
 * Command syntax something like : <TT>guildaccept &lt;username&gt;</TT>
 */
public class AcceptCommand extends GuildMasterCommand
{

	public AcceptCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	public boolean run(User aUser)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}
		String[] myParsed = getParsedCommand();
		Person toChar2 = Persons.getPerson(myParsed[1]);
		if ((toChar2 == null) || (!(toChar2 instanceof User)))
		{
			aUser.writeMessage("Cannot find that person.<BR>\r\n");
			return true;
		}
		if (toChar2 == null)
		{
			throw new RuntimeException("toChar2 is null! Impossible!");
		}
		User toChar = (User) toChar2;
		if (!toChar.isAttribute("guildwish") ||
			(!aUser.getGuild().getName().equals(toChar.getAttribute("guildwish").getValue())))
		{
			aUser.writeMessage(toChar.getName() + " does not wish to join your guild.<BR>\r\n");
			return true;
		}
		if (toChar.getGuild() != null)
		{
			throw new MudException("error occurred, a person is a member of a guild, yet has a guildwish parameter!");
		}
		toChar.removeAttribute("guildwish");
		toChar.setGuild(aUser.getGuild());
		aUser.getGuild().increaseAmountOfMembers();
		Database.writeLog(aUser.getName(), " accepted " + 
			toChar.getName() + " into guild " + aUser.getGuild().getName());
		aUser.writeMessage(toChar.getName() + " has joined your guild.<BR>\r\n");
		if (toChar.isActive())
		{
			toChar.writeMessage("You have joined guild <I>" + aUser.getGuild().getTitle() + "</I>.<BR>\r\n");
		}
		aUser.getGuild().increaseAmountOfMembers();
		return true;
	}

	public Command createCommand()
	{
		return new AcceptCommand(getRegExpr());
	}
	
}
