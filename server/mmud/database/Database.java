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

/**
 * The central class that takes care of all the commands that are to be
 * transmitted to the MySQL database server. This database class is
 * basically the class that effectively hides the database side of things
 * and only provides standard mud functions to the rest of the mud.
 */
public class Database
{

	private static Connection theConnection = null;

	public static String sqlGetUserString = "select * from mm_usertable where name = ? and active = 0 and god < 2";
	public static String sqlGetPersonsString = "select * from mm_usertable where active = 1";
	public static String sqlSetSessPwdString = "update mm_usertable set lok = ? where name = ?";
	public static String sqlActivateUserString = "update mm_usertable set active=1, lastlogin=date_sub(now(), interval 2 hour) where name = ?";
	public static String sqlDeActivateUserString = "update mm_usertable set active=0, lok=\"\", lastlogin=date_sub(now(), interval 2 hour) where name = ?";
	public static String sqlCreateUserString = "insert into mm_usertable " +
		"(name, address, password, title, realname, email, race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg, lok, active, lastlogin, birth) "+
		"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, now(), now())";
	public static String sqlSetTitleString = "update mm_usertable set title=? where name = ?";
	public static String sqlSetDrinkstatsString = "update mm_usertable set drinkstats=? where name = ?";
	public static String sqlSetEatstatsString = "update mm_usertable set eatstats=? where name = ?";
	public static String sqlExistsUserString = "select 1 from mm_usertable where name = ?";
	public static String sqlSetSleepString = "update mm_usertable set sleep=? where name = ?";
	public static String sqlSetRoomString = "update mm_usertable set room=? where name = ?";
	public static String sqlSetWhimpyString = "update mm_usertable set whimpy = ? where name = ?";
	public static String sqlUpdatePkillString = "update mm_usertable set fightable = ? where name = ?";

	public static String sqlYouHaveNewMailString = "select count(*) as count from mm_mailtable where toname = ? and newmail = 1";
	public static String sqlListMailString = "select name, haveread, newmail, header from mm_mailtable where toname = ? order by whensent asc";
	public static String sqlReadMailString = "select * from mm_mailtable where toname = ? order by whensent asc";
	public static String sqlDeleteMailString = "delete from mm_mailtable where name = ? and toname = ? and whensent = ? ";
	public static String sqlUpdateMailString = "update mm_mailtable set haveread=1 where name = ? and toname = ? and whensent = ? ";
	public static String sqlSendMailString = "insert into mm_mailtable " +
		"(name, toname, header, whensent, haveread, newmail, message) " +
		"values (?, ?, ?, now(), 0, 1, ?)";

	public static String sqlGetRoomString = "select * from mm_rooms where id = ?";

	public static String sqlGetErrMsgString = "select description from mm_errormessages where msg = ?";
	public static String sqlGetBan1String = "select count(name) as count from mm_sillynamestable where ? like name";
	public static String sqlGetBan2String = "select count(name) as count from mm_unbantable where name = ?";
	public static String sqlGetBan3String = "select count(address) as count from mm_bantable where ? like address";
	public static String sqlGetLogonMessageString = "select message from mm_logonmessage where id=0";
	public static String sqlGetHelpString = "select contents from mm_help where command = ?";

	public static String sqlGetCharAttributesString =
		"select * from mm_charattributes "
		+ "where charname = ?";
	public static String sqlGetItemAttributesString =
		"select * from mm_itemattributes "
		+ "where id = ?";
	public static String sqlGetRoomAttributesString =
		"select * from mm_roomattributes "
		+ "where id = ?";

	/**
	 * Connects to the database using an url. The url looks something like 
	 * "jdbc:mysql://localhost.localdomain/mud?user=root&password=".
	 * Uses the classname in Constants.dbjdbcclass to get the right class
	 * for interfacing with the database server. If the database used
	 * is changed (or to be more specific the jdbc driver is changed)
	 * change the constant.
	 * @throws InstantiationException happens when it is impossible to
	 * instantiate the proper database class, from the class name
	 * as provided by Constants.dbjdbcclass.
	 * @throws ClassNotFoundException happens when it is impossible to 	
	 * find the proper database class, from the class name
	 * as provided by Constants.dbjdbcclass.
	 * @throws IllegalAccessException happens when it is not possible
	 * to create the database class because access restrictions apply.
	 * @throws SQLException happens when a connection to the database
	 * Server could not be established.
	 */
	public static void connect()
		throws SQLException,
			InstantiationException,
			ClassNotFoundException,
			IllegalAccessException
	{
		Logger.getLogger("mmud").finer("");
		Class.forName(Constants.dbjdbcclass).newInstance();
	
//		DriverManager.setLogWriter(System.out);

		if (theConnection != null)
		{
			// connection already alive.
			// doing nothing;
			return;
		}

		String theUrl = Constants.dburl + "://" +
			Constants.dbhost + 
			Constants.dbdomain + "/" +
			Constants.dbname + "?user=" +
			Constants.dbuser + "&password=" +
			Constants.dbpasswd;
		Logger.getLogger("mmud").info("using url " + theUrl);
		theConnection = DriverManager.getConnection(theUrl);
	}

	/**
	 * Closes the connection to the database.
	 * @throws SQLException occurs when something goes wrong.
	 */
	public static void disconnect()
		throws SQLException
	{
		Logger.getLogger("mmud").finer("");
		theConnection.close();
		theConnection = null;
	}

	/**
	 * Refresh the connection to the database. Basically performs a
	 * disconnect/reconnect.
	 * @throws InstantiationException happens when it is impossible to
	 * instantiate the proper database class, from the class name
	 * as provided by Constants.dbjdbcclass.
	 * @throws ClassNotFoundException happens when it is impossible to 	
	 * find the proper database class, from the class name
	 * as provided by Constants.dbjdbcclass.
	 * @throws IllegalAccessException happens when it is not possible
	 * to create the database class because access restrictions apply.
	 * @throws SQLException happens when a connection to the database
	 * Server could not be established.
	 */
	public static void refresh()
		throws SQLException,
			InstantiationException,
			ClassNotFoundException,
			IllegalAccessException
	{
		Logger.getLogger("mmud").finer("");
		if (theConnection != null)
		{
			disconnect();
		}
		connect();
	}

	/**
	 * Create a prepared statement.
	 * @param aQuery the sql query used to create the statement.
	 * @return PreparedStatement object, can be used to add variables and 
	 * do a query.
	 * @see executeUpdate
	 * @see executeQuery
	 */
	static PreparedStatement prepareStatement(String aQuery)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("");
		PreparedStatement aStatement = null;
		try
		{
			aStatement = theConnection.prepareStatement(aQuery);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return aStatement;
	}

	/**
	 * Retrieve a character from the database.
	 * @param aName the name of the character. This uniquely identifies
	 * any character in the database.
	 * @return User containing all information
	 * DEBUG!! Why returns User, why not Person?
	 */
	public static User getUser(String aName)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetUser = theConnection.prepareStatement(sqlGetUserString);
//		sqlGetUser.setBigDecimal
//		sqlGetUser.setInt
		sqlGetUser.setString(1, aName);
		res = sqlGetUser.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
			return null;
		}
		if (res.first())
		{
			myUser  = new User(
				res.getString("name"), 
				res.getString("password"),
				res.getString("address"),
				res.getString("title"),
				res.getString("realname"),
				res.getString("email"),
				res.getString("race"),
				Sex.createFromString(res.getString("sex")),
				res.getString("age"),
				res.getString("length"),
				res.getString("width"),
				res.getString("complexion"),
				res.getString("eyes"),
				res.getString("face"),
				res.getString("hair"),
				res.getString("beard"),
				res.getString("arm"),
				res.getString("leg"),
				res.getInt("sleep") == 1,
				res.getInt("god") == 1,
				res.getString("lok"),
				res.getInt("whimpy"),
				res.getInt("fightable")==1,
				res.getInt("drinkstats"),
				res.getInt("eatstats"),
				Rooms.getRoom(res.getInt("room")));
		}
		res.close();
		sqlGetUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		getCharAttributes(myUser);
		return myUser;
	}

	/**
	 * Checks to see that a user exists in the database.
	 * @param aName the name that uniquely identifies the character.
	 * @return boolean, true if found, false otherwise.
	 */
	public static boolean existsUser(String aName)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		boolean myBoolean = false;
		try
		{

		PreparedStatement sqlGetUser = theConnection.prepareStatement(sqlExistsUserString);
		sqlGetUser.setString(1, aName);
		res = sqlGetUser.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
		}
		else
		{
			myBoolean  = res.first();
			res.close();
		}
		sqlGetUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return myBoolean;
	}

	/**
	 * Returns the room information based on the roomnumber.
	 * @param roomnr integer containing the number of the room.
	 * @return Room object containing all information.
	 */
	public static Room getRoom(int roomnr)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Room myRoom = null;
		try
		{

		PreparedStatement sqlGetRoom = theConnection.prepareStatement(sqlGetRoomString);
//		sqlGetRoom.setBigDecimal
//		sqlGetRoom.setInt
		sqlGetRoom.setInt(1, roomnr);
		res = sqlGetRoom.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
			return null;
		}
		res.first();
		myRoom  = new Room(
			res.getInt("id"),
			"title as yet unknown", 
			res.getString("contents"),
			res.getInt("south"), res.getInt("north"),
			res.getInt("east"), res.getInt("west"), 
			res.getInt("up"), res.getInt("down"));
		res.close();
		sqlGetRoom.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return myRoom;
	}

	/**
	 * Add all attributes found in the database regarding a certain item to
	 * that item object. Basically fills up the attribute list of the item.
	 * @param anItem item object
	 */
	public static void getItemAttributes(Item anItem)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		try
		{
		PreparedStatement sqlGetItemAttributes = theConnection.prepareStatement(sqlGetItemAttributesString);
		sqlGetItemAttributes.setInt(1, anItem.getId());
		res = sqlGetItemAttributes.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
			return;
		}
		while (res.next())
		{
			Attribute myAttrib = new Attribute(res.getString("name"),
				res.getString("value"),
				res.getString("value_type"));
			anItem.setAttribute(myAttrib);
		}
		res.close();
		sqlGetItemAttributes.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Add all attributes found in the database regarding a certain
	 * character to
	 * that character object. Basically fills up the attribute list of the
	 * character.
	 * @param aPerson character whose attributes we need
	 */
	public static void getCharAttributes(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		try
		{
		PreparedStatement sqlGetCharAttributes = theConnection.prepareStatement(sqlGetCharAttributesString);
		sqlGetCharAttributes.setString(1, aPerson.getName());
		res = sqlGetCharAttributes.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
			return;
		}
		while (res.next())
		{
			Attribute myAttrib = new Attribute(res.getString("name"),
				res.getString("value"),
				res.getString("value_type"));
			aPerson.setAttribute(myAttrib);
		}
		res.close();
		sqlGetCharAttributes.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Returns all currently active persons in the game. This method is
	 * usually used only when starting the server, as during server time
	 * the adding and deleting of users is handled by the server itself.
	 * @return Vector containing all currently active persons in the game.
	 */
	public static Vector getPersons()
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		Vector myVector = new Vector(50);
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetChars =theConnection.prepareStatement(sqlGetPersonsString);
		res = sqlGetChars.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
			return new Vector();
		}
		while (res.next())
		{
			String myName = res.getString("name");
			String myPasswd = res.getString("password");
			int gold = res.getInt("gold");
			int silver = res.getInt("silver");
			int copper = res.getInt("copper");
			Logger.getLogger("mmud").info("name: " + myName);
			if (res.getInt("god") < 2)
			{
				User myRealUser = new User(myName, 
					myPasswd,
					res.getString("address"),
					res.getString("realname"),
					res.getString("email"),
					res.getString("title"),
					res.getString("race"),
					Sex.createFromString(res.getString("sex")),
					res.getString("age"),
					res.getString("length"),
					res.getString("width"),
					res.getString("complexion"),
					res.getString("eyes"),
					res.getString("face"),
					res.getString("hair"),
					res.getString("beard"),
					res.getString("arm"),
					res.getString("leg"),
					res.getInt("sleep") == 1,
					res.getInt("god") == 1, 
					res.getString("lok"),
					res.getInt("whimpy"),
					res.getInt("fightable")==1,
					res.getInt("drinkstats"),
					res.getInt("eatstats"),
					Rooms.getRoom(res.getInt("room")));
				String mySessionPwd = res.getString("lok");
				if (mySessionPwd != null)
				{
					myRealUser.setSessionPassword(mySessionPwd);
				}
				myVector.add(myRealUser);
				getCharAttributes(myRealUser);
			}
			else
			{
				Person myNewChar = new Person(myName,
					res.getString("title"),
					res.getString("race"),
					Sex.createFromString(res.getString("sex")),
					res.getString("age"),
					res.getString("length"),
					res.getString("width"),
					res.getString("complexion"),
					res.getString("eyes"),
					res.getString("face"),
					res.getString("hair"),
					res.getString("beard"),
					res.getString("arm"),
					res.getString("leg"),
					res.getInt("sleep") == 1,
					res.getInt("whimpy"),
					res.getInt("drinkstats"),
					res.getInt("eatstats"),
					Rooms.getRoom(res.getInt("room")));
				if (myNewChar.getName().equals("Karcas"))
				{
					myNewChar.setAttribute(new Attribute("shopkeeper","","string"));
				}
				myVector.add(myNewChar);
				getCharAttributes(myNewChar);
			}
		}
		res.close();
		sqlGetChars.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return myVector;
	}

	/**
	 * Retrieves a proper description to be presented to the user
	 * based on an exception text.
	 * @param originalErr String containing the exception text.
	 * Something like, for example, "invalid user".
	 * @return String containing a description of what the user
	 * should see. Usually this is in the format of a web page.
	 */
	public static String getErrorMessage(String originalErr)
	{
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		User myUser = null;
		String myErrMsg = Constants.unknownerrormessage;
		try
		{

		PreparedStatement sqlGetErrMsg = theConnection.prepareStatement(sqlGetErrMsgString);
		sqlGetErrMsg.setString(1, originalErr);
		res = sqlGetErrMsg.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				myErrMsg = res.getString("description");
			}
			res.close();
		}
		sqlGetErrMsg.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (myErrMsg == null)
		{
			myErrMsg = "<HTML><HEAD><TITLE>"+originalErr+"</TITLE>"+
				"</HEAD><BODY BGCOLOR=#FFFFFF><H1>Error: "+originalErr+
				"</H1>The following error occurred.</BODY></HTML>";
		}
		Logger.getLogger("mmud").info("originalErr=" + originalErr +
			",myErrMsg=" + myErrMsg);
		return myErrMsg;
	}

	/**
	 * returns the logonmessage when logging into the game
	 * @return String containing the logon message.
	 */
	public static String getLogonMessage()
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		String result = null;
		try
		{

		PreparedStatement sqlGetLogonMsg = theConnection.prepareStatement(sqlGetLogonMessageString);
		res = sqlGetLogonMsg.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				result = res.getString("message");
			}
			res.close();
		}
		sqlGetLogonMsg.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * returns a helpmessage on the current command, or general help
	 * @param aCommand the command to enlist help for. If this is a null
	 * pointer, general help will be requested.
	 * @return String containing the man page of the command.
	 */
	public static String getHelp(String aCommand)
	{
		Logger.getLogger("mmud").finer("");
		if (aCommand == null)
		{
			aCommand = "general help";
		}
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		String result = null;
		try
		{

		PreparedStatement sqlGetHelpMsg = theConnection.prepareStatement(sqlGetHelpString);
		sqlGetHelpMsg.setString(1, aCommand);
		res = sqlGetHelpMsg.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				result = res.getString("contents");
			}
			res.close();
		}
		sqlGetHelpMsg.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (result == null && (!aCommand.equals("sorry")))
		{
			return getHelp("sorry");
		}
		return result;
	}

	/**
	 * returns a list of mudmails
	 * @param aUser the user to list the mudmails for
	 * @return String containing a bulleted list of mudmails.
	 */
	public static String getListOfMail(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		int j = 1;
		String result = "<TABLE BORDER=0 VALIGN=top>\r\n";
		try
		{

		PreparedStatement sqlListMailMsg = theConnection.prepareStatement(sqlListMailString);
		sqlListMailMsg.setString(1, aUser.getName());
		res = sqlListMailMsg.executeQuery();
		if (res != null)
		{
			while (res.next())
			{
				result += "<TR VALIGN=TOP><TD>" + j + ".</TD><TD>";
				if (res.getInt("newmail") > 0) 
				{
					result += "N";
				}
				if (res.getInt("haveread") > 0) 
				{
					result += "U";
				}
				result += "</TD><TD><B>From: </B>" + res.getString("name") + "</TD>";
				result += "<TD><B>Header: </B><A HREF=\"" + aUser.getUrl("readmail+" + j) + "\">";
				result += res.getString("header") + "</A></TD><TD><A HREF=\"" + aUser.getUrl("deletemail+" + j) + "\">Delete</A></TD></TR>\r\n";
				j++;
			}
			res.close();
			result += "</TABLE><BR>\r\n";
		}
		sqlListMailMsg.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * reads a mail from a user.
	 * @param aUser the user whos mudmail should be read
	 * @param messagenr the identification of the message, usually a number
	 * where "1" represents the first message, "2" the second, etc.
	 * @return String properly HTML formatted containing the mudmail.
	 */
	public static String readMail(User aUser, int messagenr)
		throws MailException
	{
		Logger.getLogger("mmud").finer("");
		return doStuffWithMail(aUser, messagenr, false);
	}

	/**
	 * deletes a mail. Returns the mudmail that has been erased.
	 * @param aUser the user whos mudmail must be erased.
	 * @param messagenr the message number identifying the mudmail
	 * where "1" represents the first message, "2" the second, etc.
	 * @return String properly HTML formatted containing the mudmail.
	 * @throws MailException if the message with messagenumber could
	 * not be found. (for example the message number was illegal)
	 */
	public static String deleteMail(User aUser, int messagenr)
		throws MailException
	{
		Logger.getLogger("mmud").finer("");
		return doStuffWithMail(aUser, messagenr, true);
	}

	/**
	 * does either of two things with mail, dependant of the boolean
	 * parameter
	 * @param aUser the user whos mudmail we wish to mutate
	 * @param messagenr the identification number of the message
	 * where "1" represents the first message, "2" the second, etc.
	 * @param deleteIt boolean, <UL><LI>true = delete mail<LI>false = do not
	 * delete mail, but update <I>haveread</I></UL>
	 * @return String containing the mail in question.
	 * @throws MailException if the messagenumber is invalid.
	 */
	private static String doStuffWithMail(User aUser, int messagenr, boolean deleteIt)
		throws MailException
	{
		Logger.getLogger("mmud").finer("");
		if (messagenr <= 0)
		{
			Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDMAILERROR);
			throw new InvalidMailException();
		}
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		int j = 1;
		String result = "<TABLE BORDER=0 VALIGN=top>\r\n";
		try
		{
		PreparedStatement sqlReadMailMsg =
			theConnection.prepareStatement(sqlReadMailString,
			ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		sqlReadMailMsg.setString(1, aUser.getName());
		res = sqlReadMailMsg.executeQuery();
		if (res != null)
		{
			if (!res.absolute(messagenr))
			{
				Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDMAILERROR);
				throw new InvalidMailException();
			}
			result += "<H1>Read Mail - " + res.getString("header") + "</H1>";
			result += "<HR noshade><TABLE BORDER=0>\r\n";
			result += "<TR><TD>Mes. Nr:</TD><TD> <B>" + messagenr + "</B></TD></TR>\r\n";
			result += "<TR><TD>From:</TD><TD><B>" + res.getString("name") + "</B></TD></TR>\r\n";
			result += "<TR><TD>On:</TD><TD><B>" + res.getDate("whensent") + " " + res.getTime("whensent") + "</B></TD></TR>\r\n";
			result += "<TR><TD>New?:</TD><TD><B>";
			if (res.getInt("newmail")>0) 
			{				
				result += "Yes</B></TD></TR>\r\n";
			}
			else 
			{
				result += "No</B></TD></TR>\r\n";
			}
			result += "<TR><TD>Read?:</TD><TD><B>";
			if (res.getInt("haveread")>0) 
			{
				result += "Yes</B></TD></TR>\r\n";
			}
			else 
			{				
				result += "No</B></TD></TR>\r\n";
			}
			result += "<TR><TD>Header:</TD><TD><B>" + res.getString("header") + "</B></TABLE>\r\n";
			result += "<HR noshade>" + res.getString("message");
			result += "<HR noshade><A HREF=\"" + aUser.getUrl("listmail") + "\">ListMail</A><P>";

			if (deleteIt)
			{
				PreparedStatement sqlDeleteMail = theConnection.prepareStatement(sqlDeleteMailString);
				sqlDeleteMail.setString(1, res.getString("name"));
				sqlDeleteMail.setString(2, aUser.getName());
				sqlDeleteMail.setTimestamp(3, res.getTimestamp("whensent"));
				sqlDeleteMail.executeUpdate();
				sqlDeleteMail.close();
			}
			else
			{
				PreparedStatement sqlUpdateMail = theConnection.prepareStatement(sqlUpdateMailString);
				sqlUpdateMail.setString(1, res.getString("name"));
				sqlUpdateMail.setString(2, aUser.getName());
				sqlUpdateMail.setTimestamp(3, res.getTimestamp("whensent"));
				sqlUpdateMail.executeUpdate();
				sqlUpdateMail.close();
			}

			res.close();
			result += "</TABLE><BR>\r\n";
		}
		else
		{
			sqlReadMailMsg.close();
			Logger.getLogger("mmud").info("thrown: " + Constants.INVALIDMAILERROR);
			throw new InvalidMailException();
		}
		sqlReadMailMsg.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * send mud mail.
	 * @param aUser the user who wishes to send mudmail.
	 * @param toUser the user who has to receive the send mudmail.
	 * @param header the header of the mudmail
	 * @param message the body of the mudmail
	 */
	public static void sendMail(User aUser, User toUser, String header, String message)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSendMailUser = theConnection.prepareStatement(sqlSendMailString);
		sqlSendMailUser.setString(1, aUser.getName());
		sqlSendMailUser.setString(2, toUser.getName());
		sqlSendMailUser.setString(3, header);
		sqlSendMailUser.setString(4, message);
		int res = sqlSendMailUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSendMailUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * search for the username amongst the banned users list in the database
	 * first checks the mm_sillynamestable in the database, then checks the
	 * mm_unbantable and
	 *  as last check checks the mm_bantable
	 * @return boolean, true if found, false if not found
	 * @param username String, name of the playercharacter 
	 * @param address String, the address of the player 
	 */
	public static boolean isUserBanned(String username, String address)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetBanStat = theConnection.prepareStatement(sqlGetBan1String);
		sqlGetBanStat.setString(1, username);
		res = sqlGetBanStat.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				if (res.getInt("count") > 0) 
				{
					res.close();
					sqlGetBanStat.close();
					return true;
				}
			}
			res.close();
		}
		sqlGetBanStat.close();

		sqlGetBanStat = theConnection.prepareStatement(sqlGetBan2String);
		sqlGetBanStat.setString(1, username);
		res = sqlGetBanStat.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				if (res.getInt("count") > 0) 
				{
					res.close();
					sqlGetBanStat.close();
					return false;
				}
			}
			res.close();
		}
		sqlGetBanStat.close();


		sqlGetBanStat = theConnection.prepareStatement(sqlGetBan3String);
		sqlGetBanStat.setString(1, address);
		res = sqlGetBanStat.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				if (res.getInt("count") > 0) 
				{
					res.close();
					sqlGetBanStat.close();
					return true;
				}
			}
			res.close();
		}
		sqlGetBanStat.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * see if the person has new mail
	 * @return boolean, true if found, false if not found
	 * @param aUser User whos mail must be checked.
	 */
	public static boolean hasUserNewMail(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		try
		{

		PreparedStatement sqlGetMailStatus = theConnection.prepareStatement(sqlYouHaveNewMailString);
		sqlGetMailStatus.setString(1, aUser.getName());
		res = sqlGetMailStatus.executeQuery();
		if (res != null)
		{
			if (res.next())
			{
				if (res.getInt("count") > 0) 
				{
					res.close();
					sqlGetMailStatus.close();
					return true;
				}
			}
			res.close();
		}
		sqlGetMailStatus.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * set the session password of a user into the mm_usertable
	 * @param username String, name of the playercharacter 
	 * @param sesspwd String, the session password of the player 
	 */
	public static void setSessionPassword(String username, String sesspwd)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetSessPwd = theConnection.prepareStatement(sqlSetSessPwdString);
		sqlSetSessPwd.setString(1, sesspwd);
		sqlSetSessPwd.setString(2, username);
		int res = sqlSetSessPwd.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetSessPwd.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * activate user.
	 * This is done by copying the record over to tmp_mm_usertable
	 * @param aUser User wishing to be active
	 */
	public static void activateUser(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlActivateUser = theConnection.prepareStatement(sqlActivateUserString);
		sqlActivateUser.setString(1, aUser.getName());
		int res = sqlActivateUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlActivateUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * deactivate user
	 * @param aUser User wishing to be deactivated
	 */
	public static void deactivateUser(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlDeActivateUser = theConnection.prepareStatement(sqlDeActivateUserString);
		sqlDeActivateUser.setString(1, aUser.getName());
		int res = sqlDeActivateUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlDeActivateUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * create user
	 * A new record will be inserted into the mm_usertable.
	 * @param aUser User wishing to be created
	 */
	public static void createUser(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlCreateUser = theConnection.prepareStatement(sqlCreateUserString);
		sqlCreateUser.setString(1, aUser.getName());
		sqlCreateUser.setString(2, aUser.getAddress());
		sqlCreateUser.setString(3, aUser.getPassword());
		sqlCreateUser.setString(4, aUser.getTitle());
		sqlCreateUser.setString(5, aUser.getRealname());
		sqlCreateUser.setString(6, aUser.getEmail());
		sqlCreateUser.setString(7, aUser.getRace());
		sqlCreateUser.setString(8, aUser.getSex().toString());
		sqlCreateUser.setString(9, aUser.getAge());
		sqlCreateUser.setString(10, aUser.getLength());
		sqlCreateUser.setString(11, aUser.getWidth());
		sqlCreateUser.setString(12, aUser.getComplexion());
		sqlCreateUser.setString(13, aUser.getEyes());
		sqlCreateUser.setString(14, aUser.getFace());
		sqlCreateUser.setString(15, aUser.getHair());
		sqlCreateUser.setString(16, aUser.getBeard());
		sqlCreateUser.setString(17, aUser.getArms());
		sqlCreateUser.setString(18, aUser.getLegs());
		sqlCreateUser.setString(19, aUser.getSessionPassword());
		int res = sqlCreateUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlCreateUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * set the title of a character
	 * @param aPerson Person with changed title
	 */
	public static void setTitle(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetTitleUser = theConnection.prepareStatement(sqlSetTitleString);
		sqlSetTitleUser.setString(1, aPerson.getTitle());
		sqlSetTitleUser.setString(2, aPerson.getName());
		int res = sqlSetTitleUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetTitleUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * set the drinkstats of a character
	 * @param aPerson Person with changed drinkstats
	 */
	public static void setDrinkstats(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetDrinkstatsUser = theConnection.prepareStatement(sqlSetDrinkstatsString);
		sqlSetDrinkstatsUser.setInt(1, aPerson.getDrinkstats());
		sqlSetDrinkstatsUser.setString(2, aPerson.getName());
		int res = sqlSetDrinkstatsUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetDrinkstatsUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * set the eatstats of a character
	 * @param aPerson Person with changed eatstats
	 */
	public static void setEatstats(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetEatstatsUser = theConnection.prepareStatement(sqlSetEatstatsString);
		sqlSetEatstatsUser.setInt(1, aPerson.getEatstats());
		sqlSetEatstatsUser.setString(2, aPerson.getName());
		int res = sqlSetEatstatsUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetEatstatsUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * set the sleep status of a character
	 * @param aPerson Person with changed sleep
	 */
	public static void setSleep(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetSleepUser = theConnection.prepareStatement(sqlSetSleepString);
		sqlSetSleepUser.setInt(1, (aPerson.isaSleep() ? 1 : 0));
		sqlSetSleepUser.setString(2, aPerson.getName());
		int res = sqlSetSleepUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetSleepUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * set the room of a character
	 * @param aPerson Person with changed room
	 */
	public static void setRoom(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetRoomUser = theConnection.prepareStatement(sqlSetRoomString);
		sqlSetRoomUser.setInt(1, aPerson.getRoom().getId());
		sqlSetRoomUser.setString(2, aPerson.getName());
		int res = sqlSetRoomUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetRoomUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * set the whimpy of a character
	 * @param aPerson Person with new whimpy setting
	 */
	public static void setWhimpy(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetWhimpyUser = theConnection.prepareStatement(sqlSetWhimpyString);
		sqlSetWhimpyUser.setInt(1, aPerson.getWhimpy());
		sqlSetWhimpyUser.setString(2, aPerson.getName());
		int res = sqlSetWhimpyUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetWhimpyUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * set the pkill of a character. If it is set to on, it means the player
	 * can attack other players and vice versa.
	 * @param aUser User with new whimpy setting
	 */
	public static void setPkill(User aUser)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetPkillUser = theConnection.prepareStatement(sqlUpdatePkillString);
		sqlSetPkillUser.setInt(1, (aUser.isPkill() ? 1 : 0));
		sqlSetPkillUser.setString(2, aUser.getName());
		int res = sqlSetPkillUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetPkillUser.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
