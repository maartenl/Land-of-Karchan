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
 * Used for queries towards the database regarding Items.
 * @see Database
 */
public class ItemsDb
{

	public static String sqlGetInventoryPersonString = 
		  "select count(*) as amount, adject1, adject2, adject3, name "
		+ "from mm_charitemtable, mm_itemtable, mm_items "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_charitemtable.id = mm_itemtable.id and "
		+ "mm_charitemtable.belongsto = ? and "
		+ "mm_charitemtable.wearing is null and "
		+ "mm_charitemtable.wielding is null and "
		+ "visible = 1 "
		+ "group by adject1, adject2, adject3, name";
	public static String sqlGetInventoryRoomString =
		  "select count(*) as amount, adject1, adject2, adject3, name "
		+ "from mm_roomitemtable, mm_itemtable, mm_items "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_roomitemtable.id = mm_itemtable.id and "
		+ "mm_roomitemtable.room = ? and "
		+ "search is null and "
		+ "visible = 1 "
		+ "group by adject1, adject2, adject3, name";

	public static String sqlPickupItem1String =
		"delete from mm_roomitemtable "
		+ "where id = ?";
	public static String sqlPickupItem2String =
		"insert into mm_charitemtable "
		+ "(id, belongsto) "
		+ "values(?, ?)";
	public static String sqlDropItem1String =
		"delete from mm_charitemtable "
		+ "where id = ?";
	public static String sqlDropItem2String =
		"insert into mm_roomitemtable "
		+ "(id, room) "
		+ "values(?, ?)";
	public static String sqlGetItemRoomString =
		"select mm_itemtable.* "
		+ "from mm_roomitemtable, mm_items, mm_itemtable "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_itemtable.id = mm_roomitemtable.id and "
        + "room = ? and "
		+ "name = ? and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0";
	public static String sqlGetItemPersonString =
		"select mm_itemtable.itemid, mm_charitemtable.* "
		+ "from mm_charitemtable, mm_itemtable, mm_items "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_itemtable.id = mm_charitemtable.id and "
        + "belongsto = ? "
		+ "and name = ? and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0";

	public static String sqlGetItemDefString = "select * from mm_items where id = ?";
	// public static String sqlCreateItem = "insert into mm_itemtable (itemid) values(?)";

	/**
	 * Returns the itemdefinition based on the itemdefinition id.
	 * @param itemdefnr item definition identification number
	 * @return ItemDef object containing all information.
	 */
	public static ItemDef getItemDef(int itemdefnr)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		ItemDef myItemDef = null;
		try
		{

		PreparedStatement sqlGetItemDef = Database.prepareStatement(sqlGetItemDefString);
//		sqlGetItemDef.setBigDecimal
//		sqlGetItemDef.setInt
		sqlGetItemDef.setInt(1, itemdefnr);
		res = sqlGetItemDef.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
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
		Logger.getLogger("mmud").info("returns: " + myItemDef);
		return myItemDef;
	}

	/**
	 * Returns the inventory of a person in the form of a bulleted list.
	 * Every item in the list is prefixed with &lt;LI&gt;.
	 * @param aPerson the person whos inventory we are interested in.
	 * @return String containing the bulleted list of items the
	 * person is carrying.
	 */
	public static String getInventory(Person aPerson)
	{
		Logger.getLogger("mmud").finer("");
		StringBuffer myInventory = new StringBuffer("");
		ResultSet res;
		try
		{

		PreparedStatement sqlGetInventories = Database.prepareStatement(sqlGetInventoryPersonString);
		sqlGetInventories.setString(1, aPerson.getName());
		res = sqlGetInventories.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
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
		Logger.getLogger("mmud").info("returns: " + myInventory);
		return myInventory.toString();
	}

	/**
	 * Returns a bulleted list of all items visible in a room.
	 * @param aRoom room object that has a number of visible items.
	 * @param String containing a list of items visible in the room.
	 */
	public static String getInventory(Room aRoom)
	{
		Logger.getLogger("mmud").finer("");
		StringBuffer myInventory = new StringBuffer();
		ResultSet res;
		try
		{

		PreparedStatement sqlGetInventories = Database.prepareStatement(sqlGetInventoryRoomString);
		sqlGetInventories.setInt(1, aRoom.getId());
		res = sqlGetInventories.executeQuery();
		if (res == null)
		{
			Logger.getLogger("mmud").info("resultset null");
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
		Logger.getLogger("mmud").info("returns: " + myInventory);
		return myInventory.toString();
	}

	/**
	 * Pick up an item off the floor. Actually,  this means an item is
	 * transferred from a room to a characters inventory.
	 * Attention! No checking takes place
	 * wether or not the item is suitable to be picked up.
	 * @param anItem the item to be picked up
	 * @param aPerson the person who wishes to pick up the item.
	 * @throws ItemDoesNotExistException when we were unable to pickup the item.
	 */
	public static void pickupItem(Item anItem,
								Person aPerson)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			// we should prepare two statements:
			// 1. the item is removed from the roomtable
			// 2. the item is added to the chartable
			PreparedStatement sqlPickItem = Database.prepareStatement(sqlPickupItem1String);
			sqlPickItem.setInt(1, anItem.getId());
			res = sqlPickItem.executeUpdate();
			sqlPickItem.close();
			if (res == 0)
			{
				// the idea here was to remove just 1 row
				// if this did not succeed, it means that we had some
				// problems getting the appropriate item
				throw new ItemDoesNotExistException();
			}
			sqlPickItem = Database.prepareStatement(sqlPickupItem2String);
			sqlPickItem.setInt(1, anItem.getId());
			sqlPickItem.setString(2, aPerson.getName());
			res = sqlPickItem.executeUpdate();
			sqlPickItem.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException("able to delete, but unable to add, database corrupted?");
		}
	}

	/**
	 * Drop an item onto the floor. Actually, this means the item is
	 * transferred from a characters inventory to a room. 
	 * Attention! No checking takes place
	 * wether or not the item is suitable to be dropped.
	 * @param anItem the item to be dropped
	 * @param aRoom the room into which the item should be dropped.
	 * In most cases this would be the same room, that a person has
	 * occupied who wishes to drop said item.
	 * @throws ItemDoesNotExistException when we were unable to drop the item.
	 */
	public static void dropItem(Item anItem,
								Room aRoom)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			// we should prepare two statements:
			// 1. the item is removed from the chartable
			// 2. the item is added to the roomtable
			PreparedStatement sqlDropItem = Database.prepareStatement(sqlDropItem1String);
			sqlDropItem.setInt(1, anItem.getId());
			res = sqlDropItem.executeUpdate();
			sqlDropItem.close();
			if (res == 0)
			{
				// the idea here was to remove just 1 row
				// if this did not succeed, it means that we had some
				// problems getting the appropriate item
				throw new ItemDoesNotExistException();
			}
			sqlDropItem = Database.prepareStatement(sqlDropItem2String);
			sqlDropItem.setInt(1, anItem.getId());
			sqlDropItem.setInt(2, aRoom.getId());
			res = sqlDropItem.executeUpdate();
			sqlDropItem.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException("able to delete, but unable to add, database corrupted?");
		}
	}


	/**
	 * Retrieve specific items from the room.
	 * @param adject1 the first adjective. Null value means first adjective
	 * not relevant, i.e. only name of the item.
	 * @param adject2 the second adjective. Null value means second adjective
	 * not relevant, i.e. only name and first adjective of the item.
	 * @param adject3 the third adjective. Null value means first adjective
	 * not relevant, i.e. only first and second adjective and name of the item.
	 * @param name the name of the item
	 * @param aRoom the room where said item should be located
	 * @return Vector containing all Item objects found.
	 */
	public static Vector getItemsFromRoom(String adject1,
								String adject2,
								String adject3,
								String name,
								Room aRoom)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Vector items = new Vector();
		try
		{
			PreparedStatement sqlGetItem = Database.prepareStatement(sqlGetItemRoomString);
			sqlGetItem.setString(1, aRoom.getId()+"");
			sqlGetItem.setString(2, name);
			sqlGetItem.setString(3, (adject1!=null ? adject1 :""));
			sqlGetItem.setString(4, (adject2!=null ? adject2 :""));
			sqlGetItem.setString(5, (adject3!=null ? adject3 :""));
			res = sqlGetItem.executeQuery();
			if (res == null)
			{
				Logger.getLogger("mmud").info("resultset null");
				return null;
			}
			int anItemId = 0;
			int anItemInstanceId = 0;
			while (res.next())
			{
				anItemInstanceId = res.getInt("id");
				anItemId = res.getInt("itemid");
				Item anItem = new Item(anItemId, anItemInstanceId);
				Database.getItemAttributes(anItem);
				items.add(anItem);
			}
			res.close();
			sqlGetItem.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Logger.getLogger("mmud").info("returns: " + items);
		return items;
	}

	/**
	 * Retrieve the item from the inventory of a character.
	 * @param adject1 the first adjective. Null value means first adjective
	 * not relevant, i.e. only name of the item.
	 * @param adject2 the second adjective. Null value means second adjective
	 * not relevant, i.e. only name and first adjective of the item.
	 * @param adject3 the third adjective. Null value means first adjective
	 * not relevant, i.e. only first and second adjective and name of the item.
	 * @param name the name of the item
	 * @param aChar the character who has the item in his/her inventory.
	 * @return Vector containing all Item objects found.
	 */
	public static Vector getItemsFromChar(String adject1,
								String adject2,
								String adject3,
								String name,
								Person aChar)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Vector items = new Vector();
		try
		{
			PreparedStatement sqlGetItem = Database.prepareStatement(sqlGetItemPersonString);
			sqlGetItem.setString(1, aChar.getName()+"");
			sqlGetItem.setString(2, name);
			sqlGetItem.setString(3, (adject1!=null ? adject1 :""));
			sqlGetItem.setString(4, (adject2!=null ? adject2 :""));
			sqlGetItem.setString(5, (adject3!=null ? adject3 :""));
			res = sqlGetItem.executeQuery();
			if (res == null)
			{
				Logger.getLogger("mmud").info("resultset null");
				return null;
			}
			int anItemId = 0;
			int anItemInstanceId = 0;
			while (res.next())
			{
				anItemInstanceId = res.getInt("id");
				anItemId = res.getInt("itemid");
				Item anItem = new Item(anItemId, anItemInstanceId);
				Database.getItemAttributes(anItem);
				items.add(anItem);
			}
			res.close();
			sqlGetItem.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		Logger.getLogger("mmud").info("returns: " + items);
		return items;
	}


}
