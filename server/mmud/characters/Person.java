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
import java.io.StringReader;
import java.util.Hashtable;

import simkin.*;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;
import mmud.commands.Command;

/**
 * Class containing all the information of a person in the game. (Not
 * necessarily a user playing)
 */
public class Person implements Executable, AttributeContainer
{
	private String theName;
	private Room theRoom;
	private String theTitle;
	private String theRace;
	private Sex theSex;
	private String theAge;
	private String theLength;
	private String theWidth;
	private String theComplexion;
	private String theEyes;
	private String theFace;
	private String theHair;
	private String theBeard;
	private String theArms;
	private String theLegs;
	private File theLogFile;
	private boolean theSleep;
	private int theWhimpy = Constants.DEFAULT_WHIMPY;
	private int theDrinkstats = Constants.DEFAULT_DRINK;
	private int theEatstats = Constants.DEFAULT_EAT;
	private int theLevel = Constants.DEFAULT_LEVEL;
	private int theHealth = Constants.DEFAULT_HEALTH;
	private int theAlignment = Constants.DEFAULT_ALIGNMENT;
	private int theMovement = Constants.DEFAULT_MOVEMENT;
	private TreeMap theAttributes = new TreeMap();

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
	public Person(String aName, 
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
		theName = aName;
		theRoom = aRoom;
		theTitle = aTitle;
		theRace = aRace;
		theSex = aSex;
		theAge = aAge;
		theLength = aLength;
		theWidth = aWidth;
		theComplexion = aComplexion;
		theEyes = aEyes;
		theFace = aFace;
		theHair = aHair;
		theBeard = aBeard;
		theArms = aArms;
		theLegs = aLegs;
		theSleep = aSleep;
		theWhimpy = aWhimpy;
		theDrinkstats = aDrinkstats;
		theEatstats = aEatstats;
		theLevel = aLevel;
		theHealth = aHealth;
		theAlignment = anAlignment;
		theMovement = aMovement;
		theLogFile = new File(Constants.mudfilepath, aName + ".log");
		createLog();
	}

	/**
	 * standard tostring implementation.
	 * @return a String containing the name.
	 */
	public String toString()
	{
		return theName;
	}

	/**
	 * returns the name of the character.
	 * @return String containing the name
	 */
	public String getName()
	{
		return theName;
	}

	/**
	 * returns the title of the character.
	 * @return String containing the title
	 */
	public String getTitle()
	{
		return theTitle;
	}

	/**
	 * Returns the health of the character.
	 * @return integer between 0 (dead) and 1000 (excellent health)
	 */
	public int getHealth()
	{
		return theHealth;
	}

	/**
	 * Returns the level of the character.
	 * @return integer.
	 */
	public int getLevel()
	{
		return theLevel / 1000;
	}

	/**
	 * Returns the experience of the character.
	 * @return integer between 0 and 1000. The closer to 1000 is
	 * the closer to the next level.
	 */
	public int getExperience()
	{
		return theLevel % 1000;
	}

	/**
	 * Returns the health of the character.
	 * @return String between "dead" and "excellent health"
	 */
	public String getHealthDesc()
	{
		int i = Constants.health.length - 1;
		Logger.getLogger("mmud").finer("health=" + theHealth + 
			",arrayindex=" + theHealth / (1000 / i) +
			",arraylength=" + Constants.health.length);
		return Constants.health[theHealth / (1000 / i)];
	}

	/**
	 * Returns the movement of the character.
	 * @return integer between 0 (fully exhausted) 
	 * and 1000 (not tired)
	 */
	public int getMovement()
	{
		return theMovement;
	}

	/**
	 * Returns the movement of the character.
	 * @return String between "fully exhausted" and "not tired"
	 */
	public String getMovementDesc()
	{
		Logger.getLogger("mmud").finer("movement=" + theMovement + 
		", movement.length=" + Constants.movement.length);
		return Constants.movement[theMovement / 1000 * (Constants.movement.length-1)];
	}

	/**
	 * Returns the alignment of the character.
	 * @return integer between 0 (evil) 
	 * and 8 (good)
	 */
	public int getAlignment()
	{
		Logger.getLogger("mmud").finer("returns " + theAlignment); 
		return theAlignment;
	}

	/**
	 * Returns the alignment of the character.
	 * @return String between "evil" and "good"
	 */
	public String getAlignmentDesc()
	{
		Logger.getLogger("mmud").
		finer("returns Constants.alignment[" + theAlignment + "]"); 
		return Constants.alignment[theAlignment];
	}

	/**
	 * sets the title of the character.
	 * @param aNewTitle String containing the title
	 */
	public void setTitle(String aNewTitle)
	{
		theTitle = aNewTitle;
		Database.setTitle(this);
	}

	/**
	 * get the setting for when to flee the fight.
	 * @return integer containing the setting
	 */
	public int getWhimpy()
	{
		return theWhimpy;
	}

	/**
	 * get whimpy desription. Returns an empty string if 
	 * the player is not whimpy at all. (i.e. if the 
	 * player does not wish to flee ever)
	 * @return string containing the setting
	 */
	public String getWhimpyDesc()
	{
		return Constants.whimpy[theWhimpy / 10];
	}

	/**
	 * sets the whimpy of the character.
	 * @param aWhimpy Integer containing the whimpy
	 */
	public void setWhimpy(int aWhimpy)
	{
		theWhimpy = aWhimpy;
		Database.setWhimpy(this);
	}

	/**
	 * Returns the thirst of the character
	 * @return integer containing thirst.
	 */
	public int getDrinkstats()
	{
		return theDrinkstats;
	}

	/**
	 * returns a description of the thirst of the character.
	 * @return String containing the description
	 */
	public String getDrinkstatsDesc()
	{
		int i = theDrinkstats;
		if (i < -59) {
					return "You are out of your skull on alcohol.<BR>";
		}
		if (i < -49) {
					return "You are very drunk.<BR>";
		}
		if (i < -39) {
					return "You are drunk.<BR>";
		}
		if (i < -29) {
					return "You are pissed.<BR>";
		}
		if (i < -19) {
					return "You are a little drunk.<BR>";
		}
		if (i < -9) {
					return "You have a headache.<BR>";
		}
		if (i < 9) {
					return "You are thirsty.<BR>";
		}
		if (i < 19) {
					return "You can drink a whole lot more.<BR>";
		}
		if (i < 29) {
					return "You can drink a lot more.<BR>";
		}
		if (i < 39) {
					return "You can drink some.<BR>";
		}
		if (i < 49) {
					return "You can drink a little more.<BR>";
		}
		return "You cannot drink anymore.<BR>";
	}

	/**
	 * returns wether or not the person can still drink something.
	 * @return boolean, true if it is possible still to drink.
	 */
	public boolean canDrink()
	{
		return (( theDrinkstats < 49) && ( theDrinkstats >= 0 ));
	}

	/**
	 * sets the thirst of the character.
	 * @param i Integer containing the thirst
	 */
	public void setDrinkstats(int i)
	{
		if (!canDrink()) return;
		theDrinkstats = i;
		Database.setDrinkstats(this);
	}

	/**
	 * returns the integer providing hunger of the character.
	 * @return integer containing the hunger
	 */
	public int getEatstats()
	{
		return theEatstats;
	}

	/**
	 * returns the hunger description of the character.
	 * @return String containing the hunger
	 */
	public String getEatstatsDesc()
	{
		int i = theEatstats;
		if (i < 9) {
			return "You are hungry.<BR>";
		}
		if (i < 19) {
			return "You can eat a whole lot more.<BR>";
		}
		if (i < 29) {
			return "You can eat a lot more.<BR>";
		}
		if (i < 39) {
			return "You can eat some.<BR>";
		}
		if (i < 49) {
			return "You can only eat a little more.<BR>";
		}
		return "You are full.<BR>";
	}

	/**
	 * returns wether or not the person can still eat something.
	 * @return boolean, true if it is possible still to eat.
	 */
	public boolean canEat()
	{
		return (( theEatstats < 49) && ( theEatstats >= 0 ));
	}

	/**
	 * sets the hunger of the character.
	 * @param i Integer containing the hunger
	 */
	public void setEatstats(int i)
	{
		if (!canEat()) return;
		theEatstats = i;
		Database.setEatstats(this);
	}

	/**
	 * returns the race of the character.
	 * @return String containing the race
	 */
	public String getRace()
	{
		return theRace;
	}

	/**
	 * returns what gender the character is.
	 * @return Sex containing either male or female.
	 */
	public Sex getSex()
	{
		return theSex;
	}

	/**
	 * returns the age of the character.
	 * @return String containing the age
	 */
	public String getAge()
	{
		return theAge;
	}

	/**
	 * returns the length of the character.
	 * @return String containing the length
	 */
	public String getLength()
	{
		return theLength;
	}

	/**
	 * returns the width of the character.
	 * @return String containing the width
	 */
	public String getWidth()
	{
		return theWidth;
	}

	/**
	 * returns the complexion of the character.
	 * @return String containing the complexion
	 */
	public String getComplexion()
	{
		return theComplexion;
	}

	/**
	 * returns the eyes of the character.
	 * @return String containing the eyes
	 */
	public String getEyes()
	{
		return theEyes;
	}

	/**
	 * returns the face of the character.
	 * @return String containing the face
	 */
	public String getFace()
	{
		return theFace;
	}

	/**
	 * returns the hair of the character.
	 * @return String containing the hair
	 */
	public String getHair()
	{
		return theHair;
	}

	/**
	 * returns the beard of the character.
	 * @return String containing the beard
	 */
	public String getBeard()
	{
		return theBeard;
	}

	/**
	 * returns the arms of the character.
	 * @return String containing the arms
	 */
	public String getArms()
	{
		return theArms;
	}

	/**
	 * returns the legs of the character.
	 * @return String containing the legs
	 */
	public String getLegs()
	{
		return theLegs;
	}

	/**
	 * returns wether or not the character is asleep.
	 * @return boolean, true if character is asleep.
	 */
	public boolean isaSleep()
	{
		return theSleep;
	}

	/**
	 * sets the sleep status of the character.
	 * @param aSleep boolean containing the sleep status
	 */
	public void setSleep(boolean aSleep)
	{
		theSleep = aSleep;
		Database.setSleep(this);
	}

	/**
	 * returns in which Room the character is.
	 * @return Room the room that the character is currently occupying.
	 */
	public Room getRoom()
	{
		return theRoom;
	}

	/**
	 * sets the room of the character.
	 * @param aRoom Room containing the current room of the character
	 */
	public void setRoom(Room aRoom)
	{
		theRoom = aRoom;
		Database.setRoom(this);
	}

	/**
	 * creates a new log file and deletes the old one.
	 */
	public void createLog()
	{
		if (theLogFile.exists())
		{
			theLogFile.delete();
		}
		try
		{
			theLogFile.createNewFile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * returns the description of the character. All characteristics, if
	 * possible, are taken into account.
	 * @return String containing the description
	 */
	public String getLongDescription()
	{
		String result = (getAge().equals("none") ? "" : getAge() + ", ");
		result += (getLength().equals("none") ? "" : getLength() + ", ");
		result += (getWidth().equals("none") ? "" : getWidth() + ", ");
		result += (getComplexion().equals("none") ? "" : getComplexion() + ", ");
		result += (getEyes().equals("none") ? "" : getEyes() + ", ");
		result += (getFace().equals("none") ? "" : getFace() + ", ");
		result += (getHair().equals("none") ? "" : getHair() + ", ");
		result += (getBeard().equals("none") ? "" : getBeard() + ", ");
		result += (getArms().equals("none") ? "" : getArms() + ", ");
		result += (getLegs().equals("none") ? "" : getLegs() + ", ");
		result += getSex() + " " + getRace() + " who calls " + getSex().indirect();
		result += "self " + getName() + " (" + getTitle() + ")";
		return result;
	}

	/**
	 * writes a message to the log file of the character that contains
	 * all communication and messages.
	 * @param aMessage the message to be written to the logfile.
	 * @see #writeMessage(Person aSource, Person aTarget, String aMessage)
	 * @see #writeMessage(Person aSource, String aMessage)
	 */
	public void writeMessage(String aMessage)
	{
		Logger.getLogger("mmud").finer("aMessage=" + aMessage);
		String message = aMessage;
		boolean check = false;
		int i = 0;
		while (!check)
		{
			if (message.charAt(i) == '<')
			{
				while (message.charAt(i) != '>') { i++; }
				i++;
			}
			if (message.charAt(i) != ' ')
			{
				check = true;
			}
			else
			{
				i++;
			}
		}
		message = message.substring(0, i) + 
			message.substring(i, i + 1).toUpperCase() +
			message.substring(i + 1);
		try
		{
			FileWriter myFileWriter = new FileWriter(theLogFile, true);
			myFileWriter.write(message, 0, message.length());
			myFileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
	 * @see #writeMessage(String aMessage)
	 * @see #writeMessage(Person aSource, String aMessage)
	 */
	public void writeMessage(Person aSource, Person aTarget, String aMessage)
	{
		Logger.getLogger("mmud").finer("aMessage=" + aMessage);
		String message = aMessage;
		if (aTarget == this)
		{
			message = message.replaceAll("%TNAMESELF", "yourself");
			message = message.replaceAll("%TNAME", "you");
			message = message.replaceAll("%THISHER", "your");
			message = message.replaceAll("%THIMHER", "you");
			message = message.replaceAll("%THESHE", "you");
			message = message.replaceAll("%TISARE", "are");
			message = message.replaceAll("%THASHAVE", "have");
			message = message.replaceAll("%TYOUPOSS", "your");
		}
		else
		{
			message = message.replaceAll("%TNAMESELF", aTarget.getName());
			message = message.replaceAll("%TNAME", aTarget.getName());
			message = message.replaceAll("%THISHER", (aTarget.getSex().posession()));
			message = message.replaceAll("%THIMHER", (aTarget.getSex().indirect()));
			message = message.replaceAll("%THESHE", (aTarget.getSex().direct()));
			message = message.replaceAll("%TISARE", "is");
			message = message.replaceAll("%THASHAVE", "has");
			message = message.replaceAll("%TYOUPOSS", aTarget.getName() + "s");
		}
		writeMessage(aSource, message);
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
	 * @see #writeMessage(Person aSource, Person aTarget, String aMessage)
	 * @see #writeMessage(String aMessage)
	 */
	public void writeMessage(Person aSource, String aMessage)
	{
		Logger.getLogger("mmud").finer("aMessage=" + aMessage);
		String message = aMessage;
		if (aSource == this)
		{
			message = message.replaceAll("%SNAMESELF", "yourself");
			message = message.replaceAll("%SNAME", "you");
			message = message.replaceAll("%SHISHER", "your");
			message = message.replaceAll("%SHIMHER", "you");
			message = message.replaceAll("%SHESHE", "you");
			message = message.replaceAll("%SISARE", "are");
			message = message.replaceAll("%SHASHAVE", "have");
			message = message.replaceAll("%SYOUPOSS", "your");
			message = message.replaceAll("%VERB1", "");
			message = message.replaceAll("%VERB2", "");
		}
		else
		{
			message = message.replaceAll("%SNAMESELF", aSource.getName());
			message = message.replaceAll("%SNAME", aSource.getName());
			message = message.replaceAll("%SHISHER", (aSource.getSex().posession()));
			message = message.replaceAll("%SHIMHER", (aSource.getSex().indirect()));
			message = message.replaceAll("%SHESHE", (aSource.getSex().direct()));
			message = message.replaceAll("%SISARE", "is");
			message = message.replaceAll("%SHASHAVE", "has");
			message = message.replaceAll("%SYOUPOSS", aSource.getName() + "s");
			message = message.replaceAll("%VERB1", "es");
			message = message.replaceAll("%VERB2", "s");
		}
		writeMessage(message);
	}

	/**
	 * read the entire log,
	 * @return String containing the entire logfile.
	 */
	public String readLog()
	{
		Logger.getLogger("mmud").finer("");
		try
		{
			return Constants.readFile(theLogFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set the attribute. Adds the attribute, if the attribute does not
	 * exist yet.
	 * @param anAttribute the attribute to be set.
	 */
	public void setAttribute(Attribute anAttribute)
	{
		theAttributes.put(anAttribute.getName(), anAttribute);
		AttributeDb.setAttribute(anAttribute, this);
	}

	/**
	 * returns the attribute found with name aName or null
	 * if it does not exist.
	 * @param aName the string with the name to search for
	 * @return Attribute with the specific name
	 */
	public Attribute getAttribute(String aName)
	{
		Attribute myAttrib = (Attribute) theAttributes.get(aName);
		return myAttrib;
	}

	/**
	 * Remove an attribute with a specific name from the list of
	 * attributes.
	 * @param aName the string with the name of the attribute to be
	 * removed.
	 */
	public void removeAttribute(String aName)
	{
		theAttributes.remove(aName);
		AttributeDb.removeAttribute(new Attribute(aName, null, null), this);
	}

	/**
	 * returns true if the attribute with name aName
	 * exists.
	 * @param aName the name of the attribute to search for
	 * @return boolean, true if found otherwise false
	 */
	public boolean isAttribute(String aName)
	{
		return theAttributes.containsKey(aName);
	}

	/**
	 * Returns a string describing the persons inventory
	 * @return a string containing a html list.
	 */
	public String inventory()
	{
		return ItemsDb.getInventory(this);
	}

	/**
	 * Retrieve items from this character.
	 * @param adject1 the first adjective
	 * @param adject2 the second adjective
	 * @param adject3 the third adjective
	 * @param name the name of the item
	 * @return Vector containing item objects found.
	 * @see mmud.database.ItemsDb#getItemsFromChar
	 */
	public Vector getItems(String adject1, String adject2, String adject3, String name) 
	{
		return ItemsDb.getItemsFromChar(adject1, adject2, adject3, name, this);
	}

	/**
	 * Display statistics .
	 * @return String containing all the statistics in html format.
	 */
	public String getStatistics()
	{
		String stuff = ItemsDb.getWearablesFromChar(this);
		stuff = stuff.replaceAll("%SHISHER", "your");
		String whimpy = ("".equals(getWhimpyDesc()) ? 
		"You are not whimpy at all.<BR>" :
		"You will flee when you are " + getWhimpyDesc() + ".<BR>");
		return "A " +
		getLongDescription() + 
		".<BR>You seem to be " +
		getHealthDesc() + ".<BR>You are " +
		getMovementDesc() + ".<BR>" +
		getDrinkstatsDesc() + 
		getEatstatsDesc() + "You are " +
//		ShowBurden
		getAlignmentDesc() + ".<BR>" +
		"You are level " + getLevel() + " and " + (1000-getExperience()) + 
		" experience points away from levelling.<BR>" + whimpy
		 +
		stuff;
//		Skill

	}

	/**
	 * Executes a script with this person as the focus point.
	 * @param aScript a String containing the script to execute. The
	 * following commands in the script are possible:
	 * <ul>
	 * <li>sendMessage(&lt;message&gt;);
	 * <li>sendMessage(&lt;person&gt;, &lt;message&gt;);
	 * <li>sendMessageExcl(&lt;message&gt;);
	 * <li>sendMessageExcl(&lt;person&gt;, &lt;message&gt;);
	 * <li>personal(&lt;message&gt;);
	 * </ul>
	 * The following fields in the script are available:
	 * <ul>
	 * <li>room
	 * <li>sex
	 * <li>name
	 * </ul>
	 * @param aXmlMethodName the name of the method in the xml
	 * script that you wish to execute.
	 * @param aCommandArray an array of Strings, that contain the
	 * different words of the command executed. If it is 
	 * a null value, the scripted method will not be called with this array.
	 * This also means that the method in the xml script needs
	 * to either have this parameter set, or not set in the
	 * declaration.
	 * @see <A HREF="http://www.simkin.co.uk">Simkin</A>
	 * @throws MudException if something goes wrong.
	 */ 
	public Object runScript(String aXmlMethodName, String aScript, 
		String[] aCommandArray)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		try
		{
			// Create an interpreter and a context
			Interpreter interp=new Interpreter();
			interp.addGlobalVariable("rooms", Rooms.create());
			interp.addGlobalVariable("persons", Persons.create());
			ExecutableContext ctxt=new ExecutableContext(interp);
	
			// create an XMLExecutable object with the xml string
			XMLExecutable executable = 
				new XMLExecutable(getName(), new StringReader(aScript));
	
			// call the "main" method with the person as an argument
			// or with the person as well as the command (split into
			// different words in the array.)
			if (aCommandArray == null)
			{
				Object args[]= { this } ;
				return executable.method(aXmlMethodName, args, ctxt);
			}
			Object args[]= { this, aCommandArray };
			return executable.method(aXmlMethodName, args, ctxt);
		}
		catch (simkin.ParseException aParseException)
		{
			System.out.println("Unable to parse command.");
			aParseException.printStackTrace();
			throw new MudException("Unable to parse command.", aParseException);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new MudException("Unable to run script.", e);
		}
	}

	/**
	 * Executes a script with this person as the focus point.
	 * @param aScript a String containing the script to execute.
	 * @param aXmlMethodName the name of the method in the xml
	 * script that you wish to execute.
	 * @see <A HREF="http://www.simkin.co.uk">Simkin</A>
	 * @see #runScript(String aXmlMethodName,String aScript,String[]
	 * aCommandArray)
	 * @throws MudException if something goes wrong.
	 */ 
	public Object runScript(String aXmlMethodName, String aScript)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		return runScript(aXmlMethodName, aScript, null);
	}
	 
	public void setValue(String field_name, String attrib_name,
		Object value, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name + ", value=" + 
			value + "[" + value.getClass() + "]");
		if (field_name.equals("room"))
		{
			if (value instanceof Null)
			{
				throw new FieldNotSupportedException(field_name + " may not be set to null.");
			}
			if (value instanceof Room)
			{
				setRoom((Room) value);
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not room.");
		}
		throw new FieldNotSupportedException(field_name + " not found.");
	}

	public void setValueAt(Object array_index, 
		String attrib_name, 
		Object value, ExecutableContext ctxt)
	{
		Logger.getLogger("mmud").finer("array_index=" + array_index +
			", atttrib_name=" + attrib_name + ", value=" + 
			value);
	}

	public ExecutableIterator createIterator()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}

	public ExecutableIterator createIterator(String qualifier)
	{
		Logger.getLogger("mmud").finer("qualifier=" + qualifier);
		return createIterator();
	}

	public Hashtable getAttributes()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}

	public Hashtable getInstanceVariables()
	{
		Logger.getLogger("mmud").finer("");
		return null;
	}

	public String getSource(String location)
	{
		Logger.getLogger("mmud").finer("location=" + location);
		return null;
	}

	public Object getValue(String field_name, String
		attrib_name, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name);
		if (field_name.equals("room"))
		{
			return getRoom();
		}
		if (field_name.equals("name"))
		{
			return getName();
		}
		if (field_name.equals("sex"))
		{
			return getSex().toString();
		}
		throw new FieldNotSupportedException(field_name + " not found.");
	}

	public Object getValueAt(Object array_index,
		String attrib_name, ExecutableContext ctxt)
	{
		Logger.getLogger("mmud").finer("array_index=" + array_index +
			", atttrib_name=" + attrib_name);
		return null;
	}

	public Object method(String method_name, Object[]
		arguments, ExecutableContext ctxt)
	throws MethodNotSupportedException
	{
		Logger.getLogger("mmud").finer("method_name=" + method_name +
			", arguments=" + arguments);
		if (method_name.equals("sendMessage"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a String as argument.");
				}
				Persons.sendMessage(this, (String) arguments[0]);
				return null;
			}
			if (arguments.length == 2)
			{
				if (!(arguments[0] instanceof Person))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a (Person,String) as argument.");
				}
				if (!(arguments[1] instanceof String))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a (Person,String) as argument.");
				}
				Persons.sendMessage(this, (Person) arguments[0], 
					(String) arguments[1]);
				return null;
			}
		}
		if (method_name.equals("sendMessageExcl"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a String as argument.");
				}
				Persons.sendMessageExcl(this, (String) arguments[0]);
			}
			if (arguments.length == 2)
			{
				if (!(arguments[0] instanceof Person))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a (Person,String) as argument.");
				}
				if (!(arguments[1] instanceof String))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a (Person,String) as argument.");
				}
				Persons.sendMessageExcl(this, (Person) arguments[0], 
					(String) arguments[1]);
				return null;
			}
		}
		if (method_name.equals("personal"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a String as argument.");
				}
				writeMessage((String) arguments[0]);
				return null;
			}
		}
		if (method_name.equals("addItem"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof Integer))
				{
					throw new MethodNotSupportedException(method_name + 
						" does not contain a Integer as argument.");
				}
				ItemDef myItemDef = ItemDefs.getItemDef(
					((Integer) arguments[0]).intValue());
				if (myItemDef == null)
				{
					throw new MethodNotSupportedException(method_name + " tried to use an unknown item definition.");
				}
				Item myItem = ItemsDb.addItem(myItemDef);
				try
				{
					ItemsDb.addItemToChar(myItem, this);
				}
				catch (ItemDoesNotExistException e)
				{
					throw new MethodNotSupportedException(e.getMessage());
				}
				Database.writeLog("root", "created item (" + myItem + ") for person " +
					getName());
				return myItem;
			}
		}
//		throw new MethodNotSupportedException(method_name + " not found.");
		return methodAttribute(method_name, arguments, ctxt);
	}

	public Object methodAttribute(String method_name, Object[]
		arguments, ExecutableContext ctxt)
	throws MethodNotSupportedException
	{
		Logger.getLogger("mmud").finer("method_name=" + method_name +
			", arguments=" + arguments);
		if (method_name.equals("getAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as argument.");
				}
				Attribute mAttrib = getAttribute((String) arguments[0]);
				if (mAttrib == null)
				{
					return null;
				}
				if (mAttrib.getValueType().equals("string"))
				{
					return mAttrib.getValue();
				}
				if (mAttrib.getValueType().equals("boolean"))
				{
					return new Boolean(mAttrib.getValue());
				}
				if (mAttrib.getValueType().equals("integer"))
				{
					try
					{
						return new Integer(mAttrib.getValue());
					}
					catch (NumberFormatException e)
					{
						throw new MethodNotSupportedException(method_name +
						" attribute " + mAttrib.getName() + " does not contain expected number.");
					}
				}
				throw new MethodNotSupportedException(method_name +
					" unknown value type in attribute " + 
					mAttrib.getName() + ". (" + 
					mAttrib.getValueType() + ")");
			}
		}
		if (method_name.equals("removeAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as argument.");
				}
				removeAttribute((String) arguments[0]);
				Database.writeLog("root", "removed attribute (" + arguments[0] + ") from person " +
					getName());
				return null;
			}
		}
		if (method_name.equals("setAttribute"))
		{
			if (arguments.length == 2)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as first argument.");
				}
				String mType = "object";
				if (arguments[1] instanceof String)
				{
					mType = "string";
				}
				if (arguments[1] instanceof Integer)
				{
					mType = "integer";
				}
				if (arguments[1] instanceof Boolean)
				{
					mType = "boolean";
				}
				Attribute mAttrib = new Attribute((String) arguments[0],
					arguments[1] + "", 
					mType);
				setAttribute(mAttrib);
				Database.writeLog("root", "set attribute (" + arguments[0] + ") for person " +
					getName());
				return null;
			}
		}
		if (method_name.equals("isAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as first argument.");
				}
				return new Boolean(isAttribute((String) arguments[0]));
			}
		}
		throw new MethodNotSupportedException(method_name + " not found.");
	}

}
