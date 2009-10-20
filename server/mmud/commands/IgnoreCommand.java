/*-------------------------------------------------------------------------
svninfo: $Id: SayCommand.java 994 2005-10-23 10:19:20Z maartenl $
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
import mmud.MudException;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;

/**
 * Ignore everything someone says. Syntax: <TT>ignore &lt;name&gt;</TT>
 */
public class IgnoreCommand extends NormalCommand
{

	public IgnoreCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(User aUser) throws MudException
	{
		Logger.getLogger("mmud").finer("");
		String command = getCommand();
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length <= 2)
		{
			return false;
		}
		Person toChar = Persons.retrievePerson(myParsed[2]);
		if ((toChar == null) || (!(toChar instanceof User)))
		{
			aUser.writeMessage("Cannot find that person.<BR>\r\n");
			return true;
		}
		((User) toChar).addName(aUser);
		Persons.sendMessageExcl(aUser, toChar,
				"%SNAME start%VERB2 to fully ignore %TNAME.<BR>\r\n");
		aUser.writeMessage(aUser, toChar,
				"%SNAME start%VERB2 to fully ignore %TNAME.<BR>\r\n");
		toChar.writeMessage(aUser, toChar,
				"%SNAME start%VERB2 to fully ignore %TNAME.<BR>\r\n");
		return true;
	}

	public Command createCommand()
	{
		return new IgnoreCommand(getRegExpr());
	}

}
