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

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

public final class Persons
{
	private static Vector thePersons = new Vector();

	public static void init()
	{
		if (Constants.logging)
		{
			System.err.println("Persons.init");
		}
		thePersons = Database.getPersons();
	}

	public Persons()
	{
		if (Constants.logging)
		{
			System.err.println("Persons.Persons");
		}
//		thePersons = new Vector();
	}

	public static Person retrievePerson(String aName)
	{
		assert thePersons != null : "thePersons vector is null";
		if (Constants.logging)
		{
			System.err.println("Persons.retrievePerson: " + aName);
		}
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
	 * @param aCookie String containing the cookie that was set in the
	 * browser. It is possible that this is "null", if the user was not
	 * logged in before.
	 */
	 	public static User activateUser(String aName, String aPassword, String aCookie)
		throws PersonException
	{
		if (Constants.logging)
		{
			System.err.println("Persons.activateUser: " + aName + "," + aPassword + "," + aCookie);
		}
		Person myChar = retrievePerson(aName);
		if ((myChar != null) && (!(myChar instanceof User)))
		{
			// found, but no user
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.NOTAUSERERROR);
			}
			throw new PersonException(Constants.NOTAUSERERROR);
		}
		User myUser = (myChar == null ? null : (User) myChar);
		if (myUser == null)
		{
			myUser = Database.getUser(aName);
			if (myUser == null)
			{
				if (Constants.logging)
				{
					System.err.println("thrown: " + Constants.USERNOTFOUNDERROR);
				}
				throw new PersonException(Constants.USERNOTFOUNDERROR);
			}
		}
		else
		{
			if ((aCookie != null) &&
				(!aCookie.equals(myUser.getSessionPassword())) &&
				!aCookie.equals("") )
			{
				if (Constants.logging)
				{
					System.err.println("thrown: " + Constants.MULTIUSERERROR);
				}
				throw new PersonException(Constants.MULTIUSERERROR);
			}
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.USERALREADYACTIVEERROR);
			}
			throw new PersonException(Constants.USERALREADYACTIVEERROR);
		}
		if (!myUser.getPassword().equals(aPassword))
		{
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.PWDINCORRECTERROR);
			}
			throw new PersonException(Constants.PWDINCORRECTERROR);
		}
		// everything seems to be okay
		Database.activateUser(myUser);
		thePersons.addElement(myUser);
		return myUser;
	}

	/**
	 * deactivate a character
	 * @throws PersonException if something is wrong
	 */
 	public static void deactivateUser(User aUser)
	throws PersonException
	{
		if (Constants.logging)
		{
			System.err.println("Persons.deactivateUser: " + aUser.getName());
		}
		Database.deactivateUser(aUser);
		thePersons.remove(aUser);
	}

	/**
	 * create a character
	 * @throws PersonException if something is wrong
	 * @param aName String containing the name of the new Person
	 * @param aPassword String containing the password of the new Person
	 * @param aCookie String containing the cookie
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
		if (Constants.logging)
		{
			System.err.println("Persons.createUser: " + aName + "," + aPassword + "," + anAddress + "," + aCookie);
		}
		if (Database.existsUser(aName))
		{
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.USERALREADYEXISTSERROR);
			}
			throw new PersonException(Constants.USERALREADYEXISTSERROR);
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

	public static String descriptionOfPersonsInRoom(Room aRoom, User aUser)
	{
		if (Constants.logging)
		{
			System.err.println("Persons.descriptionOfPersonsInRoom: " + aRoom + "," + aUser);
		}
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
	 */
	public static void sendWall(String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Persons.sendWall: " + aMessage);
		}
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			myChar.writeMessage(aMessage);
		}
	}

	/**
	 * character communication method to one specific user
	 */
	public static void sendMessage(Person aPerson, String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Persons.sendMessage: " + aPerson + " " + aMessage);
		}
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
	 * the two characters entered in the parameter list
	 */
	public static void sendMessage(Person aPerson, Person aSecondPerson, String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Persons.sendMessage: " + aPerson + " " + aSecondPerson + " " + aMessage);
		}
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
	 */
	public static String getWhoList()
	{
		if (Constants.logging)
		{
			System.err.println("Persons.getWhoList ");
		}
		String myString = "<UL>";
		int count = 0;
		for (int i=0;i < thePersons.size();i++)
		{
			Person myChar = (Person) thePersons.elementAt(i);
			System.err.println("Persons.getWhoList " + myChar);
			if (myChar instanceof User)
			{
			System.err.println("Persons.getWhoList " + myChar);
				User myUser = (User) myChar;
				myString += "<LI>" + myUser.getName() + ", " + myUser.getTitle() + (myUser.isaSleep()?", sleeping ":" ") + myUser.getIdleTime() + "\r\n";
				count++;
			}
		}
		myString = "<H2>List of All Users</H2><I>There are " + count + " persons active in the game.</I><P>" + myString + "</UL>";
		return myString;
	}

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