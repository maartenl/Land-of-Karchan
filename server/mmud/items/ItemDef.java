/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/items/ItemDef.java,v 1.8 2005/09/17 18:09:05 karn Exp $
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
 * The definition of an item. The analogy with Java would be the difference
 * between a class and an object.
 */
public class ItemDef implements AttributeContainer
{
	private String theVerb;
	private String theAdjective1;
	private String theAdjective2;
	private String theAdjective3;
	private String theDescription;
	private int theId;
	private int theGold;
	private int theSilver;
	private int theCopper;
	private int theWearable;
	private TreeMap theAttributes = new TreeMap();

	/**
	 * Create an item definition.
	 * @param anId identification of the item definition
	 * @param anAdjective1 the first adjective of the item
	 * @param anAdjective2 the second adjective of the item
	 * @param anAdjective3 the third adjective of the item
	 * @param aVerb the verb/name of the item
	 * @param aDescription a long description of the item
	 * @param aGold the number of gold coins the item costs
	 * @param aSilver the number of silver coins the item costs
	 * @param aCopper the number of copper coins the item costs 
	 * @param aWearable the possible positions that the item
	 * can be worn on.
	 */
	public ItemDef(int anId, String anAdjective1, String anAdjective2, 
		String anAdjective3, String aVerb, String aDescription, 
		int aGold, int aSilver, int aCopper,
		int aWearable)
	{
		theId = anId;
		theVerb = aVerb;
		theAdjective1 = anAdjective1;
		theAdjective2 = anAdjective2;
		theAdjective3 = anAdjective3;
		theDescription = aDescription;
		theGold = aGold;
		theSilver = aSilver;
		theCopper = aCopper;
		theWearable = aWearable;
	} 

	/**
	 * Standard copy constructor.
	 * @param anItemDef the original Item Definition.
	 */
	public ItemDef(ItemDef anItemDef)
	{
		theId = anItemDef.getId();
		theVerb = anItemDef.getVerb();
		theAdjective1 = anItemDef.getAdjective1();
		theAdjective2 = anItemDef.getAdjective2();
		theAdjective3 = anItemDef.getAdjective3();
		theDescription = anItemDef.getLongDescription();
		theGold = anItemDef.getGold();
		theSilver = anItemDef.getSilver();
		theCopper = anItemDef.getCopper();
		theWearable = anItemDef.getWearable();
	} 

	/**
	 * Return the id.
	 * @return integer containing the identification number of the item
	 * definition.
	 */
	public int getId()
	{
		return theId;
	}

	/**
	 * Return the verb.
	 * @return String containing the verb of the item
	 * definition.
	 */
	public String getVerb()
	{
		return theVerb;
	}

	/**
	 * Return the first adjective.
	 * @return String containing the first adjective of the item
	 * definition.
	 */
	public String getAdjective1()
	{
		return theAdjective1;
	}

	/**
	 * Return the second adjective.
	 * @return String containing the second adjective of the item
	 * definition.
	 */
	public String getAdjective2()
	{
		return theAdjective2;
	}

	/**
	 * Return the third adjective.
	 * @return String containing the third adjective of the item
	 * definition.
	 */
	public String getAdjective3()
	{
		return theAdjective3;
	}
	
	/**
	 * Returns the amount of money something costs in total copper coins.
	 * @return integer, gold*100+silver*10+copper
	 * @see ItemDef#getGold
	 * @see ItemDef#getSilver
	 * @see ItemDef#getCopper
	 */
	public int getValue()
	{
		return getGold() * 100 + getSilver() * 10 + getCopper();
	}

	/**
	 * Return the amount of gold it costs.
	 * @return integer containing number of gold coins.
	 */
	public int getGold()
	{
		return theGold;
	}
	
	/**
	 * Return the amount of silver it costs.
	 * @return integer containing number of silver coins.
	 */
	public int getSilver()
	{
		return theSilver;
	}
	
	/**
	 * Return the amount of copper it costs.
	 * @return integer containing number of copper coins.
	 */
	public int getCopper()
	{
		return theCopper;
	}
	
	/**
	 * Return the wearable on setting.
	 * @return integer containing different positions that this
	 * thing is wearable on.
	 */
	private int getWearable()
	{
		return theWearable;
	}
	
	/**
	 * Return if the position entered is a member
	 * of the possible positions that this item can be worn on.
	 * @return boolean, true if this item is wearable there.
	 */
	public boolean isWearable(PersonPositionEnum aPos)
	{
		return PersonPositionEnum.isIn(theWearable, aPos);
	}
	
	/**
	 * standard to string implementation.
	 * @return String in format id+adject1+adject2+adject3+verb.
	 */
	public String toString()
	{
		return theId + " " + theAdjective1 + " " +
			theAdjective2 + " " +
			theAdjective3 + " " +
			theVerb;
	}

	/**
	 * Return the long description.
	 * @return String containing the long description.
	 */
	public String getLongDescription()
	{
		return theDescription;
	}

	/**
	 * Set or add an attribute for this itemdefinition.
	 * @param anAttribute the attribute to be set or added.
	 */
	public void setAttribute(Attribute anAttribute)
	{
		theAttributes.put(anAttribute.getName(), anAttribute);
	}

	/**
	 * Retrieve an attribute for this itemdefinition.
	 * @param aName the name of the attribute.
	 * @return Attribute object if found, otherwise null.
	 */
	public Attribute getAttribute(String aName)
	{
		return (Attribute) theAttributes.get(aName);
	}

	/**
	 * Remove an attribute for this itemdefinition.
	 * @param aName the name of the attribute.
	 */
	public void removeAttribute(String aName)
	{
		theAttributes.remove(aName);
	}

	/**
	 * Check if an attribute exists.
	 * @param aName the name of the attribute to search for.
	 * @return boolean, true if the attribute exists.
	 */
	public boolean isAttribute(String aName)
	{
		return theAttributes.containsKey(aName);
	}

	/**
	 * default equals implementation.
	 */
	public boolean equals(Object r)
	{
		if (r == null)
		{
			return false;
		}
		if (!(r instanceof ItemDef))
		{
			return false;
		}
		ItemDef u = (ItemDef) r;
		return u.getId() == getId();
	}

	/**
	 * get a description of the item.
	 * @return String containing the description in the format: "an/a
	 * [adject1], [adject2], [adject3] [verb]".
	 */
	 public String getDescription()
	 {
		 return (Constants.isQwerty(getAdjective1().charAt(0)) ?
			 "an " : "a ") + getAdjective1() + ", " +
			 getAdjective2() + ", " + 
			 getAdjective3() + " " + getVerb();
	 }

}
