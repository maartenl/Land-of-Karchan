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
 * Selling an item to a bot. Syntax : sell &lt;item&gt; to &lt;character&gt;
 */
public class SellCommand extends NormalCommand
{

	public SellCommand(String aRegExpr)
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
	 * Tries out the sell command. There are a couple of requirements that
	 * need to be met, before a successful sale takes place.
	 * <ol><li>command struct. should be "<I>sell 
	 * &lt;item&gt; to &lt;character&gt;</I>", for example: "<I>sell gold ring
	 * to Karcas</I>".
	 * <li>shopkeeper buying the item should
	 * <ol><li> exist, <li>be in the same room and<li>
	 * have a occupation-attribute set to "shopkeeper"
	 * and<li>has enough money</ol>
	 * <li>the customer should have the item
	 * </ol>
	 * A best effort is tried, this means the following sequence of events:
	 * <ol><li>the item is transferred into the inventory of the shopkeeper
	 * <li>gold coins are transferred into the inventory of the customer
	 * <li>silver coins are transferred into the inventory of the customer
	 * <li>copper coins are transferred into the inventory of the customer
	 * <li>continue with next item
	 *</ol>
	 * @param aUser the character doing the selling.
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
		if (myParsed.length >= 4 && myParsed[myParsed.length-2].equalsIgnoreCase("to"))
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
			// check for item in posession of customer
			Vector stuff = Constants.parseItemDescription(myParsed, 1, myParsed.length - 3);
			int amount = ((Integer) stuff.elementAt(0)).intValue();
			String adject1 = (String) stuff.elementAt(1);
			String adject2 = (String) stuff.elementAt(2);
			String adject3 = (String) stuff.elementAt(3);
			String name = (String) stuff.elementAt(4);
			
			Vector myItems = aUser.getItems(adject1, adject2, adject3, name);
			if (myItems.size() < amount)
			{
				if (amount == 1)
				{
					aUser.writeMessage("You do not have that item.<BR>\r\n");
					return true;
				}
				else
				{
					aUser.writeMessage("You do not have that many items.<BR>\r\n");
					return true;
				}
			}
			Vector myGold = toChar.getItems("valuable",  "gold", "shiny", "coin");
			Vector mySilver = toChar.getItems("valuable",  "silver", "shiny", "coin");
			Vector myCopper = toChar.getItems("valuable",  "copper", "shiny", "coin");
			
			int sumvalue = 0;
			for (int i=0; i<amount; i++)
			{
				Item myItem = (Item) myItems.elementAt(0);
				sumvalue += myItem.getValue();
			}
			Logger.getLogger("mmud").finer("total value=" + sumvalue);
			if (myGold.size() * 100 + mySilver.size() * 10 + myCopper.size()
				< sumvalue )
			{
				aUser.writeMessage(toChar.getName() + " mutters something about not having enough money.<BR>\r\n");
				return true;
			}
			int j = 0;
			int myGoldPos = 0;
			int mySilverPos = 0;
			int myCopperPos = 0;
			for (int i = 0; ((i < myItems.size()) && (j != amount)); i++)
			{
				// here needs to be a check for validity of the item
				boolean success = true;
				Item myItem = (Item) myItems.elementAt(i);
				if (myItem.isAttribute("notsellable"))
				{
					aUser.writeMessage("You cannot sell that item.<BR>\r\n");
					success = false;
				}
				if (success)
				{
					// transfer item to user
					ItemsDb.transferItem(myItem, toChar);
					int totalitemvalue = myItem.getValue();
					while (totalitemvalue != 0)
					{
						if ((totalitemvalue >= 100) && (myGoldPos != myGold.size()))
						{
							// items value is at least 1 gold coin and person has 
							// gold coins
							Item myGoldItem = (Item) myGold.elementAt(myGoldPos++);
							ItemsDb.transferItem(myGoldItem, aUser);
							totalitemvalue -= 100;
						}
						else
						if ((totalitemvalue >= 10) && (mySilverPos != mySilver.size()))
						{
							// items value is at least 1 silver coin and person has 
							// silver coins
							Item mySilverItem = (Item) mySilver.elementAt(mySilverPos++);
							ItemsDb.transferItem(mySilverItem, aUser);
							totalitemvalue -= 10;
						}
						else
						if ((totalitemvalue >= 1) && (myCopperPos != myCopper.size()))
						{
							// items value is at least 1 copper coin and person has
							// copper coins
							Item myCopperItem = (Item) myCopper.elementAt(myCopperPos++);
							ItemsDb.transferItem(myCopperItem, aUser);
							totalitemvalue -= 1;
						}
						else
						// when we get here, it means that we need small
						// change, more silver and/or copper coins
						{
							// no more silver coins?
							// 38 == gold, 37 == silver, 36 == copper
							if (mySilverPos == mySilver.size())
							{
								// no more silver coins
								Item myGoldItem = (Item) myGold.elementAt(myGoldPos++);
								ItemsDb.deleteItem(myGoldItem);
								for (int l=0;l<10;l++)
								{
									Item newItem = ItemsDb.addItem(ItemDefs.getItemDef(37));
									ItemsDb.addItemToChar(newItem, toChar);
									mySilver.add(newItem);
								}
							}
							else
							{
								// no more copper coins
								Item mySilverItem = (Item) mySilver.elementAt(mySilverPos++);
								ItemsDb.deleteItem(mySilverItem);
								for (int l=0;l<10;l++)
								{
									Item newItem = ItemsDb.addItem(ItemDefs.getItemDef(36));
									ItemsDb.addItemToChar(newItem, toChar);
									myCopper.add(newItem);
								}
							}
						}
					} // end while totalitemvalue>0
					if (success)
					{
						aUser.sendMessage(aUser.getName() + " sells " + myItem.getDescription() + " to " + toChar.getName() + ".<BR>\r\n");
						aUser.writeMessage("You sell " + myItem.getDescription() + " to " + toChar.getName() + ".<BR>\r\n");
						toChar.writeMessage(aUser.getName() + " sells " + myItem.getDescription() + " to you.<BR>\r\n");
						j++;
					}
				}
			}
			return true;
		}
		return false;
	}

}
