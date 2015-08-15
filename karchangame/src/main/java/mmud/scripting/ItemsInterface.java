/*
 * Copyright (C) 2015 m.vanleunen
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
 *
 * @author m.vanleunen
 */
public interface ItemsInterface {

    /**
     * Creates a new item in the inventory of a person.
     *
     * Returns null if no item was successfully created.
     *
     * @param person the person
     * @param itemdefnr the id of the item definition
     * @return the new item or null if not created
     */
    public Item addItem(long itemdefnr, Person person);

    /**
     * Creates a new item in the room.
     *
     * Returns null if no item was successfully created.
     *
     * @param room the room
     * @param itemdefnr the id of the item definition
     * @return the new item or null if not created
     */
    public Item addItem(long itemdefnr, Room room);

    /**
     * Creates a new item in the bag.
     *
     * Returns null if no item was successfully created.
     *
     * @param item container/bag
     * @param itemdefnr the id of the item definition
     * @return the new item or null if not created
     */
    public Item addItem(long itemdefnr, Item item);
}
