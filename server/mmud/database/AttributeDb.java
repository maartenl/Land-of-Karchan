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
 * Used for queries towards the database regarding attributes. Attributes
 * are quite general and can be related to characters, items and room
 * instances.
 * @see Database
 */
public class AttributeDb
{

	public static String sqlSetAttributeRoom = 
		"replace into mm_roomattributes (name, value, value_type, id) " +
		"values(?, ?, ?, ?)";
	public static String sqlSetAttributeChar = 
		"replace into mm_charattributes (name, value, value_type, charname) " +
		"values(?, ?, ?, ?)";
	public static String sqlSetAttributeItem = 
		"replace into mm_itemattributes (name, value, value_type, id) " +
		"values(?, ?, ?, ?)";

	public static String sqlRemoveAttributeRoom = 
		"delete from mm_roomattributes where name = ? and id = ?";
	public static String sqlRemoveAttributeChar = 
		"delete from mm_charattributes where name = ? and charname = ?";
	public static String sqlRemoveAttributeItem = 
		"delete from mm_itemattributes where name = ? and id = ?";

	/**
	 * Set the attribute belonging to a certain room.
	 * @param anAttribute attribute to be set/added.
	 * @param aRoom the room that is to have this attribute.
	 */
	public static void setAttribute(Attribute anAttribute, Room aRoom)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement statSetAttrib =
				Database.prepareStatement(sqlSetAttributeRoom);
			statSetAttrib.setString(1, anAttribute.getName());
			statSetAttrib.setString(2, anAttribute.getValue());
			statSetAttrib.setString(3, anAttribute.getValueType());
			statSetAttrib.setInt(4, aRoom.getId());
			res = statSetAttrib.executeUpdate();
			statSetAttrib.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Set the attribute belonging to a certain person.
	 * @param anAttribute attribute to be set/added.
	 * @param aPerson the person that is to have this attribute.
	 */
	public static void setAttribute(Attribute anAttribute, Person aPerson)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement statSetAttrib =
				Database.prepareStatement(sqlSetAttributeChar);
			statSetAttrib.setString(1, anAttribute.getName());
			statSetAttrib.setString(2, anAttribute.getValue());
			statSetAttrib.setString(3, anAttribute.getValueType());
			statSetAttrib.setString(4, aPerson.getName());
			res = statSetAttrib.executeUpdate();
			statSetAttrib.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Set the attribute belonging to a certain item.
	 * @param anAttribute attribute to be set/added.
	 * @param anItem the item that is to have this attribute.
	 */
	public static void setAttribute(Attribute anAttribute, Item anItem)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement statSetAttrib =
				Database.prepareStatement(sqlSetAttributeItem);
			statSetAttrib.setString(1, anAttribute.getName());
			statSetAttrib.setString(2, anAttribute.getValue());
			statSetAttrib.setString(3, anAttribute.getValueType());
			statSetAttrib.setInt(4, anItem.getId());
			res = statSetAttrib.executeUpdate();
			statSetAttrib.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Remove the attribute belonging to a certain room. When the attribute
	 * does not exist, this does nothing.
	 * @param anAttribute attribute to be removed.
	 * @param aRoom the room that is to not have this attribute anymore.
	 */
	public static void removeAttribute(Attribute anAttribute, Room aRoom)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement statRemoveAttrib =
				Database.prepareStatement(sqlRemoveAttributeRoom);
			statRemoveAttrib.setString(1, anAttribute.getName());
			statRemoveAttrib.setInt(2, aRoom.getId());
			res = statRemoveAttrib.executeUpdate();
			statRemoveAttrib.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}

	/**
	 * Remove the attribute belonging to a certain person. When the attribute
	 * does not exist, this does nothing.
	 * @param anAttribute attribute to be removed.
	 * @param aPerson the person that is to not have this attribute anymore.
	 */
	public static void removeAttribute(Attribute anAttribute, Person aPerson)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement statRemoveAttrib =
				Database.prepareStatement(sqlRemoveAttributeChar);
			statRemoveAttrib.setString(1, anAttribute.getName());
			statRemoveAttrib.setString(2, aPerson.getName());
			res = statRemoveAttrib.executeUpdate();
			statRemoveAttrib.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}


	/**
	 * Remove the attribute belonging to a certain item. When the attribute
	 * does not exist, this does nothing.
	 * @param anAttribute attribute to be removed.
	 * @param anItem the item that is to not have this attribute anymore.
	 */
	public static void removeAttribute(Attribute anAttribute, Item anItem)
	throws MudDatabaseException
	{
		Logger.getLogger("mmud").finer("");
		int res = 0;
		try
		{
			PreparedStatement statRemoveAttrib =
				Database.prepareStatement(sqlRemoveAttributeItem);
			statRemoveAttrib.setString(1, anAttribute.getName());
			statRemoveAttrib.setInt(2, anItem.getId());
			res = statRemoveAttrib.executeUpdate();
			statRemoveAttrib.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Database.writeLog("root", e);
		}
	}


}
