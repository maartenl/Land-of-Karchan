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

package mmud;

import java.util.logging.Logger;

/**
 * An attri ute data class, containing name, value, valuetype.
 * This class is ummutable.
 */
public class Attribute
{
	/**
	 * Attribute indicating that this item is a list of items
	 * that is sold by a certain shopkeeper.
	 */
	public final static String SHOPKEEPERLIST = "shopkeeperlist";
	
	/**
	 * Attribute containing the description of when you drink something.
	 * Can also be used to indicate if something is drinkable.
	 */
	public final static String DRINKABLE = "drinkable";
	
	/**
	 * Attribute containing the description of when you eat something.
	 * Can also be used to indicate if something is eatable.
	 */
	public final static String EATABLE = "eatable";
	
	/**
	 * Attribute containing the description of when you read something.
	 * Can also be used to indicate if something is readable.
	 */
	public final static String READABLE = "readable";
	
	/**
	 * Attribute indicating that something cannot be dropped.
	 */
	public final static String NOTDROPABLE = "notdropable";
	
	/**
	 * Attribute indicating that something cannot be retrieved from the floor.
	 */
	public final static String NOTGETABLE = "notgetable";
	
	/**
	 * Attribute indicating that something is invisible. This means
	 * it will not show up in any inventories or room descriptions.
	 */
	public final static String INVISIBLE = "invisible";
	
	/**
	 * Attribute indicating that something is wearable/wieldable.
	 */
	public final static String WEARABLE = "wearable";
	
	/**
	 * Attribute indicating that someone wishes to join a certain guild.
	 */
	public final static String GUILDWISH = "guildwish";
	
	/**
	 * Attribute indicating that someone has a specific rank in a guild.
	 */
	public final static String GUILDRANK = "guildrank";
	
	/**
	 * Attribute indicating that an item can be opened.
	 */
	public final static String ISOPENABLE = "isopenable";
	
	/**
	 * Attribute indicating that an item is open or not.
	 */
	public final static String ISOPEN = "isopen";
	
	/**
	 * Attribute indicating that an item is locked.
	 */
	public final static String ISLOCKED = "islocked";
	private String theName;
	private String theValue;
	private String theValueType;

	/**
	 * constructor for Attribute with parameter values.
	 * @param aName string containing the name.
	 * @param aValue string containing the value
	 * @param aValueType string containing a description of the
	 * type of value (string, number, boolean, etc)
	 */
	public Attribute(String aName, String aValue, String aValueType)
	throws MudException
	{
		Logger.getLogger("mmud").finer("aName=" + aName +
			", aValue=" + aValue + ",aValueType=" + aValueType);
   		if ((aName == null) || (aValue == null) || (aValueType == null))
		{
			throw new MudException("error creating attribute, one of the properties was empty.");
		}
		theName = aName;
		theValue = aValue;
		theValueType = aValueType;
	} 

	/**
	 * standard copy constructor.
	 * @param anAttribute the attribute to be copied.
	 */
	public Attribute(Attribute anAttribute)
	throws MudException
	{
		this(anAttribute.getName(),
			anAttribute.getValue(), anAttribute.getValueType());
	} 

	/**
	 * returns the name.
	 * @return String containing the name.
	 */
	public String getName()
	{
		return theName;
	}

	/**
	 * returns the value.
	 * @return String containing the value.
	 */
	public String getValue()
	{
		return theValue;
	}

	/**
	 * returns the valuetype.
	 * @return String containing the valuetype.
	 */
	public String getValueType()
	{
		return theValueType;
	}

	/**
	 * standard tostring implementation.
	 * @return String with format [name]:[value]:[valuetype]
	 */
	public String toString()
	{
		return getName() + ":" + getValue() + ":" + getValueType();
	}

}
