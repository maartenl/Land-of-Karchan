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
package mmud.scripting.entities;

import mmud.database.enums.Wielding;
import mmud.exceptions.PersonNotFoundException;

/**
 *
 * @author maartenl
 */
public class Person
{

    private static final long serialVersionUID = 438270592527335642L;
    private final String name;
    private final mmud.database.entities.characters.Person person;
    private final String sex;

    public Person(mmud.database.entities.characters.Person person)
    {
        this.name = person.getName();
        this.person = person;
        this.sex = person.getSex().toString();
    }

    public String getSex()
    {
        return sex;
    }

    public String getName()
    {
        return name;
    }

    public Room getRoom()
    {
        return new Room(person.getRoom());
    }

    public String getGuild()
    {
        return person.getGuild() == null ? null : person.getGuild().getTitle();

    }

    public void personal(String string)
    {
        person.writeMessage(string);
    }

    /**
     * @param toperson the person to send the message to. (Anyone else can listen).
     * @see mmud.database.entities.game.Room#sendMessageExcl(mmud.database.entities.characters.Person, mmud.database.entities.characters.Person, java.lang.String)
     * @param message
     */
    public void sendMessage(Person toperson, String message)
    {
        if (toperson == null || toperson.getRoom().getId() != this.getRoom().getId())
        {
            throw new PersonNotFoundException();
        }
        person.getRoom().sendMessage(person, toperson.person, message);
    }

    /**
     * @see mmud.database.entities.game.Room#sendMessage(java.lang.String)
     * @param message
     */
    public void sendMessage(String message)
    {
        person.getRoom().sendMessage(person, message);
    }

    /**
     * Sends a message in the room, but not to myself.
     *
     * @see mmud.database.entities.game.Room#sendMessageExcl(mmud.database.entities.characters.Person, java.lang.String)
     * @param message
     */
    public void sendMessageExcl(String message)
    {
        person.getRoom().sendMessageExcl(person, message);
    }

    /**
     * Sends a message in the room, but not to myself and the target toperson.
     *
     * @param toperson the other guy
     * @param message the message
     */
    public void sendMessageExcl(Person toperson, String message)
    {
        if (toperson == null || toperson.getRoom().getId() != this.getRoom().getId())
        {
            throw new PersonNotFoundException();
        }
        person.getRoom().sendMessageExcl(person, toperson.person, message);
    }
    //item addItem(integer)
    //removeItem(Item)

    //item wornItem(integer)
    //item[] getItem(integer)
    public String getAttribute(String name)
    {
        return person.getAttribute(name).getValue();
    }

    public void setAttribute(String name, String value)
    {
        person.setAttribute(name, value);
    }

    public boolean isAttribute(String name)
    {
        return person.getAttribute(name) != null;
    }

    public boolean removeAttribute(String name)
    {
        return person.removeAttribute(name);
    }

    public Item getWieldingLeft()
    {
        if (person.wields(Wielding.WIELD_LEFT) == null)
        {
            return null;
        }
        return new Item(person.wields(Wielding.WIELD_LEFT));
    }

    public Item getWieldingRight()
    {
        if (person.wields(Wielding.WIELD_RIGHT) == null)
        {
            return null;
        }
        return new Item(person.wields(Wielding.WIELD_RIGHT));
    }

    public Item getWieldingBoth()
    {
        if (person.wields(Wielding.WIELD_BOTH) == null)
        {
            return null;
        }
        return new Item(person.wields(Wielding.WIELD_BOTH));
    }
}
