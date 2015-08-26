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

import mmud.database.entities.characters.User;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import mmud.exceptions.MoneyException;
import mmud.exceptions.PersonNotFoundException;
import mmud.scripting.Items;

/**
 * Scripting entity called Person.
 *
 * @see Person
 * @author maartenl
 */
public class Person
{

    private static final long serialVersionUID = 438270592527335642L;
    private final String name;
    private final mmud.database.entities.characters.Person person;
    private final String sex;

    /**
     * Constructor
     *
     * @param person the original object to redirect calls to.
     */
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

    public void setRoom(Room room)
    {
        room.useRoom(person);
    }

    /**
     * Returns the guildname (not the full title!) of the guild.
     * For example the guild Benefactors of Karchan will yield guildname
     * "deputy".
     *
     * @return The guildname.
     */
    public String getGuild()
    {
        if (!(person instanceof User))
        {
            return null;
        }
        User user = (User) person;
        return user.getGuild() == null ? null : user.getGuild().getName();
    }

    /**
     * @see mmud.database.entities.characters.Person#getCopper()
     * @return
     */
    public Integer getMoney()
    {
        return person.getMoney();
    }

    /**
     * Transfers money from one person (this one) to another.
     *
     * @param newamount the amount of copper (base currency to move)
     * @param target the target that is to receive said money
     * @return boolean, false if the money amount is illegal, or the
     * person simply does not have that much money.
     */
    public boolean transferMoney(Integer newamount, Person target)
    {
        boolean success = true;
        try
        {
            person.transferMoney(newamount, target.person);
        } catch (MoneyException e)
        {
            success = false;
        }
        return success;
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

    /**
     *
     * <p>
     * Returns the item that is being worn at that position. Returns null if nothing
     * is being worn at that position.</p>
     * <p>
     * For example: </p>
     * <p>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * wears("ON_HEAD") can return item "crown". The ON_HEAD is straight from
     * {@link Wearing#ON_HEAD}.</p>
     *
     * @param position a String
     * @return an Item or null if nothing is worn.
     * @see Wearing
     */
    public Item wears(String position)
    {
        Wearing wearing = Wearing.valueOf(position);
        final mmud.database.entities.items.Item wears = person.wears(wearing);
        return wears == null ? null : new Item(wears);
    }

    /**
     * <p>
     * Returns the item that is being wielded at that position. Returns null if
     * nothing is being wielded at that position.</p>
     * <p>
     * For example: </p>
     * <p>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * wields("WIELD_LEFT") can return item "strong, iron pick". The WIELD_LEFT is straight from
     * {@link Wielding#WIELD_LEFT}.</p>
     *
     * @param position a String
     * @return an Item or null if nothing is being wielded.
     * @see Wielding
     */
    public Item wields(String position)
    {
        Wielding wielding = Wielding.valueOf(position);
        final mmud.database.entities.items.Item wield = person.wields(wielding);
        return wield == null ? null : new Item(wield);
    }

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

    /**
     * Adds a <i>new</i> item to the inventory of this person. With <i>new</i> it is
     * understood that the item was created with a call to
     * {@link Items#createItem(int) }, and is not yet allocated
     * to a room, person or container.
     *
     * @param item the new item to add.
     * @return the exact same item, or null if unable to comply.
     */
    public Item addItem(Item item)
    {
        mmud.database.entities.items.Item result = person.addItem(item.getItem());
        if (result == null)
        {
            return null;
        }
        return new Item(result);
    }

}
