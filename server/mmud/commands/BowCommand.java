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
 * Bow : "bow".
 */
public class BowCommand extends NormalCommand
{

	public BowCommand(String aRegExpr)
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
		String[] myParsed = getParsedCommand();
		if (myParsed.length > 2 && myParsed[1].equalsIgnoreCase("to"))
		{
			Person toChar = Persons.retrievePerson(myParsed[2]);
			if ((toChar == null) ||
				(!toChar.getRoom().equals(aUser.getRoom())))
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
			}
			else
			{
				if (myParsed.length == 4)
				{
					// bow evilly
					if (Constants.existsAdverb(myParsed[3]))
					{
							Persons.sendMessage(aUser, toChar, "%SNAME bow%VERB2 to %TNAME "
								 + myParsed[3].toLowerCase() + ".<BR>\r\n");
					}
					else
					{
					   aUser.writeMessage("Unknown adverb found.<BR>\r\n");
					}
				}
				else
				{
					Persons.sendMessage(aUser, toChar, "%SNAME bow%VERB2 to %TNAME.<BR>\r\n");
				}
			}
		}
		else
		{
			if (myParsed.length == 2)
			{
				// bow evilly
				if (Constants.existsAdverb(myParsed[1]))
				{
						Persons.sendMessage(aUser, "%SNAME bow%VERB2 "
							 + myParsed[1].toLowerCase() + ".<BR>\r\n");
				}
				else
				{
				   aUser.writeMessage("Unknown adverb found.<BR>\r\n");
				}
			}
			else
			{
				Persons.sendMessage(aUser, "%SNAME bow%VERB2.<BR>\r\n");
			}
		}
		return true;
	}

	public Command createCommand()
	{
		return new BowCommand(getRegExpr());
	}
	
}
