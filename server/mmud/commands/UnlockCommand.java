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
 * Unlocks a container with a key: "unlock chest with key".
 * Requirements for it to be successfull:
 * <ul>
 * <li>the container must be in your inventory or in the room 
 * <li>the container must be a container
 * <li>the container must be closed
 * <li>the container must be lockable.
 * <li>the container must be locked.
 * <li>the key item must be in your inventory
 * </ul>
 * The possible syntax can range from: "unlock chest with key" to
 * "unlock iron studded wooden chest with old long ornate key".
 */
public class UnlockCommand extends NormalCommand
{
	private String name;
	private String adject1;
	private String adject2;
	private String adject3;
	private int amount;

	public UnlockCommand(String aRegExpr)
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
		if (myParsed.length > 1)
		{
			// check for in.
			int intopos = 0;
			while (!myParsed[intopos].equalsIgnoreCase("with")) 
			{
				intopos++;
			}
			Vector stuff = Constants.parseItemDescription(myParsed, intopos+1, myParsed.length - intopos - 1);
			adject1 = (String) stuff.elementAt(1);
			adject2 = (String) stuff.elementAt(2);
			adject3 = (String) stuff.elementAt(3);
			name = (String) stuff.elementAt(4);

			Vector myItems = 
				aUser.getItems(adject1, adject2, adject3, name);
			if (myItems.size() < 1)
			{
				aUser.writeMessage("You do not have that item.<BR>\r\n");
				return true;
			}
			Item myKey = (Item) myItems.elementAt(0);

			stuff = Constants.parseItemDescription(myParsed, 1, intopos - 1);
			adject1 = (String) stuff.elementAt(1);
			adject2 = (String) stuff.elementAt(2);
			adject3 = (String) stuff.elementAt(3);
			name = (String) stuff.elementAt(4);
			Room someRoom = null;

			Vector myContainers = 
				aUser.getItems(adject1, adject2, adject3, name);
			if (myContainers.size() < 1)
			{
				myContainers = 
					aUser.getRoom().getItems(adject1, adject2, adject3, name);
				someRoom = aUser.getRoom(); // usefull for the writeLog.
				if (myContainers.size() < 1)
				{
					aUser.writeMessage("You cannot find that container.<BR>\r\n");
					return true;
				}
			}
			Item aContainer = (Item) myContainers.elementAt(0);
			if (!(aContainer instanceof Container))
			{
				aUser.writeMessage(aContainer.getDescription() + " is not a container.<BR>\r\n");
				return true;
			}

			Container myCon = (Container) aContainer;
			if (!myCon.hasLock())
			{
				aUser.writeMessage(aContainer.getDescription() + " does not have a lock.<BR>\r\n");
				return true;
			}
			if (!myCon.isLocked())
			{
				aUser.writeMessage(aContainer.getDescription() + " is already unlocked.<BR>\r\n");
				return true;
			}
			if (!myCon.getKeyId().equals(myKey.getItemDef()))
			{
				aUser.writeMessage(myKey.getDescription() + 
					" does not fit in the lock of " + 
					aContainer.getDescription() + ".<BR>\r\n");
				return true;
			}
			myCon.setLidsNLocks(myCon.isOpenable(),
				myCon.isOpen(),
				myCon.getKeyId(),
				false);
			Persons.sendMessage(aUser, "%SNAME unlock%VERB2 " + aContainer.getDescription() +
				" with " + myKey.getDescription() + ".<BR>\r\n");
			return true;
		}
		return false;
	}

	public Command createCommand()
	{
		return new UnlockCommand(getRegExpr());
	}
	
}
