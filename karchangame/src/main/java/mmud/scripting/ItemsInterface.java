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

import mmud.database.entities.items.Item;

/**
 *
 * @author m.vanleunen
 */
public interface ItemsInterface
{

    /**
     * Creates a new item.
     *
     * Returns null if no item was successfully created.
     *
     * @param itemdefnr the id of the item definition
     * @return the new item or null if not created
     */
    public Item createItem(int itemdefnr);

}
