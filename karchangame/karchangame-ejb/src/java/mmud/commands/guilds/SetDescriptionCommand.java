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

import mmud.exceptions.MmudException;
import mmud.database.entities.Player;
import mmud.commands.Command;
import mmud.database.Database;

/**
 * Makes you, as guildmaster, set the description of the guild.
 * Command syntax something like : <TT>guilddescription &lt;description&gt;</TT>
 */
public class SetDescriptionCommand extends GuildMasterCommand
{

	public SetDescriptionCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	public boolean run(Player aPlayer)
	throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aPlayer))
		{
			return false;
		}
		String[] myParsed = getParsedCommand();
		aPlayer.getGuild().setDescription(
			getCommand().substring(myParsed[0].length() + 1)
			);
		Database.writeLog(aPlayer.getName(), " set guilddescription of " + 
			" guild " + aPlayer.getGuild().getName());
		aPlayer.writeMessage("You have set a new description for your guild.<BR>\r\n");
		return true;
	}

	public Command createCommand()
	{
		return new SetDescriptionCommand(getRegExpr());
	}
	
}
