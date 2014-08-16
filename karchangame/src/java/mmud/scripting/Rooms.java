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

import mmud.scripting.entities.Room;

/**
 *
 * @author maartenl
 */
public class Rooms
{

    private final RoomsInterface proxy;

    public Rooms(RoomsInterface proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Returns the room with this specific id. Returns null if no room is found.
     *
     * @param id the id of the room
     * @return the room or null, if not found.
     */
    public Room find(Integer id)
    {
        final mmud.database.entities.game.Room found = proxy.find(id);
        if (found == null)
        {
            return null;
        }
        return new Room(found);
    }
}
