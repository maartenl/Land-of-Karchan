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

/**
 * Intended to retrieve worldwide settings.
 * @author maartenl
 */
public class World
{

    private final WorldInterface proxy;

    public World(WorldInterface proxy)
    {
        this.proxy = proxy;

    }

    /**
     * Returns the attribute with this specific name. Returns null if attribute is not found, or value stored is null.
     *
     * @param name the name of the attribute
     * @return the value of the attribute or null if not found
     */
    public String getAttribute(String name)
    {
        return proxy.getAttribute(name);
    }
}
