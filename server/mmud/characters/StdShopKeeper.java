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

package mmud.characters;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Logger;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;
import mmud.commands.Command;

/**
 * Class containing a default shopkeeper implementation. (currently not a
 * player)
 */
public class StdShopKeeper extends Person implements ShopKeeper
{

	/**
	 * Constructor. Create a person.
	 * @param aName the name of the character
	 * @param aTitle the title of the character
	 * @param aRace the race of the character
	 * @param Sex aSex the gender of the character (male or female)
	 * @param aAge the age of the character (young, very young, old,
	 * very old, etc.)
	 * @param aLength the length of the character (ex. tall)
	 * @param aWidth the width of the character (ex. athletic)
	 * @param aComplexion the complexion of the character (ex.
	 * dark-skinned)
	 * @param aEyes the eye colour of the character (ex. blue-eyed)
	 * @param aFace the face of the character (ex. dimple-faced)
	 * @param aHair the hair of the character (ex. black-haired)
	 * @param aBeard the beard of the character (ex. with ponytail)
	 * @param aArms the arms of the character (ex. long-armed)
	 * @param aLegs the legs of the character (ex. long-legged)
	 * @param aSleep the status of the character, either asleep
	 * or awake.
	 * @param aWhimpy the boundary of the general condition of the
	 * character. If the condition worsens beyond this, the character will
	 * automatically flee from a fight.
	 * @param aDrinkstats describes the state of thirst, negative values
	 * usually mean intoxication.
	 * @param aEatstats describes the state of nourishment/hunger
	 * @param aRoom the room where this character is.
	 */
	public StdShopKeeper(String aName, 
		String aTitle,
		String aRace,
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
		Room aRoom)
	{
		super(aName, 
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
			aRoom);
	}

	// see interface description
	public boolean isSellable(Item anItem)
	{
		return false;
	}

	// see interface description
	public boolean isBuyable(Item anItem)
	{
		return false;
	}

	// see interface description
	public String getStoreList()
	{
		return "";
	}

}
