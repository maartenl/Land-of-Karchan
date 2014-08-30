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

import mmud.scripting.entities.Person;

/**
 *
 * @author maartenl
 */
public class Persons
{

    private final PersonsInterface proxy;

    public Persons(PersonsInterface proxy)
    {
        this.proxy = proxy;

    }

    /**
     * Returns the person with this specific name. Person should be playing. Returns null if no player is found.
     * Might be a bot.
     *
     * @param name the name of the person
     * @return the person or null if not found
     */
    public Person find(String name)
    {
        final mmud.database.entities.characters.Person found = proxy.find(name);
        if (found == null)
        {
            return null;
        }
        return new Person(found);
    }
}
