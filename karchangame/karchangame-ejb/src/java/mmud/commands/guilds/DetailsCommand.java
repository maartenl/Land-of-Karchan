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

/**
 * Provides all the necessary details about the current guild that you 
 * are a member of.
 * Command syntax something like : <TT>guilddetails</TT>
 */
public class DetailsCommand extends GuildCommand
{

	private String theResult;
	
	public DetailsCommand(String aRegExpr)
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
		theResult = aPlayer.getGuild().getGuildDetails() + aPlayer.printForm();
		return true;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theResult;
	}

	public Command createCommand()
	{
		return new DetailsCommand(getRegExpr());
	}
	
}
