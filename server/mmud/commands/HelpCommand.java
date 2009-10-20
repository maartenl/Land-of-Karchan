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

import mmud.MudException;
import mmud.characters.User;
import mmud.database.Database;

/**
 * Show help regarding possible commands: "help".
 */
public class HelpCommand extends NormalCommand
{

	private String theHelp;
	private User theUser;

	public HelpCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(User aUser) throws MudException
	{
		Logger.getLogger("mmud").finer("");
		theUser = aUser;
		String[] myParsed = getParsedCommand();
		if (myParsed.length == 1)
		{
			theHelp = Database.getHelp(null);
			return true;
		} else if (myParsed.length == 2)
		{
			theHelp = Database.getHelp(myParsed[1]);
		} else
		{
			theHelp = Database.getHelp("sorry");
		}
		return true;
	}

	@Override
	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		try
		{
			String aString = theHelp;
			aString += theUser.printForm();
			return aString;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public Command createCommand()
	{
		return new HelpCommand(getRegExpr());
	}

}
