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
import mmud.characters.Guild;
import mmud.characters.User;
import mmud.commands.Command;
import mmud.database.Database;

/**
 * Makes you leave a guild. There are some requirements to follow:
 * <UL>
 * <LI>you must already belong to a guild
 * </UL>
 * Command syntax something like : <TT>guildleave</TT>
 */
public class LeaveCommand extends GuildCommand
{

	public LeaveCommand(String aRegExpr)
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
		Guild guild = aUser.getGuild();
		aUser.removeAttribute("guildrank");
		aUser.getGuild().decreaseAmountOfMembers();
		aUser.setGuild(null);
		aUser.writeMessage("You leave guild <I>" + guild.getTitle() + "</I>.<BR>\r\n");
		Database.writeLog(aUser.getName(), "left guild " +
			guild.getName());
		guild.decreaseAmountOfMembers();
		return true;
	}

	public Command createCommand()
	{
		return new LeaveCommand(getRegExpr());
	}
	
}
