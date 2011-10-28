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
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;

/**
 * Curtsey to someone: "curtsey to Karn".
 */
public class CurtseyCommand extends NormalCommand
{

	public CurtseyCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws MmudException
	{
		Logger.getLogger("mmud").finer("");
		String[] myParsed = getParsedCommand();
		if (myParsed.length == 3 && myParsed[1].equalsIgnoreCase("to"))
		{
			if (myParsed[2].equalsIgnoreCase(aPlayer.getName()))
			{
				aPlayer
						.writeMessage("Drop a curtsey to myself? What are you trying to do?<BR>\r\n");
				return true;
			}
			Person toChar = Persons.retrievePerson(myParsed[2]);
			if ((toChar == null) || (toChar.getRoom() != aPlayer.getRoom()))
			{
				aPlayer.writeMessage("Cannot find that person.<BR>\r\n");
			} else
			{
				Persons.sendMessage(aPlayer, toChar,
						"%SNAME drop%VERB2 a curtsey to %TNAME.<BR>\r\n");
			}
		} else
		{
			Persons.sendMessage(aPlayer, "%SNAME drop%VERB2 a curtsey.<BR>\r\n");
		}
		return true;
	}

	public Command createCommand()
	{
		return new CurtseyCommand(getRegExpr());
	}

}
