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

package mmud.characters;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Logger;

import mmud.Constants;
import mmud.MudException;
import mmud.Sex;
import mmud.commands.AlreadyAsleepCommand;
import mmud.commands.AwakenCommand;
import mmud.commands.BogusCommand;
import mmud.commands.Command;
import mmud.commands.QuitCommand;
import mmud.database.Database;
import mmud.database.MailDb;
import mmud.database.MudDatabaseException;
import mmud.races.Race;
import mmud.rooms.Room;
import mmud.rooms.Rooms;
import simkin.ExecutableContext;
import simkin.FieldNotSupportedException;

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
	private String theGuild;
	private TreeMap theIsBeingIgnoredBy;

	/**
	 * create an instance of User, based on Database data. 
	 * @param aName the name of the character
	 * @param aPassword the password of the character
	 * @param anAddress the address of the computer connecting
	 * @param aRealName the real name of the person behind the keyboard.
	 * @param aEmail an email address of the person
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
	 * @param aCookie the sessionpassword
	 * @param aWhimpy the boundary of the general condition of the
	 * character. If the condition worsens beyond this, the character will
	 * automatically flee from a fight.
	 * @param aPkill wether or not this character is allowed
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
	 * @param aGuild the guild to which this user belongs. Not necessarily
	 * filled in, may be null.
	 */
	public User(String aName, 
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
			aCopper,
			aRoom);
		Logger.getLogger("mmud").finer("");
		thePassword = aPassword;
		theAddress = anAddress;
		theEmail = aEmail;
		theRealName = aRealName;
		theGod = aGod;
		setSessionPassword(aCookie);
		thePkill = aPkill;
		if (aGuild != null)
		{
			theGuild = aGuild.getName();
		}
		setNow();
	}

	/**
	 * create an instance of User. Usually used if you need a standard User
	 * created. Use the other constructor when you need to load a User
	 * from the database. The difference with the previous constructor
	 * is that a large number of fields in the object will have either
	 * default values are will be empty.
	 * @param aName the name of the character
	 * @param aPassword the password of the character
	 * @param anAddress the address of the computer connecting
	 * @param aRealName the real name of the person behind the keyboard.
	 * @param aEmail an email address of the person
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
	 * @param aCookie the sessionpassword
	 */
	public User(String aName, 
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
		String aCookie)
	throws MudException
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
-			Constants.DEFAULT_WHIMPY,
			Constants.DEFAULT_DRINK,
			Constants.DEFAULT_EAT,
			Constants.DEFAULT_LEVEL,
			Constants.DEFAULT_HEALTH,
			Constants.DEFAULT_ALIGNMENT,
			Constants.DEFAULT_MOVEMENT,
			Constants.DEFAULT_COPPER,
			Rooms.getRoom(Constants.DEFAULT_ROOM));
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
	 * Sets the address
	 * @param anAddress String containing the address
	 */
	public void setAddress(String anAddress)
	{
		Logger.getLogger("mmud").finer("");
		theAddress = anAddress;
	}

	/**
	 * generate a session password to be used by player during game session
	 * Use getSessionPassword to return a String containing 25 random
	 * digits, capitals and smallcaps.
	 */
	public void generateSessionPassword()
	throws MudException
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
		if (aSessionPassword == null)
		{
			theSessionPassword = null;
			return;
		}
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
		Logger.getLogger("mmud").finest("returns " + theFrames);
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
		Logger.getLogger("mmud").finest("aCommand=" + aCommand);
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
	throws MudDatabaseException
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
			command.setCommand(aCommand);
			command.run(this);
			return command.getResult();
		}

		Collection myCol = Constants.getCommand(aCommand);
		Iterator myI = myCol.iterator();
		while (myI.hasNext())
		{
			command = (Command) myI.next();
			command = command.createCommand();
			command.setCommand(aCommand);
			if (command.run(this))
			{
				return command.getResult();
			}
		}
		throw new MudException("We shouldn't reach this point." +
			" At least the bogus command should execute successfully." + 
			" This is probably a big bug!.");
	}

	 
	/**
	 * Returns true, this person can fight.
	 * @return boolean true value.
	 */
	public boolean isFightable()
	{
		return true;
	}

	/**
	 * Set the guild of a person. Can be set to null, incase the person
	 * is not part of a guild. Cannot be set to another guild if the
	 * person is already part of a guild. Wat I mean is, first leave the guild
	 * before joining another one!
	 * @param aGuild the guild this person belongs to.   
	 */
	public void setGuild(Guild aGuild)
	throws MudException
	{
		if( (aGuild != null) && (theGuild != null) )
		{
			throw new MudException("Person " + getName() + 
				" cannot become member of " + aGuild.getName() + 
				" because is already member of " + theGuild + ".");
		}
		if (aGuild == null)
		{
			theGuild = null;
		}
		else
		{
			theGuild = aGuild.getName();
		}
		Database.setGuild(this);
	}  

	/**
	 * Returns the guild a person belongs to.  
	 * @return the guild a person belongs to. Returns null if person
	 * does not belong to a guild.
	 */
	public Guild getGuild()
	throws MudException
	{
		return GuildFactory.createGuild(theGuild);
	}

	public String getStatistics()
	throws MudDatabaseException, MudException
	{
		String result = super.getStatistics();
		if (getGuild() != null)
		{
			if (getGuild().getBossName().equals(getName()))
			{
				result += "You are the Guildmaster of <B>" + getGuild().getTitle() + "</B>.<BR>";
			}
			else
			{
				result += "You are a member of <B>" + getGuild().getTitle() + "</B>.<BR>";
			}
		}
		return result;
	}

	public Object getValue(String field_name, String
		attrib_name, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name);
		if (field_name.equals("guild"))
		{
			try
			{
				return (getGuild() == null ? null : getGuild().getName());
			}
			catch (MudException e)
			{
				throw new FieldNotSupportedException("unable to retrieve guild");
			}
		}
		return super.getValue(field_name, attrib_name, ctxt);
	}

	/**
	 * Set the ignore list. Usually used by the database class.
	 * @param aList the list of persons to be ignored.
	 */
	public void setIgnoreList(TreeMap aList)
	{
		theIsBeingIgnoredBy = aList;
	}

	/**
	 * Determines if this current user is being ignored by aPerson.
	 * @param aPerson the user that could be ignoring this person.
	 * @return true if we are being ignored, false otherwise.
	 */
	public boolean isIgnored(Person aPerson)
	{
		return theIsBeingIgnoredBy.containsKey(aPerson.getName());
	}

	/**
	 * Adds a name to the ignorelist.
	 * @param aName the name to be added of the user that is ignoring us.
	 */
	public void addName(User aUser)
	throws MudException
	{
		theIsBeingIgnoredBy.put(aUser.getName(), null);
		Database.addIgnore(aUser, this);
	}

	/**
	 * Removes a name from the ignorelist.
	 * @param aName the name to be removed of the user that is ignoring us.
	 */
	public void removeName(User aUser)
	throws MudException
	{
		theIsBeingIgnoredBy.remove(aUser.getName());
		Database.removeIgnore(aUser, this);
	}
}
