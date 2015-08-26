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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import mmud.database.entities.characters.Person;
import mmud.exceptions.PersonNotFoundException;

/**
 *
 * @author maartenl
 */
public class Room
{

    private static final Logger itsLog = Logger.getLogger(Room.class.getName());

    private final mmud.database.entities.game.Room room;

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

    public void setWest(Room newroom)
    {
        if (newroom == null)
        {
            room.setWest(null);
            return;
        }
        room.setWest(newroom.room);
    }

    public void setEast(Room newroom)
    {
        if (newroom == null)
        {
            room.setEast(null);
            return;
        }
        room.setEast(newroom.room);
    }

    public void setNorth(Room newroom)
    {
        if (newroom == null)
        {
            room.setNorth(null);
            return;
        }
        room.setNorth(newroom.room);
    }

    public void setSouth(Room newroom)
    {
        if (newroom == null)
        {
            room.setSouth(null);
            return;
        }
        room.setSouth(newroom.room);
    }

    public void setUp(Room newroom)
    {
        if (newroom == null)
        {
            room.setUp(null);
            return;
        }
        room.setUp(newroom.room);
    }

    public void setDown(Room newroom)
    {
        if (newroom == null)
        {
            room.setDown(null);
            return;
        }
        room.setDown(newroom.room);
    }

    public String getDescription()
    {
        return room.getContents();
    }

    public void setDescription(String contents)
    {
        room.setContents(contents);
    }

    public String getPicture()
    {
        return room.getPicture();
    }

    public void setPicture(String contents)
    {
        room.setPicture(contents);
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

    public boolean isAttribute(String name)
    {
        return room.getAttribute(name) != null;
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

    public Item[] getItems(Integer itemdefid)
    {
        itsLog.entering(this.getClass().getName(), "getItems");
        List<Item> result = new ArrayList<>();
        final Set<mmud.database.entities.items.Item> items = room.getItems();
        for (mmud.database.entities.items.Item item : items)
        {
            if (item.getItemDefinition().getId().equals(itemdefid))
            {
                result.add(new Item(item));
            }
        }
        itsLog.exiting(this.getClass().getName(), "getItems");
        return result.toArray(new Item[result.size()]);
    }
//Item addItem(integer)

    /**
     * For setting the room on a person (without exposing stuff).
     *
     * @see mmud.scripting.entities.Person#setRoom(mmud.scripting.entities.Room)
     * @param person
     */
    void useRoom(Person person)
    {
        person.setRoom(room);
    }

    /**
     * Adds a <i>new</i> item to this room. With <i>new</i> it is
     * understood that the item was created with a call to
     * {@link Items#createItem(int) }, and is not yet allocated
     * to a room, person or container.
     *
     * @param item the new item to add.
     * @return the exact same item, or null if unable to comply.
     */
    public Item addItem(Item item)
    {
        mmud.database.entities.items.Item result = room.addItem(item.getItem());
        if (result == null)
        {
            return null;
        }
        return new Item(result);
    }
}
