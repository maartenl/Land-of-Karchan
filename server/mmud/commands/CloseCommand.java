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
 * Close a container: "close chest".
 * @see OpenCommand
 * @see LockCommand
 * @see UnlockCommand
 */
public class CloseCommand extends NormalCommand
{
	private String name;
	private String adject1;
	private String adject2;
	private String adject3;
	private int amount;

	public CloseCommand(String aRegExpr)
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
			Vector stuff = Constants.parseItemDescription(myParsed, 1, myParsed.length - 1);
			adject1 = (String) stuff.elementAt(1);
			adject2 = (String) stuff.elementAt(2);
			adject3 = (String) stuff.elementAt(3);
			name = (String) stuff.elementAt(4);

			Vector myContainers = 
				aUser.getItems(adject1, adject2, adject3, name);
			if (myContainers.size() < 1)
			{
				myContainers = aUser.getRoom().getItems(adject1, adject2,
					adject3, name);
				if (myContainers.size() < 1)
				{
					aUser.writeMessage("You cannot find that item.<BR>\r\n");
					return true;
				}
			}
			Item anItem = (Item) myContainers.elementAt(0);
			if (!(anItem instanceof Container))
			{
				aUser.writeMessage("You cannot close " + anItem.getDescription() + ".<BR>\r\n");
				return true;
			}
			Container aContainer = (Container) anItem;
			if (!aContainer.isOpenable())
			{
				aUser.writeMessage(anItem.getDescription() + " cannot be closed.<BR>\r\n");
				return true;
			}
			if (!aContainer.isOpen())
			{
				aUser.writeMessage(anItem.getDescription() + " is already closed.<BR>\r\n");
				return true;
			}
			if (aContainer.isLocked())
			{
				aUser.writeMessage(anItem.getDescription() + " is locked.<BR>\r\n");
				return true;
			}
			aContainer.setLidsNLocks(
				aContainer.isOpenable(),
				false,
				aContainer.getKeyId(),
				aContainer.isLocked());
			Persons.sendMessage(aUser, "%SNAME close%VERB2 " + anItem.getDescription() + ".<BR>\r\n");
			if (anItem.isAttribute("closeevent"))
			{
				String mySource =
					Database.getMethodSource(
						anItem.getAttribute("closeevent").getValue());
				if ( (mySource == null) || (mySource.trim().equals("")) )
				{
					throw new MethodDoesNotExistException("closeevent of item " + anItem.getId());
				}
				anItem.runScript("closeevent", mySource, aUser);
			}
			return true;
		}
		return false;
	}

}
