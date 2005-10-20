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
package mmud.rooms;

import java.util.Vector;
import java.util.TreeMap;
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
public class Room implements Executable, AttributeContainer
{
	private int theId;
	private String theTitle;
	private String theDescription;
	private Room south, north, east, west, up, down;
	private int intsouth, intnorth, inteast, intwest, intup, intdown;
	private TreeMap theAttributes = new TreeMap();

	/**
	 * Create a new room.
	 * @param anId the identification number of the room
	 * @param aTitle the title of the room.
	 * @param aDescription the contents/description of the room.
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
	public Room(int anId, String aTitle, String aDescription, int asouth, int anorth, int aeast, int awest, int aup, int adown)
	{
		theId = anId;
		theTitle = aTitle;
		theDescription = aDescription;
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
	throws MudException
	{
		south = aSouth;
		intsouth = 0;
		Database.writeRoom(this);
	}

	/**
	 * Sets the room that is directly north.
	 * @param aNorth the room to the north.
	 */
	public void setNorth(Room aNorth)
	throws MudException
	{
		north = aNorth;
		intnorth = 0;
		Database.writeRoom(this);
	}

	/**
	 * Sets the room that is directly east.
	 * @param aEast the room to the east.
	 */
	public void setEast(Room aEast)
	throws MudException
	{
		east = aEast;
		inteast = 0;
		Database.writeRoom(this);
	}

	/**
	 * Sets the room that is directly west.
	 * @param aWest the room to the west.
	 */
	public void setWest(Room aWest)
	throws MudException
	{
		west = aWest;
		intwest = 0;
		Database.writeRoom(this);
	}

	/**
	 * Sets the room that is directly up.
	 * @param aUp the room to the up.
	 */
	public void setUp(Room aUp)
	throws MudException
	{
		up = aUp;
		intup = 0;
		Database.writeRoom(this);
	}

	/**
	 * Sets the room that is directly down. 
	 * @param aDown the room to the down.
	 */
	public void setDown(Room aDown)
	throws MudException
	{
		down = aDown;
		intdown = 0;
		Database.writeRoom(this);
	}

	/**
	 * Get the room that is directly south.
	 * @return Room the room to the south. Returns null if no room is south.
	 */
	public Room getSouth()
	throws MudException
	{
		if (south == null)
		{
			if (intsouth == 0)
			{
				return null;
			}
			south = Rooms.getRoom(intsouth);
		}
		return south;
	}

	/**
	 * Get the room that is directly north.
	 * @return Room the room to the north. Returns null if no room is north.
	 */
	public Room getNorth()
	throws MudException
	{
		if (north == null)
		{
			if (intnorth == 0)
			{
				return null;
			}
			north = Rooms.getRoom(intnorth);
		}
		return north;
	}

	/**
	 * Get the room that is directly east.
	 * @return Room the room to the east. Returns null if no room is east.
	 */
	public Room getEast()
	throws MudException
	{
		if (east == null)
		{
			if (inteast == 0)
			{
				return null;
			}
			east = Rooms.getRoom(inteast);
		}
		return east;
	}

	/**
	 * Get the room that is directly west.
	 * @return Room the room to the west. Returns null if no room is west.
	 */
	public Room getWest()
	throws MudException
	{
		if (west == null)
		{
			if (intwest == 0)
			{
				return null;
			}
			west = Rooms.getRoom(intwest);
		}
		return west;
	}

	/**
	 * Get the room that is directly up.
	 * @return Room the room to the up. Returns null if no room is up.
	 */
	public Room getUp()
	throws MudException
	{
		if (up == null)
		{
			if (intup == 0)
			{
				return null;
			}
			up = Rooms.getRoom(intup);
		}
		return up;
	}

	/**
	 * Get the room that is directly down.
	 * @return Room the room to the down. Returns null if no room is down.
	 */
	public Room getDown()
	throws MudException
	{
		if (down == null)
		{
			if (intdown == 0)
			{
				return null;
			}
			down = Rooms.getRoom(intdown);
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
	 * Sets the contents, also known as the description, of the room.
	 * @param aDescription the new description for the room.
	 */
	public void setDescription(String aDescription)
	throws MudException
	{
		theDescription = aDescription;
		Database.writeRoom(this);
	}

	/**
	 * Gets the contents, also known as the description, of the room.
	 * @return String containing the description for the room.
	 */
	public String getDescription()
	{
		return theDescription;
	}

	/**
	 * Returns area information.
	 * @return Area object containing the area information.
	 */
	public Area getArea()
	throws MudException
	{
		return Database.getArea(this);
	}

	/**
	 * Returns the description of the room, suitable for webbrowsers.
	 * @param aUser the user who needs to have the webpage.
	 * @return String containing a full description of the room suitable
	 * for webbrowsing.
	 */
	public String getDescription(User aUser)
	throws MudException
	{
		String result = (getDescription() == null ? 
			"<H1>Cardboard</H1>" +
			"Everywhere around you you notice cardboard. It seems as if this part" +
			" has either not been finished yet or you encountered an error" +
			" in retrieving the room description from the server.<P>"
			: getDescription());
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
	throws MudDatabaseException
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
	throws MudDatabaseException, MudException
	{
		return ItemsDb.getItemsFromRoom(adject1, adject2, adject3, name, this);
	}

	/**
	 * Retrieve items from this room.
	 * @param anItemDef item definition of items to be looked for.
	 * @return array containing item objects found.
	 * @see mmud.database.ItemsDb#getItemsFromRoom
	 */
	public Item[] getItems(ItemDef anItemDef)
	throws MudDatabaseException, MudException
	{
		return (Item[]) ItemsDb.getItemsFromRoom(anItemDef, this).toArray(new Item[0]);
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
			interp.addGlobalVariable("rooms", Rooms.create());                  
			interp.addGlobalVariable("persons", Persons.create());                  
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
		if (field_name.equals("description"))
		{
			if (value instanceof Null)
			{
				throw new FieldNotSupportedException(field_name + " not set, cannot be null.");
			}
			if (value instanceof String)
			{
				try
				{
					setDescription((String) value);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change description " + e);
				}
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not string.");
		}
		if (field_name.equals("east"))
		{
			if (value instanceof Null)
			{
				try
				{
					setEast(null);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change east " + e);
				}
				return;
			}
			if (value instanceof Room)
			{
				try
				{
					setEast((Room) value);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change east " + e);
				}
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not room.");
		}
		if (field_name.equals("west"))
		{
			if (value instanceof Null)
			{
				try
				{
					setWest(null);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change west " + e);
				}
			return;
			}
			if (value instanceof Room)
			{
				try
				{
					setWest((Room) value);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change west " + e);
				}
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not room.");
		}
		if (field_name.equals("north"))
		{
			if (value instanceof Null)
			{
				try
				{
					setNorth(null);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change north " + e);
				}
				return;
			}
			if (value instanceof Room)
			{
				try
				{
					setNorth((Room) value);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change north " + e);
				}
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not room.");
		}
		if (field_name.equals("south"))
		{
			if (value instanceof Null)
			{
				try
				{
					setSouth(null);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change south " + e);
				}
				return;
			}
			if (value instanceof Room)
			{
				try
				{
					setSouth((Room) value);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change south " + e);
				}
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not room.");
		}
		if (field_name.equals("up"))
		{
			if (value instanceof Null)
			{
				try
				{
					setUp(null);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change up " + e);
				}
				return;
			}
			if (value instanceof Room)
			{
				try
				{
					setUp((Room) value);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change up " + e);
				}
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not room.");
		}
		if (field_name.equals("down"))
		{
			if (value instanceof Null)
			{
				try
				{
					setDown(null);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change down " + e);
				}
				return;
			}
			if (value instanceof Room)
			{
				try
				{
					setDown((Room) value);
				}
				catch (MudException e)
				{
					throw new FieldNotSupportedException(field_name + " unable to change down " + e);
				}
				return;
			}
			throw new FieldNotSupportedException(field_name + " not set, not room.");
		}
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
		try
		{
			if (field_name.equals("room"))
			{
				return new Integer(getId());
			}
			if (field_name.equals("east"))
			{
				return getEast();
			}
			if (field_name.equals("west"))
			{
				return getWest();
			}
			if (field_name.equals("north"))
			{
				return getNorth();
			}
			if (field_name.equals("south"))
			{
				return getSouth();
			}
			if (field_name.equals("up"))
			{
				return getUp();
			}
			if (field_name.equals("down"))
			{
				return getDown();
			}
		}
		catch (MudException e)
		{
			throw new FieldNotSupportedException(field_name + " error retrieving value. " + e);
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
		if (method_name.equals("getItems"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof Integer))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain an Integer as argument.");
				}
				Item[] b = null;
				try
				{
					b = getItems(ItemDefs.getItemDef(
						((Integer) arguments[0]).intValue() 
						));
				}
				catch (MudDatabaseException e)
				{
					throw new MethodNotSupportedException(e.getMessage());
				}
				catch (MudException e2)
				{
					throw new MethodNotSupportedException(e2.getMessage());
				}
				return b;
			}
		}
		if (method_name.equals("addItem"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof Integer))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain an Integer as argument.");
				}
				ItemDef myItemDef = null;
				try
				{
					myItemDef = ItemDefs.getItemDef(
						((Integer) arguments[0]).intValue());
				}
				catch (MudDatabaseException e)
				{
					throw new MethodNotSupportedException(e.getMessage());
				}
				if (myItemDef == null)
				{
					throw new MethodNotSupportedException(method_name + " tried to use an unknown item definition.");
				}
				Item myItem = null;
				try
				{
					myItem = ItemsDb.addItem(myItemDef);
				}
				catch (MudDatabaseException e2)
				{
					throw new MethodNotSupportedException(e2.getMessage());
				}
				try
				{
					ItemsDb.addItemToRoom(myItem, this);
				}
				catch (ItemDoesNotExistException e)
				{
					throw new MethodNotSupportedException(e.getMessage());
				}
				catch (MudDatabaseException e2)
				{
					throw new MethodNotSupportedException(e2.getMessage());
				}
				Database.writeLog("root", "created item (" + myItem + ") in room " + getId());
				return myItem;
			}
		}
		return methodAttribute(method_name, arguments, ctxt);
//		throw new MethodNotSupportedException(method_name + " not found.");
	}

	public Object methodAttribute(String method_name, Object[]
		arguments, ExecutableContext ctxt)
	throws MethodNotSupportedException
	{
		Logger.getLogger("mmud").finer("method_name=" + method_name +
			", arguments=" + arguments);
		if (method_name.equals("getAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as argument.");
				}
				Attribute mAttrib = getAttribute((String) arguments[0]);
				if (mAttrib == null)
				{
					return null;
				}
				if (mAttrib.getValueType().equals("string"))
				{
					return mAttrib.getValue();
				}
				if (mAttrib.getValueType().equals("boolean"))
				{
					return new Boolean(mAttrib.getValue());
				}
				if (mAttrib.getValueType().equals("integer"))
				{
					try
					{
						return new Integer(mAttrib.getValue());
					}
					catch (NumberFormatException e)
					{
						throw new MethodNotSupportedException(method_name +
						" attribute " + mAttrib.getName() + " does not contain expected number.");
					}
				}
				throw new MethodNotSupportedException(method_name +
					" unknown value type in attribute " + 
					mAttrib.getName() + ". (" + 
					mAttrib.getValueType() + ")");
			}
		}
		if (method_name.equals("removeAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as argument.");
				}
				try
				{
					removeAttribute((String) arguments[0]);
				}
				catch (MudException e)
				{
					throw new MethodNotSupportedException(method_name +
						" could not remove attribute.");
				}
				Database.writeLog("root", "removed attribute (" + arguments[0] + ") from room " +
					getId());
				return null;
			}
		}
		if (method_name.equals("setAttribute"))
		{
			if (arguments.length == 2)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as first argument.");
				}
				String mType = "object";
				if (arguments[1] instanceof String)
				{
					mType = "string";
				}
				if (arguments[1] instanceof Integer)
				{
					mType = "integer";
				}
				if (arguments[1] instanceof Boolean)
				{
					mType = "boolean";
				}
				Attribute mAttrib = new Attribute((String) arguments[0],
					arguments[1] + "", 
					mType);
				try
				{
					setAttribute(mAttrib);
				}
				catch (MudException e)
				{
					throw new MethodNotSupportedException(method_name +
						" could not set attribute.");
				}
				Database.writeLog("root", "set attribute (" + arguments[0] + ") in room " +
					getId());
				return null;
			}
		}
		if (method_name.equals("isAttribute"))
		{
			if (arguments.length == 1)
			{
				if (!(arguments[0] instanceof String))
				{
					throw new MethodNotSupportedException(method_name +
						" does not contain a String as first argument.");
				}
				return new Boolean(isAttribute((String) arguments[0]));
			}
		}
		throw new MethodNotSupportedException(method_name + " not found.");
	}

	/**
	 * Set or add an attribute of this item.
	 * @param anAttribute the attribute to be added/set.
	 */
	public void setAttribute(Attribute anAttribute)
	throws MudException
	{
		AttributeDb.setAttribute(anAttribute, this);
		theAttributes.put(anAttribute.getName(), anAttribute);
	}

	/**
	 * returns the attribute found with name aName or null
	 * if it does not exist.
	 * @param aName the name of the attribute to search for
	 * @return Attribute object containing the attribute foudn or null.
	 */
	public Attribute getAttribute(String aName)
	{
		Attribute myAttrib = (Attribute) theAttributes.get(aName);
		return myAttrib;
	}

	/**
	 * Remove a specific attribute from the item.
	 * @param aName the name of the attribute to be removed.
	 */
	public void removeAttribute(String aName)
	throws MudException
	{
		theAttributes.remove(aName);
		AttributeDb.removeAttribute(new Attribute(aName, null, null), this);
	}

	/**
	 * returns true if the attribute with name aName
	 * exists.
	 * @param aName the name of the attribute to check
	 * @return boolean, true if the attribute exists for this item,
	 * otherwise returns false.
	 */
	public boolean isAttribute(String aName)
	{
		return theAttributes.containsKey(aName);
	}

}
