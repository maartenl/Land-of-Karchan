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

import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader; 
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import mmud.database.*;
import mmud.characters.*;
import mmud.rooms.*;
import mmud.commands.*;
import mmud.Sex;
import mmud.Constants;
import mmud.MudException;

/**
 * Class containing all the information of a user connecting to the game.
 */
public class User extends mmud.characters.Person
{

	private String thePassword;
	private String theAddress;
	private String theSessionPassword;

	private String theRealName;
	private String theEmail;

	private int theFrames;
	private boolean theGod;
	private Calendar rightNow;
	private boolean thePkill;

	/**
	 * create an instance of User, based on Database data. 
	 * @param aName the name of the character
	 * @param aPassword the password of the character
	 * @param anAddress the address of the computer connecting
	 * @param aRealName the real name of the person behind the keyboard.
	 * @param aEmail an email address of the person
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
	 * @param aCookie the sessionpassword
	 * @param aWhimpy the boundary of the general condition of the
	 * character. If the condition worsens beyond this, the character will
	 * automatically flee from a fight.
	 * @param boolean aPkill wether or not this character is allowed
	 * to kill other players or be killed by other players.
	 * @param aDrinkstats describes the state of thirst, negative values
	 * usually mean intoxication.
	 * @param aEatstats describes the state of nourishment/hunger
	 * @param aLevel describes the level of the character.
	 * The level is level/1000. The experience is level%1000.
	 * @param aHealth describes the state of health. 0 is dead, 1000 is
	 * excellent health.
	 * @param aAlignment describes the alignment of the character.
	 * -90 is evil, 90 is good.
	 * @param aMovement describes the amount of movement left. 0 is
	 * no more movement possible, rest needed. 1000 is excellent movement.
	 * @param aRoom the room where this character is.
	 * @param aGod boolean, indicates wether or not the User has special
	 * privileges; also known as <I>Administrator</I>.
	 */
	public User(String aName, 
		String aPassword, 
		String anAddress, 
		String aRealName,
		String aEmail,
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
		Room aRoom)
	{
		super(aName, aTitle,
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
			aAlignment,
			aMovement,
			aRoom);
		Logger.getLogger("mmud").finer("");
		thePassword = aPassword;
		theAddress = anAddress;
		theEmail = aEmail;
		theRealName = aRealName;
		theGod = aGod;
		setSessionPassword(aCookie);
		thePkill = aPkill;
		setNow();
	}

	/**
	 * create an instance of User. Usually used if you need a standard User
	 * created. Use the other constructor when you need to load a User
	 * from the database.
	 * @param aName the name of the character
	 * @param aPassword the password of the character
	 * @param anAddress the address of the computer connecting
	 * @param aRealName the real name of the person behind the keyboard.
	 * @param aEmail an email address of the person
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
	 * @param aCookie the sessionpassword
	 */
	public User(String aName, 
		String aPassword, 
		String anAddress, 
		String aRealName,
		String aEmail,
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
		String aCookie)
	{
		super(aName, aTitle,
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
			false,
			0,
			0, // drinkstats
			0, // eatstats
			0, // level
			1000, // health
			0, // alignment
			1000, // movement
			Rooms.getRoom(1));
		Logger.getLogger("mmud").finer("");
		thePassword = aPassword;
		theAddress = anAddress;
		theEmail = aEmail;
		theRealName = aRealName;
		theGod = false;
		thePkill = true;
		setSessionPassword(aCookie);
		setNow();
	}

	/**
	 * Returns the password
	 * @return String containing password
	 */
	public String getPassword()
	{
		Logger.getLogger("mmud").finer("returns " + thePassword);
		return thePassword;
	}

	/**
	 * Sets the password. The password can only be set, if it has not been
	 * previously set, i.e. when the password provided in the constructor
	 * is the null value.
	 * @param aPassword String containing password
	 */
	public void setPassword(String aPassword)
	{
		Logger.getLogger("mmud").finer("");
		if (thePassword != null)
		{
			throw new RuntimeException("Password has already been set.");
		}
		thePassword = aPassword;
	}

	/**
	 * Verifies the password
	 * @return boolean, returns true if password provided is an exact
	 * match.
	 * @param aPassword the password that needs to be verified.
	 */
	public boolean verifyPassword(String aPassword)
	{
		Logger.getLogger("mmud").finer("");
		return (thePassword != null && thePassword.equals(aPassword));
	}

	/**
	 * Returns the address
	 * @return String containing the address
	 */
	public String getAddress()
	{
		Logger.getLogger("mmud").finer("");
		return theAddress;
	}

	/**
	 * generate a session password to be used by player during game session
	 * Use getSessionPassword to return a String containing 25 random
	 * digits, capitals and smallcaps.
	 */
	public void generateSessionPassword()
	{
		Logger.getLogger("mmud").finer("");
		char[] myCharArray =
			{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
			'1','2','3','4','5','6','7','8','9','0'};
		StringBuffer myString = new StringBuffer(26);
		Random myRandom = new Random();
		for (int i=1; i < 26; i++)
		{
			
			myString.append(myCharArray[ myRandom.nextInt(myCharArray.length) ] );
		}
		Database.setSessionPassword(getName(), myString.toString());
		theSessionPassword = myString.toString();
	}

	/**
	 * retrieve sessionpassword
	 * @return String containing the session password
	 */
	public String getSessionPassword()
	{
		Logger.getLogger("mmud").finer("");
		return theSessionPassword;
	}

	/**
	 * verify sessionpassword
	 * @param aSessionPassword the sessionpassword to be verified.
	 * @return boolean, true if the sessionpassword provided is an exact
	 * match with the original sessionpassword.
	 */
	public boolean verifySessionPassword(String aSessionPassword)
	{
		Logger.getLogger("mmud").finer("");
		return (theSessionPassword == null ? false : theSessionPassword.equals(aSessionPassword));
	}

	/**
	 * set sessionpassword
	 * @param aSessionPassword sets the session password.
	 */
	public void setSessionPassword(String aSessionPassword)
	{
		Logger.getLogger("mmud").finer("");
		theSessionPassword =
			(aSessionPassword.trim().equals("") ? null : aSessionPassword);
	}

	/**
	 * retrieve the frame number.
	 * @return integer,
	 * <UL><LI>0 = normal operation
	 * <LI>1 = operation with frames
	 * <LI>2 = operation with frames and server push
	 * </UL>
	 */
	public int getFrames()
	{
		Logger.getLogger("mmud").finer("");
		return theFrames;
	}

	/**
	 * set the frame number to be used.
	 * @param i 
	 * <UL><LI>0 = normal operation
	 * <LI>1 = operation with frames
	 * <LI>2 = operation with frames and server push
	 * </UL>
	 */
	public void setFrames(int i)
	{
		Logger.getLogger("mmud").finer("");
		theFrames  = i;
	}

	/**
	 * Is this character an administrator?
	 * @return boolean, true if administrator
	 */
	public boolean isGod()
	{
		Logger.getLogger("mmud").finer("");
		return theGod;
	}

	/**
	 * Get the real name of the person playing.
	 * @return String containing the real name.
	 */
	public String getRealname()
	{
		return theRealName;
	}

	/**
	 * Get the email of the person playing.
	 * @return String containing the email address
	 */
	public String getEmail()
	{
		return theEmail;
	}

	/**
	 * Is the player allowed to kill other players or be killed.
	 * @return boolean, true if playerkill is allowed.
	 */
	public boolean isPkill()
	{
		return thePkill;
	}

	/**
	 * Set the pkill of the player.
	 * @param newPkill the new value of the pkill status.
	 */
	public void setPkill(boolean newPkill)
	{
		thePkill = newPkill;
	}

	/**
	 * print the standard form for accepting input from the user.
	 * @return String containing the form to be printed on the browser
	 * screen.
	 */
	public String printForm()
	{
		Logger.getLogger("mmud").finer("");
		String myString = "";
	    if (getFrames() == 0)
    	{
			myString = "<SCRIPT language=\"JavaScript\">\r\n"+
				"<!-- In hiding!\r\n"+
				"function setfocus() {\r\n"+
				"	   document.CommandForm.command.focus();\r\n"+
				"   return;\r\n"+
				"   }\r\n"+
				"//-->\r\n"+
				"</SCRIPT>\r\n";
			myString += "<FORM METHOD=\"POST\" ACTION=\"" + Constants.mudcgi + "\" NAME=\"CommandForm\">\n";
			myString += "<INPUT TYPE=\"text\" NAME=\"command\" VALUE=\"\" SIZE=\"50\"><P>\n";
			myString += "<INPUT TYPE=\"hidden\" NAME=\"name\" VALUE=\"" + getName() + "\">\n";
			myString += "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"1\">\n";
			myString += "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n";
			myString += "</FORM><P>\n";
		}
		return myString;
	}

	/**
	 * Get the date/time of the last command the user executed.
	 * Used for computing the idle time of the user.
	 * @return Calendar containing the date/time when the last command
	 * was executed.
	 */
	public Calendar getNow()
	{
		if (rightNow == null) 
		{
			setNow();
		}
		return rightNow;
	}

	/**
	 * Set the date/time of the user to "now". It means that this is 
	 * a good indication of when the user was last active.
	 */
	public void setNow()
	{
		Logger.getLogger("mmud").finer("");
		rightNow = Calendar.getInstance();
	}

	/**
	 * Returns the time that a user was inactive.
	 * @return String the number of minutes and seconds that the player
	 * has been idle in the following format: "(<min> min, <sec> sec
	 * idle".
	 */
	public String getIdleTime()
	{
		Logger.getLogger("mmud").finer("");
		Calendar tempNow = Calendar.getInstance();
		long myTemp = (tempNow.getTimeInMillis() - getNow().getTimeInMillis())/1000;
		return "(" + myTemp / 60 + " min, " + myTemp % 60 + " sec idle)";
	}

	/**
	 * Check to see if the User was inactive for more than an hour.
	 * @return boolean, true if the User was inactive (i.e. has not entered
	 * a command) for more than an hour.
	 */
	public boolean isIdleTooLong()
	{
//		Logger.getLogger("mmud").finer("");
		Calendar tempNow = Calendar.getInstance();
		long myTemp = (tempNow.getTimeInMillis() - getNow().getTimeInMillis())/1000;
		return myTemp > 3600;
	}

	/**
	 * Get an url containing the name, command and frames.
	 * @param aCommand the command that has to be changed
	 * into a valid URL.
	 * @return String containing an url in the format:
	 * "/cgi-bin/mud.cgi?command=<command>&name=<name>&frames=<frames>"
	 */
	public String getUrl(String aCommand)
	{
		Logger.getLogger("mmud").finer("");
		return Constants.mudcgi + "?command=" + aCommand + "&name=" +
			getName() + "&frames=" +
			(getFrames() + 1);
	}

	/**
	 * Returns a list of mudmails.
	 * @return String containing list of mails.
	 * @see mmud.database.MailDb#getListOfMail
	 */
	public String getListOfMail()
	{
		Logger.getLogger("mmud").finer("");
		return MailDb.getListOfMail(this);
	}

	/**
	 * Reads the log of the user from file. Returns an empty string
	*  if the frame setting is 2.
	 * @return String containing the entire log of the user.
	 */
	public String readLog()
	{
		if (getFrames()==2)
		{
			return "";
		}
		return super.readLog();
	}

	/**
	 * Runs a specific command.
	 * @param aCommand the command to be run
	 */
	public void runCommand(Command aCommand)
	throws MudException
	{
		aCommand.run(this);
	}

	/**
	 * Runs a specific command. If this person appears to be asleep,
	 * the only possible commands are "quit" and "awaken". If the command
	 * found returns a false, i.e. nothing is executed, the method will call
	 * the standard BogusCommand.
	 * @param aCommand the command to be run
	 * @return String containing the result of the command executed.
	 */
	public String runCommand(String aCommand)
	throws MudException
	{
		Command boguscommand = new BogusCommand(".+");
		Command command = boguscommand;
		if (isaSleep())
		{
			if (aCommand.trim().equalsIgnoreCase("awaken"))
			{
				command = new AwakenCommand("awaken");
			} 
			else if (aCommand.trim().equalsIgnoreCase("quit"))
			{
				command = new QuitCommand("quit");
			}
			else
			{
				command = new AlreadyAsleepCommand(".+");
			}
		}
		else
		{
			command = Constants.getCommand(aCommand);
		}
		command.setCommand(aCommand);
		if (!command.run(this))
		{
			boguscommand.setCommand(aCommand);
			boguscommand.run(this);
			command = boguscommand;
		}
		return command.getResult();
	}
}
