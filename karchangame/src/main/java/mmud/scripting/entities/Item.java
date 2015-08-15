/*
 *  Copyright (C) 2013 maartenl
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
package mmud.scripting.entities;

import java.util.logging.Logger;
import mmud.database.entities.items.ItemDefinition;

/**
 *
 * @author maartenl
 */
public class Item
{

    private static final Logger itsLog = Logger.getLogger(Item.class.getName());

    private final mmud.database.entities.items.Item item;

    public Item(mmud.database.entities.items.Item item)
    {
        this.item = item;
    }

    public Integer getId()
    {
        return item.getId();
    }

    public Integer getItemdef()
    {
        return item.getItemDefinition().getId();
    }

    public String getName()
    {
        return item.getItemDefinition().getName();
    }

    public String getDescription()
    {
        return item.getDescription();
    }

    public String getAttribute(String name)
    {
        return item.getAttribute(name).getValue();
    }

    public void setAttribute(String name, String value)
    {
        item.setAttribute(name, value);
    }

    public boolean isAttribute(String name)
    {
        return item.getAttribute(name) != null;
    }

    public boolean removeAttribute(String name)
    {
        return item.removeAttribute(name);
    }

    ItemDefinition retrieveItemdef()
    {
        return item.getItemDefinition();
    }

    mmud.database.entities.items.Item getItem()
    {
        return item;
    }

    /**
     * Adds an item to this bag (if it is a bag).
     *
     * @param item the item to add
     * @return the item if it was successful otherwise null.
     */
    public Item addItem(Item item)
    {
        mmud.database.entities.items.Item result = getItem().addItem(item.getItem());
        if (result == null)
        {
            return null;
        }
        return new Item(result);
    }
}
