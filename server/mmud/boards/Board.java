/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/boards/Board.java,v 1.1 2004/05/22 11:36:04 karn Exp $
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
 * A message board in the mud.
 * Basically consists of a number of messages and a description and name of
 * the board.
 */
public class Board
{
	private String theDescription;
	private String theName;
	private int theId;

	/**
	 * Create this board.
	 * @param aDescription the description.
	 * @param aName the name.
	 * @param anId the identification.
	 */
	public Board(int anId, String aName, String aDescription)
	{
		theDescription = aDescription;
		theName = aName;
		theId = anId;
	} 

	/**
	 * Get the name.
	 * @return the name.
	 */
	public String getName()
	{
		return theName;
	}

	/**
	 * Get the description.
	 * @return the description.
	 */
	public String getDescription()
	{
		return theDescription;
	}

	/**
	 * Get the id.
	 * @return the id.
	 */
	public int getId()
	{
		return theId;
	}

	/**
	 * Post a message to the board.
	 * @param aUser the user who wishes to post.
	 * @param aMessage the message to post.
	 */
	public void post(User aUser, String aMessage)
	{
		Logger.getLogger("mmud").finer("");
		BoardsDb.sendBoard(getName(), aUser.getName(), aMessage);
	}

	/**
	 * Read the board. Reads message up to one week in the past.
	 * @return String containing messages.
	 */
	public String read()
	{
		Logger.getLogger("mmud").finer("");
		return read(BoardFormatEnum.BOARD);
	}

	/**
	 * Read the board. Reads message up to one week in the past.
	 * @return String containing messages.
	 * @param aFormat the format to be used for displaying the messages.
	 */
	public String read(BoardFormatEnum aFormat)
	{
		Logger.getLogger("mmud").finer("");
		return BoardsDb.readBoard(getName(), aFormat);
	}
}
