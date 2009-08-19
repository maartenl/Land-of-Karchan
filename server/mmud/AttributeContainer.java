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

package mmud;

import java.util.Vector;

import mmud.database.MudDatabaseException;

/**
 * Interface to indicate that an object can contain attributes.
 */
public interface AttributeContainer
{

	/**
	 * Set the attribute. Adds the attribute, if the attribute does not exist
	 * yet.
	 * 
	 * @param anAttribute
	 *            the attribute to be set.
	 */
	public void setAttribute(Attribute anAttribute) throws MudException;

	/**
	 * Set the attributes found in the vector. Adds the attribute, if the
	 * attribute does not exist yet.
	 * 
	 * @param anAttributeVector
	 *            a vector containing the attributes to be set. May be null.
	 */
	public void setAttributes(Vector anAttributeVector) throws MudException;

	/**
	 * returns the attribute found with name aName or null if it does not exist.
	 * 
	 * @param aName
	 *            the string with the name to search for
	 * @return Attribute with the specific name
	 * @throws MudDatabaseException
	 */
	public Attribute getAttribute(String aName) throws MudDatabaseException;

	/**
	 * Remove an attribute with a specific name from the list of attributes. If
	 * the attribute did not exist in the first place the method does not have
	 * any effect.
	 * 
	 * @param aName
	 *            the string with the name of the attribute to be removed.
	 */
	public void removeAttribute(String aName) throws MudException;

	/**
	 * returns true if the attribute with name aName exists.
	 * 
	 * @param aName
	 *            the name of the attribute to search for
	 * @return boolean, true if found otherwise false
	 * @throws MudDatabaseException
	 */
	public boolean isAttribute(String aName) throws MudDatabaseException;

}
