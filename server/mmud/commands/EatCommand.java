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
 * Eat an item from inventory. "eat apple pie".
 * Should improve your eat stats.
 */
public class EatCommand extends NormalCommand
{

	String theResult = null;

	public EatCommand(String aRegExpr)
	{
		super(aRegExpr);
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
		if (myParsed.length > 1)
		{
			Vector stuff = Constants.parseItemDescription(myParsed, 1, myParsed.length - 1);
			int amount = 1;
			String adject1 = (String) stuff.elementAt(1);
			String adject2 = (String) stuff.elementAt(2);
			String adject3 = (String) stuff.elementAt(3);
			String name = (String) stuff.elementAt(4);

			Vector myItems = aUser.getItems(adject1, adject2, adject3, name);
			if (myItems.size() == 0)
			{
				aUser.writeMessage("You cannot find that item in your inventory.<BR>\r\n");
				return true;
			}
			Item myItem = (Item) myItems.elementAt(0);
			if (!myItem.isAttribute("eatable"))
			{
				aUser.writeMessage("You cannot eat that.<BR>\r\n");
				return true;
			}
			theResult = myItem.getItemDef().getAttribute("eatable").getValue();
			if (theResult == null)
			{
				aUser.writeMessage("You cannot eat that.<BR>\r\n");
				return true;
			}
			theResult += aUser.printForm();
			Database.writeLog(aUser.getName(), "eaten " + myItem);
			ItemsDb.deleteItem(myItem);
			Persons.sendMessage(aUser, "%SNAME eat%VERB2 " + myItem.getDescription() + ".<BR>\r\n");

			// increase eat stats

			return true;
		}
		return false;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theResult;
	}

	public Command createCommand(String aRegExpr)
	{
		return new EatCommand(aRegExpr);
	}
	
}
