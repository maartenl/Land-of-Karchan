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

import java.util.Vector;
import java.util.logging.Logger;

import mmud.Constants;
import mmud.MudException;
import mmud.ParseException;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.database.Database;
import mmud.database.ItemsDb;
import mmud.items.Item;
import mmud.items.ItemException;

/**
 * Buying an item from a bot. Syntax : buy &lt;item&gt; from &lt;character&gt;
 * @see SellCommand
 */
public class BuyCommand extends NormalCommand
{

	public BuyCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

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

	/**
	 * Tries out the buy command. There are a couple of requirements that
	 * need to be met, before a successful sale takes place.
	 * <ol><li>command struct. should be "<I>buy 
	 * &lt;item&gt; from &lt;character&gt;</I>", for example: "<I>buy
	 * gold ring from Karcas</I>".
	 * <li>shopkeeper selling the item should
	 * <ol><li> exist, <li>be in the same room and<li>
	 * have a occupation-attribute set to "shopkeeper"
	 * and<li>has the appropriate item for sale</ol>
	 * <li>the customer should be able to afford the item
	 * </ol>
	 * A best effort is tried, this means the following sequence of events:
	 * <ol><li>the item is transferred into the inventory of the customer
	 * <li>gold coins are transferred into the inventory of the shopkeeper
	 * <li>silver coins are transferred into the inventory of the shopkeeper
	 * <li>copper coins are transferred into the inventory of the shopkeeper
	 * <li>when gold/silver/copper coins are not available in the
	 * requested quantity, the game will automatically provide
	 * appropriate change.
	 * <li>continue with next item
	 *</ol>
	 * @param aUser the character doing the buying.
	 * @throws ItemException in case the appropriate items could not be
	 * properly processed.
	 * @throws ParseException if the item description or the number of
	 * items requested is illegal.
	 */
	public boolean run(User aUser)
	throws ItemException, ParseException, MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}
		String[] myParsed = getParsedCommand();
		// parse command string
		if (myParsed.length >= 4 && myParsed[myParsed.length-2].equalsIgnoreCase("from"))
		{
			// determine if appropriate shopkeeper is found.
			Person toChar = Persons.retrievePerson(myParsed[myParsed.length-1]);
			if ((toChar == null) || (!toChar.getRoom().equals(aUser.getRoom())))
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
				return true;
			}
			if ( (!toChar.isAttribute("occupation")) || 
				(!"shopkeeper".equals(
				toChar.getAttribute("occupation").getValue())) )
			{
				aUser.writeMessage("That person is not a shopkeeper.<BR>\r\n");
				return true;
			}
			// check for item in posession of shopkeeper
			Vector stuff = Constants.parseItemDescription(myParsed, 1, myParsed.length - 3);
			int amount = ((Integer) stuff.elementAt(0)).intValue();
			String adject1 = (String) stuff.elementAt(1);
			String adject2 = (String) stuff.elementAt(2);
			String adject3 = (String) stuff.elementAt(3);
			String name = (String) stuff.elementAt(4);
			
			Vector myItems = toChar.getItems(adject1, adject2, adject3, name);
			if (myItems.size() < amount)
			{
				if (amount == 1)
				{
					aUser.writeMessage(toChar.getName() + " does not have that item.<BR>\r\n");
					return true;
				}
				else
				{
					aUser.writeMessage(toChar.getName() + " does not have that many items.<BR>\r\n");
					return true;
				}
			}
			int sumvalue = 0;
			for (int i=0; i<amount; i++)
			{
				Item myItem = (Item) myItems.elementAt(i);
				sumvalue += myItem.getMoney();
			}
			if (aUser.getMoney() < sumvalue )
			{
				aUser.writeMessage("You do not have enough money.<BR>\r\n");
				return true;
			}
			int j = 0;
			for (int i = 0; ((i < myItems.size()) && (j != amount)); i++)
			{
				// here needs to be a check for validity of the item
				boolean success = true;
				Item myItem = (Item) myItems.elementAt(i);
				if (myItem.isAttribute("notbuyable"))
				{
					aUser.writeMessage("You cannot buy that item.<BR>\r\n");
					success = false;
				}
				if (success)
				{
					// transfer item to user
					int totalitemvalue = myItem.getMoney();
					if (success)
					{
						aUser.transferMoneyTo(totalitemvalue, toChar);
						Database.writeLog(aUser.getName(), "paid " + totalitemvalue + " copper to " + toChar);
						ItemsDb.transferItem(myItem, aUser);
						Database.writeLog(aUser.getName(), "bought " + myItem + " from " + toChar);
						Persons.sendMessage(aUser, toChar, "%SNAME buy%VERB2 " + myItem.getDescription() + " from %TNAME.<BR>\r\n");
						j++;
					}
				}
			}
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new BuyCommand(getRegExpr());
	}
	
}
