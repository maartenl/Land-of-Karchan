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
import mmud.items.Container;
import mmud.items.Item;
import mmud.items.ItemException;
import mmud.items.StdItemContainer;
import mmud.rooms.Room;

/**
 * Retrieve an item from a container: "retrieve ring from sack". Requirements
 * for it to be successfull:
 * <ul>
 * <li>the item to retrieve must be in this container
 * <li>the container must be in your inventory or in the room
 * <li>the container must be a container
 * </ul>
 * The possible syntax can range from: "retrieve ring from sack" to
 * "retrieve 8 old gold shiny ring from new leather beaten sack".
 * 
 * @see PutCommand
 */
public class RetrieveCommand extends NormalCommand
{
	private String name;
	private String adject1;
	private String adject2;
	private String adject3;
	private int amount;

	public RetrieveCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * This method will make a <I>best effort</I> regarding transferring of
	 * items out of items.
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
		if (!super.run(aUser))
		{
			return false;
		}
		// initialise string, important otherwise previous instances will return
		// this
		String[] myParsed = getParsedCommand();
		if (myParsed.length > 1)
		{
			// check for into.
			Room someRoom = null;
			int frompos = 0;
			while (!myParsed[frompos].equalsIgnoreCase("from"))
			{
				frompos++;
			}

			Vector stuff = Constants.parseItemDescription(myParsed,
					frompos + 1, myParsed.length - frompos - 1);
			adject1 = (String) stuff.elementAt(1);
			adject2 = (String) stuff.elementAt(2);
			adject3 = (String) stuff.elementAt(3);
			name = (String) stuff.elementAt(4);

			Vector myContainers = aUser.getItems(adject1, adject2, adject3,
					name);
			if (myContainers.size() < 1)
			{
				myContainers = aUser.getRoom().getItems(adject1, adject2,
						adject3, name);
				someRoom = aUser.getRoom(); // for the writeLog.
				if (myContainers.size() < 1)
				{
					aUser
							.writeMessage("You do not have that container.<BR>\r\n");
					return true;
				}
			}
			Item aContainer2 = (Item) myContainers.elementAt(0);
			if (!(aContainer2 instanceof Container))
			{
				aUser.writeMessage(aContainer2.getDescription()
						+ " is not a container.<BR>\r\n");
				return true;
			}
			StdItemContainer aContainer = (StdItemContainer) aContainer2;
			if (!aContainer.isOpen())
			{
				aUser.writeMessage(aContainer.getDescription()
						+ " is closed.<BR>\r\n");
				return true;
			}

			stuff = Constants.parseItemDescription(myParsed, 1, frompos - 1);
			amount = ((Integer) stuff.elementAt(0)).intValue();
			adject1 = (String) stuff.elementAt(1);
			adject2 = (String) stuff.elementAt(2);
			adject3 = (String) stuff.elementAt(3);
			name = (String) stuff.elementAt(4);
			Vector myItems = aContainer.getItems(adject1, adject2, adject3,
					name);
			if (myItems.size() < amount)
			{
				if (amount == 1)
				{
					aUser.writeMessage("That item was not found in "
							+ aContainer.getDescription() + ".<BR>\r\n");
				} else
				{
					aUser.writeMessage("Those items were not found in "
							+ aContainer.getDescription() + ".<BR>\r\n");
				}
				return true;
			}

			int j = 0;
			for (int i = 0; ((i < myItems.size()) && (j != amount)); i++)
			{
				Item myItem = (Item) myItems.elementAt(i);
				Database.writeLog(aUser.getName(), "retrieved "
						+ myItem
						+ " from "
						+ aContainer
						+ (someRoom != null ? " from room " + someRoom.getId()
								: ""));
				ItemsDb.deleteItemFromContainer(myItem);
				ItemsDb.addItemToChar(myItem, aUser);
				Persons.sendMessage(aUser, "%SNAME retrieve%VERB2 "
						+ myItem.getDescription() + " from "
						+ aContainer.getDescription() + ".<BR>\r\n");
				j++;
			}
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new RetrieveCommand(getRegExpr());
	}

}
