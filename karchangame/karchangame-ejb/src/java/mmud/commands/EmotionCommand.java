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

import mmud.Constants;
import mmud.exceptions.MmudException;
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;

/**
 * Provides an emotional response. Acceptable format is:
 * <TT>[emotion] &lt;[adverb]&gt; &lt;to [person]&gt;</TT><P>
 * For example: <UL><LI>agree<LI>smile to Karn
 * <LI>smile evilly<LI>smile evilly to Karn</UL>
 */
public class EmotionCommand extends NormalCommand
{

	public EmotionCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	public boolean run(Player aPlayer)
	throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		String[] myParsed = getParsedCommand();
		String[] plural = {myParsed[0].toLowerCase(), Constants.returnEmotion(myParsed[0])};
		Logger.getLogger("mmud").finer("");
		if (plural == null)
		{
			return false;
		}
		switch (myParsed.length)
		{
			case 1:
			{
				// agree
				aPlayer.writeMessage("You " + plural[0] + ".<BR>\r\n");
				Persons.sendMessageExcl(aPlayer, aPlayer.getName() + " " + plural[1] + ".<BR>\r\n");
				break;
			}
			case 2:
			{
				// agree evilly
				if (Constants.existsAdverb(myParsed[1]))
				{
					aPlayer.writeMessage("You " + plural[0] + " " + myParsed[1].toLowerCase() + ".<BR>\r\n");
					Persons.sendMessageExcl(aPlayer, aPlayer.getName() + " " + plural[1] + " " + myParsed[1].toLowerCase() + ".<BR>\r\n");
				}
				else
				{
					aPlayer.writeMessage("Unknown adverb found.<BR>\r\n");
				}
				break;
			}
			case 3:
			{
				// agree to Karn
				if (myParsed[1].equalsIgnoreCase("to"))
				{
					Person toChar = Persons.retrievePerson(myParsed[2]);
					if ( (toChar == null) || (!toChar.getRoom().equals(aPlayer.getRoom())) )
					{
						aPlayer.writeMessage("Cannot find that person.<BR>\r\n");
					}
					else
					{
						aPlayer.writeMessage("You " + plural[0] + " to " + toChar.getName() + ".<BR>\r\n");
						toChar.writeMessage(aPlayer.getName() + " " + plural[1] + " to you.<BR>\r\n");
						Persons.sendMessageExcl(aPlayer, toChar, aPlayer.getName() + " " + plural[1] + " to " + toChar.getName() + ".<BR>\r\n");
					}
				}
				else
				{
					return false;
				}
				break;
			}
			case 4:
			{
				// agree evilly to Karn
				if (myParsed[2].equalsIgnoreCase("to"))
				{
					Person toChar = Persons.retrievePerson(myParsed[3]);
					if (toChar == null)
					{
						aPlayer.writeMessage("Cannot find that person.<BR>\r\n");
					}
					else
					{
						if (Constants.existsAdverb(myParsed[1]))
						{
							aPlayer.writeMessage("You " + plural[0] + " " + myParsed[1].toLowerCase() + " to " + toChar.getName() + ".<BR>\r\n");
							toChar.writeMessage(aPlayer.getName() + " " + plural[1] + " " + myParsed[1].toLowerCase() + " to you.<BR>\r\n");
							Persons.sendMessageExcl(aPlayer, toChar, aPlayer.getName() + " " + plural[1] + " " + myParsed[1].toLowerCase() + " to " + toChar.getName() + ".<BR>\r\n");
						}
						else
						{
							aPlayer.writeMessage("Unknown adverb found.<BR>\r\n");
						}
					}
				}
				else
				{
					return false;
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

	public Command createCommand()
	{
		return new EmotionCommand(getRegExpr());
	}
	
}
