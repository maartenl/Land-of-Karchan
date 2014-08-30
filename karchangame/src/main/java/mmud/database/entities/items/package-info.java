/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * <p>Provides the JPA Database Entities for item definitions and item instances.</p>
 *
 * The items has been completely overhauled from a database point of view. I'll
 * describe the old situation first.
 * 
 * The following tables exist:
 * <ul><li>mm_charitemtable</li><li>
 * mm_itemattributes</li><li>
 * mm_itemitemtable</li><li>
 * mm_items</li><li>
 * mm_itemtable</li><li>
 * mm_roomitemtable</li><li>
 * mm_shopkeeperitems</li></ul>
 * <h3>Old situation</h3>
 * <p>The <i>definition</i> of an item was stored in 
 * <strong>mm_items</strong>. For example the description and characteristics 
 * of a bucket.</p>
 * <p>The instance of an item, say an actual bucket, one of many, is stored in 
 * <strong>mm_itemtable</strong>. Where this bucket resides is then stored 
 * <ul><li>either <strong>mm_charitemtable</strong> if it belongs to a person, </li><li>
 * <strong>mm_itemitemtable</strong> if it is contained in another item 
 * or </li><li><strong>mm_roomitemtable</strong> if it is contained in a room. </li></ul>
 * Of course some items have attributes and some do not, hence we have an 
 * <strong>mm_itemattributes</strong> table.</p>
 * 
 * <p>This was a hassle, as it means having to copy records from one table to the
 * other when a change in situation took place.</p>
 * <h3>New situation</h3>
 * <p>The <i>definition</i> of an item is stored in 
 * <strong>mm_items</strong>. For example the description and characteristics 
 * of a bucket.</p>
 * <p>The instance of an item, say an actual bucket, one of many, is stored in 
 * <strong>mm_itemtable</strong>. It is also registered, at the same place,
 * where this bucket resides. This is done by means of several extra columns.
 * <ul><li>belongsto (mm_usertable.name, varchar)</li><li>
 * room (mm_rooms.id, int)</li><li>
 * containerid (mm_itemtable.id, int)</li></ul>
 * <p><strong>NOTE:</strong> Care must be taken to make sure that only <strong>one
 * and exactly one</strong> of these fields is actually filled (at all times).</p>
 * <p>A person can, ofcourse, only wear an item if he/she actually owns the item. 
 * The person table will be exanded with the idfields for different parts of the body
 * so they can contain items.</p>
 * <p>I've not moved the 'search' facility of items in rooms. If you want that,
 * I'm going to implement a hidden, invisible item bag in the room, that you can 
 * 'search'. </p>
 * <p>Of course some items have attributes and some do not, hence we have an 
 * <strong>mm_itemattributes</strong> table.</p>
 * 
 * <h3>Deprecated</h3>
 * <p>The following tables are deprecated and will need to be removed.
 * </ul><li>mm_charitemtable</li>
 * <li>mm_itemitemtable</li>
 * <li>mm_roomitemtable</li>
 * </ul></p>
 */
package mmud.database.entities.items;


