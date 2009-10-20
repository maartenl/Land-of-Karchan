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

import mmud.Attribute;
import mmud.Constants;
import mmud.MudException;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.items.Item;
import mmud.items.ItemDoesNotExistException;
import mmud.items.ItemException;

/**
 * Read stuff: "read sign". There are two different possibilities:
 * <ul>
 * <li>read something in your inventory.
 * <li>read something in the room that you occupy
 * </ul>
 */
public class ReadCommand extends NormalCommand
{

	String theResult = null;

	public ReadCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	private boolean ReadItem(User aUser, Vector aItems) throws MudException
	{
		int i = 0;
		Item myItem = (Item) aItems.elementAt(0);
		if (myItem == null)
		{
			throw new ItemDoesNotExistException("item not found... BUG!");
		}
		while (!myItem.isAttribute("readable"))
		{
			if (i == aItems.size() - 1)
			{
				return false;
			}
			myItem = (Item) aItems.elementAt(++i);
			if (myItem == null)
			{
				throw new ItemDoesNotExistException("item not found... BUG!");
			}
		}
		Attribute attrib = myItem.getAttribute("readable");
		theResult = attrib.getValue();
		theResult += aUser.printForm();
		Persons.sendMessage(aUser, "%SNAME read%VERB2 "
				+ myItem.getDescription() + ".<BR>\r\n");
		return true;
	}

	@Override
	public boolean run(User aUser) throws ItemException, MudException
	{
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return
		// this
		theResult = null;
		String[] myParsed = getParsedCommand();
		if (myParsed.length > 1)
		{
			Vector stuff = Constants.parseItemDescription(myParsed, 1,
					myParsed.length - 1);
			String adject1 = (String) stuff.elementAt(1);
			String adject2 = (String) stuff.elementAt(2);
			String adject3 = (String) stuff.elementAt(3);
			String name = (String) stuff.elementAt(4);

			Vector myItems = aUser.getItems(adject1, adject2, adject3, name);
			if (myItems.size() != 0)
			{
				return ReadItem(aUser, myItems);
			}
			myItems = aUser.getRoom().getItems(adject1, adject2, adject3, name);
			if (myItems.size() != 0)
			{
				return ReadItem(aUser, myItems);
			}
		}
		return false;
	}

	@Override
	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theResult;
	}

	public Command createCommand()
	{
		return new ReadCommand(getRegExpr());
	}

}
