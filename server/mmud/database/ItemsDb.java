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
	public static String sqlGetInventoryContainerString =
		  "select count(*) as amount, adject1, adject2, adject3, name "
		+ "from mm_itemitemtable, mm_itemtable, mm_items "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_itemitemtable.id = mm_itemtable.id and "
		+ "mm_itemitemtable.containerid = ? and "
		+ "visible = 1 "
		+ "group by adject1, adject2, adject3, name";

	public static String sqlDeleteItemCharString =
		"delete from mm_charitemtable "
		+ "where id = ?";
	public static String sqlDeleteItemRoomString =
		"delete from mm_roomitemtable "
		+ "where id = ?";
	public static String sqlDeleteItemItemString =
		"delete from mm_itemitemtable "
		+ "where id = ?";
	public static String sqlAddItemCharString =
		"insert into mm_charitemtable "
		+ "(id, belongsto) "
		+ "values(?, ?)";
	public static String sqlAddItemRoomString =
		"insert into mm_roomitemtable "
		+ "(id, room) "
		+ "values(?, ?)";
	public static String sqlAddItemItemString =
		"insert into mm_itemitemtable "
		+ "(id, containerid) "
		+ "values(?, ?)";
	public static String sqlGetItemRoomString =
		"select mm_itemtable.* "
		+ "from mm_roomitemtable, mm_items, mm_itemtable "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_itemtable.id = mm_roomitemtable.id and "
        + "mm_roomitemtable.room = ? and "
		+ "name = ? and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0";
	public static String sqlGetItemPersonString =
		"select mm_itemtable.itemid, mm_charitemtable.* "
		+ "from mm_charitemtable, mm_itemtable, mm_items "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_itemtable.id = mm_charitemtable.id and "
        + "belongsto = ? and "
		+ "name = ? and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0";
	public static String sqlGetItemContainerString =
		"select mm_itemtable.itemid, mm_itemitemtable.* "
		+ "from mm_itemitemtable, mm_itemtable, mm_items "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_itemtable.id = mm_itemitemtable.id and "
        + "containerid = ? and "
		+ "name = ? and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0 and "
		+ "field(?, adject1, adject2, adject3, \"\")!=0";
	public static String sqlGetAllItemContainerString =
		"select mm_itemtable.itemid, mm_itemitemtable.* "
		+ "from mm_itemitemtable, mm_itemtable, mm_items "
		+ "where mm_itemtable.itemid = mm_items.id and "
		+ "mm_itemtable.id = mm_itemitemtable.id and "
        + "containerid = ?";
	public static String sqlTransferItemString =
		"update mm_charitemtable "
		+ "set belongsto = ? "
		+ "where id = ?";
	public static String sqlDeleteItemString =
		"delete from mm_itemtable "
		+ "where id = ?";
	public static String sqlAddItemString =
		"insert into mm_itemtable "
		+ "(itemid) "
		+ "values(?)";

	public static String sqlGetItemDefString = "select * from mm_items where id = ?";

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
		int container = res.getInt("container");
		String eatable = res.getString("eatable");
		String readable = res.getString("readdescr");
		int visible = res.getInt("visible");
		int dropable = (itemdefnr < 0 ? 0 : res.getInt("dropable"));
		int getable = (itemdefnr < 0 ? 0 : res.getInt("getable"));
		if (container != 0)
		{
			myItemDef.setAttribute(new Attribute("container", "true", "boolean"));
		}
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		Logger.getLogger("mmud").info("returns: " + myInventory);
		return myInventory.toString();
	}

	/**
	 * Returns a bulleted list of all items visible in a container.
	 * @param aContainer item object that has a number of visible items.
	 * @param String containing a list of items visible in the room.
	 */
	public static String getInventory(Item aContainer)
	{
		Logger.getLogger("mmud").finer("");
		StringBuffer myInventory = new StringBuffer();
		ResultSet res;
		try
		{

		PreparedStatement sqlGetInventories = Database.prepareStatement(sqlGetInventoryContainerString);
		sqlGetInventories.setInt(1, aContainer.getId());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		Logger.getLogger("mmud").info("returns: " + myInventory);
		return myInventory.toString();
	}

	/**
	 * Remove item from inventory of room.
	 * @param anItem the item to be removed.
	 * @throws ItemDoesNotExistException when we were unable to remove the item.
	 */
	public static void deleteItemFromRoom(Item anItem)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement sqlDeleteItem = Database.prepareStatement(sqlDeleteItemRoomString);
			sqlDeleteItem.setInt(1, anItem.getId());
			res = sqlDeleteItem.executeUpdate();
			sqlDeleteItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * Adds an item to the inventory of a person.
	 * @param anItem the item to be added
	 * @param aPerson the person
	 * @throws ItemDoesNotExistException when we were unable to add the item.
	 */
	public static void addItemToChar(Item anItem, Person aPerson)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement sqlAddItem = Database.prepareStatement(sqlAddItemCharString);
			sqlAddItem.setInt(1, anItem.getId());
			sqlAddItem.setString(2, aPerson.getName());
			res = sqlAddItem.executeUpdate();
			sqlAddItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * Remove an item from the inventory of a character.
	 * @param anItem the item to be removed
	 * @throws ItemDoesNotExistException when we were unable to remove the item.
	 */
	public static void deleteItemFromChar(Item anItem)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement sqlDeleteItem = Database.prepareStatement(sqlDeleteItemCharString);
			sqlDeleteItem.setInt(1, anItem.getId());
			res = sqlDeleteItem.executeUpdate();
			sqlDeleteItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * Remove an item from the contents of another item. A good example
	 * of this method is for instance removing a gold coin from a  bag.
	 * @param anItem the item to be removed
	 * @throws ItemDoesNotExistException when we were unable to remove the item.
	 */
	public static void deleteItemFromContainer(Item anItem)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement sqlDeleteItem = Database.prepareStatement(sqlDeleteItemItemString);
			sqlDeleteItem.setInt(1, anItem.getId());
			res = sqlDeleteItem.executeUpdate();
			sqlDeleteItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * Delete an item. Item is also removed from wherever it resides, be it
	 * person or room or inside another item.
	 * @param anItem the item to be removed
	 * @throws ItemDoesNotExistException when we were unable to remove the item.
	 */
	public static void deleteItem(Item anItem)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			try
			{
				deleteItemFromChar(anItem);
			}
			catch (ItemException e)
			{
			}
			try
			{
				deleteItemFromRoom(anItem);
			}
			catch (ItemException e)
			{
			}
			try
			{
				deleteItemFromContainer(anItem);
			}
			catch (ItemException e)
			{
			}
			PreparedStatement sqlDeleteItem = Database.prepareStatement(sqlDeleteItemString);
			sqlDeleteItem.setInt(1, anItem.getId());
			res = sqlDeleteItem.executeUpdate();
			sqlDeleteItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * Create a new item. Item is not used automatically.
	 * @param anItemDef the item definition to use as a template
	 * @return Item containing the id of the new item.
	 */
	public static Item addItem(ItemDef anItemDef)
	{
		Logger.getLogger("mmud").finer("");
		Item myItem = null;
		try
		{
			PreparedStatement sqlAddItem = Database.prepareStatement(sqlAddItemString);
			sqlAddItem.setInt(1, anItemDef.getId());
			sqlAddItem.executeUpdate();
			ResultSet res = sqlAddItem.getGeneratedKeys();
			System.out.println(res);
			if (res.next())
			{
				myItem = new Item(anItemDef, res.getInt(1));
			}
			sqlAddItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		return myItem;
	}

	/**
	 * Add an item to a room.
	 * @param anItem the item to be added
	 * @param aRoom the room into which the item should be dropped.
	 * @throws ItemDoesNotExistException when we were unable to add the item.
	 */
	public static void addItemToRoom(Item anItem,
								Room aRoom)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement sqlAddItem = Database.prepareStatement(sqlAddItemRoomString);
			sqlAddItem.setInt(1, anItem.getId());
			sqlAddItem.setInt(2, aRoom.getId());
			res = sqlAddItem.executeUpdate();
			sqlAddItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * Add an item to an item.
	 * @param anItem the item to be added
	 * @param aContainer the item to act as a container
	 * @throws ItemDoesNotExistException when we were unable to add the item.
	 */
	public static void addItemToContainer(Item anItem, Item aContainer)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement sqlAddItem = Database.prepareStatement(sqlAddItemItemString);
			sqlAddItem.setInt(1, anItem.getId());
			sqlAddItem.setInt(2, aContainer.getId());
			res = sqlAddItem.executeUpdate();
			sqlAddItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res != 1)
		{
			throw new ItemDoesNotExistException();
		}
	}

	/**
	 * Transfer an item from one persons inventory to the inventory of
	 * another person. Usefull when giving/buying/selling items.
	 * Attention! No checking takes place
	 * wether or not the item is suitable to be transferred.
	 * @param anItem the item to be transferred
	 * @param aUser the user who needs to receive the item in his/her
	 * inventory.
	 * @throws ItemDoesNotExistException when we were unable to transfer the item.
	 */
	public static void transferItem(Item anItem,
								Person aPerson)
	throws ItemDoesNotExistException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement sqlDropItem = Database.prepareStatement(sqlTransferItemString);
			sqlDropItem.setString(1, aPerson.getName());
			sqlDropItem.setInt(2, anItem.getId());
			res = sqlDropItem.executeUpdate();
			sqlDropItem.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		if (res == 0)
		{
			// the idea here was to change just 1 row containing 1 item
			// if this did not succeed, it means that we had some
			// problems getting the appropriate item
			throw new ItemDoesNotExistException();
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		Logger.getLogger("mmud").info("returns: " + items);
		return items;
	}

	/**
	 * Retrieve specific items from a container.
	 * @param adject1 the first adjective. Null value means first adjective
	 * not relevant, i.e. only name of the item.
	 * @param adject2 the second adjective. Null value means second adjective
	 * not relevant, i.e. only name and first adjective of the item.
	 * @param adject3 the third adjective. Null value means first adjective
	 * not relevant, i.e. only first and second adjective and name of the item.
	 * @param name the name of the item
	 * @param aContainer the item where said item(s) should be located
	 * @return Vector containing all Item objects found.
	 */
	public static Vector getItemsFromContainer(String adject1,
								String adject2,
								String adject3,
								String name,
								Item aContainer)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Vector items = new Vector();
		try
		{
			PreparedStatement sqlGetItem = Database.prepareStatement(sqlGetItemContainerString);
			sqlGetItem.setString(1, aContainer.getId()+"");
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		Logger.getLogger("mmud").info("returns: " + items);
		return items;
	}

	/**
	 * Retrieve all items from a container.
	 * @param aContainer the item where said items are located
	 * @return Vector containing all Item objects found.
	 */
	public static Vector getItemsFromContainer(Item aContainer)
	{
		Logger.getLogger("mmud").finer("");
		ResultSet res;
		Vector items = new Vector();
		try
		{
			PreparedStatement sqlGetItem = Database.prepareStatement(sqlGetAllItemContainerString);
			sqlGetItem.setString(1, aContainer.getId()+"");
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
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
		Logger.getLogger("mmud").finer("adject1=" + adject1 + 
			",adject2=" + adject2 +
			",adject3=" + adject3 + 
			",name=" + name + ",char=" + aChar.getName());
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
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", "sqlexception: " + e.getMessage());
		}
		Logger.getLogger("mmud").info("returns: " + items);
		return items;
	}


}