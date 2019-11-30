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
package mmud.rest.services;

import java.util.Objects;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Shopkeeper;
import mmud.database.entities.characters.User;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;
import mmud.database.entities.items.NormalItem;
import mmud.exceptions.ItemException;

/**
 * Specific bean for the dropping, getting, selling and buying of items from
 * containers, from rooms and from persons.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
public class ItemBean
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    @EJB
    private LogBean logBean;

    /**
     * Returns the entity manager of JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }
    private static final Logger LOGGER = Logger.getLogger(ItemBean.class.getName());

    /**
     * Drops an item on the floor.
     *
     * @param person the person
     * @param item the item to be dropped.
     * @return true if successful.
     */
    public boolean drop(Item item, Person person)
    {
        Query query = getEntityManager().createNamedQuery("Item.drop");
        query.setParameter("item", item);
        query.setParameter("person", person);
        query.setParameter("room", person.getRoom());
        final boolean success = query.executeUpdate() == 1;
        if (success)
        {
            logBean.writeLog(person, " dropped " + item.getDescription() + " in room " + person.getRoom().getId() + ".");
        }
        return success;
    }

    /**
     * Gets an item from off the floor/room.
     *
     * @param person the person
     * @param item the item to be retrieved from the room.
     * @return true if successful.
     */
    public boolean get(Item item, Person person)
    {
        Query query = getEntityManager().createNamedQuery("Item.get");
        query.setParameter("item", item);
        query.setParameter("person", person);
        query.setParameter("room", person.getRoom());
        final boolean success = query.executeUpdate() == 1;
        if (success)
        {
            logBean.writeLog(person, " got " + item.getDescription() + " from room " + person.getRoom().getId() + ".");
        }
        return success;
    }

    /**
     * Gives an item from one person to another person.
     *
     * @param fromperson the person giving the item
     * @param item the item to be shared
     * @param toperson the person receiving the item
     * @return true if successful.
     */
    public boolean give(Item item, Person fromperson, Person toperson)
    {
        Query query = getEntityManager().createNamedQuery("Item.give");
        query.setParameter("item", item);
        query.setParameter("fromperson", fromperson);
        query.setParameter("toperson", toperson);
        final boolean success = query.executeUpdate() == 1;
        if (success)
        {
            logBean.writeLog(fromperson, " gave " + item.getDescription() + " to " + toperson.getName() + ".");
        }
        return success;
    }

    /**
     * Puts an item into a container.
     *
     * @param item the item to be put
     * @param container the container that the item should receive
     * @param person the person performing the action
     * @return true if successful.
     */
    public boolean put(Item item, Item container, Person person)
    {
        if (!container.isContainer())
        {
            throw new ItemException("Item is not a container and cannot be used for storage.");
        }
        if (!person.getItems().contains(item))
        {
            throw new ItemException("Person does not have that item.");
        }
        if (item.isContainer())
        {
            throw new ItemException("Item is a container, and cannot be put.");
        }
        Query query = getEntityManager().createNamedQuery("Item.put");
        query.setParameter("item", item);
        query.setParameter("container", container);
        query.setParameter("person", person);
        final boolean success = query.executeUpdate() == 1;
        if (success)
        {
            logBean.writeLog(person, " put " + item.getDescription() + " into " + container.getDescription() + ".");
        }
        return success;
    }

    /**
     * Retrieves an item from a container.
     *
     * @param item the item to be retrieved
     * @param container the container that contains the item
     * @param person the person performing the action
     * @return true if successful.
     */
    public boolean retrieve(Item item, Item container, Person person)
    {
        if (!container.isContainer())
        {
            throw new ItemException("Item is not a container and cannot be used for storage.");
        }
        if (!container.getItems().contains(item))
        {
            throw new ItemException("Container does not contain that item.");
        }
        if (item.isContainer())
        {
            throw new ItemException("Item is a container, and cannot be retrieved.");
        }
        if (!person.getItems().contains(container) && !person.getRoom().getItems().contains(container))
        {
            throw new ItemException("Container not found.");
        }
        Query query = getEntityManager().createNamedQuery("Item.retrieve");
        query.setParameter("item", item);
        query.setParameter("container", container);
        query.setParameter("person", person);
        final boolean success = query.executeUpdate() == 1;
        if (success)
        {
            logBean.writeLog(person, " retrieved " + item.getDescription() + " from " + container.getDescription() + ".");
        }
        return success;
    }

    /**
     * Sells an item from a user to a shopkeeper.
     *
     * @param item the item to be sold
     * @param aUser the user selling the item
     * @param shopkeeper the shopkeeper buying the item
     * @return the amount of money transferred, or Null if it was unsuccessful.
     */
    public Integer sell(Item item, User aUser, Shopkeeper shopkeeper)
    {
        if (shopkeeper == null)
        {
            return null;
        }
        if (aUser == null)
        {
            return null;
        }
        if (item == null)
        {
            return null;
        }
        if (!item.isSellable())
        {
            return null;
        }
        if (shopkeeper.getCopper() < item.getCopper())
        {
            return null;
        }
        if (!aUser.getItems().contains(item))
        {
            return null;
        }
        if (!Objects.equals(shopkeeper.getRoom().getId(), aUser.getRoom().getId()))
        {
            return null;
        }
        aUser.give(item, shopkeeper);
        final int amount = item.getCopper() * 80 / 100;
        shopkeeper.transferMoney(amount, aUser);
        logBean.writeLog(aUser, " sold " + item.getDescription() + " to " + shopkeeper.getName() + " for " + amount + ".");
        return amount;
    }

    /**
     * Buys an item from a shopkeeper.
     *
     * @param item the item to be bought
     * @param aUser the user buying the item
     * @param shopkeeper the shopkeeper selling the item
     * @return the amount of money transferred, or Null if it was unsuccessful.
     */
    public Integer buy(Item item, User aUser, Shopkeeper shopkeeper)
    {
        if (shopkeeper == null)
        {
            return null;
        }
        if (aUser == null)
        {
            return null;
        }
        if (item == null)
        {
            return null;
        }
        if (!item.isBuyable())
        {
            return null;
        }
        if (aUser.getCopper() < item.getCopper())
        {
            return null;
        }
        if (!shopkeeper.getItems().contains(item))
        {
            return null;
        }
        if (!Objects.equals(shopkeeper.getRoom().getId(), aUser.getRoom().getId()))
        {
            return null;
        }
        shopkeeper.give(item, aUser);
        final int amount = item.getCopper();
        aUser.transferMoney(amount, shopkeeper);
        logBean.writeLog(aUser, " bought " + item.getDescription() + " from " + shopkeeper.getName() + " for " + amount + ".");
        return amount;
    }

    public Item createItem(int itemdefnr)
    {
        ItemDefinition itemDefinition = getEntityManager().find(ItemDefinition.class, itemdefnr);
        if (itemDefinition == null)
        {
            logBean.writeLog("Unable to create item because item definition was empty.");
            return null;
        }
        Item item = new NormalItem(itemDefinition);
        return item;
    }
}
