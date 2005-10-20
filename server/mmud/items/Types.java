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

import java.util.TreeMap;

/**
 * Enumerated type for the different types that a item belongs to.
 */
public class Types
{

	private String theTypeName;
	private static TreeMap theMap = new TreeMap();

	public static final Types ANYTHING = new Types("anything");

	public static final Types BODIES = new Types("bodies");

	public static final Types DEFAULT = new Types("default");

	public static final Types CAGED = new Types("caged items");

	public static final Types CLOTHES = new Types("clothes");

	public static final Types COINS = new Types("coins");

	public static final Types DAGGERS = new Types("daggers");

	public static final Types DRINKABLE = new Types("drinkables");

	public static final Types EATABLES = new Types("eatables");

	public static final Types KEYS = new Types("keys");

	public static final Types LIQUID = new Types("liquid");

	public static final Types ONEHANDWEAPONS = new Types("onehand-weapons");

	public static final Types WEAPONS = new Types("weapons");

	public static final Types READABLES = new Types("readables");

	public static final Types SCROLLS = new Types("scrolls");

	public static final Types SMOKEABLES = new Types("smokeables");

	public static final Types SWORDS = new Types("swords");

	private Types(String aType)
	{
		theTypeName = aType;
		theMap.put(aType, this);
	}

	/**
	 * Little factory method for creating a Types object.
	 * @param aString string describing the type 
	 * to be created.
	 * @return Types object.
	 * @throws RuntimeException if the type is unknown.
	 * In this case we do not know what to do.
	 */
	public static Types createFromString(String aString)
	{
		Types myType = (Types) theMap.get(aString);
		if (myType == null)
		{
			throw new RuntimeException("Illegal type!!!");
		}
		return myType;
	}

	/**
	 * returns string representation of the type.
	 * @return returns the type, for example "anything".
	 */
	public String toString()
	{
		return theTypeName;
	}

	/**
	 * default equals implementation.
	 */
	public boolean equals(Object r)
	{
		if (!(r instanceof Types))
		{
			return false;
		}
		Types u = (Types) r;
		if (u.theTypeName == null)
		{
			return theTypeName == null;
		}
		if (theTypeName == null)
		{
			return false;
		}
		return theTypeName.equals(u.theTypeName);
	}

}
