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

/**
 * Enumerated type for the male/female thing: 
 */
public final class Sex
{
	private String theSex;
	public static final Sex MALE = new Sex("male");
	public static final Sex FEMALE = new Sex("female");

	private Sex(String aSex)
	{
		theSex = aSex;
	}

	public static Sex createFromString(String aString)
	{
		if (aString.equals("female")) return FEMALE;
		return MALE;
	}

	/**
	 * returns either "male" or "female"
	 */
	public String toString()
	{
		return theSex;
	}

	/**
	 * returns either "his" or "her"
	 */
	public String posession()
	{
		return (theSex.equals("male") ? "his" : "her");
	}

	/**
	 * returns either "him" or "her"
	 */
	public String indirect()
	{
		return (theSex.equals("male") ? "him" : "her");
	}

	/**
	 * returns either "he" or "she"
	 */
	public String direct()
	{
		return (theSex.equals("male") ? "he" : "she");
	}

	public boolean equals(Object r)
	{
		if (!(r instanceof Sex))
		{
			return false;
		}
		Sex u = (Sex) r;
		if (u.theSex == null)
		{
			return theSex == null;
		}
		if (theSex == null)
		{
			return false;
		}
		return theSex.equals(u.theSex);
	}
}
