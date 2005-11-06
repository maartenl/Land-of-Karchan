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
package mmud.items;     

import java.util.Vector;

import mmud.MudException;

/**
 * An interface for a container. Useful for items in the mud that can 
 * contain other items.
 */
public interface Container
{

	/**
	 * Retrieve items from this container.
	 * @param adject1 the first adjective
	 * @param adject2 the second adjective
	 * @param adject3 the third adjective
	 * @param name the name of the item
	 * @return Vector containing item objects found.
	 */
	public Vector getItems(String adject1, String adject2, String adject3, String name)
	throws MudException;

	/**
	 * Returns the amount of weight that can be contained inside
	 * the container before it is full.
	 * @return integer containing the maximum capacity.
	 */
	public int getCapacity();

	/**
	 * Returns wether or not the container has a lid. Wether
	 * or not the container can be opened.
	 * @return boolean true if the container has a lid.
	 */
	public boolean isOpenable();

	/**
	 * Returns wether or not the container has a lock.
	 * Meaning wether or not the container can be unlocked/locked.
	 * @return boolean true if the container can be locked/unlocked.
	 */
	public boolean hasLock();

	/**
	 * Returns wether or not the container is locked. If it can be locked,
	 * it needs a key.
	 * @return boolean true if the container is locked.
	 * @see getKeyId
	 */
	public boolean isLocked();

	/**
	 * Returns wether or not the container is open.
	 * @return boolean true if the container is open.
	 */
	public boolean isOpen();

	/**
	 * Returns the item definition of the item
	 * that can be used to unlock the container.
	 * @return ItemDef of the key.
	 */
	public ItemDef getKeyId();

	/**
	 * Retrieves the types that are possible to be stored in this container.
	 * @see Types
	 */
	public Types getContainTypes();

	/**
	 * Sets the status of the container.
	 */
	public void setLidsNLocks(boolean isOpenable, boolean newIsOpen, ItemDef newHasLock, boolean newIsLocked)
	throws MudException;

}
