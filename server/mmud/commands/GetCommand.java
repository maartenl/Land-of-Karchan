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
 * Retrieve item from floor: "get bucket".
 */
public class GetCommand extends NormalCommand
{
	private String name;
	private String adject1;
	private String adject2;
	private String adject3;
	private int amount;

	public boolean run(User aUser)
		throws ItemException
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
			Item myItem = aUser.getRoom().getItem(adject1, adject2, adject3, name);
			int result = aUser.pickupItems(amount, adject1, adject2, adject3, name);
			if (result == 0)
			{
				aUser.writeMessage("You cannot find that item in the room.<BR>\r\n");
				return true;
			}
			if (result == 1)
			{
				aUser.sendMessage(aUser.getName() + " gets " + myItem.getDescription() + ".<BR>\r\n");
				aUser.writeMessage("You get " + myItem.getDescription() + ".<BR>\r\n");
			}
			else
			{
				aUser.sendMessage(aUser.getName() + " gets " + result + " " + myItem.getDescription() + ".<BR>\r\n");
				aUser.writeMessage("You get " + result + " " + myItem.getDescription() + ".<BR>\r\n");
			}

			return true;
		}
		return false;
	}

}
