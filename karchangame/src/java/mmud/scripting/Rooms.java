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

import mmud.database.entities.game.Room;


/**
 *
 * @author maartenl
 */
public class Rooms implements RoomsInterface
{
    private RoomsInterface proxy;
    public Rooms(RoomsInterface proxy)
    {
        this.proxy = proxy;
    }

    @Override
    public Room find(Integer id)
    {
        return proxy.find(id);
    }
}