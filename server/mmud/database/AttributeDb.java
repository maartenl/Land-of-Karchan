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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import mmud.Attribute;
import mmud.MudException;
import mmud.characters.Person;
import mmud.items.Item;
import mmud.rooms.Room;

/**
 * Used for queries towards the database regarding attributes. Attributes are
 * quite general and can be related to characters, items and room instances.
 * 
 * @see Database
 */
public class AttributeDb {

	private final static String sqlSetAttributeRoom = "replace into mm_roomattributes (name, value, value_type, id) "
			+ "values(?, ?, ?, ?)";
	private final static String sqlSetAttributeChar = "replace into mm_charattributes (name, value, value_type, charname) "
			+ "values(?, ?, ?, ?)";
	private final static String sqlSetAttributeItem = "replace into mm_itemattributes (name, value, value_type, id) "
			+ "values(?, ?, ?, ?)";

	private final static String sqlRemoveAttributeRoom = "delete from mm_roomattributes where name = ? and id = ?";
	private final static String sqlRemoveAttributeChar = "delete from mm_charattributes where name = ? and charname = ?";
	private final static String sqlRemoveAttributeItem = "delete from mm_itemattributes where name = ? and id = ?";

	private final static String sqlGetAttributesChar = "select * from mm_charattributes "
			+ "where charname = ?";
	private final static String sqlGetAttributesItem = "select * from mm_itemattributes "
			+ "where id = ?";
	private final static String sqlGetAttributesRoom = "select * from mm_roomattributes "
			+ "where id = ?";

	/**
	 * Set the attribute belonging to a certain room.
	 * 
	 * @param anAttribute
	 *            attribute to be set/added.
	 * @param aRoom
	 *            the room that is to have this attribute.
	 */
	public static void setAttribute(Attribute anAttribute, Room aRoom)
			throws MudDatabaseException {
		Logger.getLogger("mmud").finer("");
		try {
			PreparedStatement statSetAttrib = Database
					.prepareStatement(sqlSetAttributeRoom);
			statSetAttrib.setString(1, anAttribute.getName());
			statSetAttrib.setString(2, anAttribute.getValue());
			statSetAttrib.setString(3, anAttribute.getValueType());
			statSetAttrib.setInt(4, aRoom.getId());
			statSetAttrib.executeUpdate();
			statSetAttrib.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error setting attribute of room.", e);
		}
	}

	/**
	 * Set the attribute belonging to a certain person.
	 * 
	 * @param anAttribute
	 *            attribute to be set/added.
	 * @param aPerson
	 *            the person that is to have this attribute.
	 */
	public static void setAttribute(Attribute anAttribute, Person aPerson)
			throws MudDatabaseException {
		Logger.getLogger("mmud").finer("");
		try {
			PreparedStatement statSetAttrib = Database
					.prepareStatement(sqlSetAttributeChar);
			statSetAttrib.setString(1, anAttribute.getName());
			statSetAttrib.setString(2, anAttribute.getValue());
			statSetAttrib.setString(3, anAttribute.getValueType());
			statSetAttrib.setString(4, aPerson.getName());
			statSetAttrib.executeUpdate();
			statSetAttrib.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error setting attribute of character.", e);
		}
	}

	/**
	 * Set the attribute belonging to a certain item.
	 * 
	 * @param anAttribute
	 *            attribute to be set/added.
	 * @param anItem
	 *            the item that is to have this attribute.
	 */
	public static void setAttribute(Attribute anAttribute, Item anItem)
			throws MudDatabaseException {
		Logger.getLogger("mmud").finer("");
		try {
			PreparedStatement statSetAttrib = Database
					.prepareStatement(sqlSetAttributeItem);
			statSetAttrib.setString(1, anAttribute.getName());
			statSetAttrib.setString(2, anAttribute.getValue());
			statSetAttrib.setString(3, anAttribute.getValueType());
			statSetAttrib.setInt(4, anItem.getId());
			statSetAttrib.executeUpdate();
			statSetAttrib.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error setting attribute of item.", e);
		}
	}

	/**
	 * Remove the attribute belonging to a certain room. When the attribute does
	 * not exist, this does nothing.
	 * 
	 * @param anAttribute
	 *            attribute to be removed.
	 * @param aRoom
	 *            the room that is to not have this attribute anymore.
	 */
	public static void removeAttribute(Attribute anAttribute, Room aRoom)
			throws MudDatabaseException {
		Logger.getLogger("mmud").finer("");
		try {
			PreparedStatement statRemoveAttrib = Database
					.prepareStatement(sqlRemoveAttributeRoom);
			statRemoveAttrib.setString(1, anAttribute.getName());
			statRemoveAttrib.setInt(2, aRoom.getId());
			statRemoveAttrib.executeUpdate();
			statRemoveAttrib.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error removing attribute from room.", e);
		}
	}

	/**
	 * Remove the attribute belonging to a certain person. When the attribute
	 * does not exist, this does nothing.
	 * 
	 * @param anAttribute
	 *            attribute to be removed.
	 * @param aPerson
	 *            the person that is to not have this attribute anymore.
	 */
	public static void removeAttribute(Attribute anAttribute, Person aPerson)
			throws MudDatabaseException {
		Logger.getLogger("mmud").finer("");
		try {
			PreparedStatement statRemoveAttrib = Database
					.prepareStatement(sqlRemoveAttributeChar);
			statRemoveAttrib.setString(1, anAttribute.getName());
			statRemoveAttrib.setString(2, aPerson.getName());
			statRemoveAttrib.executeUpdate();
			statRemoveAttrib.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error removing attribute from character.", e);
		}
	}

	/**
	 * Remove the attribute belonging to a certain item. When the attribute does
	 * not exist, this does nothing.
	 * 
	 * @param anAttribute
	 *            attribute to be removed.
	 * @param anItem
	 *            the item that is to not have this attribute anymore.
	 */
	public static void removeAttribute(Attribute anAttribute, Item anItem)
			throws MudDatabaseException {
		Logger.getLogger("mmud").finer("");
		try {
			PreparedStatement statRemoveAttrib = Database
					.prepareStatement(sqlRemoveAttributeItem);
			statRemoveAttrib.setString(1, anAttribute.getName());
			statRemoveAttrib.setInt(2, anItem.getId());
			statRemoveAttrib.executeUpdate();
			statRemoveAttrib.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error removing atttribute from item.", e);
		}
	}

	/**
	 * Retrieve all the attributes belonging to a certain person. When the
	 * person does not exist or has no attributes, this returns an empty array.
	 * 
	 * @param aName
	 *            the name of the person whose attributes need to be retrieved.
	 * @return Attribute Vector containing the attributes.
	 */
	public static Vector getAttributesPerson(String aName) throws MudException {
		Logger.getLogger("mmud").finer("aName=" + aName);
		ResultSet res;
		Vector result = new Vector();
		try {
			PreparedStatement sqlGetCharAttributes = Database
					.prepareStatement(sqlGetAttributesChar);
			sqlGetCharAttributes.setString(1, aName);
			res = sqlGetCharAttributes.executeQuery();
			if (res == null) {
				Logger.getLogger("mmud").info("resultset null");
				return null;
			}
			while (res.next()) {
				Attribute myAttrib = new Attribute(res.getString("name"), res
						.getString("value"), res.getString("value_type"));
				result.add(myAttrib);
			}
			res.close();
			sqlGetCharAttributes.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error getting attributes from character.", e);
		}
		return result;
	}

	/**
	 * Retrieve all the attributes belonging to a certain item. When the item
	 * does not exist or has no attributes, this returns an empty array.
	 * 
	 * @param anItemId
	 *            the identification of the item which attributes need to be
	 *            retrieved.
	 * @return Attribute Vector containing the attributes.
	 */
	public static Vector getAttributesItem(int anItemId) throws MudException {
		Logger.getLogger("mmud").finer("anItemId=" + anItemId);
		ResultSet res;
		Vector result = new Vector();
		try {
			PreparedStatement sqlGetItemAttributes = Database
					.prepareStatement(sqlGetAttributesItem);
			sqlGetItemAttributes.setInt(1, anItemId);
			res = sqlGetItemAttributes.executeQuery();
			if (res == null) {
				Logger.getLogger("mmud").info("resultset null");
				return null;
			}
			while (res.next()) {
				Attribute myAttrib = new Attribute(res.getString("name"), res
						.getString("value"), res.getString("value_type"));
				result.add(myAttrib);
			}
			res.close();
			sqlGetItemAttributes.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error getting attributes from item.", e);
		}
		return result;
	}

	/**
	 * Retrieve all the attributes belonging to a certain room. When the room
	 * does not exist or has no attributes, this returns an empty array.
	 * 
	 * @param aRoom
	 *            the room which attributes need to be retrieved.
	 * @return Attribute Vector containing the attributes.
	 */
	public static Vector getAttributes(Room aRoom) throws MudException {
		return (aRoom == null ? null : getAttributesRoom(aRoom.getId()));
	}

	/**
	 * Retrieve all the attributes belonging to a certain item. When the item
	 * does not exist or has no attributes, this returns an empty array.
	 * 
	 * @param anItem
	 *            the item which attributes need to be retrieved.
	 * @return Attribute Vector containing the attributes.
	 */
	public static Vector getAttributes(Item anItem) throws MudException {
		return (anItem == null ? null : getAttributesItem(anItem.getId()));
	}

	/**
	 * Retrieve all the attributes belonging to a certain person. When the
	 * person does not exist or has no attributes, this returns an empty array.
	 * 
	 * @param aPerson
	 *            the person whose attributes need to be retrieved.
	 * @return Attribute Vector containing the attributes.
	 */
	public static Vector getAttributes(Person aPerson) throws MudException {
		return (aPerson == null ? null : getAttributesPerson(aPerson.getName()));
	}

	/**
	 * Retrieve all the attributes belonging to a certain room. When the room
	 * does not exist or has no attributes, this returns an empty array.
	 * 
	 * @param aRoomId
	 *            the identification of the room which attributes need to be
	 *            retrieved.
	 * @return Attribute Vector containing the attributes.
	 */
	public static Vector getAttributesRoom(int aRoomId) throws MudException {
		Logger.getLogger("mmud").finer("aRoomId=" + aRoomId);
		ResultSet res;
		Vector result = new Vector();
		try {
			PreparedStatement sqlGetRoomAttributes = Database
					.prepareStatement(sqlGetAttributesRoom);
			sqlGetRoomAttributes.setInt(1, aRoomId);
			res = sqlGetRoomAttributes.executeQuery();
			if (res == null) {
				Logger.getLogger("mmud").info("resultset null");
				return null;
			}
			while (res.next()) {
				Attribute myAttrib = new Attribute(res.getString("name"), res
						.getString("value"), res.getString("value_type"));
				result.add(myAttrib);
			}
			res.close();
			sqlGetRoomAttributes.close();
		} catch (SQLException e) {
			throw new MudDatabaseException(
					"database error getting attributes from room.", e);
		}
		return result;
	}

}
