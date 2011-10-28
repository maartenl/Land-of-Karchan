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
import mmud.characters.CommunicationListener;
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
import mmud.characters.CommunicationListener.CommType;

/**
 * Whisper something : "whisper Anybody here?"
 */
public class WhisperCommand extends CommunicationCommand
{

	public WhisperCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	/*
	 * * Special case again, because this whisper has a very different message
	 * when whispering to people. Other people will not understand you.
	 */
	public boolean run(Player aPlayer) throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		String command = getCommand();
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length <= 1)
		{
			return false;
		}
		if (myParsed.length > 3 && myParsed[1].equalsIgnoreCase("to"))
		{
			// whisper to someone
			Person toChar = Persons.retrievePerson(myParsed[2]);
			if ((toChar == null) || (toChar.getRoom() != aPlayer.getRoom()))
			{
				aPlayer.writeMessage("Cannot find that person.<BR>\r\n");
			} else
			{
				if (aPlayer.isIgnored(toChar))
				{
					aPlayer.writeMessage(toChar.getName()
							+ " is ignoring you fully.<BR>\r\n");
					return true;
				}
				setMessage(command.substring(
						command.indexOf(myParsed[3], getCommType().toString()
								.length()
								+ 1 + 2 + 1 + myParsed[2].length())).trim());
				aPlayer.writeMessage("<B>You whisper [to " + toChar.getName()
						+ "]</B> : " + getMessage() + "<BR>\r\n");
				toChar.writeMessage("<B>" + aPlayer.getName()
						+ " whispers [to you]</B> : " + getMessage()
						+ "<BR>\r\n");
				Persons
						.sendMessageExcl(
								aPlayer,
								toChar,
								"%SNAME %SISARE whispering something to %TNAME, but you cannot hear what.<BR>\r\n");
				if (toChar instanceof CommunicationListener)
				{
					((CommunicationListener) toChar).commEvent(aPlayer,
							CommunicationListener.WHISPER, getMessage());
				}
			}
			return true;
		}
		setMessage(command.substring(getCommType().toString().length() + 1)
				.trim());
		aPlayer.writeMessage("<B>You whisper</B> : " + getMessage() + "<BR>\r\n");
		Persons.sendMessageExcl(aPlayer, "%SNAME whisper%VERB2 : " + getMessage()
				+ "<BR>\r\n");
		return true;
	}

	@Override
	public Command createCommand()
	{
		return new WhisperCommand(getRegExpr());
	}

	@Override
	public CommType getCommType()
	{
		return CommType.WHISPER;
	}

}
