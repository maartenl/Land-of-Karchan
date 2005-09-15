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
 * An abstract class for the most normal commands.
 */
public abstract class NormalCommand implements Command
{

	private String theCommand;
	private String[] theParsedCommand;

	/**
	 * the regular expression the command structure must follow
	 */
	private String theRegExpr;

	public String getRegExpr()
	{
		return theRegExpr;
	}
	
	/**
	 * Constructor.
	 * @param aRegExpr a regular expression to which the command should
	 * follow. For example "give [A..Za..z]*1-4 to [A..Za..z]*". %me is a
	 * parameter that can be used when the name of the character playing
	 * is requested.
	 */
	public NormalCommand(String aRegExpr)
	{
		theRegExpr = aRegExpr;
	}

	public boolean run(User aUser)
	throws MudException
	{
		Logger.getLogger("mmud").finer("theRegExpr=" + theRegExpr);
		aUser.setNow();
		String myregexpr = theRegExpr.replaceAll("%s", aUser.getName());
		boolean result = true;
		if (myregexpr.endsWith(".+") && getCommand().indexOf('\n')!=-1)
		{
			String stuff = getCommand().substring(0, getCommand().indexOf('\n'));
			result = stuff.matches(myregexpr);
			Logger.getLogger("mmud").finer("returns case 1 " + result);
		}
		else
		{
			result = (getCommand().matches(myregexpr));
			Logger.getLogger("mmud").finer("returns case 2 " + result);
		}
		return result;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}

	public void setCommand(String aCommand)
	{
		theCommand = aCommand;
		theParsedCommand = Constants.parseCommand(theCommand);
	}

	public String getCommand()
	{
		return theCommand;
	}

	public String[] getParsedCommand()
	{
		return theParsedCommand;
	}

}
