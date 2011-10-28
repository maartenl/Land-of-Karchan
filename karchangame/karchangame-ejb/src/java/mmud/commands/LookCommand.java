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

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import mmud.Constants;
import mmud.exceptions.MmudException;
import mmud.database.entities.Person;
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
import mmud.database.ItemsDb;
import mmud.items.Container;
import mmud.items.Item;
import mmud.items.ItemDoesNotExistException;
import mmud.items.ItemException;

/**
 * Look at stuff: "look at well". There are three different possibilities:
 * <ul>
 * <li>look at something in your inventory.
 * <li>look at something in the room that you occupy
 * <li>look at a person in the same room as you
 * </ul>
 */
public class LookCommand extends NormalCommand
{

	String theResult = null;

	public LookCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	private boolean LookItem(Player aPlayer, Vector aItems) throws ItemException,
			MmudException
	{
		Logger.getLogger("mmud").finer("");
		Item myItem = (Item) aItems.elementAt(0);
		if (myItem == null)
		{
			throw new ItemDoesNotExistException("item not found... BUG!");
		}
		theResult = myItem.getLongDescription();
		theResult += aPlayer.printForm();
		Persons.sendMessage(aPlayer, "%SNAME look%VERB2 at "
				+ myItem.getDescription() + ".<BR>\r\n");
		return true;
	}

	private boolean LookInItem(Player aPlayer, Vector aItems) throws ItemException,
			MmudException
	{
		Logger.getLogger("mmud").finer("");
		Iterator myIterator = aItems.iterator();
		while (myIterator.hasNext())
		{
			Item myItem = (Item) myIterator.next();
			if (myItem instanceof Container)
			{
				Container myCon = (Container) myItem;
				if (!myCon.isOpen())
				{
					Persons
							.sendMessage(
									aPlayer,
									"%SNAME attempt%VERB2 to look in "
											+ myItem.getDescription()
											+ ", but unfortunately it seems closed.<BR>\r\n");
					return true;
				}
				Persons.sendMessage(aPlayer, "%SNAME look%VERB2 in "
						+ myItem.getDescription() + ".<BR>\r\n");
				theResult = "<H1>" + myItem.getDescription() + "</H1>"
						+ "You look in " + myItem.getDescription() + ".<P>";
				String myInvent = ItemsDb.getInventory(myItem);
				if (myInvent.equals(""))
				{
					theResult += "It is totally empty.<BR>\r\n";
				} else
				{
					theResult += "You see<UL>" + myInvent + "</UL>";
				}
				theResult += aPlayer.printForm();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean run(Player aPlayer) throws ItemException, MmudException
	{
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return
		// this
		theResult = null;
		String[] myParsed = getParsedCommand();
		if (myParsed.length > 2)
		{
			if (myParsed[1].equalsIgnoreCase("at"))
			{
				Logger.getLogger("mmud").finer("if looking at");
				Vector stuff = Constants.parseItemDescription(myParsed, 2,
						myParsed.length - 2);
				String adject1 = (String) stuff.elementAt(1);
				String adject2 = (String) stuff.elementAt(2);
				String adject3 = (String) stuff.elementAt(3);
				String name = (String) stuff.elementAt(4);

				Vector myItems = aPlayer
						.getItems(adject1, adject2, adject3, name);
				if (myItems.size() != 0)
				{
					return LookItem(aPlayer, myItems);
				}
				myItems = aPlayer.getRoom().getItems(adject1, adject2, adject3,
						name);
				if (myItems.size() != 0)
				{
					return LookItem(aPlayer, myItems);
				}
				Person toChar = Persons.retrievePerson(myParsed[2]);
				if ((toChar == null)
						|| (!toChar.getRoom().equals(aPlayer.getRoom())))
				{
					aPlayer.writeMessage("You cannot see that.<BR>\r\n");
					return true;
				}
				Persons.sendMessage(aPlayer, toChar,
						"%SNAME look%VERB2 at %TNAME.<BR>\r\n");
				String stuff2 = "You look at the "
						+ toChar.getLongDescription() + "<BR>"
						+ ItemsDb.getWearablesFromChar(toChar);
				stuff2 = stuff2.replaceAll("%SHESHE", toChar.getSex().Direct());
				stuff2 = stuff2.replaceAll("%SHISHER", toChar.getSex()
						.posession());
				stuff2 = stuff2.replaceAll("%SISARE", "is");
                                if (toChar.getState() != null)
                                {
                                    stuff2 += toChar.getState() + "<br/>\r\n";
                                }
				aPlayer.writeMessage(stuff2);
				return true;
			}
			if (myParsed[1].equalsIgnoreCase("in"))
			{
				Logger.getLogger("mmud").finer("if looking in");
				Vector stuff = Constants.parseItemDescription(myParsed, 2,
						myParsed.length - 2);
				String adject1 = (String) stuff.elementAt(1);
				String adject2 = (String) stuff.elementAt(2);
				String adject3 = (String) stuff.elementAt(3);
				String name = (String) stuff.elementAt(4);

				Vector myItems = aPlayer
						.getItems(adject1, adject2, adject3, name);
				if (myItems.size() != 0)
				{
					return LookInItem(aPlayer, myItems);
				}
				myItems = aPlayer.getRoom().getItems(adject1, adject2, adject3,
						name);
				if (myItems.size() != 0)
				{
					return LookInItem(aPlayer, myItems);
				}
				aPlayer.writeMessage("You cannot look in that item.<BR>\r\n");
				return true;
			}
			return false;
		}
		if (getCommand().equalsIgnoreCase("l"))
		{
			// do nothing, just look
			return true;
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
		return new LookCommand(getRegExpr());
	}

}
