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
 * syntax : buy from &lt;character&gt; &lt;item&gt;
 */
public class BuyCommand extends NormalCommand
{

	/**
	 * should be the same as item 38
	 */
	public static final String[] goldcoin = {"valuable",  "gold", "shiny", "coin"};

	/**
	 * should be the same as item 37
	 */
	public static final String[] silvercoin = {"valuable",  "silver", "shiny", "coin"};

	/**
	 * should be the same as item 36
	 */
	public static final String[] coppercoin = {"valuable",  "copper", "shiny", "coin"};

	public boolean run(User aUser, String command)
		throws ItemException
	{
		if (Constants.logging)
		{
			System.err.println("BuyCommand.run " + aUser + "," + command);
		}
		String[] myParsed = Constants.parseCommand(command);
		if (myParsed.length >= 4 && myParsed[myParsed.length-2].equalsIgnoreCase("from"))
		{
			Person toChar = Persons.retrievePerson(myParsed[myParsed.length-1]);
			if ((toChar == null) || (!toChar.isAttribute("shopkeeper")))
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
				return true;
			}
            Item myItem = null;// = toChar.getItem(myParsed, 1, myParsed.length - 2);
            if ((myItem == null) || (myItem.isAttribute("notbuyable")))

            {
                aUser.writeMessage("You cannot buy that.<BR>\r\n");
                return true;
            }
//			try
//			{
//				aUser.moveCoins(toChar, myItem.getGold(), myItem.getSilver(), myItem.getCopper());
//				aUser.addToInventory(myItem);
//				toChar.removeFromInventory(myItem);
				aUser.writeMessage("You buy " + myItem.getDescription() + " from " + toChar.getName() + ".<BR>\r\n");
				toChar.writeMessage(aUser.getName() + " buys " + myItem.getDescription() + " from you.<BR>\r\n");
				aUser.sendMessage(toChar, aUser.getName() + " buys " + myItem.getDescription() + " from " + toChar.getName() + ".<BR>\r\n");
//			}
//			catch (ItemException e)
//			{
 //               aUser.writeMessage("You do not have enough money.<BR>\r\n");
  //              return true;
//			}
			// the reason for not creating a new object, is because
			// the existing object might have special characteristics
			// and a full compare is just not doable.
//			toChar.removeFromInventory(myItem);
//			aUser.addToInventory(myItem);

			aUser.writeMessage("You buy " + myItem.getDescription() + " from " + toChar.getName() + ".<BR>\r\n");
			toChar.writeMessage(aUser.getName() + " buys " + myItem.getDescription() + " from you.<BR>\r\n");
			aUser.sendMessage(toChar, aUser.getName() + " buys " + myItem.getDescription() + " from " +  toChar.getName() + ".<BR>\r\n");
			
			return true;
		}
		return false;
	}

}
