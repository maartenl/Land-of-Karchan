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
package mmud.scripting;

import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Can create and remove items. For retrieval of items, let me refer you
 * back to the containers, {@link Person}, {@link Item} and {@link Room}.
 *
 * @author maartenl
 */
public class Items
{
    private static final Logger LOGGER = Logger.getLogger(Items.class.getName());

    private final ItemsInterface proxy;

    public Items(ItemsInterface proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Creates an item based on an item definition number.
     *
     * @param itemdefnr the item definition number
     * @return the new item or null if unable to comply.
     */
    public mmud.scripting.entities.Item createItem(int itemdefnr)
    {
        LOGGER.log(Level.FINE, "createItem {0}", itemdefnr);
        final Item created = proxy.createItem(itemdefnr);
        if (created == null)
        {
            LOGGER.fine("createItem not created");
            return null;
        }
        return new mmud.scripting.entities.Item(created);
    }

}
