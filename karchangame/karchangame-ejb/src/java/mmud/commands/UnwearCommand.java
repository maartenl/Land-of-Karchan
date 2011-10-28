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
import mmud.database.entities.Persons;
import mmud.database.entities.Player;
import mmud.database.Database;
import mmud.items.Item;
import mmud.items.ItemException;
import mmud.items.PersonPositionEnum;

/**
 * Stop you wearing an item on you. Syntax: remove &lt;item&gt; from &lt;body
 * position&gt;
 * @see mmud.command.WearCommand
 */
public class UnwearCommand extends NormalCommand
{

        /**
         * For special purposes of the "UndressCommand".
         */
	public UnwearCommand()
	{
            super("");
	}

	public UnwearCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	@Override
	public boolean run(Player aPlayer) throws ItemException, ParseException,
			MmudException
	{
	        if (Constants.debugOn(aPlayer.getName()))
		{   
		        Logger.getLogger("mmud_debug").finest("run");
                }
		Logger.getLogger("mmud").finer("");
		// initialise string, important otherwise previous instances will return
		// this
		String[] myParsed = getParsedCommand();
		// determine the appropriate body position entered by the
		// user
		String pos = myParsed[myParsed.length - 1];
		PersonPositionEnum position = null;
		if (pos.equalsIgnoreCase("head"))
		{
			position = PersonPositionEnum.ON_HEAD;
		} else if (pos.equalsIgnoreCase("neck"))
		{
			position = PersonPositionEnum.ON_NECK;
		} else if (pos.equalsIgnoreCase("torso"))
		{
			position = PersonPositionEnum.ON_TORSO;
		} else if (pos.equalsIgnoreCase("arms"))
		{
			position = PersonPositionEnum.ON_ARMS;
		} else if (pos.equalsIgnoreCase("leftwrist"))
		{
			position = PersonPositionEnum.ON_LEFT_WRIST;
		} else if (pos.equalsIgnoreCase("rightwrist"))
		{
			position = PersonPositionEnum.ON_RIGHT_WRIST;
		} else if (pos.equalsIgnoreCase("leftfinger"))
		{
			position = PersonPositionEnum.ON_LEFT_FINGER;
		} else if (pos.equalsIgnoreCase("rightfinger"))
		{
			position = PersonPositionEnum.ON_RIGHT_FINGER;
		} else if (pos.equalsIgnoreCase("feet"))
		{
			position = PersonPositionEnum.ON_FEET;
		} else if (pos.equalsIgnoreCase("hands"))
		{
			position = PersonPositionEnum.ON_HANDS;
		} else if (pos.equalsIgnoreCase("waist"))
		{
			position = PersonPositionEnum.ON_WAIST;
		} else if (pos.equalsIgnoreCase("legs"))
		{
			position = PersonPositionEnum.ON_LEGS;
		} else if (pos.equalsIgnoreCase("eyes"))
		{
			position = PersonPositionEnum.ON_EYES;
		} else if (pos.equalsIgnoreCase("ears"))
		{
			position = PersonPositionEnum.ON_EARS;
		} else if (pos.equalsIgnoreCase("body"))
		{
			position = PersonPositionEnum.ABOUT_BODY;
		} else
		{
			aPlayer.writeMessage("Cannot wear something there.<BR>\r\n");
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

		Vector myItems = aPlayer.getItems(adject1, adject2, adject3, name);
		if (myItems.size() == 0)
		{
			aPlayer.writeMessage("You do not have that item.<BR>\r\n");
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
                                return stopWearing(myItem, aPlayer, position);
			}
		}
		return false;
	}

        public static boolean stopWearing(Item item, Player aPlayer, PersonPositionEnum position)
                throws ItemException, ParseException,
			MmudException
        {
            if (item == null)
            {
                return false;
            }
            item.setWearing(null);
            Database.writeLog(aPlayer.getName(), "remove " + item
                            + " from " + position);
            Persons.sendMessage(aPlayer, "%SNAME remove%VERB2 "
                            + item.getDescription() + " from " + position
                            + ".<BR>\r\n");
            return true;
        }

	public Command createCommand()
	{
		return new UnwearCommand(getRegExpr());
	}

}
