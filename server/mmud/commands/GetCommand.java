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
 * Retrieve item from floor: "get bucket".
 */
public class GetCommand extends NormalCommand
{
	private String name;
	private String adject1;
	private String adject2;
	private String adject3;
	private int amount;

	/**
	 * This method will make a <I>best effort</I> regarding picking up of
	 * the items requested. This means that, if you request 5 items, and
	 * there are 5 or more items in the room, this method will attempt to
	 * aquire 5 items. It is possible that not all items are available, in which 
	 * case you
	 * could conceivably only receive 3 items or instance.
	 * @throws ItemException in case the item requested could not be located
	 * or is not allowed to be picked up.
	 * @throws ParseException in case the user entered an illegal amount of
	 * items. Illegal being defined as smaller than 1.
	 */
	public boolean run(User aUser)
		throws ItemException, ParseException
	{
		String command = getCommand();
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return this
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length > 1)
		{
			// get [amount] [adject1..3] name
			amount = 1;
			try
			{
				amount = Integer.parseInt(myParsed[1]);
				if (amount < 1)
				{
					throw new ParseException();
				}
				switch (myParsed.length)
				{
					case 3:
					adject1 =  null;
					adject2 =  null;
					adject3 =  null;
					name = myParsed[2];
					break;
					case 4:
					adject1 = myParsed[2];
					adject2 =  null;
					adject3 =  null;
					name = myParsed[3];
					break;
					case 5:
					adject1 = myParsed[2];
					adject2 = myParsed[3];
					adject3 =  null;
					name = myParsed[4];
					break;
					case 6:
					adject1 = myParsed[2];
					adject2 = myParsed[3];
					adject3 = myParsed[4];
					name = myParsed[5];
					break;
				}
			}
			catch (NumberFormatException e)
			{
				switch (myParsed.length)
				{
					case 2:
					adject1 =  null;
					adject2 =  null;
					adject3 =  null;
					name = myParsed[1];
					break;
					case 3:
					adject1 = myParsed[1];
					adject2 =  null;
					adject3 =  null;
					name = myParsed[2];
					break;
					case 4:
					adject1 = myParsed[1];
					adject2 = myParsed[2];
					adject3 =  null;
					name = myParsed[3];
					break;
					case 5:
					adject1 = myParsed[1];
					adject2 = myParsed[2];
					adject3 = myParsed[3];
					name = myParsed[4];
					break;
				}
			}
			Vector myItems = null;
			myItems = aUser.getRoom().getItems(adject1, adject2, adject3, name);
			if (myItems.size() < amount)
			{
				if (amount == 1)
				{
					aUser.writeMessage("You cannot find that item in the room.<BR>\r\n");
					return true;
				}
				else
				{
					aUser.writeMessage("You cannot find that many items in the room.<BR>\r\n");
					return true;
				}
			}
			int j = 0;
			for (int i = 0; (i < myItems.size() && j != amount); i++)
			{
				// here needs to be a check for validity of the item
				Item myItem = (Item) myItems.elementAt(i);
				ItemsDb.pickupItem(myItem, aUser);
				aUser.sendMessage(aUser.getName() + " gets " + myItem.getDescription() + ".<BR>\r\n");
				aUser.writeMessage("You get " + myItem.getDescription() + ".<BR>\r\n");
				j++;
			}
			return true;
		}
		return false;
	}

}
