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

import java.util.TreeMap;

public class ItemDef
{
	private String theVerb;
	private String theAdjective1;
	private String theAdjective2;
	private String theAdjective3;
	private String theDescription;
	private int theId;
	private int theGold;
	private int theSilver;
	private int theCopper;
	private TreeMap theAttributes = new TreeMap();

	public ItemDef(int anId, String anAdjective1, String anAdjective2, String anAdjective3, String aVerb, String aDescription, int aGold, int aSilver, int aCopper)
	{
		theId = anId;
		theVerb = aVerb;
		theAdjective1 = anAdjective1;
		theAdjective2 = anAdjective2;
		theAdjective3 = anAdjective3;
		theDescription = aDescription;
		theGold = aGold;
		theSilver = aSilver;
		theCopper = aCopper;
	} 

	public ItemDef(ItemDef anItemDef)
	{
		theId = anItemDef.getId();
		theVerb = anItemDef.getVerb();
		theAdjective1 = anItemDef.getAdjective1();
		theAdjective2 = anItemDef.getAdjective2();
		theAdjective3 = anItemDef.getAdjective3();
		theDescription = anItemDef.getLongDescription();
		theGold = anItemDef.getGold();
		theSilver = anItemDef.getSilver();
		theCopper = anItemDef.getCopper();
	} 

	public int getId()
	{
		return theId;
	}

	public String getVerb()
	{
		return theVerb;
	}

	public String getAdjective1()
	{
		return theAdjective1;
	}

	public String getAdjective2()
	{
		return theAdjective2;
	}

	public String getAdjective3()
	{
		return theAdjective3;
	}
	
	public int getGold()
	{
		return theGold;
	}
	
	public int getSilver()
	{
		return theSilver;
	}
	
	public int getCopper()
	{
		return theCopper;
	}
	
	public String toString()
	{
		return theId + " " + theAdjective1 + " " +
			theAdjective2 + " " +
			theAdjective3 + " " +
			theVerb;
	}

	public String getLongDescription()
	{
		return theDescription;
	}

	public void setAttribute(Attribute anAttribute)
	{
		theAttributes.put(anAttribute.getName(), anAttribute);
	}

	public Attribute getAttribute(String aName)
	{
		return (Attribute) theAttributes.get(aName);
	}

	public void removeAttribute(String aName)
	{
		theAttributes.remove(aName);
	}

	public boolean isAttribute(String aName)
	{
		return theAttributes.containsKey(aName);
	}

}
