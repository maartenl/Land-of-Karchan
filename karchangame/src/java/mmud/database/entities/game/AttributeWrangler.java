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
package mmud.database.entities.game;

/**
 *
 * @author maartenl
 */
public interface AttributeWrangler
{

    /**
     * Removes an attribute by name.
     * @param name the name of the attribute, for example "guildwish".
     * @return false if not found, true if removed.
     */
    public boolean removeAttribute(String name);

    /**
     * Retrieves an attribute, returns null if not found.
     * @param name the name of the attribute
     * @return an attribute
     */
    public Attribute getAttribute(String name);

    /**
     * Set the value of the attribute, creates the attribute if the attribute
     * does not exist.
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(String name, String value);

    /**
     * Verify if the attribute exists, and has the proper value.
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @return true if it exists, false otherwise.
     */
    public boolean verifyAttribute(String name, String value);
}
