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

import java.util.Vector;
import java.util.Hashtable;
import java.util.logging.Logger;
import java.io.StringReader;

import simkin.*;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Data class containing all the information with regards to a room in the
 * mud.
 */
public class Room implements Executable
{
	private int theId;
	private String theTitle;
	private String theContents;
	private Room south, north, east, west, up, down;
	private int intsouth, intnorth, inteast, intwest, intup, intdown;

	/**
	 * Create a new room.
	 * @param anId the identification number of the room
	 * @param aTitle the title of the room.
	 * @param aContents the contents/description of the room.
	 * @param asouth identification of the room to the south (0 if not
	 * applicable)
	 * @param anorth identification of the room to the north (0 if not
	 * applicable)
	 * @param aeast identification of the room to the east (0 if not
	 * applicable)
	 * @param awest identification of the room to the west (0 if not
	 * applicable)
	 * @param aup identification of the room up (0 if not
	 * applicable)
	 * @param adown identification of the room down (0 if not
	 * applicable)
	 */
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
	
	/**
	 * Returns the id of the room.
	 * @return integer containing the id of the room.
	 */
	public int getId()
	{
		return theId;
	}

	/**
	 * Sets the room that is directly south.
	 * @param aSouth the room to the south.
	 */
	public void setSouth(Room aSouth)
	{
		south = aSouth;
		intsouth = 0;
	}

	/**
	 * Sets the room that is directly north.
	 * @param aSouth the room to the north.
	 */
	public void setNorth(Room aNorth)
	{
		north = aNorth;
		intnorth = 0;
	}

	/**
	 * Sets the room that is directly east.
	 * @param aSouth the room to the east.
	 */
	public void setEast(Room aEast)
	{
		east = aEast;
		inteast = 0;
	}

	/**
	 * Sets the room that is directly west.
	 * @param aSouth the room to the west.
	 */
	public void setWest(Room aWest)
	{
		west = aWest;
		intwest = 0;
	}

	/**
	 * Sets the room that is directly up.
	 * @param aSouth the room to the up.
	 */
	public void setUp(Room aUp)
	{
		up = aUp;
		intup = 0;
	}

	/**
	 * Sets the room that is directly down.
	 * @param aSouth the room to the down.
	 */
	public void setDown(Room aDown)
	{
		down = aDown;
		intdown = 0;
	}

	/**
	 * Get the room that is directly south.
	 * @return Room the roomo to the south.
	 */
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

	/**
	 * Get the room that is directly north.
	 * @return Room the roomo to the north.
	 */
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

	/**
	 * Get the room that is directly east.
	 * @return Room the roomo to the east.
	 */
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

	/**
	 * Get the room that is directly west.
	 * @return Room the roomo to the west.
	 */
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

	/**
	 * Get the room that is directly up.
	 * @return Room the roomo to the up.
	 */
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

	/**
	 * Get the room that is directly down.
	 * @return Room the roomo to the down.
	 */
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

	/**
	 * standard to string implementation.
	 * @return String containing both the identification number and the
	 * title.
	 */
	public String toString()
	{
		return theId + ":" + theTitle;
	}

	/**
	 * Returns the description of the room, suitable for webbrowsers.
	 * @param aUser the user who needs to have the webpage.
	 * @return String containing a full description of the room suitable
	 * for webbrowsing.
	 */
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

	/**
 	 * Return the inventory of this room. I.e. all items currently
	 * present in the room.
	 * @return String a string representation of a HTML bulleted list
	 * of all items.
	 * @see ItemsDb#getInventory
	 */
	public String inventory()
	{
		return ItemsDb.getInventory(this);
	}

	/**
	 * Retrieve items from this room.
	 * @param adject1 the first adjective
	 * @param adject2 the second adjective
	 * @param adject3 the third adjective
	 * @param name the name of the item
	 * @return Vector containing item objects found.
	 * @see mmud.database.ItemsDb#getItemsFromRoom
	 */
	public Vector getItems(String adject1, String adject2, String adject3, String name)
	{
		return ItemsDb.getItemsFromRoom(adject1, adject2, adject3, name, this);
	}

	/**
	 * Implements the equals method.
	 * @param o object to be compared.
	 * @return boolean, true if the id of the rooms are the same, otherwise
	 * false.
	 */
	public boolean equals(Object o)
	{
		if (!(o instanceof Room))
		{
			return false;
		}
		return ((Room) o).getId() == getId();
	}

	/**
	 * Sends a message to everyone in this room. Ideal to communicate
	 * room events.
	 * @param aMessage the message to be sent
	 */
	public void sendMessage(String aMessage) 
	{
		Persons.sendMessage(this, aMessage);
	}

	/**
	 * Executes a script with this room as the focus point.
	 * @param aScript a String containing the script to execute. The
	 * following commands in the script are possible:
	 * <ul>
	 * <li>sendMessage(&lt;message&gt;);
	 * </ul>
	 * @param aXmlMethodName the name of the method in the xml
	 * script that you wish to execute.
	 * @see <A HREF="http://www.simkin.co.uk">Simkin</A>
	 * @throws MudException if something goes wrong.
	 */
	public void runScript(String aXmlMethodName, String aScript)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		try
		{
			// Create an interpreter and a context
			Interpreter interp=new Interpreter();
			ExecutableContext ctxt=new ExecutableContext(interp);
																																							
			// create an XMLExecutable object with the xml string
			XMLExecutable executable =
				new XMLExecutable(getId() + "", new StringReader(aScript));
																																							
			// call the "main" method with the person as an argument
			Object args[]={this};
			executable.method(aXmlMethodName, args, ctxt);
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
																																							
	public void setValue(String field_name, String attrib_name,
		Object value, ExecutableContext ctxt)
	throws FieldNotSupportedException
	{
		Logger.getLogger("mmud").finer("field_name=" + field_name +
			", atttrib_name=" + attrib_name + ", value=" +
			value + "[" + value.getClass() + "]");
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
			return new Integer(getId());
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
		}
		throw new MethodNotSupportedException(method_name + " not found.");
	}

}
