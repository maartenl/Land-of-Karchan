/*-------------------------------------------------------------------------
svninfo: $Id: ShowCommand.java 1005 2005-10-30 13:21:36Z maartenl $
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
import mmud.items.Item;
import mmud.items.ItemException;

/**
 * Showing an item to a bot. Syntax : show &lt;item&gt; to &lt;character&gt;.
 * The one reason why this is usefull is when you wish to know the value of a
 * certain item.
 * 
 * @see BuyCommand
 * @see SellCommand
 */
public class ShowCommand extends NormalCommand
{

	public ShowCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * Provides the item to a shopkeeper for appraisal. There are a couple of
	 * requirements that need to be met, before a successful appraisal takes
	 * place.
	 * <ol>
	 * <li>command struct. should be "<I>show &lt;item&gt; to
	 * &lt;character&gt;</I>", for example: "<I>show gold ring to Karcas</I>".
	 * <li>shopkeeper buying the item should
	 * <ol>
	 * <li>exist,
	 * <li>be in the same room and
	 * <li>
	 * have a god==4 to indicate "shopkeeper"
	 * </ol>
	 * <li>the customer should have the item
	 * </ol>
	 * The following sequence of events:
	 * <ol>
	 * <li>the shopkeeper mentions how much he/she would give for the item.
	 * <li>continue with next item
	 *</ol>
	 * 
	 * @param aPlayer
	 *            the character doing the showing.
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

			for (int i = 0; i < amount; i++)
			{
				Item myItem = (Item) myItems.elementAt(i);
				String message = null;
				if (myItem.getMoney() == 0)
				{
					message = "A" + myItem.getDescription().substring(1)
							+ " is not worth anything to me.";
				} else
				{
					message = "I'd be willing to pay "
							+ myItem.getDescriptionOfMoney() + " for "
							+ myItem.getDescription() + ".";
				}
				Persons.sendMessageExcl(toChar, aPlayer,
						"%SNAME say%VERB2 [to %TNAME] : " + message
								+ "<BR>\r\n");
				aPlayer.writeMessage(toChar, aPlayer,
						"<B>%SNAME say%VERB2 [to %TNAME]</B> : " + message
								+ "<BR>\r\n");
			}
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new ShowCommand(getRegExpr());
	}

}
