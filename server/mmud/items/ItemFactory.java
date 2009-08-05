/*-------------------------------------------------------------------------
svninfo: $Id: Types.java 987 2005-10-20 19:26:50Z maartenl $
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
import java.util.logging.Logger;

import mmud.Attribute;
import mmud.MudException;
import mmud.characters.Persons;

/**
 * Creates items. These Items can be special items like containers.
 */
public class ItemFactory
{

	/**
	 * Creates an item based on input parameters.
     * @param anItemDef definition of the item
     * @param anId integer identification of the item
	 * @return Created item
	 * @throws MudException if it was unable to create the appropriate item.
	 */
	public static Item createItem(ItemDef anItemDef, int anId)
	throws MudException
	{
		return createItem(anItemDef, anId, null);
	}

	/**
	 * Creates an item based on input parameters.
     * @param anItemDef definition of the item
     * @param anId integer identification of the item
     * @param anAttribVector vector containing the attributes belonging
     * to the item.
	 * @return Created item
	 * @throws MudException if it was unable to create the appropriate item.
	 */
	public static Item createItem(ItemDef anItemDef, int anId, Vector anAttribVector)
	throws MudException
	{
		return createItem(anItemDef, anId, null, anAttribVector);
	}

	/**
	 * Creates an item based on input parameters.
     * @param anItemDef definition of the item
     * @param anId integer identification of the item
	 * @param aPosBody the place on the body that this item is worn on 
	 * or wielded.
     * @param anAttribVector vector containing the attributes belonging
     * to the item.
	 * @return Created item
	 * @throws MudException if it was unable to create the appropriate item.
	 */
	public static Item createItem(ItemDef anItemDef, int anId, 
		PersonPositionEnum aPosBody, Vector anAttribVector)
	throws MudException
	{
		Logger.getLogger("mmud").finer("anItemDef=" + anItemDef +
			", anId=" + anId + ",aPosBody=" + aPosBody + ",anAttribVector=" +
			anAttribVector);
		if (anItemDef == null)
		{
			throw new MudException("could not create item, no item definition.");
		}
		Item item = null;
		String shopkeeperList = null;
		if (anAttribVector != null)
		{
			for (int i=0;i<anAttribVector.size();i++)
			{
				Attribute attrib = (Attribute) anAttribVector.elementAt(i);
				if (attrib.getName().equals(Attribute.SHOPKEEPERLIST))
				{
					shopkeeperList = attrib.getValue();
				}
			}
		}
		if (shopkeeperList != null)
		{
			item = new ShopkeeperList(
				anItemDef, anId, aPosBody, Persons.retrievePerson(shopkeeperList));
		}
		else
		{
			if (anItemDef instanceof ContainerDef)
			{
				item = new StdItemContainer(
					(ContainerDef) anItemDef, anId, aPosBody);
			}
			else
			{
				item = new Item(anItemDef, anId, aPosBody);
			}
		}
		item.setAttributes(anAttribVector);
		Logger.getLogger("mmud").finer("returns new item " + item);
		return item;
	}
}
