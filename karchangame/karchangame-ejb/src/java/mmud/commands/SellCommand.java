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
import mmud.exceptions.MmudException;
import mmud.exceptions.ParseException;
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.characters.ShopKeeper;
import mmud.database.entities.Player;
import mmud.database.Database;
import mmud.database.ItemsDb;
import mmud.items.Item;
import mmud.items.ItemException;

/**
 * Selling an item to a bot. Syntax : sell &lt;item&gt; to &lt;character&gt;
 * 
 * @see BuyCommand
 */
public class SellCommand extends NormalCommand
{

	public SellCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * Tries out the sell command. There are a couple of requirements that need
	 * to be met, before a successful sale takes place.
	 * <ol>
	 * <li>command struct. should be "<I>sell &lt;item&gt; to
	 * &lt;character&gt;</I>", for example: "<I>sell gold ring to Karcas</I>".
	 * <li>shopkeeper buying the item should
	 * <ol>
	 * <li>exist,
	 * <li>be in the same room and
	 * <li>
	 * have a god==4 to indicate a "shopkeeper" and
	 * <li>has enough money
	 * </ol>
	 * <li>the customer should have the item
	 * <li>the item itself should <I>NOT</I> have a attribute called
	 * "notsellable".
	 * </ol>
	 * A best effort is tried, this means the following sequence of events:
	 * <ol>
	 * <li>the item is transferred into the inventory of the shopkeeper
	 * <li>money is transferred into the inventory of the customer
	 * <li>continue with next item
	 *</ol>
	 * 
	 * @param aPlayer
	 *            the character doing the selling.
	 * @throws ItemException
	 *             in case the appropriate items could not be properly
	 *             processed.
	 * @throws ParseException
	 *             if the item description or the number of items requested is
	 *             illegal.
	 */
	@Override
	public boolean run(Player aPlayer) throws ItemException, ParseException,
			MmudException
	{
		Logger.getLogger("mmud").finer("");
		String[] myParsed = getParsedCommand();
		// parse command string
		if (myParsed.length >= 4
				&& myParsed[myParsed.length - 2].equalsIgnoreCase("to"))
		{
			// determine if appropriate shopkeeper is found.
			Person toChar = Persons
					.retrievePerson(myParsed[myParsed.length - 1]);
			if ((toChar == null) || (!toChar.getRoom().equals(aPlayer.getRoom())))
			{
				aPlayer.writeMessage("Cannot find that person.<BR>\r\n");
				return true;
			}
			if (!(toChar instanceof ShopKeeper))
			{
				aPlayer.writeMessage("That person is not a shopkeeper.<BR>\r\n");
				return true;
			}
			// check for item in posession of customer
			Vector stuff = Constants.parseItemDescription(myParsed, 1,
					myParsed.length - 3);
			int amount = ((Integer) stuff.elementAt(0)).intValue();
			String adject1 = (String) stuff.elementAt(1);
			String adject2 = (String) stuff.elementAt(2);
			String adject3 = (String) stuff.elementAt(3);
			String name = (String) stuff.elementAt(4);

			Vector myItems = aPlayer.getItems(adject1, adject2, adject3, name);
			if (myItems.size() < amount)
			{
				if (amount == 1)
				{
					aPlayer.writeMessage("You do not have that item.<BR>\r\n");
					return true;
				} else
				{
					aPlayer
							.writeMessage("You do not have that many items.<BR>\r\n");
					return true;
				}
			}

			int sumvalue = 0;
			for (int i = 0; i < amount; i++)
			{
				Item myItem = (Item) myItems.elementAt(i);
				sumvalue += myItem.getMoney();
			}
			Logger.getLogger("mmud").finer(
					aPlayer.getName() + " has items worth " + sumvalue
							+ " copper");
			Logger.getLogger("mmud").finer(
					toChar.getName() + " has " + toChar.getMoney()
							+ " copper coins");
			if (toChar.getMoney() < sumvalue)
			{
				aPlayer
						.writeMessage(toChar.getName()
								+ " mutters something about not having enough money.<BR>\r\n");
				return true;
			}
			int j = 0;
			for (int i = 0; ((i < myItems.size()) && (j != amount)); i++)
			{
				// here needs to be a check for validity of the item
				boolean success = true;
				Item myItem = (Item) myItems.elementAt(i);
				if (myItem.getMoney() == 0)
				{
					String message = "That item is not worth anything.";
					Persons.sendMessageExcl(toChar, aPlayer,
							"%SNAME say%VERB2 [to %TNAME] : " + message
									+ "<BR>\r\n");
					aPlayer.writeMessage(toChar, aPlayer,
							"<B>%SNAME say%VERB2 [to %TNAME]</B> : " + message
									+ "<BR>\r\n");
					success = false;
				}
				if (!myItem.isSellable())
				{
					aPlayer.writeMessage("You cannot sell that item.<BR>\r\n");
					success = false;
				}
				if (success)
				{
					int totalitemvalue = myItem.getMoney();
					// transfer item to shopkeeper
					ItemsDb.transferItem(myItem, toChar);
					Database.writeLog(aPlayer.getName(), "sold " + myItem
							+ " to " + toChar + " in room "
							+ toChar.getRoom().getId());
					// transfer money to user
					toChar.transferMoneyTo(totalitemvalue, aPlayer);
					Database.writeLog(aPlayer.getName(), "received "
							+ totalitemvalue + " copper from " + toChar);
					Persons.sendMessage(aPlayer, toChar, "%SNAME sell%VERB2 "
							+ myItem.getDescription() + " to %TNAME.<BR>\r\n");
					Persons.sendMessage(aPlayer, toChar, "%SNAME receive%VERB2 "
							+ myItem.getDescriptionOfMoney()
							+ " from %TNAME.<BR>\r\n");
					j++;
				}
			}
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new SellCommand(getRegExpr());
	}

}
