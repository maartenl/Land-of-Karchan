/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/rooms/Area.java,v 1.1 2005/09/10 22:41:47 karchan Exp $
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
 * Data class containing all the information with regards to a group of rooms
 * classified as an area.
 */
public class Area
{
	private String theName;
	private String theDescription;
	private String theShortDescription;

	/**
	 * Create a new area.
	 * @param aName the name of the area. This one uniquely identifies an area.
	 * @param aDescription the entire description of the area in question.
	 * @param aShortDescription one sentence containing the <I>full</I> name
	 * of the area.
	 */
	public Area(String aName, 
		String aDescription, 
		String aShortDescription)
	{
		theName = aName;
		theDescription = aDescription;
		theShortDescription = aShortDescription;
	}
	
	/**
	 * Returns the name of the area.
	 * @return String containing the name of the area.
	 */
	public String getName()
	{
		return theName;
	}

	/**
	 * Returns the description of the area.
	 * @return String containing the description of the area.
	 */
	public String getDescription()
	{
		return theDescription;
	}
	
	/**
	 * Returns the short description of the area. This is always
	 * a single short sentence.
	 * @return String containing the short description of the area.
	 */
	public String getShortDescription()
	{
		return theShortDescription;
	}

	/**
	 * Returns true if the areaname is the same in both objects.
	 */
	public boolean equals(Object r)
	{
		if (!(r instanceof Area))
		{
			return false;
		}
		Area u = (Area) r;
		if (u.theName == null)
		{
			return theName == null;
		}
		if (theName == null)
		{
			return false;
		}
		return theName.equals(u.theName);
	}
	
	/**
	 * Default toString implementation.
	 */
	public String toString()
	{
		return super.toString() + theName;
	}
}
