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
 * Curtsey to someone: "curtsey to Karn".
 */
public class CurtseyCommand extends NormalCommand
{

	public CurtseyCommand(String aRegExpr)
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
		if (myParsed.length == 3 && myParsed[1].equalsIgnoreCase("to"))
		{
			if (myParsed[2].equalsIgnoreCase(aUser.getName()))
			{
				aUser.writeMessage("Drop a curtsey to myself? What are you trying to do?<BR>\r\n");
				return true;
			}
			Person toChar = Persons.retrievePerson(myParsed[2]);
			if ( (toChar == null) || (toChar.getRoom() != aUser.getRoom()) )
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
			}
			else
			{
				Persons.sendMessage(aUser, toChar, "%SNAME drop%VERB2 a curtsey to %TNAME.<BR>\r\n");
			}
		}
		else
		{
			Persons.sendMessage(aUser, "%SNAME drop%VERB2 a curtsey.<BR>\r\n");
		}
		return true;
	}

	public Command createCommand()
	{
		return new CurtseyCommand(getRegExpr());
	}
	
}
