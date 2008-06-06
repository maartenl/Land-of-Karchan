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
package mmud.items;     

import java.util.Vector;
import java.util.logging.Logger;

import mmud.database.ItemsDb;
import mmud.database.MudDatabaseException;

/**
 * Collection class containing all the item definitions.
 */
public final class ItemDefs 
{
	private static Vector theItemDefs = new Vector();

	/**
	 * Create a new itemdefs object.
	 */
	public ItemDefs()
	{
	}

	/**
	 * Initialise this object with an empty list.
	 */
	public static void init()
	{
		theItemDefs = new Vector();
	}

	/**
	 * Get the number of ItemDefs that are cached.
	 * @param int, containing the amount of ItemDefs in the cache.
	 */
	public static int getSize()
	{
		return theItemDefs.size();
	}
 
 	/**
	 * Return the itemdefinition from the list. Returns a null
	 * if the item definition could not be found.
	 * @param aItemDefNr identification number of an itemdefinition.
	 * @return ItemDef the found Item Definition.
	 */
	public static ItemDef getItemDef(int aItemDefNr)
	throws MudDatabaseException
	{
		if (theItemDefs == null)
		{
			throw new RuntimeException("theItemDefs vector is null");
		}
		ItemDef myItemDef = null;
		Logger.getLogger("mmud").finer("aItemDefNr=" + aItemDefNr);
		for (int i=0;i < theItemDefs.size(); i++)
		{
			myItemDef = (ItemDef) theItemDefs.elementAt(i);
			if ((myItemDef != null) && (myItemDef.getId() == aItemDefNr))
			{
				return myItemDef;
			}
		}
		myItemDef = ItemsDb.getItemDef(aItemDefNr);
		if (myItemDef != null)
		{
			theItemDefs.addElement(myItemDef);
		}
		return myItemDef;
	}
}
