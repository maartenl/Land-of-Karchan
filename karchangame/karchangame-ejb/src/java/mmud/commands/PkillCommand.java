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

import java.util.logging.Logger;

import mmud.exceptions.MmudException;
import mmud.database.entities.Player;
import mmud.database.Database;

/**
 * Turn the player killing option on or off or displays the current setting:
 * "pkill".
 */
public class PkillCommand extends NormalCommand
{

	public PkillCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		String[] myParsed = getParsedCommand();
		if (myParsed.length == 2)
		{
			boolean isOn = myParsed[1].equalsIgnoreCase("on");
			aPlayer.setPkill(isOn);
			Database.setPkill(aPlayer);
			aPlayer.writeMessage("Pkill is now " + (isOn ? "on" : "off")
					+ ".<BR>\r\n");
			return true;
		}
		if (myParsed.length == 1)
		{
			aPlayer.writeMessage("Your pkill is "
					+ (aPlayer.isPkill() ? "on" : "off") + ".<BR>\r\n");
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new PkillCommand(getRegExpr());
	}

}
