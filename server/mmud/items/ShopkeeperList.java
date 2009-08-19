/*-------------------------------------------------------------------------
svninfo: $Id: ShopkeeperList.java 1027 2005-11-06 11:01:52Z maartenl $
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

import java.util.logging.Logger;

import mmud.Attribute;
import mmud.AttributeContainer;
import mmud.MudException;
import mmud.characters.Person;
import mmud.database.MudDatabaseException;
import simkin.Executable;

/**
 * An item in the mud. Basically consists of an ItemDefinition and a number of
 * Attributes specific to this item.
 */
public class ShopkeeperList extends Item implements Executable,
		AttributeContainer, Viewer
{

	/**
	 * Contains the person/shopkeeper whos inventory must be visible.
	 */
	private final Person thePerson;

	/**
	 * Create this item object with a default ShopkeeperList Definition and id.
	 * This method is usually only used by the database.
	 * 
	 * @param anItemDef
	 *            definition of the item
	 * @param anId
	 *            integer identification of the item
	 * @param aPosBody
	 *            the place on the body that this item is worn on or wielded.
	 * @param aPerson
	 *            the person whos inventory should be seen by looking at this
	 *            item.
	 */
	ShopkeeperList(ItemDef anItemDef, int anId, PersonPositionEnum aPosBody,
			Person aPerson)
	{
		super(anItemDef, anId, aPosBody);
		thePerson = aPerson;
	}

	/**
	 * Create this item object with a default ShopkeeperList Definition and id.
	 * This method is usually only used by the database.
	 * 
	 * @param anItemDef
	 *            definition of the item
	 * @param anId
	 *            integer identification of the item
	 * @param aPerson
	 *            the person whos inventory should be seen by looking at this
	 *            item.
	 */
	ShopkeeperList(ItemDef anItemDef, int anId, Person aPerson)
	{
		super(anItemDef, anId);
		thePerson = aPerson;
	}

	/**
	 * Create this item object with a default ShopkeeperList Definition and id.
	 * This method is usually only used by the database.
	 * 
	 * @param anItemDef
	 *            integer definition identification of the item
	 * @param anId
	 *            integer identification of the item
	 * @param aPerson
	 *            the person whos inventory should be seen by looking at this
	 *            item.
	 */
	ShopkeeperList(int anItemDef, int anId, PersonPositionEnum aPosBody,
			Person aPerson) throws MudDatabaseException
	{
		super(ItemDefs.getItemDef(anItemDef), anId, aPosBody);
		thePerson = aPerson;
	}

	/**
	 * Create this item object with a default ShopkeeperList Definition and id.
	 * This method is usually only used by the database.
	 * 
	 * @param anItemDef
	 *            integer definition identification of the item
	 * @param anId
	 *            integer identification of the item
	 * @param aPerson
	 *            the person whos inventory should be seen by looking at this
	 *            item.
	 */
	ShopkeeperList(int anItemDef, int anId, Person aPerson)
			throws MudDatabaseException
	{
		super(ItemDefs.getItemDef(anItemDef), anId);
		thePerson = aPerson;
	}

	@Override
	public Attribute getAttribute(String aName) throws MudDatabaseException
	{
		Attribute attrib = super.getAttribute(aName);
		if ((aName.equals("readable")) && (attrib != null))
		{
			try
			{
				String inventory = thePerson.inventory();
				attrib = new Attribute(attrib.getName(), attrib.getValue()
						.replaceAll("%VIEW", inventory), attrib.getValueType());
			} catch (MudException e)
			{
				Logger.getLogger("mmud").throwing("mmud.items.ShopkeeperList",
						"getAttribute()", e);
				return attrib;
			}
		}
		return attrib;
	}

	/**
	 * get the description of the item (the long one). If the attribute
	 * <I>description</I> exists, than this one is used instead. However, seeing
	 * as this is a ShopkeeperListItem, if the description contains the tag
	 * "<I>%VIEW</I>", it is replaced with a n unnumbered list containing all
	 * items for sale with their prices.
	 * 
	 * @return String containing the description.
	 */
	@Override
	public String getLongDescription() throws MudException
	{
		String desc = super.getLongDescription();
		String inventory = thePerson.inventory();
		return desc.replaceAll("%VIEW", inventory);
	}

}
