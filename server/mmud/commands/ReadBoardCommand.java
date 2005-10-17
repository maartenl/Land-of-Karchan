/*-------------------------------------------------------------------------
cvsinfo: $Header: /karchan/mud/cvsroot/server/mmud/commands/ReadBoardCommand.java,v 1.1 2004/10/30 22:56:41 karn Exp $
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
package mmud.commands;  

import java.util.logging.Logger;
import java.util.Vector;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.database.*;
import mmud.boards.*;

/**
 * Reads a general board.
 */
public abstract class ReadBoardCommand extends NormalCommand
{
	private String theResult = null;

	public ReadBoardCommand(String aRegExpr)
	{
		super(aRegExpr);
	}

	/**
	 * Reads a board for a user.
	 * @param aUser the user reading the board. Used to convey messages.
	 * @param aBoardName the name of the board. 
	 * @param aRoomId the identification number of the room where
	*  this board is active.
	 */
	protected boolean readMessage(User aUser, 
		String aBoardName, int aRoomId)
	{
		if (aUser.getRoom().getId() != aRoomId)
		{
			return false;
		}
		theResult = null;
		Board myBoard = BoardsDb.getBoard(aBoardName);
		theResult = myBoard.getDescription() + 
			myBoard.read() + aUser.printForm();
		Persons.sendMessage(aUser, "%SNAME read%VERB2 the " + myBoard.getName() 
			+ " board.<BR>\r\n");
		return true;
	}

	public String getResult()
	{
		Logger.getLogger("mmud").finer("");
		return theResult;
	}

}
