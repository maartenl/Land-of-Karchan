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
 * Provides an emotional response to a person. Acceptable format is:
 * <TT>[emotion] to [person]</TT><P>
 * For example: <UL><LI>lick Karn<LI>nudge Westril</UL>
 */ 
public class EmotionToCommand extends NormalCommand
{

	public boolean run(User aUser)
	{
		String command = getCommand();
		Logger.getLogger("mmud").finer("");
		String[] myParsed = Constants.parseCommand(command);
		String[] plural = {myParsed[0].toLowerCase(), Constants.returnEmotionTo(myParsed[0])};
		if (plural == null)
		{
			return false;
		}
		switch (myParsed.length)
		{
			case 2:
			{
				// cuddle Karn
				Person toChar = Persons.retrievePerson(myParsed[1]);
				if (toChar == null)
				{
					aUser.writeMessage("Cannot find that person.<BR>\r\n");
				}
				else
				{
					aUser.writeMessage("You " + plural[0] + " " + toChar.getName() + ".<BR>\r\n");
					toChar.writeMessage(aUser.getName() + " " + plural[1] + " you.<BR>\r\n");
					aUser.sendMessage(toChar, aUser.getName() + " " + plural[1] + " " + toChar.getName() + ".<BR>\r\n");
				}
				break;
			}
			case 3:
			{
				// cuddle Karn evilly
				Person toChar = Persons.retrievePerson(myParsed[1]);
				if (toChar == null)
				{
					aUser.writeMessage("Cannot find that person.<BR>\r\n");
				}
				else
				{
					if (Constants.existsAdverb(myParsed[2]))
					{
						aUser.writeMessage("You " + plural[0] + " " + toChar.getName() + " " + myParsed[2].toLowerCase() + ".<BR>\r\n");
						toChar.writeMessage(aUser.getName() + " " + plural[1] + " you " + myParsed[2].toLowerCase() + ">\r\n");
						aUser.sendMessage(toChar, aUser.getName() + " " + plural[1] + " " + toChar.getName() + " " + myParsed[2].toLowerCase() + ".<BR>\r\n");
					}
					else
					{
						aUser.writeMessage("Unknown adverb found.<BR>\r\n");
					}
				}
				break;
			}
			default :
			{
				return false;
			}
		}
		return true;
	}

}