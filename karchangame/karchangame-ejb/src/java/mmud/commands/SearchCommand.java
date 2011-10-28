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
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.database.Database;
import mmud.database.ItemsDb;
import mmud.items.Item;
import mmud.items.ItemException;
import mmud.items.StdItemContainer;

/**
 * Retrieve item from another item in the room: "search shrubbery". Basically,
 * you search <i>inside</i> an item for items. The first item found will be
 * returned into your inventory, if possible.
 */
public class SearchCommand extends NormalCommand
{
	private String name;
	private String adject1;
	private String adject2;
	private String adject3;
	private int amount;

	public SearchCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * This method will make a <I>best effort</I> regarding searching for items
	 * inside items. The first item in the container that we are able to get is
	 * returned.
	 * 
	 * @throws ItemException
	 *             in case the item requested could not be located or is not
	 *             allowed to be retrieved from the container.
	 * @throws ParseException
	 *             in case the user entered an illegal amount of items. Illegal
	 *             being defined as smaller than 1.
	 */
	@Override
	public boolean run(User aUser) throws ItemException, ParseException,
			MudException
	{
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return
		// this
		String[] myParsed = getParsedCommand();
		if (myParsed.length > 1)
		{
			Vector stuff = Constants.parseItemDescription(myParsed, 1,
					myParsed.length - 1);
			amount = 1;
			adject1 = (String) stuff.elementAt(1);
			adject2 = (String) stuff.elementAt(2);
			adject3 = (String) stuff.elementAt(3);
			name = (String) stuff.elementAt(4);

			Vector myItems = aUser.getRoom().getItems(adject1, adject2,
					adject3, name);
			if (myItems.size() < amount)
			{
				aUser.writeMessage("You search but find nothing.<BR>\r\n");
				return true;
			}
			if (myItems.elementAt(0) instanceof StdItemContainer)
			{
				Item item = (Item) myItems.elementAt(0);
				Persons.sendMessage(aUser, "%SNAME search%VERB1 "
						+ item.getDescription()
						+ " but find%VERB2 nothing.<BR>\r\n");
				return true;
			}
			StdItemContainer aContainer = (StdItemContainer) myItems
					.elementAt(0);
			myItems = ItemsDb.getItemsFromContainer(aContainer);
			if (myItems.size() == 0)
			{
				Persons.sendMessage(aUser, "%SNAME search%VERB1 "
						+ aContainer.getDescription()
						+ " but find%VERB1 nothing.<BR>\r\n");
				return true;
			}
			Item firstItem = (Item) myItems.elementAt(0);
			// here needs to be a check for validity of the item
			Database.writeLog(aUser.getName(), "searched " + aContainer
					+ " in room " + aUser.getRoom().getId() + " and found "
					+ firstItem);
			ItemsDb.deleteItemFromContainer(firstItem);
			ItemsDb.addItemToChar(firstItem, aUser);
			Persons.sendMessage(aUser, "%SNAME search%VERB1 "
					+ aContainer.getDescription() + " and find%VERB2 "
					+ firstItem.getDescription() + ".<BR>\r\n");
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new SearchCommand(getRegExpr());
	}

}
