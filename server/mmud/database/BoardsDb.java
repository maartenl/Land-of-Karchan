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
package mmud.database; 

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import mmud.*;
import mmud.characters.*;
import mmud.items.*;
import mmud.rooms.*;
import mmud.boards.*;

/**
 * Used for queries towards the database regarding Boards.
 * @see Database
 */
public class BoardsDb
{

	public static final String sqlPostBoardString = 
		"insert into mm_boardmessages (boardid, name, message) " +
		"select id, ?, ? " +
		"from mm_boards " +
		"where name = ?";
		// sequence: username, message, boardname
	public static final String sqlReadBoardString = 
		"select * " +
		"from mm_boards " + 
		"where mm_boards.name = ?";
		// boardname
	public static final String sqlReadBoardMessageString = 
		"select concat('<HR noshade>From: <B>', mm_boardmessages.name, '</B><BR>" +
		"Posted: <B>', date_format(posttime, '%W, %M %e, %H:%i:%s'), '</B><P>\r\n', message, " +
		"'<BR>') as message " +
		"from mm_boardmessages, mm_boards " + 
		"where mm_boards.name = ? and " +
		"mm_boards.id = mm_boardmessages.boardid and " +
		"week(posttime)=week(now()) and year(posttime)=year(now()) " +
		"order by posttime";
		// boardname
	public static final String sqlReadBoardMessage2String = 
		"select concat('<HR>" +
		"', date_format(posttime, '%W, %M %e, %H:%i'), '<P>\r\n', message, " +
		"'<p><I>', mm_boardmessages.name, '</I>') as message " +
		"from mm_boardmessages, mm_boards " + 
		"where mm_boards.name = ? and " +
		"mm_boards.id = mm_boardmessages.boardid and " +
		"week(posttime)=week(now()) and year(posttime)=year(now()) " +
		"order by posttime";
		// boardname

	/**
	 * returns an object identifying the board.
	 * @param aBoard the String containing the name of the board.
	 * @return a Board object containing relevant information.
	 * Returns null pointer if the board could not be found.
	 */
	public static Board getBoard(String aBoard)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Board newBoard = null;
		int j = 1;
		try
		{

		PreparedStatement sqlListBoardsMsg =
			Database.prepareStatement(sqlReadBoardString);
		sqlListBoardsMsg.setString(1, aBoard);
		res = sqlListBoardsMsg.executeQuery();
		if (res != null)
		{
			while (res.next())
			{
				newBoard = new Board(res.getInt("id"),
					res.getString("name"),
					res.getString("description"));
			}
			res.close();
		}
		sqlListBoardsMsg.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
		return newBoard;
	}

	/**
	 * returns a list of messages of the past week regarding a specific
	 * public board.
	 * @param aBoard the String containing the name of the board.
	 * @param aFormat the format to use for displaying messages.
	 * @return String containing a list of messages.
	 */
	public static String readBoard(String aBoard, BoardFormatEnum aFormat)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		int j = 1;
		String query = sqlReadBoardMessageString;
		if (aFormat == BoardFormatEnum.SIMPLE)
		{
			query = sqlReadBoardMessage2String;
		}
		else
		{
			query = sqlReadBoardMessageString;
		}
		StringBuffer result = new StringBuffer();
		try
		{

			PreparedStatement sqlListBoardsMsg =
				Database.prepareStatement(query);
			sqlListBoardsMsg.setString(1, aBoard);
			res = sqlListBoardsMsg.executeQuery();
			if (res != null)
			{
				while (res.next())
				{
					result.append(res.getString("message"));
				}
				result.append("<HR>");
				res.close();
			}
			sqlListBoardsMsg.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
		return result.toString();
	}

	/**
	 * Post a message on one of the boards.
	 * @param aUserName the user who wishes to post.
	 * @param aBoardName the name of the board to post to.
	 * @param aMessage the body of the post.
	 */
	public static void sendBoard(String aBoardName, String aUserName, 
		String aMessage)
	{
		Logger.getLogger("mmud").finer("");
		try
		{
		PreparedStatement sqlSendBoard = Database.prepareStatement(sqlPostBoardString);
		// sequence: username, message, boardname
		sqlSendBoard.setString(1, aUserName);
		sqlSendBoard.setString(2, aMessage);
		sqlSendBoard.setString(3, aBoardName);
		int res = sqlSendBoard.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSendBoard.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}


}
