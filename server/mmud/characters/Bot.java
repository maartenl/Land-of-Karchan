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

/**
 * This class contains a Bot, a 
 * independant acting character in the game without control by a user.
 */
public class Bot extends Person
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
	public Bot(String aName, 
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
	}

	/**
	 * writes a message to the log file of the character that contains
	 * all communication and messages.
	 * @param aMessage the message to be written to the logfile.
	 */
	public void writeMessage(String aMessage)
	{
		super.writeMessage(aMessage);
	}

	/**
	 * writes a message to the log file of the character that contains
	 * all communication and messages.
	 * The message will be
	 * <I>interpreted</I> by replacing the following values by the following
	 * other values:
	 * <TABLE>
	 * <TR><TD><B>REPLACE</B></TD><TD><B>WITH (if target)</B></TD><TD><B>WITH (if not target)</B></TD></TR>
	 * <TR><TD>%TNAME</TD><TD>you</TD><TD>name</TD></TR>
	 * <TR><TD>%TNAMESELF</TD><TD>yourself</TD><TD>name</TD></TR>
	 * <TR><TD>%THISHER</TD><TD>your</TD><TD>his/her</TD></TR>
	 * <TR><TD>%THIMHER</TD><TD>you</TD><TD>him/her</TD></TR>
	 * <TR><TD>%THESHE</TD><TD>you</TD><TD>he/she</TD></TR>
	 * <TR><TD>%TISARE</TD><TD>are</TD><TD>is</TD></TR>
	 * <TR><TD>%THASHAVE</TD><TD>have</TD><TD>has</TD></TR>
	 * <TR><TD>%TYOUPOSS</TD><TD>your</TD><TD>name + s</TD></TR>
	 * <TR><TD></TD><TD></TD><TD></TD></TR>
	 * </TABLE>
	 * @param aMessage the message to be written to the logfile.
	 * @param aSource the source of the message, the thing originating the
	 * message.
	 * @param aTarget the target of the message, could be null if there
	 * is not target for this specific message.
	 */
	public void writeMessage(Person aSource, Person aTarget, String aMessage)
	{
		super.writeMessage(aSource, aTarget, aMessage);
	}

	/**
	 * writes a message to the log file of the character that contains
	 * all communication and messages.
	 * The message will be
	 * <I>interpreted</I> by replacing the following values by the following
	 * other values:
	 * <TABLE>
	 * <TR><TD><B>REPLACE</B></TD><TD><B>WITH (if source)</B></TD><TD><B>WITH (if not source)</B></TD></TR>
	 * <TR><TD>%SNAME</TD><TD>you</TD><TD>name</TD></TR>
	 * <TR><TD>%SNAMESELF</TD><TD>yourself</TD><TD>name</TD></TR>
	 * <TR><TD>%SHISHER</TD><TD>your</TD><TD>his/her</TD></TR>
	 * <TR><TD>%SHIMHER</TD><TD>you</TD><TD>him/her</TD></TR>
	 * <TR><TD>%SHESHE</TD><TD>you</TD><TD>he/she</TD></TR>
	 * <TR><TD>%SISARE</TD><TD>are</TD><TD>is</TD></TR>
	 * <TR><TD>%SHASHAVE</TD><TD>have</TD><TD>has</TD></TR>
	 * <TR><TD>%SYOUPOSS</TD><TD>your</TD><TD>name + s</TD></TR>
	 * <TR><TD>%VERB1</TD><TD></TD><TD>es</TD></TR>
	 * <TR><TD>%VERB2</TD><TD></TD><TD>s</TD></TR>
	 * <TR><TD></TD><TD></TD><TD></TD></TR>
	 * </TABLE>
	 * @param aMessage the message to be written to the logfile.
	 * @param aSource the source of the message, the thing originating the
	 * message.
	 */
	public void writeMessage(Person aSource, String aMessage)
	{
		super.writeMessage(aSource, aMessage);
	}


}
