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
 * Ask something : "ask Okay, where's everybody?"
 */
public class AskCommand extends NormalCommand
{

	public boolean run(User aUser)
	{
		String command = getCommand();
		Logger.getLogger("mmud").finer("");
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length <= 1)
		{
			return false;
		}
		if (myParsed.length > 3 && myParsed[1].equalsIgnoreCase("to"))
		{
			Person toChar = Persons.retrievePerson(myParsed[2]);
			if ( (toChar == null) || (toChar.getRoom() != aUser.getRoom()) )
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
			}
			else
			{
				String message = command.substring(command.indexOf(myParsed[3], 3 + 1 + 2 + 1 + myParsed[2].length())).trim();
				aUser.writeMessage("You ask " + toChar.getName() + " : " + message + ".<BR>\r\n");
				toChar.writeMessage(aUser.getName() + " asks you : " + message + ".<BR>\r\n");
				aUser.sendMessage(toChar, aUser.getName() + " asks " + toChar.getName() + " : " + message + ".<BR>\r\n");
			}
		}
		else
		{
			String message = command.substring(3 + 1).trim();
			aUser.writeMessage("You ask : " + message + ".<BR>\r\n");
			aUser.sendMessage(aUser.getName() + " asks : " + message + ".<BR>\r\n");
		}
		return true;
	}

}