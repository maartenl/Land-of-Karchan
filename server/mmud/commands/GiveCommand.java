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

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Syntax: give &lt;item&gt; to &lt;character&gt;
 */
public class GiveCommand extends NormalCommand
{

	public boolean run(User aUser, String command)
		throws ItemException
	{
		if (Constants.logging)
		{
			System.err.println("GiveCommand.run " + aUser + "," + command);
		}
		// initialise string, important otherwise previous instances will return this
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length >= 4)
		{
			Item myItem = null;// = aUser.getItem(myParsed, 1, myParsed.length - 2);
			if (myItem == null)
			{
				aUser.writeMessage("You cannot find that item in your inventory.<BR>\r\n");
				return true;
			}
			if (myItem.isAttribute("notgiveable"))
			{
				aUser.writeMessage("You cannot give that item.<BR>\r\n");
				return true;
			}
			Person toChar = Persons.retrievePerson(myParsed[myParsed.length - 1]);
			if ( (toChar == null) || (toChar.getRoom() != aUser.getRoom()) )
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
				return true;
			}

			aUser.writeMessage("You give " + myItem.getDescription() + " to " + toChar.getName() + ".<BR>\r\n");
			toChar.writeMessage(aUser.getName() + " gives " + myItem.getDescription() + " to you.<BR>\r\n");
			aUser.sendMessage(toChar, aUser.getName() + " gives " + myItem.getDescription() + " to " + toChar.getName() + ".<BR>\r\n");

			// the reason for not creating a new object, is because 
			// the existing object might have special characteristics
			// and a full compare is just not doable.
//			aUser.removeFromInventory(myItem);
//			toChar.addToInventory(myItem);
			return true;
		}
		return false;
	}

}
