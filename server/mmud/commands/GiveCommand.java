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
 * Give an item in your inventory to another character.
 * Syntax: give &lt;item&gt; to &lt;character&gt;
 */
public class GiveCommand extends NormalCommand
{

	public GiveCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	public boolean run(User aUser)
	throws ItemException, ParseException, MudException
	{
		Logger.getLogger("mmud").finer("");
		if (!super.run(aUser))
		{
			return false;
		}
		// initialise string, important otherwise previous instances will return this
		String[] myParsed = getParsedCommand();
		if (myParsed.length >= 4 && 
			myParsed[myParsed.length-2].equalsIgnoreCase("to")) 
		{
			// determine if appropriate shopkeeper is found.
			Person toChar = Persons.retrievePerson(myParsed[myParsed.length-1]);
			if ((toChar == null) || (!toChar.getRoom().equals(aUser.getRoom())) )
			{
				aUser.writeMessage("Cannot find that person.<BR>\r\n");
				return true;
			}

			// check for item in posession of shopkeeper
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
				}
				else
				{
					aUser.writeMessage("You do not have that many items.<BR>\r\n");
				}
				return true;
			}
			int j = 0;
			for (int i = 0; ((i < myItems.size()) && (j != amount)); i++)
			{
				// here needs to be a check for validity of the item
				boolean valid = true;
				Item myItem = (Item) myItems.elementAt(i);
				if (myItem.isAttribute("notgiveable"))
				{
					aUser.writeMessage("You cannot give that item.<BR>\r\n");
					valid = false;
				}
				if (myItem.isWearing())
				{
					aUser.writeMessage("You are wearing or wielding that item.<BR>\r\n");
					valid = false;
				}
				if (valid)
				{
					// transfer item to other person
					try
					{
						ItemsDb.transferItem(myItem, toChar);
						Database.writeLog(aUser.getName(), "gave " + myItem + " to " + toChar.getName());
						Persons.sendMessage(aUser, toChar, "%SNAME give%VERB2 " + myItem.getDescription() + " to %TNAME.<BR>\r\n");
						j++;
					}
					catch (ItemDoesNotExistException e)
					{
						Database.writeLog(aUser.getName(), "tried to give " + myItem + " to " + toChar.getName() + " but failed.");
						// skipping this itemm, heading over to the next one.
					}
				}
			}
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new GiveCommand(getRegExpr());
	}
	
}
