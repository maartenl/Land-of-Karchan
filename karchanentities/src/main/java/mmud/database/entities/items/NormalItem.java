/*
 * Copyright (C) 2014 maartenl
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

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 *
 * @author maartenl
 */
@Entity
@DiscriminatorValue("0")
public class NormalItem extends Item {

    /**
     * Default constructor. Used by the ORM itself, not by us.
     */
    public NormalItem() {
    }

    /**
     * Constructor of a normal item with an item definition.
     *
     * @param itemDefinition the definition/template of the item.
     */
    public NormalItem(ItemDefinition itemDefinition) {
        super(itemDefinition);
    }

    @Override
    public ItemCategory getCategory() {
        return ItemCategory.NORMALITEM;
    }

}
