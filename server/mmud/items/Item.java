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

import java.util.TreeMap;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * An item in the mud.
 * Basically consists of an ItemDefinition and a number of Attributes
 * specific to this item.
 */
public class Item
{
	private ItemDef theItemDef;
	private TreeMap theAttributes = new TreeMap();

	/**
	 * Create this item object with a default Item Definition.
	 */
	public Item(ItemDef anItemDef)
	{
		theItemDef = anItemDef;
	} 

	/**
	 * Create this item object as a copy of an existing item.
	 */
	public Item(Item anItem)
	{
		this(anItem.getItemDef());
	} 

	/**
	 * Returns the id of the Item Definition.
	 * @return integer identifying the Item Definition.
	 */
	public int getId()
	{
		return theItemDef.getId();
	}

	/**
	 * get the verb of the item.
	 * @return String containing the verb.
	 */
	public String getVerb()
	{
		return theItemDef.getVerb();
	}

	/**
	 * get the first adjective of the item.
	 * @return String containing the first adjective.
	 */
	public String getAdjective1()
	{
		return theItemDef.getAdjective1();
	}

	/**
	 * get the second adjective of the item.
	 * @return String containing the second adjective.
	 */
	public String getAdjective2()
	{
		return theItemDef.getAdjective2();
	}

	/**
	 * get the third adjective of the item.
	 * @return String containing the third adjective.
	 */
	public String getAdjective3()
	{
		return theItemDef.getAdjective3();
	}
	
	/**
	 * get the gold coins part of the value of the item.
	 * @return integer containing the number of gold coins.
	 */
	public int getGold()
	{
		return theItemDef.getGold();
	}

	/**
	 * get the silver coins part of the value of the item.
	 * @return integer containing the number of silver coins.
	 */
	public int getSilver()
	{
		return theItemDef.getSilver();
	}

	/**
	 * get the copper coins part of the value of the item.
	 * @return integer containing the number of copper coins.
	 */
	public int getCopper()
	{
		return theItemDef.getCopper();
	}

	/**
	 * get the item definition belonging to this item.
	 * @return ItemDef object containing the item definition.
	 */
	public ItemDef getItemDef()
	{
		return theItemDef;
	}

	/**
	 * get a description of the item.
	 * @return String containing the description in the format: "an/a
	 * [adject1], [adject2] [verb]".
	 */
	public String getDescription()
	{
		return (Constants.isQwerty(getAdjective1().charAt(0)) ? "an " : "a ") + getAdjective1() + ", " + getAdjective2() + " " + getVerb();
	}

	/**
	 * get the description of the item (the long one).
	 * @return String containing the description.
	 */
	public String getLongDescription()
	{
		return theItemDef.getLongDescription();
	}

	/**
	 * standard tostring implementation.
	 * @return String containing the super.tostring + : + item def.
	 */
	public String toString()
	{
		return super.toString() + ":" + getItemDef();
	}

	/**
	 * Set or add an attribute of this item.
	 * @param anAttribute the attribute to be added/set.
	 */
	public void setAttribute(Attribute anAttribute)
	{
		theAttributes.put(anAttribute.getName(), anAttribute);
	}

	/**
	 * returns the attribute found with name aName or null
	 * if it does not exist.
	 * @param aName the name of the attribute to search for
	 * @return Attribute object containing the attribute foudn or null.
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

	/**
	 * Remove a specific attribute from the item.
	 * @param aName the name of the attribute to be removed.
	 */
	public void removeAttribute(String aName)
	{
		theAttributes.remove(aName);
	}

	/**
	 * returns true if the attribute with name aName
	 * exists.
	 * @param aName the name of the attribute to check
	 * @return boolean, true if the attribute exists for this item,
	 * otherwise returns false.
	 */
	public boolean isAttribute(String aName)
	{
		return theAttributes.containsKey(aName) ||
			getItemDef().isAttribute(aName);
	}

}
