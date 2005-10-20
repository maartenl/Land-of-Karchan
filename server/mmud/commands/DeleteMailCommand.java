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
 * Delete an existing mail from the mail list of the current user.
 */
public class DeleteMailCommand extends NormalCommand
{

	String theString;

	public DeleteMailCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	public boolean run(User aUser)
	throws MailException, MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}
		String[] myParsed = getParsedCommand();
		if (myParsed.length == 2)
		{
			try
			{
				theString = MailDb.deleteMail(aUser, Integer.parseInt(myParsed[1]));
				theString += aUser.printForm();
			}
			catch (NumberFormatException e)
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDMAILERROR);
				throw new InvalidMailException();
			}
			return true;
		}

		return false;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theString;
	}

	public Command createCommand()
	{
		return new DeleteMailCommand(getRegExpr());
	}
	

}
