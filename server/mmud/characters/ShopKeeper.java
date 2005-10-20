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

package mmud.characters;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.logging.Logger;

import mmud.items.Item;
import mmud.database.Database;

/**
 * Class containing all the methods that have to be implemented for a
 * character to become a shopkeeper. A shopkeeper basically has two things:
 * <ul><li>A list of items that he/she can sell
 * <li>an inventory of items (this is the usual, what every character has)
 * </ul>
 * The shopkeeper can sell items in his/her inventory, and buy items
 * from other characters that are in his list of acceptable items
 * and of which he does not yet have a maximum supply.
 */
public interface ShopKeeper
{

	/**
	 * Checks to see if the item is available for sale. The way this is
	 * done, is by checking if the item is in the inventory of the
	 * shopkeeper.
	 * @return boolean, true if the item is available for sale.
	 * @param anItem the item to be checked.
	 */
	public boolean isSellable(Item anItem);

	/**
	 * Checks to see if the item can be sold to the shopkeeper. The way this
	 * is done, is by checking if the item is in the store list of the
	 * shopkeeper, <I>and</I> if the stock of the shopkeeper is not already
	 * maxed out.
	 * @param anItem the item to be checked.
	 * @return boolean, true if the item can be sold to this shopkeeper.
	 */
	public boolean isBuyable(Item anItem);

	/**
	 * Retrieve a bulleted list of everything the shopkeeper is allowed to
	 * sell or buy.
	 * @return String containing a html-bulleted list of allowed items.
	 */
	public String getStoreList();

}
