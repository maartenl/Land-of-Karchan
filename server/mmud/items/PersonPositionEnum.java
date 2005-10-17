/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/items/PersonPositionEnum.java,v 1.2 2004/11/15 22:28:40 karn Exp $
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
import java.util.logging.Logger;
import java.util.Vector;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Different formats that can be used to display message boards.
 */
public class PersonPositionEnum
{
	private int enumVal;
	private String name;

	/**
	 * Recreates the constants from an integer. An integer value
	 * of 0 causes a null pointer to be returned.
	 * @param aVal the integer corresponding to the constant.
	 * @return the constant object
	 * @throws RuntimeException in case the integer provided
	 * does not correspond to any of the available objects.
	 */
	public static PersonPositionEnum get(int aVal)
	{
		if (aVal == 0)
		{
			return null;
		}
		for (int i = 0; i<list.length ; i++)
		{
			if (list[i].toInt() == aVal)
			{
				return list[i];
			}
		}
		throw new RuntimeException("value " + aVal + " does not " +
			"correspond to a PersonPositionEnum");
	}

	/**
	 * Receives an integer and checks to see that the PersonPositionEnum
	 * is a part of it.
	 * @param aVal the integer corresponding to a number of
	 * PersonPositionEnums
	 * @param aPos the PersonPositionEnum for which to check.
	 * @return boolean, true if the integer contains the
	 * PersonPositionEnum.
	 */
	public static boolean isIn(int aVal, PersonPositionEnum aPos)
	{
		if (aPos == null)
		{
			// the empty position is always a good position.
			return true;
		}
		return (aVal & aPos.toInt()) == aPos.toInt();
	}

	/**
	 * Returns true if wielding constant.
	 * @return boolean, true if the constant is a wielding constant.
	 */
	public boolean isWielding()
	{
		return (enumVal == 65536 ||
			enumVal == 131072 ||
			enumVal == 262144);
	}

	private PersonPositionEnum(int aVal, String str)
	{
		name = str;
		enumVal = aVal;
	}
	
	/**
	 * Returns the name of the format.
	 * @return format name.
	 */
	public String toString() 
	{ 
		return name; 
	}

	/**
	 * Returns the numerical representation of the format.
	 * @return identification integer.
	 */
	public int toInt() 
	{ 
		return enumVal; 
	}

	public static final PersonPositionEnum ON_HEAD = 
		new PersonPositionEnum(1, "on %SHISHER head");

	public static final PersonPositionEnum ON_NECK = 
		new PersonPositionEnum(2, "around %SHISHER neck");

	public static final PersonPositionEnum ON_TORSO = 
		new PersonPositionEnum(4, "around %SHISHER torso");

	public static final PersonPositionEnum ON_ARMS = 
		new PersonPositionEnum(8, "on %SHISHER arms");

	public static final PersonPositionEnum ON_LEFT_WRIST = 
		new PersonPositionEnum(16, "on %SHISHER left wrist");

	public static final PersonPositionEnum ON_RIGHT_WRIST = 
		new PersonPositionEnum(32, "on %SHISHER right wrist");

	public static final PersonPositionEnum ON_LEFT_FINGER = 
		new PersonPositionEnum(64, "on %SHISHER left finger");

	public static final PersonPositionEnum ON_RIGHT_FINGER = 
		new PersonPositionEnum(128, "on %SHISHER right finger");

	public static final PersonPositionEnum ON_FEET = 
		new PersonPositionEnum(256, "on %SHISHER feet");

	public static final PersonPositionEnum ON_HANDS = 
		new PersonPositionEnum(512, "on %SHISHER hands");

	public static final PersonPositionEnum FLOATING_NEARBY = 
		new PersonPositionEnum(1024, "floating nearby");

	public static final PersonPositionEnum ON_WAIST = 
		new PersonPositionEnum(2048, "on %SHISHER waist");

	public static final PersonPositionEnum ON_LEGS = 
		new PersonPositionEnum(4096, "on %SHISHER legs");

	public static final PersonPositionEnum ON_EYES = 
		new PersonPositionEnum(8192, "over %SHISHER eyes");

	public static final PersonPositionEnum ON_EARS = 
		new PersonPositionEnum(16384, "on %SHISHER ears");

	public static final PersonPositionEnum ABOUT_BODY = 
		new PersonPositionEnum(32768, "about %SHISHER body");

	public static final PersonPositionEnum WIELD_LEFT = 
		new PersonPositionEnum(65536, "with %SHISHER left hand");

	public static final PersonPositionEnum WIELD_RIGHT = 
		new PersonPositionEnum(131072, "with %SHISHER right hand");

	public static final PersonPositionEnum WIELD_BOTH = 
		new PersonPositionEnum(262144, "with both %SHISHER hands");

	private static final PersonPositionEnum[] list =
		{ON_HEAD, 
		ON_NECK, 
		ON_TORSO, 
		ON_ARMS, 
		ON_LEFT_WRIST, 
		ON_RIGHT_WRIST, 
		ON_LEFT_FINGER, 
		ON_RIGHT_FINGER, 
		ON_FEET, 
		ON_HANDS, 
		FLOATING_NEARBY, 
		ON_WAIST, 
		ON_LEGS, 
		ON_EYES, 
		ON_EARS, 
		ABOUT_BODY, 
		WIELD_LEFT, 
		WIELD_RIGHT, 
		WIELD_BOTH};

}
