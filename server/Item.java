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

import java.util.TreeMap;

public class Item
{
	private ItemDef theItemDef;
	private TreeMap theAttributes = new TreeMap();


	public Item(ItemDef anItemDef)
	{
		theItemDef = anItemDef;
	} 

	public static Item Item(Item anItem)
	{
		return new Item(anItem.getItemDef());
	} 

	public int getId()
	{
		return theItemDef.getId();
	}

	public String getVerb()
	{
		return theItemDef.getVerb();
	}

	public String getAdjective1()
	{
		return theItemDef.getAdjective1();
	}

	public String getAdjective2()
	{
		return theItemDef.getAdjective2();
	}

	public String getAdjective3()
	{
		return theItemDef.getAdjective3();
	}
	
	public int getGold()
	{
		return theItemDef.getGold();
	}

	public int getSilver()
	{
		return theItemDef.getSilver();
	}

	public int getCopper()
	{
		return theItemDef.getCopper();
	}

	public ItemDef getItemDef()
	{
		return theItemDef;
	}

	public String getDescription()
	{
		return (Constants.isQwerty(getAdjective1().charAt(0)) ? "an " : "a ") + getAdjective1() + ", " + getAdjective2() + " " + getVerb();
	}

	public String getLongDescription()
	{
		return theItemDef.getLongDescription();
	}

	public String toString()
	{
		return super.toString() + ":" + getItemDef();
	}

	public void setAttribute(Attribute anAttribute)
	{
		theAttributes.put(anAttribute.getName(), anAttribute);
	}

    /**
     * returns the attribute found with name aName or null
     * if it does not exist.
     */
	public Attribute getAttribute(String aName)
	{
		Attribute myAttrib = (Attribute) theAttributes.get(aName);
		if (myAttrib == null)
		{
			myAttrib = getItemDef().getAttribute(aName);
		}
		return myAttrib;
	}

	public void removeAttribute(String aName)
	{
		theAttributes.remove(aName);
	}

	/**
     * returns true if the attribute with name aName
     * exists.
     */
	public boolean isAttribute(String aName)
	{
		return theAttributes.containsKey(aName) ||
			getItemDef().isAttribute(aName);
	}

}
