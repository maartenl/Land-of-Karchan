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

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

public class Person
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
	private int theWhimpy;
	private int theDrinkstats;
	private int theEatstats;
    private TreeMap theAttributes = new TreeMap();

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
		theLogFile = new File(Constants.mudfilepath, aName + ".log");
		createLog();
	}

	public String toString()
	{
		return theName;
	}

	public String getName()
	{
		return theName;
	}

	public String getTitle()
	{
		return theTitle;
	}

	public void setTitle(String aNewTitle)
	{
		theTitle = aNewTitle;
		Database.setTitle(this);
	}

	public int getWhimpy()
	{
		return theWhimpy;
	}

	public void setWhimpy(int aWhimpy)
	{
		theWhimpy = aWhimpy;
		Database.setWhimpy(this);
	}

	public int getDrinkstats()
	{
		return theDrinkstats;
	}

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

	public boolean canDrink()
	{
		return (( theDrinkstats < 49) && ( theDrinkstats >= 0 ));
	}

	public void setDrinkstats(int i)
	{
		if (!canDrink()) return;
		theDrinkstats = i;
		Database.setDrinkstats(this);
	}

	public int getEatstats()
	{
		return theEatstats;
	}

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

	public boolean canEat()
	{
		return (( theEatstats < 49) && ( theEatstats >= 0 ));
	}

	public void setEatstats(int i)
	{
		if (!canEat()) return;
		theEatstats = i;
		Database.setEatstats(this);
	}

	public String getRace()
	{
		return theRace;
	}

	public Sex getSex()
	{
		return theSex;
	}

	public String getAge()
	{
		return theAge;
	}

	public String getLength()
	{
		return theLength;
	}

	public String getWidth()
	{
		return theWidth;
	}

	public String getComplexion()
	{
		return theComplexion;
	}

	public String getEyes()
	{
		return theEyes;
	}

	public String getFace()
	{
		return theFace;
	}

	public String getHair()
	{
		return theHair;
	}

	public String getBeard()
	{
		return theBeard;
	}

	public String getArms()
	{
		return theArms;
	}

	public String getLegs()
	{
		return theLegs;
	}

	public boolean isaSleep()
	{
		return theSleep;
	}

	public void setSleep(boolean aSleep)
	{
		theSleep = aSleep;
		Database.setSleep(this);
	}

	public Room getRoom()
	{
		return theRoom;
	}

	public void setRoom(Room aRoom)
	{
		theRoom = aRoom;
		Database.setRoom(this);
	}

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
		result += "self " + getName() + " (" + getTitle() + ").<BR>\r\n";
		return result;
	}

	public void writeMessage(String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Person.writeMessage " + getName() + "," + aMessage);
		}
		try
		{
			FileWriter myFileWriter = new FileWriter(theLogFile, true);
			myFileWriter.write(aMessage, 0, aMessage.length());
			myFileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Sends a message to the other people in the room
	 */
	public void sendMessage(String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Person.sendMessage " + aMessage);
		}
		Persons.sendMessage(this, aMessage);
	}

	/**
	 * Sends a message to the other people in the room, except
	 * for one other character
	 */
	public void sendMessage(Person aPerson, String aMessage)
	{
		if (Constants.logging)
		{
			System.err.println("Person.sendMessage " + aMessage);
		}
		Persons.sendMessage(this, aPerson, aMessage);
	}

	public String readLog()
	{
		if (Constants.logging)
		{
			System.err.println("Person.readLog");
		}
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
	 * moves items 36, 37, 38 (copper, silver, gold).
	 * @param toChar character to be provided with the coins
	 * @param gold how many gold coins need to be moved
	 * @param silver how many silver coins need to be moved
	 * @param copper how many copper coins need to be moved
	 */
/*	public void moveCoins(Person toChar, int gold, int silver, int copper)
		throws ItemException
	{
		if (Constants.logging)
		{
			System.err.println("Person.moveCoins "+toChar+","+gold+","+silver+","+copper);
		}
		int mygold = getItemCount(goldcoin, 0, goldcoin.length);
		int mysilver = getItemCount(silvercoin, 0, silvercoin.length);
		int mycopper = getItemCount(coppercoin, 0, coppercoin.length);

		if (mygold*100+mysilver*10+mycopper<gold*100+silver*10+copper)
		{
			if (Constants.logging)
			{   
				System.err.println("thrown: " + Constants.NOTENOUGHMONEYERROR);
			}
			throw new ItemException(Constants.NOTENOUGHMONEYERROR);
		}

		for (int i=0; i < gold; i++)
		{
			Item myItem = getItem(goldcoin, 0, goldcoin.length);
			removeFromInventory(myItem);
			toChar.addToInventory(myItem);
		}
		while (mysilver < silver)
		{
			Item myItem = getItem(goldcoin, 0, goldcoin.length);
			removeFromInventory(myItem);
			mysilver += 10;
			for (int j=0;j<10;j++)
			{
				Item newCoin = new Item(ItemDefs.getItemDef(37));
				addToInventory(newCoin);
			}
		}
		for (int i=0; i < silver; i++)
		{
			Item myItem = getItem(silvercoin, 0, silvercoin.length);
			removeFromInventory(myItem);
			toChar.addToInventory(myItem);
		}
		while (mycopper < copper)
		{
			Item myItem = getItem(silvercoin, 0, silvercoin.length);
			removeFromInventory(myItem);
			mycopper += 10;
			for (int j=0;j<10;j++)
			{
				Item newCoin = new Item(ItemDefs.getItemDef(36));
				addToInventory(newCoin);
			}
		}
		for (int i=0; i < copper; i++)
		{
			Item myItem = getItem(coppercoin, 0, coppercoin.length);
			removeFromInventory(myItem);
			toChar.addToInventory(myItem);
		}
	}
*/
	/**
	 * should be the same as item 38
	 */
	public static final String[] goldcoin = {"valuable",  "gold", "shiny", "coin"};

	/**
	 * should be the same as item 37
	 */
	public static final String[] silvercoin = {"valuable",  "silver", "shiny", "coin"};

	/**
	 * should be the same as item 36
	 */
	public static final String[] coppercoin = {"valuable",  "copper", "shiny", "coin"};

	public void setAttribute(Attribute anAttribute)
	{
		theAttributes.put(anAttribute.getName(), anAttribute);
	}

	/**
	 * returns the attribute found with name aName or null
	 * if it does not exist.
	 */
	public Attribute getAttribute(String aName)
	{
		Attribute myAttrib = (Attribute) theAttributes.get(aName);
		return myAttrib;
	}

	public void removeAttribute(String aName)
	{
		theAttributes.remove(aName);
	}

	/**
	 * returns true if the attribute with name aName
	 * exists.
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
		return Database.getInventory(this);
	}

	/**
	 * Pick up an item lying on the floor, visible, and
	 * gettable.
	 */
	public int pickupItems(int amount, 
				String adject1, 
				String adject2, 
				String adject3,
				String name)
	{
		return Database.pickupItem(amount, adject1, adject2, adject3, name, this);
	}

	/**
	 * Drop an item onto the floor, from your inventory.
	 */
	public int dropItems(int amount, 
				String adject1, 
				String adject2, 
				String adject3,
				String name)
	{
		return 0;
		// DEBUG
//		return Database.dropItem(amount, adject1, adject2, adject3, name, this);
	}

	
}
