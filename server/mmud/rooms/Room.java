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
package mmud.rooms;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

public class Room
{
	private int theId;
	private String theTitle;
	private String theContents;
	private Room south, north, east, west, up, down;
	private int intsouth, intnorth, inteast, intwest, intup, intdown;

	public Room(int anId, String aTitle, String aContents, int asouth, int anorth, int aeast, int awest, int aup, int adown)
	{
		theId = anId;
		theTitle = aTitle;
		theContents = aContents;
		south = null;
		north = null;
		east = null;
		west = null;
		intsouth = asouth;
		intnorth = anorth;
		inteast = aeast;
		intwest = awest;
		intup = aup;
		intdown = adown;
	}
	
	public int getId()
	{
		return theId;
	}

	public void setSouth(Room aSouth)
	{
		south = aSouth;
		intsouth = 0;
	}

	public void setNorth(Room aNorth)
	{
		north = aNorth;
		intnorth = 0;
	}

	public void setEast(Room aEast)
	{
		east = aEast;
		inteast = 0;
	}

	public void setWest(Room aWest)
	{
		west = aWest;
		intwest = 0;
	}

	public void setUp(Room aUp)
	{
		up = aUp;
		intup = 0;
	}

	public void setDown(Room aDown)
	{
		down = aDown;
		intdown = 0;
	}

	public Room getSouth()
	{
		if (south == null)
		{
			if (intsouth == 0)
			{
				return null;
			}
			setSouth(Rooms.getRoom(intsouth));
		}
		return south;
	}

	public Room getNorth()
	{
		if (north == null)
		{
			if (intnorth == 0)
			{
				return null;
			}
			setNorth(Rooms.getRoom(intnorth));
		}
		return north;
	}

	public Room getEast()
	{
		if (east == null)
		{
			if (inteast == 0)
			{
				return null;
			}
			setEast(Rooms.getRoom(inteast));
		}
		return east;
	}

	public Room getWest()
	{
		if (west == null)
		{
			if (intwest == 0)
			{
				return null;
			}
			setWest(Rooms.getRoom(intwest));
		}
		return west;
	}

	public Room getUp()
	{
		if (up == null)
		{
			if (intup == 0)
			{
				return null;
			}
			setUp(Rooms.getRoom(intup));
		}
		return up;
	}

	public Room getDown()
	{
		if (down == null)
		{
			if (intdown == 0)
			{
				return null;
			}
			setDown(Rooms.getRoom(intdown));
		}
		return down;
	}

	public String toString()
	{
		return theId + ":" + theTitle;
	}

	public String getDescription(User aUser)
	{
		String result = (theContents == null ? 
			"<H1>Cardboard</H1>" +
			"Everywhere around you you notice cardboard. It seems as if this part" +
			" has either not been finished yet or you encountered an error" +
			" in retrieving the room description from the server.<P>"
			: theContents);
		result += "[ ";
		if (getWest() != null)
		{
			result += "<A HREF=\"" + aUser.getUrl("w") + "\">west </A>";
		}
		if (getEast() != null)
		{
			result += "<A HREF=\"" + aUser.getUrl("e") + "\">east </A>";
		}
		if (getNorth() != null)
		{
			result += "<A HREF=\"" + aUser.getUrl("n") + "\">north </A>";
		}
		if (getSouth() != null)
		{
			result += "<A HREF=\"" + aUser.getUrl("s") + "\">south </A>";
		}
		if (getUp() != null)
		{
			result += "<A HREF=\"" + aUser.getUrl("up") + "\">up </A>";
		}
		if (getDown() != null)
		{
			result += "<A HREF=\"" + aUser.getUrl("down") + "\">down </A>";
		}
		result += "]<P>\r\n";
		if (aUser.getFrames() == 0)
		{
			result += "<TABLE ALIGN=right>\n";
			result += "<TR><TD><IMG ALIGN=right SRC=\"/images/gif/roos.gif\" USEMAP=\"#roosmap\" BORDER=\"0\" ISMAP ALT=\"N-S-E-W\"><P>";
	
			if (aUser.isaSleep()) 
			{
				result +=  "<script language=\"JavaScript\">\r\n";
	
				result +=  "<!-- In hiding!\r\n";
				result +=  " browserName = navigator.appName;\r\n";
				result +=  "		   browserVer = parseInt(navigator.appVersion);\r\n";
				result +=  "			   if (browserName == \"Netscape\" && browserVer >= 3) version =\r\n";
				result +=  "\"n3\";\r\n";
				result +=  "			   else version = \"n2\";\r\n";
	
				result +=  "			   if (version == \"n3\"){				\r\n";
				result +=  "			   toc1on = new Image;\r\n";
				result +=  "			   toc1on.src = \"../images/gif/webpic/new/buttonl.gif\";\r\n";
	
	
				result +=  "			   toc1off = new Image;\r\n";
				result +=  "			   toc1off.src = \"../images/gif/webpic/buttonl.gif\";\r\n";
	
				result +=  "		}\r\n";
	
				result +=  "function img_act(imgName) {\r\n";
				result +=  "		if (version == \"n3\") {\r\n";
				result +=  "		imgOn = eval(imgName + \"on.src\";\r\n";
				result +=  "		document [imgName].src = imgOn;\r\n";
				result +=  "		}\r\n";
				result +=  "}\r\n";
	
				result +=  "function img_inact(imgName) {\r\n";
				result +=  "		if (version == \"n3\") {\r\n";
				result +=  "		imgOff = eval(imgName + \"off.src\";\r\n";
				result +=  "		document [imgName].src = imgOff;\r\n";
				result +=  "		}\r\n";
				result +=  "}\r\n";
	
				result +=  "//-->\r\n";
	
	
				result +=  "</SCRIPT>\r\n";
				result +=  "<TR><TD><A HREF=\"" + aUser.getUrl("awaken") + "\" onMouseOver=\"img_act('toc1')\" onMouseOut=\"img_inact('toc1')\">\n";
				result +=  "<IMG ALIGN=left SRC=\"/images/gif/webpic/buttonl.gif\" BORDER=0 ALT=\"AWAKEN\" NAME=\"toc1\"></A><P>\n";
			} 
			else 
			{
				result +=  "<script language=\"JavaScript\">\r\n";
	
				result +=  "<!-- In hiding!\r\n";
				result +=  " browserName = navigator.appName;\r\n";
				result +=  "		   browserVer = parseInt(navigator.appVersion);\r\n";
				result +=  "			   if (browserName == \"Netscape\" && browserVer >= 3) version =\r\n";
				result +=  "\"n3\";\r\n";
				result +=  "			   else version = \"n2\";\r\n";
	
				result +=  "			   if (version == \"n3\"){				\r\n";
				result +=  "			   toc1on = new Image;\r\n";
				result +=  "			   toc1on.src = \"../images/gif/webpic/new/buttonk.gif\";\r\n";
				result +=  "			   toc2on = new Image;\r\n";
				result +=  "			   toc2on.src = \"../images/gif/webpic/new/buttonj.gif\";\r\n";
				result +=  "			   toc3on = new Image;\r\n";
				result +=  "			   toc3on.src =\"../images/gif/webpic/new/buttonr.gif\";\r\n";
	
				result +=  "			   toc1off = new Image;\r\n";
				result +=  "			   toc1off.src = \"../images/gif/webpic/buttonk.gif\";\r\n";
				result +=  "			   toc2off = new Image;\r\n";
				result +=  "			   toc2off.src = \"../images/gif/webpic/buttonj.gif\";\r\n";
				result +=  "			   toc3off = new Image;\r\n";
				result +=  "			   toc3off.src = \"../images/gif/webpic/buttonr.gif\";\r\n";
	
				result +=  "		}\r\n";
	
				result +=  "function img_act(imgName) {\r\n";
				result +=  "		if (version == \"n3\") {\r\n";
				result +=  "		imgOn = eval(imgName + \"on.src\";\r\n";
				result +=  "		document [imgName].src = imgOn;\r\n";
				result +=  "		}\r\n";
				result +=  "}\r\n";
	
				result +=  "function img_inact(imgName) {\r\n";
				result +=  "		if (version == \"n3\") {\r\n";
				result +=  "		imgOff = eval(imgName + \"off.src\";\r\n";
				result +=  "		document [imgName].src = imgOff;\r\n";
				result +=  "		}\r\n";
				result +=  "}\r\n";
	
				result +=  "//-->\r\n";
	
	
				result +=  "</SCRIPT>\r\n";
	
				result +=  "<TR><TD><A HREF=\"" + aUser.getUrl("quit") + "\" onMouseOver=\"img_act('toc2')\" onMouseOut=\"img_inact('toc2')\">\n";
				result +=  "<IMG ALIGN=left SRC=\"/images/gif/webpic/buttonj.gif\" BORDER=0 ALT=\"QUIT\" NAME=\"toc2\"></A><P>\n";
	
				result +=  "<TR><TD><A HREF=\"" + aUser.getUrl("sleep") + "\" onMouseOver=\"img_act('toc1')\" onMouseOut=\"img_inact('toc1')\">\n";
				result +=  "<IMG ALIGN=left SRC=\"/images/gif/webpic/buttonk.gif\" BORDER=0 ALT=\"SLEEP\" NAME=\"toc1\"></A><P>\n";
	
				result +=  "<TR><TD><A HREF=\"" + aUser.getUrl("clear") + "\" onMouseOver=\"img_act('toc3')\" onMouseOut=\"img_inact('toc3')\">\n";
				result +=  "<IMG ALIGN=left SRC=\"/images/gif/webpic/buttonr.gif\" BORDER=0 ALT=\"CLEAR\" NAME=\"toc3\"></A><P>\n";
			}
			result += "</TABLE>\n";
			result +=  "<MAP NAME=\"roosmap\">\n";
			result +=  "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,63,0,0,0\" HREF=\"" + aUser.getUrl("n") + "\">\n";
			result +=  "<AREA SHAPE=\"POLY\" COORDS=\"0,63,33,31,63,63,0,63\" HREF=\"" + aUser.getUrl("s") + "\">\n";
			result +=  "<AREA SHAPE=\"POLY\" COORDS=\"0,0,33,31,0,63,0,0\" HREF=\"" + aUser.getUrl("w") + "\">\n";
			result +=  "<AREA SHAPE=\"POLY\" COORDS=\"63,0,33,31,63,63,63,0\" HREF=\"" + aUser.getUrl("e") + "\">\n";
			result +=  "</MAP>\n";
		} /*end if fmudstruct->frames dude*/

		// print characters in room
		result += Persons.descriptionOfPersonsInRoom(this, aUser);
		// print items in room
		result += inventory();
		return result;
	}

	public String inventory()
	{
		return Database.getInventory(this);
	}

	public Item getItem(String adject1, String adject2, String adject3, String name)
	{
		return Database.getItem(adject1, adject2, adject3, name, this);
	}

}
