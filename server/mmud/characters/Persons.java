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

import java.util.Vector;
import java.util.logging.Logger;


import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Collection class containing all persons at the moment active in the game.
 * Can contain not only users, but also bots and the like. As long as the
 * base class is Person.
 * @see mmud.characters.Person
 */
public final class Persons
{
	private static Vector thePersons = new Vector();

	/**
	 * Initialise this object by retrieving all persons from the
	 * database that are playing the game.
	 * @see mmud.database.Database#getPersons
	 */
	public static void init()
	{
		Logger.getLogger("mmud").finer("");
		thePersons = Database.getPersons();
	}

	/**
	 * Default constructor.
	 */
	public Persons()
	{
		Logger.getLogger("mmud").finer("");
//		thePersons = new Vector();
	}

	/**
	 * retrieve the character from the list of characters currently active in
	 * the game.
	 * @param aName name of the character to search for.
	 * @return Person object containing all relevant information of the
	 * character. Will return null pointer if character not active
	 * in the game.
	 */
	public static Person retrievePerson(String aName)
	{
		assert thePersons != null : "thePersons vector is null";
		Logger.getLogger("mmud").finer("aName=" + aName);
		for (int i=0;i < thePersons.size(); i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			if ((myChar != null) && (myChar.getName().compareToIgnoreCase(aName) == 0))
			{
				return myChar;
			}
		}
		return null;
	}

	/**
	 * activate a character
	 * @throws PersonException if something is wrong
	 * @param aName String containing the name of the Person
	 * @param aPassword String containing the password of the Person
	 * @param aCookie String containing the session password. It is
	 * possible that this is "null", if the user was not logged in
	 * before.
	 * @return User containing all information. If the class would not
	 * be user but a Person, it means that it is not a valid player in
	 * the game, but more likely a bot.
	 */
 	public static User activateUser(String aName, String aPassword, String aCookie)
	throws PersonException
	{
		Logger.getLogger("mmud").finer("aName=" + aName +
			",aPassword=" + aPassword +
			",aCookie=" + aCookie);
		Person myChar = retrievePerson(aName);
		if ((myChar != null) && (!(myChar instanceof User)))
		{
			// found, but no user
			Logger.getLogger("mmud").info("thrown: " + Constants.NOTAUSERERROR);
			throw new NotAUserException();
		}
		User myUser = (myChar == null ? null : (User) myChar);
		if (myUser == null)
		{
			myUser = Database.getUser(aName, aPassword);
			if (myUser == null)
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.USERNOTFOUNDERROR);
				throw new UserNotFoundException();
			}
		}
		else
		{
			if (myUser.getPassword() == null)
			{
				User tempUser = Database.getUser(aName, aPassword);
				if (!tempUser.verifyPassword(aPassword))
				{
					Logger.getLogger("mmud").info("thrown: " + Constants.PWDINCORRECTERROR);
					throw new PwdIncorrectException();
				}
				myUser.setPassword(aPassword);
			}
			if ((aCookie != null) &&
				(!aCookie.equals(myUser.getSessionPassword())) &&
				!aCookie.equals("") )
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.MULTIUSERERROR);
				throw new MultiUserException();
			}
			if (!myUser.verifyPassword(aPassword))
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.PWDINCORRECTERROR);
				throw new PwdIncorrectException();
			}
			Logger.getLogger("mmud").info("thrown: " + Constants.USERALREADYACTIVEERROR);
			throw new UserAlreadyActiveException();
		}

		if (!myUser.verifyPassword(aPassword))
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.PWDINCORRECTERROR);
			throw new PwdIncorrectException();
		}
		// everything seems to be okay
		Database.activateUser(myUser);
		thePersons.addElement(myUser);
		return myUser;
	}

	/**
	 * deactivate a character (usually because someone typed quit.)
	 * @param aUser the player to be deactivated
	 * @throws PersonException if something is wrong
	 */
 	public static void deactivateUser(User aUser)
	throws PersonException
	{
		Logger.getLogger("mmud").finer("aUser=" + aUser);
		Database.deactivateUser(aUser);
		thePersons.remove(aUser);
	}

	/**
	 * create a new character
	 * @throws PersonException if something is wrong
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
	 * @return User object
	 */
	public static User createUser(String aName, String aPassword, String anAddress,
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
		throws PersonException
	{
		Logger.getLogger("mmud").finer(
			"aName=" + aName + 
			",aPassword=" + aPassword + 
			",anAddress=" + anAddress +
			",aRealName=" + aRealName +
			",aEmail=" + aEmail +
			",aTitle=" + aTitle +
			",aRace=" + aRace +
			",aSex=" + aSex +
			",aAge=" + aAge +
			",aLength=" + aLength +
			",aWidth=" + aWidth +
			",aComplexion=" + aComplexion +
			",aEyes=" + aEyes +
			",aFace=" + aFace +
			",aHair=" + aHair +
			",aBeard=" + aBeard +
			",aArms=" + aArms +
			",aLegs=" + aLegs +
			",aCookie=" + aCookie);
		if (Database.existsUser(aName))
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.USERALREADYEXISTSERROR);
			throw new UserAlreadyExistsException();
		}
		// everything seems to be okay
		User myUser = new User(aName, aPassword, anAddress,
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
			aCookie);
		Database.createUser(myUser);
		thePersons.addElement(myUser);
		return myUser;
	}

	/**
	 * Returns a description of everyone visible in a room.
	 * @param aRoom the room of which the description is required.
	 * @param aUser the user (in most cases we want a description of
	 * everyone in the room, except ourselves.
	 * @return String containing the description of everyone visible in
	 * the room.
	 */
	public static String descriptionOfPersonsInRoom(Room aRoom, User aUser)
	{
		Logger.getLogger("mmud").finer("aRoom=" + aRoom +
			",aUser=" + aUser);
		String myOutput = new String();
		
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			if ( (myChar.getRoom() == aRoom) &&
				(myChar != aUser) )
			{
				myOutput = myOutput + "A " + myChar.getRace() + " called " + myChar.getName() + " is here.<BR>\r\n";
			}
		}
		return myOutput;
	}

	/**
	 * paging all users
	 * @param aMessage message to be sent to all users.
	 */
	public static void sendWall(String aMessage)
	{
		Logger.getLogger("mmud").finer("aMessage=" + aMessage);
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			myChar.writeMessage(aMessage);
		}
	}

	/**
	 * character communication method to one specific user
	 * @param aPerson the person to send a message to.
	 * @param aMessage the message
	 */
	public static void sendMessage(Person aPerson, String aMessage)
	{
		Logger.getLogger("mmud").finer("aPerson=" + aPerson +
			",aMessage=" + aMessage);
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			if ( (myChar.getRoom() == aPerson.getRoom()) &&
				(myChar != aPerson) )
			{
				myChar.writeMessage(aMessage);
			}
		}
	}

	/**
	 * character communication method to everyone in the room except 
	 * the two characters entered in the parameter list.
	 * @param aPerson the person doing the communicatin'
	 * @param aSecondPerson the person communicated to.
	 * @param aMessage the message to be sent
	 */
	public static void sendMessage(Person aPerson, Person aSecondPerson, String aMessage)
	{
		Logger.getLogger("mmud").finer("aPerson=" + aPerson +
			",aSecondPerson=" + aSecondPerson +
			",aMessage=" + aMessage);
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			if ( (myChar.getRoom() == aPerson.getRoom()) &&
				(myChar != aSecondPerson) &&
				(myChar != aPerson) )
			{
				myChar.writeMessage(aMessage);
			}
		}
	}

	/**
	 * returns a list of persons currently playing the game
	 * @return String containing who's who.
	 */
	public static String getWhoList()
	{
		Logger.getLogger("mmud").finer("");
		String myString = "<UL>";
		int count = 0;
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			if (myChar instanceof User)
			{
				User myUser = (User) myChar;
				myString += "<LI>" + myUser.getName() + ", " + myUser.getTitle() + (myUser.isaSleep()?", sleeping ":" ") + myUser.getIdleTime() + "\r\n";
				count++;
			}
		}
		myString = "<H2>List of All Users</H2><I>There are " + count + " persons active in the game.</I><P>" + myString + "</UL>";
		return myString;
	}

	/**
	 * Standard tostring implementation.
	 * @return String containing the characters in the list.
	 */
	public String toString()
	{
		String myOutput = new String();
		
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			if (myChar != null)
			{
				myOutput = myOutput + myChar + ",";
			}
		}
		return myOutput;
	}

}