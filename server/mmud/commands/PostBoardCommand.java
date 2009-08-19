/*-------------------------------------------------------------------------
svninfo: $Id$
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Board License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Board License for more details.

You should have received a copy of the GNU General Board License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-------------------------------------------------------------------------*/
package mmud.commands;

import mmud.MudException;
import mmud.boards.Board;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.database.BoardsDb;
import mmud.database.MudDatabaseException;

/**
 * Reads a generic board.
 */
public abstract class PostBoardCommand extends NormalCommand
{

	public PostBoardCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * Posts a message of a user to a board.
	 * 
	 * @param aUser
	 *            the user posting to the board. Used to convey messages.
	 * @param aBoardName
	 *            the name of the board.
	 * @param aRoomId
	 *            the identification number of the room where this board is
	 *            active.
	 * @return boolean, false if a message was not properly posted.
	 * @throws MudException
	 *             room not found
	 */
	protected boolean postMessage(User aUser, String aBoardName, int aRoomId,
			String aMessage) throws MudException
	{
		if (aUser.getRoom().getId() != aRoomId)
		{
			return false;
		}
		Board myBoard = null;
		try
		{
			myBoard = BoardsDb.getBoard(aBoardName);
		} catch (MudDatabaseException e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			myBoard.post(aUser, aMessage);
		} catch (MudDatabaseException e)
		{
			e.printStackTrace();
			return false;
		}
		Persons.sendMessage(aUser, "%SNAME posted something on the "
				+ myBoard.getName() + " board.<BR>\r\n");
		return true;
	}

}
