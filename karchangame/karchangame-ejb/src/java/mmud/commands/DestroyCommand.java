/*-------------------------------------------------------------------------
svninfo: $Id: EatCommand.java 1192 2009-10-20 05:42:27Z maartenl $
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
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
import mmud.database.Database;
import mmud.database.ItemsDb;
import mmud.items.Item;
import mmud.items.ItemException;

/**
 * Destroys an item from inventory. "destroy apple pie". Will nicely cleanup your inventory
 * without having to litter the floor with miscellaneous items.
 */
public class DestroyCommand extends NormalCommand
{

	public DestroyCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws ItemException, MmudException
	{
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return
		// this
		String[] myParsed = getParsedCommand();
		if (myParsed.length > 1)
		{
			Vector stuff = Constants.parseItemDescription(myParsed, 1,
					myParsed.length - 1);
			String adject1 = (String) stuff.elementAt(1);
			String adject2 = (String) stuff.elementAt(2);
			String adject3 = (String) stuff.elementAt(3);
			String name = (String) stuff.elementAt(4);

			Vector myItems = aPlayer.getItems(adject1, adject2, adject3, name);
			if (myItems.size() == 0)
			{
				aPlayer
						.writeMessage("You cannot find that item in your inventory.<BR>\r\n");
				return true;
			}
			Item myItem = (Item) myItems.elementAt(0);
			Database.writeLog(aPlayer.getName(), "destroyed " + myItem);
			ItemsDb.deleteItem(myItem);
			Persons.sendMessage(aPlayer, "%SNAME destroy%VERB2 "
					+ myItem.getDescription() + ".<BR>\r\n");

			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new DestroyCommand(getRegExpr());
	}

}
