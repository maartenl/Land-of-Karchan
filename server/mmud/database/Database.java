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
import mmud.boards.BoardFormatEnum;
import mmud.boards.Board;

/**
 * The central class that takes care of all the commands that are to be
 * transmitted to the MySQL database server. This database class is
 * basically the class that effectively hides the database side of things
 * and only provides standard mud functions to the rest of the mud.
 */
public class Database
{

	private static Connection theConnection = null;

	public static String sqlGetUserString = "select *, password(?) as encrypted from mm_usertable where name = ? and active = 0 and god < 2";
	public static String sqlGetActiveUserString = "select *, password(?) as encrypted from mm_usertable where name = ? and active = 1 and god < 2";
	public static String sqlGetPersonsString = "select * from mm_usertable where active = 1";
	public static String sqlSetSessPwdString = "update mm_usertable set lok = ? where name = ?";
	public static String sqlActivateUserString = "update mm_usertable set active=1, lastlogin=now() where name = ?";
	public static String sqlDeActivateUserString = "update mm_usertable set active=0, lok=\"\", lastlogin=now() where name = ?";
	public static String sqlCreateUserString = "insert into mm_usertable " +
		"(name, address, password, title, realname, email, race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg, lok, active, lastlogin, birth) "+
		"values(?, ?, password(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, now(), now())";
	public static String sqlSetTitleString = "update mm_usertable set title=? where name = ?";
	public static String sqlSetDrinkstatsString = "update mm_usertable set drinkstats=? where name = ?";
	public static String sqlSetEatstatsString = "update mm_usertable set eatstats=? where name = ?";
	public static String sqlExistsUserString = "select 1 from mm_usertable where name = ?";
	public static String sqlSetSleepString = "update mm_usertable set sleep=? where name = ?";
	public static String sqlSetRoomString = "update mm_usertable set room=? where name = ?";
	public static String sqlSetWhimpyString = "update mm_usertable set whimpy = ? where name = ?";
	public static String sqlUpdatePkillString = "update mm_usertable set fightable = ? where name = ?";

	public static String sqlGetRoomString = "select * from mm_rooms where id = ?";

	public static String sqlGetErrMsgString = "select description from mm_errormessages where msg = ?";
	public static String sqlGetBan1String = "select count(name) as count from mm_sillynamestable where ? like name";
	public static String sqlGetBan2String = "select count(name) as count from mm_unbantable where name = ?";
	public static String sqlGetBan3String = "select count(address) as count from mm_bantable where ? like address";
	public static String sqlGetLogonMessageString = "select message from mm_logonmessage where id=0";
	public static String sqlWriteLogString = "insert into mm_log (name, message) values(?, ?)";
	public static String sqlGetHelpString = "select contents from mm_help where command = ?";
	public static String sqlAuthorizeString = "select \"yes\" from mm_admin where name = ? and validuntil > now()";

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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		return aStatement;
	}

	/**
	 * Create a prepared statement with some added options.
	 * @param aQuery the sql query used to create the statement.
	 * @param resultSetType a result set type; one of
	 * ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or
	 * ResultSet.TYPE_SCROLL_SENSITIVE
	 * @param resultSetConcurrency a concurrency type; one of
	 * ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
	 * @return PreparedStatement object, can be used to add variables and 
	 * do a query.
	 */
	static PreparedStatement prepareStatement(String aQuery, 
		int resultSetType, 
		int resultSetConcurrency)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("");
		PreparedStatement aStatement = null;
		try
		{
			aStatement = theConnection.prepareStatement(aQuery,
				resultSetType, 
				resultSetConcurrency);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		return aStatement;
	}

	/**
	 * Retrieve a character from the database that is currently NOT playing.
	 * @param aName the name of the character. This uniquely identifies
	 * any character in the database.
	 * @param aPassword the password of the character. Used to verify the
	 * encrypted password in the database.
	 * If the password does not match, the record is still returned,
	 * but with the password set to the null pointer.
	 * @return User containing all information. Returns null value if the user
	 * could not be found.
	 * DEBUG!! Why returns User, why not Person?
	 */
	public static User getUser(String aName, String aPassword)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("aName=" + aName + 
			",aPassword=" + aPassword);
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetUser = theConnection.prepareStatement(sqlGetUserString);
//		sqlGetUser.setBigDecimal
//		sqlGetUser.setInt
		sqlGetUser.setString(1, aPassword);
		sqlGetUser.setString(2, aName);
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
				(res.getString("encrypted").equals(res.getString("password")) ? 
					aPassword:
					null),
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
				isAuthorizedGod(res.getString("name")),
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (myUser != null)
		{
			getCharAttributes(myUser);
		}
		return myUser;
	}

	/**
	 * Retrieve a character from the database that is currently playing.
	 * @param aName the name of the character. This uniquely identifies
	 * any character in the database.
	 * @param aPassword the password of the character. Used to verify the
	 * encrypted password in the database.
	 * If the password does not match, the record is still returned,
	 * but with the password set to the null pointer.
	 * @return User containing all information. Returns null value if the user
	 * could not be found.
	 * DEBUG!! Why returns User, why not Person?
	 */
	public static User getActiveUser(String aName, String aPassword)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("aName=" + aName + 
			",aPassword=" + aPassword);
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetUser =
			theConnection.prepareStatement(sqlGetActiveUserString);
//		sqlGetUser.setBigDecimal
//		sqlGetUser.setInt
		sqlGetUser.setString(1, aPassword);
		sqlGetUser.setString(2, aName);
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
				(res.getString("encrypted").equals(res.getString("password")) ? 
					aPassword:
					null),
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
				isAuthorizedGod(res.getString("name")),
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (myUser != null)
		{
			getCharAttributes(myUser);
		}
		return myUser;
	}

	/**
	 * Retrieve a true or false regarding the god like status of the 
	 *character
	 * @param aName the name of the character. This uniquely identifies
	 * any character in the database.
	 * @return boolean, true if it is an administrator.
	 */
	public static boolean isAuthorizedGod(String aName)
	{
		assert theConnection != null : "theConnection is null";
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlAutho = theConnection.prepareStatement(sqlAuthorizeString);
		sqlAutho.setString(1, aName);
		res = sqlAutho.executeQuery();
		if (res == null)
		{
			return false;
		}
		if (res.next())
		{
			if (res.getString(1).equals("yes"))
			{
				res.close();
				sqlAutho.close();
				return true;
			}
		}
		res.close();
		sqlAutho.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		return false;
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		Logger.getLogger("mmud").finer(aPerson + "");
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
					null,
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
					isAuthorizedGod(myName),
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
				myVector.add(myNewChar);
				getCharAttributes(myNewChar);
			}
		}
		res.close();
		sqlGetChars.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
	 * returns the logonmessage when logging into the game.
	 * The logonmessage is basically the board called "logonmessage".
	 * @return String containing the logon message.
	 */
	public static String getLogonMessage()
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		Board logonBoard = BoardsDb.getBoard("logonmessage");
		String result = logonBoard.getDescription() + 
			logonBoard.read(BoardFormatEnum.SIMPLE);
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (result == null && (!aCommand.equals("sorry")))
		{
			return getHelp("sorry");
		}
		return result;
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
		Logger.getLogger("mmud").finer("username=" + username + ",address=" + address);
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
					Logger.getLogger("mmud").finer("returns true (mm_sillynamestable)");
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
					Logger.getLogger("mmud").finer("returns false (mm_unbantable)");
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
					Logger.getLogger("mmud").finer("returns true (mm_bantable)");
					return true;
				}
			}
			res.close();
		}
		sqlGetBanStat.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		Logger.getLogger("mmud").finer("returns false");
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
	}

	/**
	 * activate user for game play. This is done by setting
	 * the flag <I>active</I>.
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
	}

	/**
	 * write a log message to the database. This log facility is primarily
	 * used to keep a record of what kind of important mutations are done
	 * or attempted by both characters as well as administrators.
	 * Some examples:
	 * <ul><li>an item is picked up off the floor by a character
	 * <li>an item is eaten
	 * <li>an administrator creates a new item/room/character
	 * </ul>
	 * @param aName the name of the person to be inscribed in the log table
	 * @param aMessage the message to be written in the log, may not be
	 * larger than 255 characters.
	 */
	public static void writeLog(String aName, String aMessage)
	{
		Logger.getLogger("mmud").finer("");
		assert theConnection != null : "theConnection is null";
		try
		{
			PreparedStatement sqlWriteLog = theConnection.prepareStatement(sqlWriteLogString);
			sqlWriteLog.setString(1, aName);
			sqlWriteLog.setString(2, aMessage);
			int res = sqlWriteLog.executeUpdate();
			if (res != 1)
			{
				// error, not correct number of results returned
				// TOBEDONE
			}
			sqlWriteLog.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
