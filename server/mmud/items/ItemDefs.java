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
package mmud.items;     

import java.util.Vector;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

public final class ItemDefs 
{
	private static Vector theItemDefs = new Vector();

	public ItemDefs()
	{
		theItemDefs = new Vector();
	}

	public static void init()
	{
		theItemDefs = new Vector();
	}

	public static ItemDef getItemDef(int aItemDefNr)
	{
		ItemDef myItemDef = null;
		assert theItemDefs != null : "theItemDefs vector is null";
		if (Constants.logging)
		{
//			System.err.println("ItemDefs.getItemDef: " + aItemDefNr);
		}
		if (aItemDefNr == 0) 
		{
			return null;
		}
		for (int i=0;i < theItemDefs.size(); i++)
		{
			myItemDef = (ItemDef) theItemDefs.elementAt(i);
			if ((myItemDef != null) && (myItemDef.getId() == aItemDefNr))
			{
				return myItemDef;
			}
		}
		myItemDef = Database.getItemDef(aItemDefNr);
		if (myItemDef != null)
		{
			theItemDefs.addElement(myItemDef);
		}
		return myItemDef;
	}

}