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

/**
 *
 * @author maartenl
 */
public class Item
{

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

    public boolean isAttribute(String name, String value)
    {
        return item.verifyAttribute(name, value);
    }

    public boolean removeAttribute(String name)
    {
        return item.removeAttribute(name);
    }

}
