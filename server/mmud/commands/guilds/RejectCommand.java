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
import mmud.Attribute;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.characters.GuildFactory;
import mmud.characters.Guild;
import mmud.database.Database;
import mmud.commands.NormalCommand;
import mmud.commands.Command;

/**
 * Makes you, as guildmaster, reject a person wanting to join your guild.
 * There are some requirements to follow:
 * <UL><LI>the user must exist and be a normal player
 * <LI>the user must have a <I>guildwish</I>
 * <LI>the user must not already be a member of a guild
 * </UL>
 * Command syntax something like : <TT>guildreject &lt;username&gt;</TT>
 */
public class RejectCommand extends GuildMasterCommand
{

	public RejectCommand(String aRegExpr)
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
		Person toChar2 = (User) Persons.retrievePerson(myParsed[1]);
		if ((toChar2 == null) || (!(toChar2 instanceof User)))
		{
			aUser.writeMessage("Cannot find that person.<BR>\r\n");
			return true;
		}
		User toChar = (User) toChar2;
		if (!toChar.isAttribute("guildwish") && 
			aUser.getGuild().getName().equals(toChar.getAttribute("guildwish").getValue()))
		{
			aUser.writeMessage(toChar.getName() + " does not wish to join your guild.<BR>\r\n");
			return false;
		}
		if (toChar.getGuild() != null)
		{
			throw new MudException("error occurred, a person is a member of a guild, yet has a guildwish parameter!");
		}
		toChar.removeAttribute("guildwish");
		Database.writeLog(aUser.getName(), "denied " + 
			toChar.getName() + " membership into guild " + aUser.getGuild().getName());
		aUser.writeMessage("You have denied " + toChar.getName() + " admittance to your guild.<BR>\r\n");
		toChar.writeMessage("You have been denied membership of guild <I>" + aUser.getGuild().getTitle() + "</I>.<BR>\r\n");
		return true;
	}

	public Command createCommand()
	{
		return new RejectCommand(getRegExpr());
	}
	
}
