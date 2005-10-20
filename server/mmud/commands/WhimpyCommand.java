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

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Sets the whimpy or displays the whimpy of the user: "whimpy".
 */
public class WhimpyCommand extends NormalCommand
{

	public WhimpyCommand(String aRegExpr)
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
		String command = getCommand();
		command = command.trim();
		if (command.equalsIgnoreCase("whimpy help"))
		{
			aUser.writeMessage("Syntax: <B>whimpy &lt;string&gt;</B><UL><LI>feeling well" +
				"<LI>feeling fine<LI>feeling quite nice<LI>slightly hurt" +
				"<LI>hurt<LI>quite hurt<LI>extremely hurt<LI>terribly hurt" +
				"<LI>feeling bad<LI>feeling very bad<LI>at death's door</UL>\r\n");
			return true;

		}
		if (command.equalsIgnoreCase("whimpy"))
		{
			aUser.writeMessage("Current whimpy setting: <B>" +
				Constants.whimpy[aUser.getWhimpy()/10] + "</B>.<BR>\r\n");
			return true;

		}
		String myString = command.substring(7);
		for (int i=0;i<Constants.whimpy.length;i++)
		{
			if (myString.equalsIgnoreCase(Constants.whimpy[i]))
			{
				aUser.writeMessage("Whimpy level set to : " + Constants.whimpy[i] + ".<BR>\r\n");
				aUser.setWhimpy(i*10);
				return true;
			}
		}
		aUser.writeMessage("Cannot find that whimpy level.<BR>\r\n");
		return true;
	}

	public Command createCommand()
	{
		return new WhimpyCommand(getRegExpr());
	}
	
}
