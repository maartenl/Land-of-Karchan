/*
 *  Copyright (C) 2012 maartenl
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
package mmud.database.entities.items;

import java.util.List;
import java.util.Set;

/**
 * Interface on what is allowed to be done with items.
 * @author maartenl
 */
public interface ItemWrangler
{

    /**
     * Retrieves a set of items that this entity has.
     * @return
     */
    public Set<Item> getItems();

    /**
     * Returns items this entity has, based on description
     * provided.
     * @param parsed the parsed description of the item,
     * for example {"light-green", "leather", "pants"}.
     * @return list of found items, empty if not found.
     */
    public List<Item> findItems(List<String> parsed);

    /**
     * Physically destroys an item instance.
     * @param item the item to be destroyed
     * @return returns true, if the item was found and destroyed. False if
     * the item was not found.
     */
    public boolean destroyItem(Item item);
}
