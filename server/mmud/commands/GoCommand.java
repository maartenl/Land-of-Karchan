/*-------------------------------------------------------------------------
cvsinfo: $Header$
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

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Go in a specific direction: "go south".
 */
public class GoCommand extends NormalCommand
{

	public GoCommand(String aRegExpr)
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
		if (myParsed.length == 2)
		{
			Command newCommand = new BogusCommand(".+");
			if (myParsed[1].equalsIgnoreCase("south"))
			{
				newCommand = new SouthCommand(".+");
			}
			if (myParsed[1].equalsIgnoreCase("north"))
			{
				newCommand = new NorthCommand(".+");
			}
			if (myParsed[1].equalsIgnoreCase("west"))
			{
				newCommand = new WestCommand(".+");
			}
			if (myParsed[1].equalsIgnoreCase("east"))
			{
				newCommand = new EastCommand(".+");
			}
			if (myParsed[1].equalsIgnoreCase("up"))
			{
				newCommand = new UpCommand(".+");
			}
			if (myParsed[1].equalsIgnoreCase("down"))
			{
				newCommand = new DownCommand(".+");
			}
			newCommand.setCommand(getCommand());
			return newCommand.run(aUser);
		}
		return false;
	}

	public Command createCommand(String aRegExpr)
	{
		return new GoCommand(aRegExpr);
	}
	
}
