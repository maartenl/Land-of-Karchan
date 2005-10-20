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

package mmud.items;

import java.util.Vector;
import java.util.logging.Logger;

import mmud.Constants;

/**
 * Collection class for storing of items.
 */
public class Inventory
{
	private Vector theItems;

	/**
	 * Creates an inventory object with an empty list.
	 */
	public Inventory()
	{
		Logger.getLogger("mmud").finer("");
		theItems = new Vector();
	}

	/**
	 * appends an item to the list of items in this inventory
	 * (use with care)
	 * @param anItem the item to be added.
	 */
	public void append(Item anItem)
	{
		theItems.addElement(anItem);
	}

	/**
	 * deletes an item from the list of items in this inventory
	 * (use with care)
	 * @param anItem the item to be deleted.
	 */
	public void delete(Item anItem)
	{
	}

	/** 
	 * Add an item.
	 * @param anItem the item to be added.
	 */
	public void add(Item anItem)
	{
		Logger.getLogger("mmud").finer("");
		theItems.addElement(anItem);
	}

	/** 
	 * Remove an item from the list.
	 * @param anItem the item to be removed.
	 */
	public void remove(Item anItem)
		throws ItemException
	{
		Logger.getLogger("mmud").finer("");
		if (!theItems.remove(anItem))
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.ITEMDOESNOTEXISTERROR);
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * returns the item existing in the inventory that fits
	 * the desription.
	 * @param parsedarray required array parsedarray[startpos..endpos-1].
	 * @param startpos first required part of the description of the item.
	 * @param endpos last required part of the description of the item.
	 * endpos must be <I>at least</I> startpos+1.
	 */
	public Item getItem(String[] parsedarray, int startpos, int endpos)
	{
		Logger.getLogger("mmud").finer("");
		for (int i=0;i<theItems.size();i++)
		{
			Item myItem = (Item) theItems.elementAt(i);
			if (myItem != null)
			{
				if (myItem.getVerb().equalsIgnoreCase(parsedarray[endpos-1]))
				{
					boolean found = true;
					for (int j=startpos;j<endpos-1;j++)
					{
						if ( (!myItem.getAdjective1().equalsIgnoreCase(parsedarray[j])) &&
							(!myItem.getAdjective2().equalsIgnoreCase(parsedarray[j])) &&
							(!myItem.getAdjective3().equalsIgnoreCase(parsedarray[j])) )
						{
							found = false;
						}
					}
					if (found)
					{
						Logger.getLogger("mmud").info("returns: " + myItem);
						return myItem;
					}
				}
			}
		}
		return null;
	}

	/**
	 * returns the number of items existing in the inventory that fits
	 * the description.
	 * @param parsedarray the required array parsedarray[startpos..endpos-1].
	 * @param startpos the first required part of the description of the
	 * item.
	 * @param endpos the last required part of the description of the item.
	 * endpos must be <I>at least</I> startpos+1.
	 * @return integer providing the number of items available in the
	 * inventory list.
	 */
	public int getItemCount(String[] parsedarray, int startpos, int endpos)
	{
		int myCount = 0;
		Logger.getLogger("mmud").finer("");
		for (int i=0;i<theItems.size();i++)
		{
			Item myItem = (Item) theItems.elementAt(i);
			if (myItem != null)
			{
				if (myItem.getVerb().equalsIgnoreCase(parsedarray[endpos-1]))
				{
					boolean found = true;
					for (int j=startpos;j<endpos-1;j++)
					{
						if ( (!myItem.getAdjective1().equalsIgnoreCase(parsedarray[j])) &&
							(!myItem.getAdjective2().equalsIgnoreCase(parsedarray[j])) &&
							(!myItem.getAdjective3().equalsIgnoreCase(parsedarray[j])) )
						{
							found = false;
						}
					}
					if (found)
					{
						myCount++;found = false;
					}
				}
			}
		}
		Logger.getLogger("mmud").info("returns: " + myCount);
		return myCount;
	}

	/**
	 * Attempts to create a description of all items in the inventory.
	 * @return String containing description of all items in the inventory
	 * in the form of a bulleted html list.
	 */
	public String returnItemList()
	{
		StringBuffer myStringBuffer = new StringBuffer("");
		for (int i=0;i<theItems.size();i++)
		{
			Item myItem = (Item) theItems.elementAt(i);
			if ((myItem != null) && (!myItem.isAttribute("invisible")) )
			{
				myStringBuffer.append("<LI>a " + myItem.getAdjective1() + " " + myItem.getAdjective2() + " " + myItem.getVerb() + ".<BR>\r\n");
			}
		}
		return myStringBuffer.toString();
	}

	/**
	 * Attempts to create a decsription of all items in a room.
	 * @return String containing description of all items in the room
	 * in the form of a bulleted html list.
	 */
	public String returnRoomItemList()
	{
		StringBuffer myStringBuffer = new StringBuffer("");
		for (int i=0;i<theItems.size();i++)
		{
			Item myItem = (Item) theItems.elementAt(i);
			if ( (myItem != null) && (!myItem.isAttribute("invisible")) )
			{
				myStringBuffer.append("A " + myItem.getAdjective1() + " " + myItem.getAdjective2() + " " + myItem.getVerb() + " is here .<BR>\r\n");
			}
		}
		return myStringBuffer.toString();
	}

	/**
	 * Standard tostring implementation.
	 * @return String containing a description of all items.
	 * @see Item#toString
	 */
	public String toString()
	{
		StringBuffer myStringBuffer = new StringBuffer("");
		for (int i=0;i<theItems.size();i++)
		{
			Item myItem = (Item) theItems.elementAt(i);
			if (myItem != null)
			{
				myStringBuffer.append(i + " " + myItem + "\n");
			}
		}
		return myStringBuffer.toString();
	}

}

