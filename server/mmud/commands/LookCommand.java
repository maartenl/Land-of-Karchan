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
import java.util.Vector;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Look at stuff: "look at well". There are three different possibilities:
 * <ul><li>look at something in your inventory.
 * <li>look at something in the room that you occupy
 * <li>look at a person in the same room as you
 * </ul>
 */
public class LookCommand extends NormalCommand
{

	String theResult = null;

	public LookCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	private boolean LookItem(User aUser, Vector aItems)
	throws ItemException
	{
		Item myItem = (Item) aItems.elementAt(0);
		if (myItem == null)
		{
			throw new ItemDoesNotExistException("item not found... BUG!");
		}
		theResult = myItem.getLongDescription();
		theResult += aUser.printForm();
		aUser.sendMessage(aUser.getName() + " looks at " + myItem.getDescription() + ".<BR>\r\n");
		aUser.writeMessage("You look at " + myItem.getDescription() + ".<BR>\r\n");
		return true;
	}

	public boolean run(User aUser)
	throws ItemException, MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}
		// initialise string, important otherwise previous instances will return this
		theResult = null;
		String[] myParsed = getParsedCommand();
		if (myParsed.length > 2)
		{
			if (myParsed[1].equalsIgnoreCase("at"))
			{
				Vector stuff = Constants.parseItemDescription(myParsed, 2, myParsed.length - 2);
				int amount = 1;
				String adject1 = (String) stuff.elementAt(1);
				String adject2 = (String) stuff.elementAt(2);
				String adject3 = (String) stuff.elementAt(3);
				String name = (String) stuff.elementAt(4);

				Vector myItems = aUser.getItems(adject1, adject2, adject3, name);
				if (myItems.size() != 0)
				{
					return LookItem(aUser, myItems);
				}
				myItems = aUser.getRoom().getItems(adject1, adject2, adject3, name);
				if (myItems.size() != 0)
				{
					return LookItem(aUser, myItems);	
				}
				Person toChar = Persons.retrievePerson(myParsed[2]);
				if ( (toChar == null) || (!toChar.getRoom().equals(aUser.getRoom())) )
				{
					aUser.writeMessage("You cannot see that.<BR>\r\n");
					return true;
				}
				aUser.writeMessage("You look at the " + toChar.getLongDescription() + ".<BR>\r\n");
				toChar.writeMessage(aUser.getName() + " looks at you.<BR>\r\n");
				aUser.sendMessage(toChar, aUser.getName() + " looks at " + toChar.getName() + ".<BR>\r\n");
				return true;
			}
			return true;
		}
		return false;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theResult;
	}

}
