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

import java.util.Vector;

public final class Characters
{
	private static Vector theCharacters = new Vector();

	public static void init()
	{
		if (Constants.logging)
		{
			System.err.println("Characters.init");
		}
		theCharacters = Database.getCharacters();
	}

	public Characters()
	{
		if (Constants.logging)
		{
			System.err.println("Characters.Characters");
		}
//		theCharacters = new Vector();
	}

	public static Character retrieveCharacter(String aName)
	{
		assert theCharacters != null : "theCharacters vector is null";
		if (Constants.logging)
		{
			System.err.println("Characters.retrieveCharacter: " + aName);
		}
		for (int i=0;i < theCharacters.size(); i++)
		{
			Character myChar = (Character) theCharacters.elementAt(i);
			if ((myChar != null) && (myChar.getName().compareToIgnoreCase(aName) == 0))
			{
				return myChar;
			}
		}
		return null;
	}

	/**
	 * activate a character
	 * @throws CharacterException if something is wrong
	 * @param aName String containing the name of the Character
	 * @param aPassword String containing the password of the Character
	 * @param aCookie String containing the cookie that was set in the
	 * browser. It is possible that this is "null", if the user was not
	 * logged in before.
	 */
	 	public static User activateUser(String aName, String aPassword, String aCookie)
		throws CharacterException
	{
		if (Constants.logging)
		{
			System.err.println("Characters.activateUser: " + aName + "," + aPassword + "," + aCookie);
		}
		Character myChar = retrieveCharacter(aName);
		if ((myChar != null) && (!(myChar instanceof User)))
		{
			// found, but no user
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.NOTAUSERERROR);
			}
			throw new CharacterException(Constants.NOTAUSERERROR);
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
				throw new CharacterException(Constants.USERNOTFOUNDERROR);
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
				throw new CharacterException(Constants.MULTIUSERERROR);
			}
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.USERALREADYACTIVEERROR);
			}
			throw new CharacterException(Constants.USERALREADYACTIVEERROR);
		}
		if (!myUser.getPassword().equals(aPassword))
		{
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.PWDINCORRECTERROR);
			}
			throw new CharacterException(Constants.PWDINCORRECTERROR);
		}
		// everything seems to be okay
		Database.activateUser(myUser);
		theCharacters.addElement(myUser);
		return myUser;
	}

	/**
	 * deactivate a character
	 * @throws CharacterException if something is wrong
	 */
 	public static void deactivateUser(User aUser)
	throws CharacterException
	{
		if (Constants.logging)
		{
			System.err.println("Characters.deactivateUser: " + aUser.getName());
		}
		Database.deactivateUser(aUser);
		theCharacters.remove(aUser);
	}

	/**
	 * create a character
	 * @throws CharacterException if something is wrong
	 * @param aName String containing the name of the new Character
	 * @param aPassword String containing the password of the new Character
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
		throws CharacterException
	{
		if (Constants.logging)
		{
			System.err.println("Characters.createUser: " + aName + "," + aPassword + "," + anAddress + "," + aCookie);
		}
		if (Database.existsUser(aName))
		{
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.USERALREADYEXISTSERROR);
			}
			throw new CharacterException(Constants.USERALREADYEXISTSERROR);
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
		theCharacters.addElement(myUser);
		return myUser;
	}

	public static String descriptionOfCharactersInRoom(Room aRoom, User aUser)
	{
		if (Constants.logging)
		{
			System.err.println("Characters.descriptionOfCharactersInRoom: " + aRoom + "," + aUser);
		}
		String myOutput = new String();
		
		for (int i=0;i < theCharacters.size();i++)
		{
			Character myChar = (Character) theCharacters.elementAt(i);
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
			System.err.println("Characters.sendWall: " + aMessage);
		}
		for (int i=0;i < theCharacters.size();i++)
		{
			Character myChar = (Character) theCharacters.elementAt(i);
			myChar.writeMessage(aMessage);
		}
	}

	/**
	 * character communication method to one specific user
	 */
	public static void sendMessage(Character aCharacter, String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Characters.sendMessage: " + aCharacter + " " + aMessage);
		}
		for (int i=0;i < theCharacters.size();i++)
		{
			Character myChar = (Character) theCharacters.elementAt(i);
			if ( (myChar.getRoom() == aCharacter.getRoom()) &&
				(myChar != aCharacter) )
			{
				myChar.writeMessage(aMessage);
			}
		}
	}

	/**
	 * character communication method to everyone in the room except 
	 * the two characters entered in the parameter list
	 */
	public static void sendMessage(Character aCharacter, Character aSecondCharacter, String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Characters.sendMessage: " + aCharacter + " " + aSecondCharacter + " " + aMessage);
		}
		for (int i=0;i < theCharacters.size();i++)
		{
			Character myChar = (Character) theCharacters.elementAt(i);
			if ( (myChar.getRoom() == aCharacter.getRoom()) &&
				(myChar != aSecondCharacter) &&
				(myChar != aCharacter) )
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
			System.err.println("Characters.getWhoList ");
		}
		String myString = "<UL>";
		int count = 0;
		for (int i=0;i < theCharacters.size();i++)
		{
			Character myChar = (Character) theCharacters.elementAt(i);
			System.err.println("Characters.getWhoList " + myChar);
			if (myChar instanceof User)
			{
			System.err.println("Characters.getWhoList " + myChar);
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
		
		for (int i=0;i < theCharacters.size();i++)
		{
			Character myChar = (Character) theCharacters.elementAt(i);
			if (myChar != null)
			{
				myOutput = myOutput + myChar + ",";
			}
		}
		return myOutput;
	}

}