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
package mmud.scripting.entities;

import mmud.exceptions.PersonNotFoundException;

/**
 *
 * @author maartenl
 */
public class Room
{

    private mmud.database.entities.game.Room room;

    public Room(mmud.database.entities.game.Room room)
    {
        this.room = room;
    }

    public Integer getId()
    {
        return room.getId();
    }

    public Room getWest()
    {
        if (room.getWest() == null)
        {
            return null;
        }
        return new Room(room.getWest());
    }

    public Room getEast()
    {
        if (room.getEast() == null)
        {
            return null;
        }
        return new Room(room.getEast());
    }

    public Room getNorth()
    {
        if (room.getNorth() == null)
        {
            return null;
        }
        return new Room(room.getNorth());
    }

    public Room getSouth()
    {
        if (room.getSouth() == null)
        {
            return null;
        }
        return new Room(room.getSouth());
    }

    public Room getUp()
    {
        if (room.getUp() == null)
        {
            return null;
        }
        return new Room(room.getUp());
    }

    public Room getDown()
    {
        if (room.getDown() == null)
        {
            return null;
        }
        return new Room(room.getDown());
    }

    public String getDescription()
    {
        return room.getContents();
    }

    public String getTitle()
    {
        return room.getTitle();
    }

    public String getImage()
    {
        return room.getImage();
    }

    public String getAttribute(String name)
    {
        return room.getAttribute(name).getValue();
    }

    public void setAttribute(String name, String value)
    {
        room.setAttribute(name, value);
    }

    public boolean verifyAttribute(String name, String value)
    {
        return room.verifyAttribute(name, value);
    }

    public boolean removeAttribute(String name)
    {
        return room.removeAttribute(name);
    }

    public void sendMessage(String targetname, String message)
    {
        mmud.database.entities.characters.Person target = room.retrievePerson(targetname);
        if (target == null)
        {
            throw new PersonNotFoundException("Person " + targetname + " not found.");
        }
        room.sendMessage(target, message);
    }

    public void sendMessage(String message)
    {
        room.sendMessage(message);
    }

    public void sendMessageExcl(String targetname, String message)
    {
        mmud.database.entities.characters.Person target = room.retrievePerson(targetname);
        if (target == null)
        {
            throw new PersonNotFoundException();
        }
        room.sendMessageExcl(target, message);
    }
//item[] getItems(integer)
//Item addItem(integer)
}
