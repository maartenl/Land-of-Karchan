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
 * Makes you, as guildmaster, promote somebody else as guildmaster. There are
 * some requirements to follow:
 * <UL>
 * <LI>the user must exist and be a normal player
 * <LI>the user must be online
 * <LI>the user must be a member of the guild
 * </UL>
 * Command syntax something like : <TT>guildmasterchange &lt;username&gt;</TT>
 */
public class ChangeMasterCommand extends GuildMasterCommand
{

	public ChangeMasterCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(User aUser) throws MudException
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
		if (toChar.getGuild() == null
				|| (!toChar.getGuild().getName().equals(
						aUser.getGuild().getName())))
		{
			throw new MudException(
					"error occurred, the person to promote to guildmaster is either not in a guild or not in the correct guild.");
		}
		if (!toChar.isActive())
		{
			aUser.writeMessage("That person is not currently playing.<BR>\r\n");
			return true;
		}
		Database.writeLog(aUser.getName(), " stepped down as guildmaster of "
				+ aUser.getGuild().getName() + " in favor of "
				+ toChar.getName());
		aUser.getGuild().setBossName(toChar2.getName());
		aUser.writeMessage(toChar.getName()
				+ " is now the guildmaster.<BR>\r\n");
		toChar.writeMessage("You are now the guildmaster of <I>"
				+ aUser.getGuild().getTitle() + "</I>.<BR>\r\n");
		return true;
	}

	public Command createCommand()
	{
		return new ChangeMasterCommand(getRegExpr());
	}

}
