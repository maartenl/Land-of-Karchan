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

package mmud.races;

import java.util.logging.Logger;

/**
 * Enumerated type for the male/female thing: 
 */
public final class RaceFactory
{

	private static final Race theRaces[] = 
		{ new Buggie(),
		new Chipmunk(),
		new Deity(),
		new Dragon(),
		new Duck(),
		new Dwarf(),
		new Elf(),
		new Fox(),
		new Human(),
		new Ooze(),
		new Orc(),
		new Rabbit(),
		new Ropegnaw(),
		new Slug(),
		new Spider(),
		new Troll(),
		new Turtle(),
		new Wolf(),
		new Wyvern(),
		new Zombie() }; 

	/**
	 * Little factory method for creating a Race object.
	 * @param aString string describing the race object
	 * to be created: "wolf" or "elf" or something.
	 * @return Race object
	 * @throws RuntimeException if the race is unknown.
	 * In this case we do not know what to do.
	 */
	public static Race createFromString(String aString)
	{
		Logger.getLogger("mmud").finer("aString=" + aString);
		int i = 0;
		while (i < theRaces.length)
		{
			if (theRaces[i].toString().equals(aString))
			{
				Logger.getLogger("mmud").finer("returns " + theRaces[i]);
				return theRaces[i];
			}
			i++;
		}
		throw new RuntimeException("Illegal race for character!!!");
	}

}
