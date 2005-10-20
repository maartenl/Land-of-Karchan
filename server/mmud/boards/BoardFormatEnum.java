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
package mmud.boards;

import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.Vector;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;

/**
 * Different formats that can be used to display message boards.
 */
public class BoardFormatEnum
{
	private static int enumCount = 1;
	private int enumVal;
	private String name;

	private BoardFormatEnum(String str)
	{
	  name = str;
	  enumVal = enumCount;
	  enumCount++;
	}
	
	/**
	 * Returns the name of the format.
	 * @return format name.
	 */
	public String toString() 
	{ 
		return name; 
	}

	/**
	 * Returns the numerical representation of the format.
	 * @return identification integer.
	 */
	public int toInt() 
	{ 
		return enumVal; 
	}

	/**
	 * Default format used for displaying board messages.
	 * Format is in the form of:
	 * <PRE>
	 * <HR noshade>From: <B>Karn</B><BR>
	 * Posted: <B>Monday, January 2, 12:45:23</B><P>
	 * [message]
	 * <BR>
	 * </PRE>
	 */
	public static final BoardFormatEnum BOARD = 
		new BoardFormatEnum("board format");

	/**
	 * Simple format used for displaying board messages.
	 * Format is in the form of:
	 * <PRE>
	 * <HR noshade>Monday, January 2, 12:45:23<P>
	 * [message]
	 * <I>Karn</I>
	 * </PRE>
	 */
	public static final BoardFormatEnum SIMPLE = 
		new BoardFormatEnum("simple format");

}
