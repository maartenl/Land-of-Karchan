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

package mmud;

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

public class User extends mmud.characters.Person
{

	private String thePassword;
	private String theAddress;
	private String theSessionPassword;

	private String theRealName;
	private String theEmail;

	private int theFrames;
	private boolean theGod;
	private String theCookie;
	private Calendar rightNow;
	private boolean thePkill;

	/**
	 * create an instance of User, based on Database data. 
	 * @param aName String containing the name of the user
	 * @param aPassword String containing the password of the User
	 * @param anAddress String containing the address from whence the
	 * usr is connecting to the game.
	 * @param aGod boolean, indicates wether or not the User has special
	 * privileges; also known as <I>Administrator</I>.
	 * @param aCookie String containing the cookie or <I>Session
	 * Password</I> used during gameplay.
	 * @param aRoom Room, containing the room of the user.
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
			aRoom);
		Logger.getLogger("mmud").finer("");
		thePassword = aPassword;
		theAddress = anAddress;
		theEmail = aEmail;
		theRealName = aRealName;
		theGod = aGod;
		theCookie = aCookie;
		thePkill = aPkill;
		rightNow = Calendar.getInstance();
		if ((theCookie != null) && (theCookie.trim().equals(""))) 
		{
			theCookie = null;
		}
	}

	/**
	 * create an instance of User. Usually used if you need a standard User
	 * created. Use the other constructor when you need to load a User
	 * from the database.
	 * @param aName String containing the name of the user
	 * @param aPassword String containing the password of the User
	 * @param anAddress String containing the address from whence the
	 * usr is connecting to the game.
	 * @param aGod boolean, indicates wether or not the User has special
	 * privileges; also known as <I>Administrator</I>.
	 * @param aCookie String containing the cookie or <I>Session
	 * Password</I> used during gameplay.
	 * @param aRoom Room, containing the room of the user.
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
			Rooms.getRoom(1));
		Logger.getLogger("mmud").finer("");
		thePassword = aPassword;
		theAddress = anAddress;
		theEmail = aEmail;
		theRealName = aRealName;
		theGod = false;
		thePkill = true;
		theCookie = aCookie;
		rightNow = Calendar.getInstance();
		if ((theCookie != null) && (theCookie.trim().equals(""))) 
		{
			theCookie = null;
		}
	}

	public String getPassword()
	{
		Logger.getLogger("mmud").finer("");
		return thePassword;
	}

	public boolean verifyPassword(String aPassword)
	{
		Logger.getLogger("mmud").finer("");
		return thePassword.equals(aPassword);
	}

	public String getAddress()
	{
		Logger.getLogger("mmud").finer("");
		return theAddress;
	}

	/**
	 * generate a session password to be used by player during game session
	 * @return a String containing 25 random
	 * digits, capitals and smallcaps
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
	 *
	 */
	public String getSessionPassword()
	{
		Logger.getLogger("mmud").finer("");
		return theSessionPassword;
	}

	/**
	 * verify sessionpassword
	 *
	 */
	public boolean verifySessionPassword(String aSessionPassword)
	{
		Logger.getLogger("mmud").finer("");
		return (theSessionPassword == null ? false : theSessionPassword.equals(aSessionPassword));
	}

	/**
	 * set sessionpassword
	 *
	 */
	public void setSessionPassword(String aSessionPassword)
	{
		Logger.getLogger("mmud").finer("");
		theSessionPassword = aSessionPassword;
	}

	/**
	 * retrieve the frame number<P>
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

	public void setFrames(int i)
	{
		Logger.getLogger("mmud").finer("");
		theFrames  = i;
	}

	public boolean isGod()
	{
		Logger.getLogger("mmud").finer("");
		return theGod;
	}

	public String getRealname()
	{
		return theRealName;
	}

	public String getEmail()
	{
		return theEmail;
	}

	public boolean isPkill()
	{
		return thePkill;
	}

	public void setPkill(boolean newPkill)
	{
		thePkill = newPkill;
	}

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
			myString += "<INPUT TYPE=\"hidden\" NAME=\"password\" VALUE=\"" + getPassword() + "\">\n";
			myString += "<INPUT TYPE=\"hidden\" NAME=\"frames\" VALUE=\"1\">\n";
			myString += "<INPUT TYPE=\"submit\" VALUE=\"Submit\">\n";
			myString += "</FORM><P>\n";
		}
		return myString;
	}

	public void setNow()
	{
		Logger.getLogger("mmud").finer("");
		rightNow = Calendar.getInstance();
	}

	public String getIdleTime()
	{
		Logger.getLogger("mmud").finer("");
		Calendar tempNow = Calendar.getInstance();
		if (rightNow == null) 
		{
			setNow();
		}
		long myTemp = (tempNow.getTimeInMillis() - rightNow.getTimeInMillis())/1000;
		return "(" + myTemp / 60 + "min, " + myTemp % 60 + " sec idle)";
	}

	public String getUrl(String aCommand)
	{
		Logger.getLogger("mmud").finer("");
		return Constants.mudcgi + "?command=" + aCommand + "&name=" +
			getName() + "&password=" + getPassword() + "&frames=" +
			getFrames();
	}

	public String getListOfMail()
	{
		Logger.getLogger("mmud").finer("");
		return Database.getListOfMail(this);
	}

}
