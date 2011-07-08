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
import mmud.items.Item;
import mmud.items.ItemException;
import mmud.items.PersonPositionEnum;

/**
 * Stops you wielding an item. Syntax: wear &lt;item&gt; on &lt;body
 * position&gt;
 */
public class UnwieldCommand extends NormalCommand
{

        /**
         * For special purposes of the "DisarmCommand".
         */
	public UnwieldCommand()
	{
            super("");
	}

	public UnwieldCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(User aUser) throws ItemException, ParseException,
			MudException
	{
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return
		// this
		String[] myParsed = getParsedCommand();
		// determine the appropriate body position entered by the
		// user
		String pos = myParsed[myParsed.length - 1];
		PersonPositionEnum position = null;
		if (pos.equalsIgnoreCase("lefthand"))
		{
			position = PersonPositionEnum.WIELD_LEFT;
		} else if (pos.equalsIgnoreCase("righthand"))
		{
			position = PersonPositionEnum.WIELD_RIGHT;
		} else if (pos.equalsIgnoreCase("hands"))
		{
			position = PersonPositionEnum.WIELD_BOTH;
		} else if (pos.equalsIgnoreCase("both"))
		{
			position = PersonPositionEnum.WIELD_BOTH;
		} else if (pos.equalsIgnoreCase("bothhands"))
		{
			position = PersonPositionEnum.WIELD_BOTH;
		} else
		{
			aUser.writeMessage("Cannot wield something that way.<BR>\r\n");
			return true;
		}

		Logger.getLogger("mmud").finer("position=" + position);
		// check for item in posession
		Vector stuff = Constants.parseItemDescription(myParsed, 1,
				myParsed.length - 3);
		int amount = ((Integer) stuff.elementAt(0)).intValue();
		String adject1 = (String) stuff.elementAt(1);
		String adject2 = (String) stuff.elementAt(2);
		String adject3 = (String) stuff.elementAt(3);
		String name = (String) stuff.elementAt(4);

		Vector myItems = aUser.getItems(adject1, adject2, adject3, name);
		if (myItems.size() == 0)
		{
			aUser.writeMessage("You do not have that item.<BR>\r\n");
			return true;
		}
		int j = 0;
		for (int i = 0; ((i < myItems.size()) && (j != amount)); i++)
		{
			// here needs to be a check for validity of the item
			boolean success = true;
			Item myItem = (Item) myItems.elementAt(i);
			if (myItem.getWearing() != position)
			{
				success = false;
			}
			if (success)
			{
				// transfer item to other person

				return stopWielding(myItem, aUser, position);
			}
		}
		return false;
	}

        public boolean stopWielding(Item item, User aUser, PersonPositionEnum position)
        throws ItemException, ParseException,
                MudException
        {
            if (item == null)
            {
                return false;
            }
            item.setWearing(null);
            Database.writeLog(aUser.getName(), "stops wielding " + item
                            + " " + position);
            Persons.sendMessage(aUser, "%SNAME stop%VERB2 wielding "
                            + item.getDescription() + " " + position
                            + ".<BR>\r\n");
            return true;
        }

	public Command createCommand()
	{
		return new UnwieldCommand(getRegExpr());
	}

}
