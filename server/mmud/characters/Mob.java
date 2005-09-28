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
import java.util.Vector;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;
import mmud.commands.Command;
import mmud.races.*;

/**
 * This class contains a Mob, a 
 * enemy to be defeated in the game.
 * @see User
 * @see Bot
 */
public class Mob extends Person implements CommunicationListener
{

	/**
	 * Constructor. Create a person.
	 * @param aName the name of the character
	 * @param aTitle the title of the character
	 * @param aRace the race of the character
	 * @param aSex the gender of the character (male or female)
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
	 * @param aLevel describes the level of the character.
	 * The level is level/1000. The experience is level%1000.
	 * @param aHealth describes the state of health. 0 is dead, 1000 is
	 * excellent health.
	 * @param anAlignment describes the alignment of the character.
	 * -90 is evil, 90 is good.
	 * @param aMovement describes the amount of movement left. 0 is
	 * no more movement possible, rest needed. 1000 is excellent movement.
	 * @param aRoom the room where this character is.
	 */
	public Mob(String aName, 
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
		aLevel,
		aHealth,
		anAlignment,
		aMovement,
		aRoom);
		Logger.getLogger("mmud").finer("");
	}

	public void commEvent(Person aPerson, CommType aType, String aSentence)
	{
		Logger.getLogger("mmud").finer("aType=" + aType +
			", aSentence=" + aSentence);
		if (aType == CommType.TELL)
		{
			return;
		}
		if (aType == CommType.WHISPER)
		{
			Persons.sendMessage(this, aPerson, "%SNAME say%VERB2 [to %TNAME] : Speak up! I cannot hear you!<BR>");
			return;
		}
		String anAnswer = Database.getAnswers(this, aSentence);
		if (anAnswer == null)
		{
			Persons.sendMessage(this, aPerson, "%SNAME ignore%VERB2 %TNAME.<BR>");
			Database.writeLog(aPerson.getName(), 
				"unknown comm to bot " + this.getName() +
				", sentence was \"" + aSentence + "\".");
		}
		else
		{
			Persons.sendMessage(this, aPerson, "%SNAME " + 
				aType + "%VERB2 [to %TNAME]: " + anAnswer + "<BR>");
		}
	}

	 /**
	 * Returns true, this is a mob, an enemy to be defeated for the good
	 * of the world.
	 * @return boolean true value.
	 */
	public boolean isFightable()
	{
		return true;
	}
	
}
