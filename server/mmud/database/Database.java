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


import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

public class Database
{

	private static Connection theConnection = null;
	public static String sqlGetUserString = "select * from usertable where name = ? and active = 0 and god < 2";
	public static String sqlGetRoomString = "select * from rooms where id = ?";
	public static String sqlGetCharactersString = "select * from usertable where active = 1";
	public static String sqlGetErrMsgString = "select description from mm_errormessages where msg = ?";
	public static String sqlGetBan1String = "select count(name) as count from sillynamestable where ? like name";
	public static String sqlGetBan2String = "select count(name) as count from unbantable where name = ?";
	public static String sqlGetBan3String = "select count(address) as count from bantable where ? like address";
	public static String sqlSetSessPwdString = "update usertable set lok = ? where name = ?";
	public static String sqlActivateUserString = "update usertable set active=1, lastlogin=date_sub(now(), interval 2 hour) where name = ?";
	public static String sqlDeActivateUserString = "update usertable set active=0, lok=\"\", lastlogin=date_sub(now(), interval 2 hour) where name = ?";
	public static String sqlGetLogonMessageString = "select message from logonmessage where id=0";
	public static String sqlYouHaveNewMailString = "select count(*) as count from mailtable where toname = ? and newmail = 1";
	public static String sqlCreateUserString = "insert into usertable " +
		"(name, address, password, title, realname, email, race, sex, age, length, width, complexion, eyes, face, hair, beard, arm, leg, lok, active, lastlogin, birth) "+
		"values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, now(), now())";
	public static String sqlSetTitleString = "update usertable set title=? where name = ?";
	public static String sqlSetDrinkstatsString = "update usertable set drinkstats=? where name = ?";
	public static String sqlSetEatstatsString = "update usertable set eatstats=? where name = ?";
	public static String sqlExistsUserString = "select 1 from usertable where name = ?";
	public static String sqlSetSleepString = "update usertable set sleep=? where name = ?";
	public static String sqlSetRoomString = "update usertable set room=? where name = ?";
	public static String sqlGetHelpString = "select contents from help where command = ?";
	public static String sqlSetWhimpyString = "update usertable set whimpy = ? where name = ?";
	public static String sqlListMailString = "select name, haveread, newmail, header from mailtable where toname = ? order by whensent asc";
	public static String sqlReadMailString = "select * from mailtable where toname = ? order by whensent asc";
	public static String sqlDeleteMailString = "delete from mailtable where name = ? and toname = ? and whensent = ? ";
	public static String sqlUpdateMailString = "update mailtable set haveread=1 where name = ? and toname = ? and whensent = ? ";
	public static String sqlSendMailString = "insert into mailtable " +
		"(name, toname, header, whensent, haveread, newmail, message) " +
		"values (?, ?, ?, now(), 0, 1, ?)";
	public static String sqlUpdatePkillString = "update usertable set fightable = ? where name = ?";
	public static String sqlGetInventoryCharacterString = 
		"select count(*) as amount, adject1, adject2, adject3, name from mm_itemtable, items "
		+ "where mm_itemtable.itemid = items.id and owner = ? and ownertype = 1 "
		+ "and visible = 1 "
		+ "group by adject1, adject2, adject3, name";
	public static String sqlGetInventoryRoomString =
		"select count(*) as amount, adject1, adject2, adject3, name from mm_itemtable, items "
		+ "where mm_itemtable.itemid = items.id and owner = ? and ownertype = 2 "
		+ "and visible = 1 "
		+ "group by adject1, adject2, adject3, name";
	public static String sqlPickupItemString =
		"update mm_itemtable "
		+ "set owner = ?, ownertype = 1 "
		+ "where mm_itemtable.itemid = ? and owner = ? and ownertype = 2 "
		+ "limit ?";
	public static String sqlGetItemRoomString =
		"select * from mm_itemtable, items "
		+ "where mm_itemtable.itemid = items.id and "
        + "owner = ? and ownertype = 2 "
		+ "and name = ? and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 "
		+ "limit 1";
	public static String sqlGetItemCharacterString =
		"select * from mm_itemtable, items "
		+ "where mm_itemtable.itemid = items.id and "
        + "owner = ? and ownertype = 1 "
		+ "and name = ? and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 "
		+ "limit 1";
	public static String sqlGetItemAttributesString =
		"select * from mm_itemattributes "
		+ "where id = ?";
	public static String sqlGetItemDefString = "select * from items where id = ?";

	public static void runMethod()
		throws SQLException,
			InstantiationException,
			ClassNotFoundException,
			IllegalAccessException
	{
		if (Constants.logging)
		{
			System.err.println("Database.runMethod ");
		}
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn;
		ResultSet res;
		Statement myStatement;
	
//		DriverManager.setLogWriter(System.out);
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost.localdomain/mud?user=root&password=");
		myStatement = conn.createStatement();
		res = myStatement.executeQuery("select 2+2 as result");
		res.first();
		System.out.println("Answer: " + res.getInt("result"));
	
		res.close();
		myStatement.close();
	
		conn.close();
	}

	/**
	 *"jdbc:mysql://localhost.localdomain/mud?user=root&password=");
	 *
	 */
	public static void connect()
		throws SQLException,
			InstantiationException,
			ClassNotFoundException,
			IllegalAccessException
	{
		if (Constants.logging)
		{
			System.err.println("Database.connect ");
		}
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

		if (Constants.logging)
		{
			System.err.println(" " + theUrl);
		}
		theConnection = DriverManager.getConnection(theUrl);
	}

	public static void disconnect()
		throws SQLException,
			InstantiationException,
			ClassNotFoundException,
			IllegalAccessException
	{
		if (Constants.logging)
		{
			System.err.println("Database.disconnect ");
		}
		theConnection.close();
		theConnection = null;
	}

	public static void refresh()
		throws SQLException,
			InstantiationException,
			ClassNotFoundException,
			IllegalAccessException
	{
		if (Constants.logging)
		{
			System.err.println("Database.refresh ");
		}
		if (theConnection != null)
		{
			disconnect();
		}
		connect();
	}

	public static User getUser(String aName)
	{
		assert theConnection != null : "theConnection is null";
		if (Constants.logging)
		{
			System.err.println("Database.getUser: " + aName);
		}
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
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
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
		return myUser;
	}

	public static boolean existsUser(String aName)
	{
		assert theConnection != null : "theConnection is null";
		if (Constants.logging)
		{
			System.err.println("Database.existsUser: " + aName);
		}
		ResultSet res;
		boolean myBoolean = false;
		try
		{

		PreparedStatement sqlGetUser = theConnection.prepareStatement(sqlExistsUserString);
		sqlGetUser.setString(1, aName);
		res = sqlGetUser.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
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

	public static Room getRoom(int roomnr)
	{
		assert theConnection != null : "theConnection is null";
		if (Constants.logging)
		{
			System.err.println("Database.getRoom: " + roomnr);
		}
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
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
			return null;
		}
		res.first();
		myRoom  = new Room(
			res.getInt("id"),
			"title as yet unknown", 
			res.getString("contents"),
			res.getInt("south"), res.getInt("north"),
			res.getInt("west"), res.getInt("east"),
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

	public static ItemDef getItemDef(int itemdefnr)
	{
		assert theConnection != null : "theConnection is null";
		if (Constants.logging)
		{
			System.err.println("Database.getItemDef: " + itemdefnr);
		}
		ResultSet res;
		ItemDef myItemDef = null;
		try
		{

		PreparedStatement sqlGetItemDef = theConnection.prepareStatement(sqlGetItemDefString);
//		sqlGetItemDef.setBigDecimal
//		sqlGetItemDef.setInt
		sqlGetItemDef.setInt(1, itemdefnr);
		res = sqlGetItemDef.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
			return null;
		}
		res.first();
		myItemDef  = new ItemDef(
			itemdefnr,
			res.getString("adject1"),
			res.getString("adject2"), res.getString("adject3"),
			res.getString("name"), res.getString("description"),
			res.getInt("gold"), res.getInt("silver"), res.getInt("copper"));
		// do stuff with attributes.
		String drinkable = res.getString("drinkable");
		String eatable = res.getString("eatable");
		String readable = res.getString("readdescr");
		int visible = res.getInt("visible");
		int dropable = res.getInt("dropable");
		int getable = res.getInt("getable");
		if ( (drinkable != null) && (!drinkable.trim().equals("")) )
		{
			myItemDef.setAttribute(new Attribute("drinkable", drinkable, "string"));
		}
		if ( (eatable != null) && (!eatable.trim().equals("")) )
		{
			myItemDef.setAttribute(new Attribute("eatable", eatable, "string"));
		}
		if ( (readable != null) && (!readable.trim().equals("")) )
		{
			myItemDef.setAttribute(new Attribute("readable", readable, "string"));
		}
		if (dropable == 0)
		{
			myItemDef.setAttribute(new Attribute("notdropable", "", "string"));
		}
		if (getable == 0)
		{
			myItemDef.setAttribute(new Attribute("notgetable", "", "string"));
		}
		if (visible == 0)
		{
			myItemDef.setAttribute(new Attribute("invisible", "", "string"));
		}
		res.close();
		sqlGetItemDef.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (Constants.logging)
		{
			System.err.println("Database.getItemDef: returns " + myItemDef);
		}
		return myItemDef;
	}

	public static String getInventory(Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.getInventory: " + aCharacter.getName());
		}
		assert theConnection != null : "theConnection is null";
		StringBuffer myInventory = new StringBuffer("<UL>");
		ResultSet res;
		try
		{

		PreparedStatement sqlGetInventories = theConnection.prepareStatement(sqlGetInventoryCharacterString);
		sqlGetInventories.setString(1, aCharacter.getName());
		res = sqlGetInventories.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
			return null;
		}
		while (res.next())
		{
			int amount = res.getInt("amount");
			if (amount != 1)
			{
				// 5 gold, hard cups
				myInventory.append("<LI>" + amount + " " + res.getString("adject1")
					+ ", " + res.getString("adject2") + " " + 
					res.getString("name") + "s.\r\n");
			}
			else
			{
				// a gold, hard cup
				myInventory.append("<LI>a " + res.getString("adject1")
					+ ", " + res.getString("adject2") + " " + 
					res.getString("name") + ".\r\n");
			}
		}
		res.close();
		sqlGetInventories.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		myInventory.append("</UL><BR>");
		if (Constants.logging)
		{
			System.err.println("Database.getInventory: returns " + myInventory);
		}
		return myInventory.toString();
	}

	public static String getInventory(Room aRoom)
	{
		if (Constants.logging)
		{
			System.err.println("Database.getInventory: " + aRoom.getId());
		}
		assert theConnection != null : "theConnection is null";
		StringBuffer myInventory = new StringBuffer();
		ResultSet res;
		try
		{

		PreparedStatement sqlGetInventories = theConnection.prepareStatement(sqlGetInventoryRoomString);
		sqlGetInventories.setString(1, aRoom.getId() + "");
		res = sqlGetInventories.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
			return null;
		}
		while (res.next())
		{
			int amount = res.getInt("amount");
			if (amount > 1)
			{
				myInventory.append(amount + " " + res.getString("adject1")
					+ ", " + res.getString("adject2") + " "
					+ res.getString("name") + "s are here.<BR>\r\n");
			}
			else
			{
				myInventory.append("A " + res.getString("adject1")
					+ ", " + res.getString("adject2") + " "
					+ res.getString("name") + " is here.<BR>\r\n");
			}
		}
		res.close();
		sqlGetInventories.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (Constants.logging)
		{
			System.err.println("Database.getInventory: returns " + myInventory);
		}
		return myInventory.toString();
	}

	public static int pickupItem(int amount,
								String adject1,
								String adject2,
								String adject3,
								String name,
								Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.pickupItem: " + amount +
								" " + adject1 + 
								" " + adject2 +
								" " + adject3 +
								" " + name + " " + aCharacter.getName());
		}
		assert theConnection != null : "theConnection is null";
		int res = 0;
		Item myItem = getItem(adject1, adject2, adject3, name, aCharacter);
		try
		{
		PreparedStatement sqlPickItem = theConnection.prepareStatement(sqlPickupItemString);
		sqlPickItem.setString(1, aCharacter.getName());
		sqlPickItem.setInt(2, myItem.getId());
		sqlPickItem.setInt(3, aCharacter.getRoom().getId());
		sqlPickItem.setInt(4, amount);
		res = sqlPickItem.executeUpdate();
		sqlPickItem.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (Constants.logging)
		{
			System.err.println("Database.pickupItem: returns " + res);
		}
		return res;
	}

	public static void getItemAttributes(int anId, Item anItem)
	{
		if (Constants.logging)
		{
			System.err.println("Database.getItemAttributes: " + anId + 
								" " + anItem);
		}
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		try
		{
		PreparedStatement sqlGetItemAttributes = theConnection.prepareStatement(sqlGetItemAttributesString);
		sqlGetItemAttributes.setInt(1, anId);
		res = sqlGetItemAttributes.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
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

	public static Item getItem(String adject1,
								String adject2,
								String adject3,
								String name,
								Room aRoom)
	{
		if (Constants.logging)
		{
			System.err.println("Database.getItem: " + adject1 + 
								" " + adject2 +
								" " + adject3 +
								" " + name + " " + aRoom.getId());
		}
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		Item anItem = null;
		try
		{
		PreparedStatement sqlGetItem = theConnection.prepareStatement(sqlGetItemRoomString);
		sqlGetItem.setString(1, aRoom.getId()+"");
		sqlGetItem.setString(2, name);
		sqlGetItem.setString(3, (adject1!=null ? adject1 :""));
		sqlGetItem.setString(4, (adject2!=null ? adject1 :""));
		sqlGetItem.setString(5, (adject3!=null ? adject1 :""));
		res = sqlGetItem.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
			return null;
		}
		int anItemId = 0;
		int anItemInstanceId = 0;
		while (res.next())
		{
			anItemInstanceId = res.getInt("id");
			anItemId = res.getInt("itemid");
			
		}
		res.close();
		sqlGetItem.close();
		anItem = new Item(getItemDef(anItemId));
		getItemAttributes(anItemInstanceId, anItem);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (Constants.logging)
		{
			System.err.println("Database.getItem: returns " + anItem);
		}
		return anItem;
	}

	public static Item getItem(String adject1,
								String adject2,
								String adject3,
								String name,
								Character aChar)
	{
		if (Constants.logging)
		{
			System.err.println("Database.getItem: " + adject1 + 
								" " + adject2 +
								" " + adject3 +
								" " + name + " " + aChar.getName());
		}
		assert theConnection != null : "theConnection is null";
		ResultSet res;
		Item anItem = null;
		try
		{
		PreparedStatement sqlGetItem = theConnection.prepareStatement(sqlGetItemCharacterString);
		sqlGetItem.setString(1, aChar.getName()+"");
		sqlGetItem.setString(2, name);
		sqlGetItem.setString(3, (adject1!=null ? adject1 :""));
		sqlGetItem.setString(4, (adject2!=null ? adject1 :""));
		sqlGetItem.setString(5, (adject3!=null ? adject1 :""));
		res = sqlGetItem.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
			return null;
		}
		int anItemId = 0;
		int anItemInstanceId = 0;
		while (res.next())
		{
			anItemInstanceId = res.getInt("id");
			anItemId = res.getInt("itemid");
			
		}
		res.close();
		sqlGetItem.close();
		anItem = new Item(getItemDef(anItemId));
		getItemAttributes(anItemInstanceId, anItem);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (Constants.logging)
		{
			System.err.println("Database.getItem: returns " + anItem);
		}
		return anItem;
	}

	public static Vector getCharacters()
	{
		if (Constants.logging)
		{
			System.err.println("Database.getCharacters: ");
		}
		assert theConnection != null : "theConnection is null";
		Vector myVector = new Vector(50);
		ResultSet res;
		User myUser = null;
		try
		{

		PreparedStatement sqlGetChars =theConnection.prepareStatement(sqlGetCharactersString);
		res = sqlGetChars.executeQuery();
		if (res == null)
		{
			if (Constants.logging)
			{
				System.err.println("   resultset null");
			}
			return new Vector();
		}
		while (res.next())
		{
			String myName = res.getString("name");
			String myPasswd = res.getString("password");
			int gold = res.getInt("gold");
			int silver = res.getInt("silver");
			int copper = res.getInt("copper");
			if (Constants.logging)
			{
				System.err.println("   name: " + myName);
			}
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
			}
			else
			{
				Character myNewChar = new Character(myName,
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
		if (Constants.logging)
		{
			System.err.println("Database.getErrorMessage: " + originalErr + "," + myErrMsg);
		}
		return myErrMsg;
	}

	/**
	 * returns the logonmessage when logging into the game
	 *
	 */
	public static String getLogonMessage()
	{
		if (Constants.logging)
		{
			System.err.println("Database.getLogonMessage: ");
		}
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
	 *
	 */
	public static String getHelp(String aCommand)
	{
		if (Constants.logging)
		{
			System.err.println("Database.getHelp: " + aCommand);
		}
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
	 * returns a list of emails
	 *
	 */
	public static String getListOfMail(User aUser)
	{
		if (Constants.logging)
		{
			System.err.println("Database.getListOfMail " + aUser);
		}
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
	 * reads a mail
	 *
	 */
	public static String readMail(User aUser, int messagenr)
		throws MailException
	{
		if (Constants.logging)
		{
			System.err.println("Database.readMail " + aUser + "," + messagenr);
		}
		return doStuffWithMail(aUser, messagenr, false);
	}

	/**
	 * deletes a mail
	 *
	 */
	public static String deleteMail(User aUser, int messagenr)
		throws MailException
	{
		if (Constants.logging)
		{
			System.err.println("Database.deleteMail " + aUser + "," + messagenr);
		}
		return doStuffWithMail(aUser, messagenr, true);
	}

	/**
	 * does either of two things with mail, dependant of the boolean
parameter
	 * @param deleteIt boolean, <UL><LI>true = delete mail<LI>false = do not
	 * delete mail, but update <I>haveread</I></UL>
	 */
	private static String doStuffWithMail(User aUser, int messagenr, boolean deleteIt)
		throws MailException
	{
		if (Constants.logging)
		{
			System.err.println("Database.doStuffWitheMail " + aUser + "," + messagenr);
		}
		if (messagenr <= 0)
		{
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.INVALIDMAILERROR);
			}
			throw new MailException(Constants.INVALIDMAILERROR);
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
				if (Constants.logging)
				{
					System.err.println("thrown: " + Constants.INVALIDMAILERROR);
				}
				throw new MailException(Constants.INVALIDMAILERROR);
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
			if (Constants.logging)
			{
				System.err.println("thrown: " + Constants.INVALIDMAILERROR);
			}
			throw new MailException(Constants.INVALIDMAILERROR);
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
	 * send mud mail
	 */
	public static void sendMail(User aUser, User toUser, String header, String message)
	{
		if (Constants.logging)
		{
			System.err.println("Database.sendMail: " + aUser + "," + toUser + "," + header + "," + message);
		}
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
	 * first checks the sillynamestable in the database, then checks the
	 * unbantable and
	 *  as last check checks the bantable
	 * @return boolean, true if found, false if not found
	 * @param username String, name of the playercharacter 
	 * @param address String, the address of the player 
	 */
	public static boolean isUserBanned(String username, String address)
	{
		if (Constants.logging)
		{
			System.err.println("Database.isUserBanned: ");
		}
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
	 * @param aUser User
	 */
	public static boolean hasUserNewMail(User aUser)
	{
		if (Constants.logging)
		{
			System.err.println("Database.hasUserNewMail: ");
		}
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
	 * set the session password of a user into the usertable
	 * @param username String, name of the playercharacter 
	 * @param sesspwd String, the session password of the player 
	 */
	public static void setSessionPassword(String username, String sesspwd)
	{
		if (Constants.logging)
		{
			System.err.println("Database.setSessionPassword: " + username + "," + sesspwd);
		}
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
	 * activate user
	 * This is done by copying the record over to tmp_usertable
	 * @param aUser User wishing to be active
	 */
	public static void activateUser(User aUser)
	{
		if (Constants.logging)
		{
			System.err.println("Database.activateUser: " + aUser);
		}
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
		if (Constants.logging)
		{
			System.err.println("Database.deactivateUser: " + aUser);
		}
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
	 * A new record will be inserted into the usertable.
	 * @param aUser User wishing to be created
	 */
	public static void createUser(User aUser)
	{
		if (Constants.logging)
		{
			System.err.println("Database.createUser: " + aUser);
		}
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
	 * @param aCharacter Character with changed title
	 */
	public static void setTitle(Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.setTitle: " + aCharacter + "," + aCharacter.getTitle());
		}
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetTitleUser = theConnection.prepareStatement(sqlSetTitleString);
		sqlSetTitleUser.setString(1, aCharacter.getTitle());
		sqlSetTitleUser.setString(2, aCharacter.getName());
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
	 * @param aCharacter Character with changed drinkstats
	 */
	public static void setDrinkstats(Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.setDrinkstats: " + aCharacter + "," + aCharacter.getDrinkstats());
		}
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetDrinkstatsUser = theConnection.prepareStatement(sqlSetDrinkstatsString);
		sqlSetDrinkstatsUser.setInt(1, aCharacter.getDrinkstats());
		sqlSetDrinkstatsUser.setString(2, aCharacter.getName());
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
	 * @param aCharacter Character with changed eatstats
	 */
	public static void setEatstats(Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.setEatstats: " + aCharacter + "," + aCharacter.getEatstats());
		}
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetEatstatsUser = theConnection.prepareStatement(sqlSetEatstatsString);
		sqlSetEatstatsUser.setInt(1, aCharacter.getEatstats());
		sqlSetEatstatsUser.setString(2, aCharacter.getName());
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
	 * @param aCharacter Character with changed sleep
	 */
	public static void setSleep(Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.setSleep: " + aCharacter + "," + aCharacter.isaSleep());
		}
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetSleepUser = theConnection.prepareStatement(sqlSetSleepString);
		sqlSetSleepUser.setInt(1, (aCharacter.isaSleep() ? 1 : 0));
		sqlSetSleepUser.setString(2, aCharacter.getName());
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
	 * @param aCharacter Character with changed room
	 */
	public static void setRoom(Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.setRoom: " + aCharacter + "," + aCharacter.getRoom());
		}
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetRoomUser = theConnection.prepareStatement(sqlSetRoomString);
		sqlSetRoomUser.setInt(1, aCharacter.getRoom().getId());
		sqlSetRoomUser.setString(2, aCharacter.getName());
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
	 * @param aCharacter Character with new whimpy setting
	 */
	public static void setWhimpy(Character aCharacter)
	{
		if (Constants.logging)
		{
			System.err.println("Database.setWhimpy: " + aCharacter + "," + aCharacter.getWhimpy());
		}
		assert theConnection != null : "theConnection is null";
		try
		{

		PreparedStatement sqlSetWhimpyUser = theConnection.prepareStatement(sqlSetWhimpyString);
		sqlSetWhimpyUser.setInt(1, aCharacter.getWhimpy());
		sqlSetWhimpyUser.setString(2, aCharacter.getName());
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
		if (Constants.logging)
		{
			System.err.println("Database.setPkill: " + aUser + "," + aUser.isPkill());
		}
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
