/*-------------------------------------------------------------------------
svninfo: $Id: Bot.java 1005 2005-10-30 13:21:36Z maartenl $
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

package mmud.characters;

import mmud.MudException;
import mmud.Sex;
import mmud.races.Race;
import mmud.rooms.Room;

/**
 * This is a factory for the creation of appropriate 
 * Persons.
 * @see User
 * ee Person
 */
public class CharacterFactory
{

	/**
	 * Default Constructor privatised.
	 */
	public CharacterFactory()
	{
		// disable default constructor
	}

	/**
	 * Creates a new Person class and returns it.
	 * Automagically determines what kind of Person class
	 * (for example, User or Bot or Mob). Right now
	 * which class is generated is based on the aGod
	 * parameter.
	 * @param aGod an integer indicating what this person 
	 * is, <2 is a normal user, ==2 is a bot, otherwise
	 * it is a mob.
	 * @return the person.
	 */
	public static Person create(int aGod)
	throws MudException
	{
		if (aGod < 2)
		{
			return null;//new User();
		}
		if (aGod == 2)
		{
			return null;//new Bot();
		}
		return null;//new Mob();
	}

	public static User create(String aName,
	String aPassword,
	String anAddress,
	String aRealName,
	String aEmail,  
	String aTitle, 
	Race aRace,
	Sex aSex,  
	String aAge,   
	String aLength,
	String aWidth,
	String aComplexion,
	String aEyes, 
	String aFace,  
	String aHair,  
	String aBeard,  
	String aArms,  
	String aLegs, 
	boolean aSleep,  
	boolean aGod,   
	String aCookie, 
	int aWhimpy,
	boolean aPkill, 
	int aDrinkstats,
	int aEatstats,  
	int aLevel,
	int aHealth,   
	int aAlignment,
	int aMovement, 
	int aCopper,   
	Room aRoom,
	Guild aGuild)
	throws MudException
	{
		return new User(aName,
	aPassword,
	anAddress,
	aRealName,
	aEmail,  
	aTitle, 
	aRace,
	aSex,  
	aAge,   
	aLength,
	aWidth,
	aComplexion,
	aEyes, 
	aFace,  
	aHair,  
	aBeard,  
	aArms,  
	aLegs, 
	aSleep,  
	aGod,   
	aCookie, 
	aWhimpy,
	aPkill, 
	aDrinkstats,
	aEatstats,  
	aLevel,
	aHealth,   
	aAlignment,
	aMovement, 
	aCopper,   
	aRoom,
	aGuild);
	}

	public static Person create(String aName, 
		String aTitle,
		Race aRace,
		Sex aSex,
		String aAge,
		String aLength,
		String aWidth,
		String aComplexion,
		String aEyes,
		String aFace,
		String aHair,
		String aBeard,
		String aArms,
		String aLegs,   
		boolean aSleep,
		int aWhimpy,
		int aDrinkstats,
		int aEatstats,
		int aLevel,
		int aHealth,
		int anAlignment,
		int aMovement,
		int aCopper,
		Room aRoom,
		int aGod)
	throws MudException
	{
		if (aGod == 2)
		{
			return new Bot(aName,
			aTitle,
			aRace, 
			aSex,  
			aAge,  
			aLength,
			aWidth, 
			aComplexion,
			aEyes,
			aFace,
			aHair,
			aBeard,
			aArms, 
			aLegs, 
			aSleep,
			aWhimpy,
			aDrinkstats,
			aEatstats,  
			aLevel, 
			aHealth,
			anAlignment,
			aMovement,  
			aCopper,
			aRoom); 
		}
		if (aGod == 3)
		{
			return new Mob(aName,
			aTitle,
			aRace, 
			aSex,  
			aAge,  
			aLength,
			aWidth, 
			aComplexion,
			aEyes,
			aFace,
			aHair,
			aBeard,
			aArms, 
			aLegs, 
			aSleep,
			aWhimpy,
			aDrinkstats,
			aEatstats,  
			aLevel, 
			aHealth,
			anAlignment,
			aMovement,  
			aCopper,
			aRoom); 
		}
		if (aGod == 4)
		{
			return new StdShopKeeper(aName,
			aTitle,
			aRace, 
			aSex,  
			aAge,  
			aLength,
			aWidth, 
			aComplexion,
			aEyes,
			aFace,
			aHair,
			aBeard,
			aArms, 
			aLegs, 
			aSleep,
			aWhimpy,
			aDrinkstats,
			aEatstats,  
			aLevel, 
			aHealth,
			anAlignment,
			aMovement,  
			aCopper,
			aRoom); 
		}
		throw new RuntimeException("unknown god value (" + aGod + 
		"). This method does not create normal users.");
	}
}
		