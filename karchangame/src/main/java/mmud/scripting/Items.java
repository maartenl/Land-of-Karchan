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

/**
 * Can create and remove items. For retrieval of items, let me refer you
 * back to the containers, {@link Person}, {@link Item} and {@link Room}.
 * @author maartenl
 */
public class Items
{

    private final ItemsInterface proxy;

    public Items(ItemsInterface proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Creates an item based on an item definition number. 
     * The item is added to a persons inventory.
     *
     * @param itemdefnr the itemdefinition number
     * @param person the person who will receive the new item in his/her
     * inventory.
     * @return the new item or null if unable to comply.
     */
    public mmud.scripting.entities.Item addItem(Long itemdefnr, Person person)
    {
        final Item created = proxy.addItem(itemdefnr, person);
        if (created == null)
        {
            return null;
        }
        return new mmud.scripting.entities.Item(created);
    }
    /**
     * Creates an item based on an item definition number. The item is added to
     * a room.
     *
     * @param itemdefnr the itemdefinition number
     * @param room the room which will contain the new item
     * @return the new item or null if unable to comply.
     */
    public mmud.scripting.entities.Item addItem(Long itemdefnr, Room room) {
        final Item created = proxy.addItem(itemdefnr, room);
        if (created == null) {
            return null;
        }
        return new mmud.scripting.entities.Item(created);
    }
    /**
     * Creates an item based on an item definition number. The item is added to
     * a container, which is basically another item.
     *
     * @param itemdefnr the itemdefinition number
     * @param item the bag or container about to contain the new item.
     * @return the new item or null if unable to comply.
     */
    public mmud.scripting.entities.Item addItem(Long itemdefnr, Item item) {
        final Item created = proxy.addItem(itemdefnr, item);
        if (created == null) {
            return null;
        }
        return new mmud.scripting.entities.Item(created);
    }
}
