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
package mmud.database; 

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import mmud.Attribute;
import mmud.Constants;
import mmud.MudException;
import mmud.Sex;
import mmud.UserCommandInfo;
import mmud.boards.Board;
import mmud.boards.BoardFormatEnum;
import mmud.characters.Bot;
import mmud.characters.Mob;
import mmud.characters.Person;
import mmud.characters.Persons;
import mmud.characters.User;
import mmud.characters.UserNotFoundException;
import mmud.items.Item;
import mmud.races.RaceFactory;
import mmud.rooms.Area;
import mmud.rooms.Room;
import mmud.rooms.RoomNotFoundException;
import mmud.rooms.Rooms;

/**
 * The central class that takes care of all the commands that are to be
 * transmitted to the MySQL database server. This database class is
 * basically the class that effectively hides the database side of things
 * and only provides standard mud functions to the rest of the mud.
 */
public class Database
{

	private static Connection theConnection = null;

	public static String sqlGetUserString = "select *, old_password(?) as encrypted from mm_usertable where name = ? and active = 0 and god < 2";
	public static String sqlGetActiveUserString = "select *, old_password(?) as encrypted from mm_usertable where name = ? and active = 1 and god < 2";
	public static String sqlGetPersonsString = "select * from mm_usertable where active = 1";
	public static String sqlSetSessPwdString = "update mm_usertable set lok = ? where name = ?";
	public static String sqlActivateUserString = "update mm_usertable set active=1, address = ?, lastlogin=now() where name = ?";
	public static String sqlDeActivateUserString = "update mm_usertable set active=0, lok=\"\", lastlogin=now() where name = ?";
	public static String sqlCreateUserString = "insert into mm_usertable " +
		"(name, address, password, title, realname, email, race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg, lok, active, lastlogin, birth) "+
		"values(?, ?, old_password(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, now(), now())";
	public static String sqlSetTitleString = "update mm_usertable set title=? where name = ?";
	public static String sqlSetDrinkstatsString = "update mm_usertable set drinkstats=? where name = ?";
	public static String sqlSetEatstatsString = "update mm_usertable set eatstats=? where name = ?";
	public static String sqlSetMoneyString = "update mm_usertable set copper=? where name = ?";
	public static String sqlExistsUserString = "select 1 from mm_usertable where name = ?";
	public static String sqlSetSleepString = "update mm_usertable set sleep=? where name = ?";
	public static String sqlSetRoomString = "update mm_usertable set room=? where name = ?";
	public static String sqlSetWhimpyString = "update mm_usertable set whimpy = ? where name = ?";
	public static String sqlUpdatePkillString = "update mm_usertable set fightable = ? where name = ?";

	public static String sqlGetRoomString = "select * from mm_rooms where id = ?";
	public static String sqlWriteRoomString = "update mm_rooms set north = ?, south = ?, east = ?, west = ?, up = ?, down = ?, contents = ? where id = ?";

	public static String sqlGetErrMsgString = "select description from mm_errormessages where msg = ?";
	public static String sqlGetBan1String = "select count(name) as count from mm_sillynamestable where ? like name";
	public static String sqlGetBan2String = "select count(name) as count from mm_unbantable where name = ?";
	public static String sqlGetBan3String = "select count(address) as count from mm_bantable where ? like address";
	public static String sqlGetBan4String = "select count(*) as count from mm_bannednamestable where name = ?";
	public static String sqlGetLogonMessageString = "select message from mm_logonmessage where id=0";
	public static String sqlWriteLogString = "insert into mm_log (name, message) values(?, ?)";
	public static String sqlWriteLog2String = "insert into mm_log (name, message, addendum) values(?, ?, ?)";
	public static String sqlGetHelpString = "select contents from mm_help where command = ?";
	public static String sqlAuthorizeString = "select \"yes\" from mm_admin where name = ? and validuntil > now()";

	public static String sqlDeactivateEvent = 
		"update mm_events " +
		"set callable = 0 " +
		"where eventid = ?";
	public static String sqlGetEvents = "select mm_methods.name as method_name, " +
		"mm_events.name, src, room, mm_events.eventid from mm_events, mm_methods " +
		"where callable = 1 " +
		"and mm_methods.name = mm_events.method_name " +
		"and ( month = -1 or month = MONTH(NOW()) ) " + 
		"and ( dayofmonth = -1 or dayofmonth = DAYOFMONTH(NOW()) ) " +
		"and ( hour = -1 or hour = HOUR(NOW()) ) " +
		"and ( minute = -1 or minute = MINUTE(NOW()) ) " +
		"and ( dayofweek = -1 or dayofweek = DAYOFWEEK(NOW()) )" +
		"and ( month <> -1 or dayofmonth <> -1 or hour <> -1 or minute <> -1 or dayofweek <> -1 )";
	public static String sqlDeactivateCommand = 
		"update mm_commands " +
		"set callable = 0 " +
		"where id = ?";
	public static String sqlGetMethod = "select src " +
		"from mm_methods " +
		"where name = ?";
	public static String sqlGetUserCommands = 
		"select * " +
		"from mm_commands " +
		"where callable = 1";

	public static String sqlGetCharAttributesString =
		"select * from mm_charattributes "
		+ "where charname = ?";
	public static String sqlGetItemAttributesString =
		"select * from mm_itemattributes "
		+ "where id = ?";
	public static String sqlGetRoomAttributesString =
		"select * from mm_roomattributes "
		+ "where id = ?";

	public static String sqlGetAnswers = 
		"select * from mm_answers where ? like question and name = ?";
		
	public static String sqlGetAreaString = 
		"select mm_area.* from mm_area, mm_rooms where mm_rooms.id = ? "
		+ "and mm_rooms.area = mm_area.area";

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

	private static void checkConnection()
	throws MudDatabaseException
	{
		if (theConnection == null)
		{
			try
			{
				connect();
			}
			catch (Exception e)
			{
				throw new MudDatabaseException(
					Constants.DATABASECONNECTIONERROR, e);
			}
		}
		boolean closed = true;
		try
		{
			closed = theConnection.isClosed();
		}
		catch (SQLException e)
		{
			throw new MudDatabaseException(
			Constants.DATABASECONNECTIONERROR, e);
		}
		if (closed)
		{
			theConnection = null;
			try
			{
				connect();
			}
			catch (InstantiationException e)
			{
				throw new MudDatabaseException(
				Constants.DATABASECONNECTIONERROR, e);
			}
			catch (SQLException e2)
			{
				throw new MudDatabaseException(
				Constants.DATABASECONNECTIONERROR, e2);
			}
			catch (ClassNotFoundException e3)
			{
				throw new MudDatabaseException(
				Constants.DATABASECONNECTIONERROR, e3);
			}
			catch (IllegalAccessException e4)
			{
				throw new MudDatabaseException(
				Constants.DATABASECONNECTIONERROR, e4);
			}
		}
	}

	/**
	 * Create a prepared statement.
	 * @param aQuery the sql query used to create the statement.
	 * @return PreparedStatement object, can be used to add variables and 
	 * do a query.
	 */
	static PreparedStatement prepareStatement(String aQuery)
	throws MudDatabaseException
	{

		Logger.getLogger("mmud").finer("");
		checkConnection();
		PreparedStatement aStatement = null;
		try
		{
			aStatement = theConnection.prepareStatement(aQuery);
		}
//		catch (InstantiationException e)
//		{
//			throw new MudDatabaseException(
//			Constants.DATABASECONNECTIONERROR, e);
//		}
		catch (SQLException e2)
		{
			throw new MudDatabaseException(
			Constants.DATABASECONNECTIONERROR, e2);
		}
//		catch (ClassNotFoundException e3)
//		{
//			throw new MudDatabaseException(
//			Constants.DATABASECONNECTIONERROR, e3);
//		}
//		catch (IllegalAccessException e4)
//		{
//			throw new MudDatabaseException(
//			Constants.DATABASECONNECTIONERROR, e4);
//		}
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
		throws MudDatabaseException
	{

		Logger.getLogger("mmud").finer("");
		checkConnection();
		PreparedStatement aStatement = null;
		try
		{
			aStatement = theConnection.prepareStatement(aQuery,
				resultSetType, 
				resultSetConcurrency);
		}
		catch (SQLException e2)
		{
			throw new MudDatabaseException(
			Constants.DATABASECONNECTIONERROR, e2);
		}
		return aStatement;
	}

	/**
	 * Retrieve a character from the database that is currently NOT playing.
	 * @param aName the name of the character. This uniquely identifies
	 * any character in the database. The character is always a <I>user</I>
	 * playing the game, because if it is not a User, than it is a bot and
	 * bots should always be active in the game.
	 * @param aPassword the password of the character. Used to verify the
	 * encrypted password in the database.
	 * If the password does not match, the record is still returned,
	 * but with the password set to the null pointer.
	 * @return User containing all information. Returns null value if the user
	 * could not be found.
	 */
	public static User getUser(String aName, String aPassword)
	throws MudException
	{

		Logger.getLogger("mmud").finer("aName=" + aName + 
			",aPassword=" + aPassword);
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetUser = prepareStatement(sqlGetUserString);
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
				RaceFactory.createFromString(res.getString("race")),
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
				res.getInt("experience"),
				res.getInt("vitals"),
				res.getInt("alignment"),
				res.getInt("movementstats"),
				res.getInt("copper"),
				Rooms.getRoom(res.getInt("room")));

		}
		res.close();
		sqlGetUser.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
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
	throws MudException
	{

		Logger.getLogger("mmud").finer("aName=" + aName + 
			",aPassword=" + aPassword);
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetUser =
			prepareStatement(sqlGetActiveUserString);
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
				RaceFactory.createFromString(res.getString("race")),
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
				res.getInt("experience"),
				res.getInt("vitals"),
				res.getInt("alignment"),
				res.getInt("movementstats"),
				res.getInt("copper"),
				Rooms.getRoom(res.getInt("room")));

		}
		res.close();
		sqlGetUser.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
		if (myUser != null)
		{
			getCharAttributes(myUser);
		}
		return myUser;
	}

	/**
	 * Retrieve a true or false regarding the god like status of the 
	 * character
	 * @param aName the name of the character. This uniquely identifies
	 * any character in the database.
	 * @return boolean, true if it is an administrator.
	 */
	public static boolean isAuthorizedGod(String aName)
	throws MudException
	{

		Logger.getLogger("mmud").finer("");
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlAutho = prepareStatement(sqlAuthorizeString);
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
			Database.writeLog("root", e);
		}
		return false;
	}

	/**
	 * Checks to see that a user exists in the database.
	 * @param aName the name that uniquely identifies the character.
	 * @return boolean, true if found, false otherwise.
	 */
	public static boolean existsUser(String aName)
	throws MudException
	{

		Logger.getLogger("mmud").finer("");
		ResultSet res;
		boolean myBoolean = false;
		try
		{

		PreparedStatement sqlGetUser = prepareStatement(sqlExistsUserString);
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
			Database.writeLog("root", e);
		}
		return myBoolean;
	}

	/**
	 * Returns the room information based on the roomnumber.
	 * @param roomnr integer containing the number of the room.
	 * @return Room object containing all information. null pointer if the
	 * room could not be found in the database.
	 */
	public static Room getRoom(int roomnr)
	throws MudException
	{

		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Room myRoom = null;
		try
		{

		PreparedStatement sqlGetRoom = prepareStatement(sqlGetRoomString);
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
			Database.writeLog("root", e);
		}
		return myRoom;
	}

	/**
	 * Returns the area information of a certain room.
	 * @param aRoom object used for finding out to which are it belongs.
	 * @return Area object containing all area information. null pointer if the
	 * area could not be found in the database.
	 */
	public static Area getArea(Room aRoom)
	throws MudException
	{
		if (aRoom == null)
		{
			throw new RuntimeException("aRoom is null");
		}
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Area myArea  = null;
		try
		{

		PreparedStatement sqlGetArea = prepareStatement(sqlGetAreaString);
		sqlGetArea.setInt(1, aRoom.getId());
		res = sqlGetArea.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
			return null;
		}
		res.first();
		myArea  = new Area(
			res.getString("area"),
			res.getString("description"),
			res.getString("shortdesc"));
		res.close();
		sqlGetArea.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
		Logger.getLogger("mmud").finer("returns " + myArea);
		return myArea;
	}

	/**
	 * Writes the room information back to the database based on the roomnumber.
	 * @param aRoom Room object containing all information.
	 */
	public static void writeRoom(Room aRoom)
	throws MudException
	{
		if (aRoom == null)
		{
			throw new RuntimeException("aRoom is null");
		}
		Logger.getLogger("mmud").finer("aRoom=" + aRoom);
		ResultSet res;
		try
		{

			PreparedStatement statWriteRoom = prepareStatement(sqlWriteRoomString);
			if (aRoom.getNorth() == null)
			{
				statWriteRoom.setNull(1, Types.INTEGER);
			}
			else
			{
				statWriteRoom.setInt(1, aRoom.getNorth().getId());
			}
			if (aRoom.getSouth() == null)
			{
				statWriteRoom.setNull(2, Types.INTEGER);
			}
			else
			{
				statWriteRoom.setInt(2, aRoom.getSouth().getId());
			}
			if (aRoom.getEast() == null)
			{
				statWriteRoom.setNull(3, Types.INTEGER);
			}
			else
			{
				statWriteRoom.setInt(3, aRoom.getEast().getId());
			}
			if (aRoom.getWest() == null)
			{
				statWriteRoom.setNull(4, Types.INTEGER);
			}
			else
			{
				statWriteRoom.setInt(4, aRoom.getWest().getId());
			}
			if (aRoom.getUp() == null)
			{
				statWriteRoom.setNull(5, Types.INTEGER);
			}
			else
			{
				statWriteRoom.setInt(5, aRoom.getUp().getId());
			}
			if (aRoom.getDown() == null)
			{
				statWriteRoom.setNull(6, Types.INTEGER);
			}
			else
			{
				statWriteRoom.setInt(6, aRoom.getDown().getId());
			}
			statWriteRoom.setString(7, aRoom.getDescription());
			statWriteRoom.setInt(8, aRoom.getId());
			statWriteRoom.executeUpdate();
			statWriteRoom.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Add all attributes found in the database regarding a certain item to
	 * that item object. Basically fills up the attribute list of the item.
	 * @param anItem item object
	 */
	public static void getItemAttributes(Item anItem)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		ResultSet res;
		try
		{
		PreparedStatement sqlGetItemAttributes = prepareStatement(sqlGetItemAttributesString);
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
			Database.writeLog("root", e);
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
	throws MudException
	{
		Logger.getLogger("mmud").finer(aPerson + "");

		ResultSet res;
		try
		{
		PreparedStatement sqlGetCharAttributes = prepareStatement(sqlGetCharAttributesString);
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
			Database.writeLog("root", e);
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
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		Vector myVector = new Vector(50);
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetChars = prepareStatement(sqlGetPersonsString);
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
					RaceFactory.createFromString(res.getString("race")),
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
					res.getInt("experience"),
					res.getInt("vitals"),
					res.getInt("alignment"),
					res.getInt("movementstats"),
					res.getInt("copper"),
					Rooms.getRoom(res.getInt("room")));
				String mySessionPwd = res.getString("lok");
				if (mySessionPwd != null)
				{
					myRealUser.setSessionPassword(mySessionPwd);
				}
				myVector.add(myRealUser);
				getCharAttributes(myRealUser);
			}
			else if (res.getInt("god") == 2)
			{
				Bot myNewChar = new Bot(myName,
					res.getString("title"),
					RaceFactory.createFromString(res.getString("race")),
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
					res.getInt("experience"),
					res.getInt("vitals"),
					res.getInt("alignment"),
					res.getInt("movementstats"),
					res.getInt("copper"),
					Rooms.getRoom(res.getInt("room")));
				myVector.add(myNewChar);
				getCharAttributes(myNewChar);
			}
			else
			{
				Mob myNewChar = new Mob(myName,
					res.getString("title"),
					RaceFactory.createFromString(res.getString("race")),
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
					res.getInt("experience"),
					res.getInt("vitals"),
					res.getInt("alignment"),
					res.getInt("movementstats"),
					res.getInt("copper"),
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
			Database.writeLog("root", e);
		}
		return myVector;
	}

	/**
	 * Retrieves all events from the database that are to take place and,
	 * if possible, executes them. Events can take place originating
	 * from a certain character or originating in a certain room,
	 * or originating in a global game event type thingy.
	 * This method is usually called from a separate thread dealing
	 * with events.
	 * @see #sqlGetEvents
	 */
	public static void runEvents()
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		ResultSet res;
		try
		{

			PreparedStatement statGetEvents = prepareStatement(sqlGetEvents);
			res = statGetEvents.executeQuery();
			if (res == null)
			{
				return;
			}
			while (res.next())
			{
				String myName = res.getString("name");
				int myRoom = res.getInt("room");
				String mySource = res.getString("src");
				int myEventId = res.getInt("eventid");
				if (myName != null)
				{
					// character detected
					Logger.getLogger("mmud").info("method_name=" +
						res.getString("method_name") + ", person=" + myName);
					Person aPerson = Persons.retrievePerson(myName);
					if (aPerson == null)
					{
						throw new UserNotFoundException();
					}
					try
					{
						aPerson.runScript("event", mySource);
					}
					catch (MudException myMudException)
					{
						deactivateEvent(myEventId);
						throw myMudException;
					}
				}
				else
				if (myRoom != 0)
				{
					// room detected
					Logger.getLogger("mmud").info("method_name=" +
						res.getString("method_name") + ", room=" + myRoom);
					Room aRoom = Rooms.getRoom(myRoom);
					if (aRoom == null)
					{
						throw new RoomNotFoundException();
					}
					try
					{
						aRoom.runScript("event", mySource);
					}
					catch (MudException myMudException)
					{
						deactivateEvent(myEventId);
						throw myMudException;
					}
				}
				else
				{
					// neither detected, overall game executing.
					Logger.getLogger("mmud").info("method_name=" +
						res.getString("method_name"));
					// TODO: not implemented yet
				}
			}
			res.close();
			statGetEvents.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Deactivates a certain event in the mud. This is done
	 * when a certain event is not executed properly, i.e. when an
	 * exception has occurred. This is important to prevent any event
	 * from making the game unplayable.
	 * @param anEventId the unique id of the event to be deactivated.
	 */
	public static void deactivateEvent(int anEventId)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{
			PreparedStatement statDeactivateEvent =
				prepareStatement(sqlDeactivateEvent);
			statDeactivateEvent.setInt(1, anEventId);
			int res = statDeactivateEvent.executeUpdate();
			if (res != 1)
			{
				// error, not correct number of results returned
				// TOBEDONE
			}
			statDeactivateEvent.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Deactivates a certain (special) command in the mud. This is done
	 * when a certain command is not executed properly, i.e. when an
	 * exception has occurred. This is important to prevent any command
	 * from making the game unplayable.
	 * @param aCommandId the unique id of the command to be deactivated.
	 */
	public static void deactivateCommand(int aCommandId)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{
			PreparedStatement statDeactivateCommand =
				prepareStatement(sqlDeactivateCommand);
			statDeactivateCommand.setInt(1, aCommandId);
			int res = statDeactivateCommand.executeUpdate();
			if (res != 1)
			{
				// error, not correct number of results returned
				// TOBEDONE
			}
			statDeactivateCommand.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Retrieves all commands defined in the database
	 * that are <I>active</I>. 
	 * @return Collection (Vector in this case) 
	 * containing all the special commands defined in
	 * the database. Each command is stored in special UserCommandInfo class.
	 * @see mmud.UserCommandInfo
	 */
	public static Collection getUserCommands()
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");

		Vector myResult = new Vector(25);
		ResultSet res;
		try
		{

			PreparedStatement statGetUserCommands = prepareStatement(sqlGetUserCommands);
			res = statGetUserCommands.executeQuery();
			if (res == null)
			{
				return myResult;
			}
			while (res.next())
			{
				String myCommand = res.getString("command");
				String myMethod = res.getString("method_name");
				int myCommandId = res.getInt("id");
				int myRoom = res.getInt("room");
				UserCommandInfo aUserCommandInfo;
				if (myRoom != 0)
				{
					// room detected
					aUserCommandInfo = new UserCommandInfo(
						myCommandId, myCommand, myMethod, new Integer(myRoom));
				}
				else
				{
					// no room detected
					aUserCommandInfo = new UserCommandInfo(
						myCommandId, myCommand, myMethod);
				}
				myResult.add(aUserCommandInfo);
			}
			res.close();
			statGetUserCommands.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
		return myResult;
	}

	/**
	 * Retrieves the answer to a question from a player or otherwise to a
	 * bot.
	 * @return String containing the answer. Returns null if the answer was
	 * not found.
	 * @param aPerson the person that is being asked the question.
	 * @param aQuestion the question asked.
	 */
	public static String getAnswers(Person aPerson, 
		String aQuestion)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("aPerson=" + aPerson +
			", aQuestion=" + aQuestion);

		String result = null;
		ResultSet res;
		try
		{

			PreparedStatement statGetAnswers =
				prepareStatement(sqlGetAnswers);
			statGetAnswers.setString(1, aQuestion);
			statGetAnswers.setString(2, aPerson.getName());
			res = statGetAnswers.executeQuery();
			if (res == null)
			{
				return null;
			}
			if (res.next())
			{
				result = res.getString("answer");
			}
			res.close();
			statGetAnswers.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
		Logger.getLogger("mmud").finer("returns " + result);
		return result;
	}

	/**
	 * Retrieves the source of a method from the database.
	 * @return String containing the source of the method. Returns null
	 * if the method source was not found. Probably when the method
	 * does not exist.
	 * @param aMethodName the name of the method, used to find
	 * the source of the method.
	 */
	public static String getMethodSource(String aMethodName)
	throws MudException
	{
		Logger.getLogger("mmud").finer("aMethodName=" + aMethodName);

		String result = null;
		ResultSet res;
		try
		{

			PreparedStatement statGetMethod =
				prepareStatement(sqlGetMethod);
			statGetMethod.setString(1, aMethodName);
			res = statGetMethod.executeQuery();
			
			if (res == null)
			{
				return null;
			}
			while (res.next())
			{
				result = res.getString("src");
			}
			res.close();
			statGetMethod.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
		return result;
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

		ResultSet res;
		User myUser = null;
		String myErrMsg = Constants.unknownerrormessage;
		try
		{

		PreparedStatement sqlGetErrMsg = prepareStatement(sqlGetErrMsgString);
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
		}
		catch (MudDatabaseException e2)
		{
			e2.printStackTrace();
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
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

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
	throws MudException
	{
		Logger.getLogger("mmud").finer("");
		if (aCommand == null)
		{
			aCommand = "general help";
		}

		ResultSet res;
		String result = null;
		try
		{

		PreparedStatement sqlGetHelpMsg = prepareStatement(sqlGetHelpString);
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
			Database.writeLog("root", e);
		}
		if (result == null && (!aCommand.equals("sorry")))
		{
			return getHelp("sorry");
		}
		return result;
	}

	/**
	 * search for the username amongst the banned users list in the
	 * database.
	 * First checks the mm_sillynamestable in the database, if found returns
	 * true. Then checks the
	 * mm_unbantable, if found returns false. Then checks the mm_bannednamestable,
	 * if found returns true. And
	 * as last check checks the mm_bantable, if found returns true,
	 * otherwise returns false.
	 * @return boolean, true if found, false if not found
	 * @param username String, name of the playercharacter 
	 * @param address String, the address of the player 
	 */
	public static boolean isUserBanned(String username, String address)
	throws MudException
	{
		Logger.getLogger("mmud").finer("username=" + username + ",address=" + address);

		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetBanStat = prepareStatement(sqlGetBan1String);
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

		sqlGetBanStat = prepareStatement(sqlGetBan2String);
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

		sqlGetBanStat = prepareStatement(sqlGetBan4String);
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
					Logger.getLogger("mmud").finer("returns true (mm_bannednamestable)");
					return true;
				}
			}
			res.close();
		}
		sqlGetBanStat.close();

		sqlGetBanStat = prepareStatement(sqlGetBan3String);
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
			Database.writeLog("root", e);
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
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetSessPwd = prepareStatement(sqlSetSessPwdString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * activate user for game play. This is done by setting
	 * the flag <I>active</I>.
	 * @param aUser User wishing to be active
	 */
	public static void activateUser(User aUser)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlActivateUser = prepareStatement(sqlActivateUserString);
		sqlActivateUser.setString(1, aUser.getAddress());
		sqlActivateUser.setString(2, aUser.getName());
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * deactivate user
	 * @param aUser User wishing to be deactivated
	 */
	public static void deactivateUser(User aUser)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlDeActivateUser = prepareStatement(sqlDeActivateUserString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * create user
	 * A new record will be inserted into the mm_usertable.
	 * @param aUser User wishing to be created
	 */
	public static void createUser(User aUser)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlCreateUser = prepareStatement(sqlCreateUserString);
		sqlCreateUser.setString(1, aUser.getName());
		sqlCreateUser.setString(2, aUser.getAddress());
		sqlCreateUser.setString(3, aUser.getPassword());
		sqlCreateUser.setString(4, aUser.getTitle());
		sqlCreateUser.setString(5, aUser.getRealname());
		sqlCreateUser.setString(6, aUser.getEmail());
		sqlCreateUser.setString(7, aUser.getRace().toString());
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the title of a character
	 * @param aPerson Person with changed title
	 */
	public static void setTitle(Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetTitleUser = prepareStatement(sqlSetTitleString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the drinkstats of a character
	 * @param aPerson Person with changed drinkstats
	 */
	public static void setDrinkstats(Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetDrinkstatsUser = prepareStatement(sqlSetDrinkstatsString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the eatstats of a character
	 * @param aPerson Person with changed eatstats
	 */
	public static void setEatstats(Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetEatstatsUser = prepareStatement(sqlSetEatstatsString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the money of a character
	 * @param aPerson Person with changed money.
	 */
	public static void setMoney(Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetMoneyUser = prepareStatement(sqlSetMoneyString);
		sqlSetMoneyUser.setInt(1, aPerson.getMoney());
		sqlSetMoneyUser.setString(2, aPerson.getName());
		int res = sqlSetMoneyUser.executeUpdate();
		if (res != 1)
		{
			// error, not correct number of results returned
			// TOBEDONE
		}
		sqlSetMoneyUser.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the sleep status of a character
	 * @param aPerson Person with changed sleep
	 */
	public static void setSleep(Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetSleepUser = prepareStatement(sqlSetSleepString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the room of a character
	 * @param aPerson Person with changed room
	 */
	public static void setRoom(Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetRoomUser = prepareStatement(sqlSetRoomString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the whimpy of a character
	 * @param aPerson Person with new whimpy setting
	 */
	public static void setWhimpy(Person aPerson)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetWhimpyUser = prepareStatement(sqlSetWhimpyString);
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
			Database.writeLog("root", e);
		}
	}

	/**
	 * set the pkill of a character. If it is set to on, it means the player
	 * can attack other players and vice versa.
	 * @param aUser User with new whimpy setting
	 */
	public static void setPkill(User aUser)
	throws MudException
	{
		Logger.getLogger("mmud").finer("");

		try
		{

		PreparedStatement sqlSetPkillUser = prepareStatement(sqlUpdatePkillString);
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
			Database.writeLog("root", e);
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

		try
		{
			PreparedStatement sqlWriteLog = prepareStatement(sqlWriteLogString);
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
		catch (MudDatabaseException e2)
		{
			e2.printStackTrace();
		}
	}

	/**
	 * write a log message of an exception to the database. 
	 * @param aName the name of the person to be inscribed in the log table
	 * @param aThrowable the exception or error to be written to the log
	 * table.
	 */
	public static void writeLog(String aName, Throwable aThrowable)
	{
		Logger.getLogger("mmud").finer("");

		ByteArrayOutputStream myStream = new ByteArrayOutputStream();
		PrintStream myPrintStream = new PrintStream(myStream);
		aThrowable.printStackTrace(myPrintStream);
		myPrintStream.close();
//		myStream.close();
		try
		{
			PreparedStatement sqlWriteLog = prepareStatement(sqlWriteLog2String);
			sqlWriteLog.setString(1, aName);
			sqlWriteLog.setString(2, aThrowable.toString());
			sqlWriteLog.setString(3, myStream.toString());
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
		catch (MudDatabaseException e2)
		{
			e2.printStackTrace();
		}
	}

}
