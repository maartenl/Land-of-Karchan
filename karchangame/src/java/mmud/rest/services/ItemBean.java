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

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mmud.database.entities.characters.Person;
import mmud.database.entities.items.Item;
import mmud.exceptions.ItemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specific bean for the dropping and getting of items from containers, from
 * rooms and from persons.
 * @author maartenl
 */
@Stateless
@LocalBean
public class ItemBean
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    /**
     * Returns the entity manager of Hibernate/JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }
    private static final Logger itsLog = LoggerFactory.getLogger(ItemBean.class);

    /**
     * Drops an item on the floor.
     * @param person the person
     * @param item the item to be dropped.
     * @return true if successfull.
     */
    public boolean drop(Item item, Person person)
    {
        Query query = getEntityManager().createNamedQuery("Item.drop");
        query.setParameter("item", item);
        query.setParameter("person", person);
        query.setParameter("room", person.getRoom());
        return query.executeUpdate() == 1;
    }

    /**
     * Gets an item from off the floor/room.
     * @param person the person
     * @param item the item to be retrieved from the room.
     * @return true if successfull.
     */
    public boolean get(Item item, Person person)
    {
        Query query = getEntityManager().createNamedQuery("Item.get");
        query.setParameter("item", item);
        query.setParameter("person", person);
        query.setParameter("room", person.getRoom());
        return query.executeUpdate() == 1;
    }

    /**
     * Gives an item from one person to another person.
     * @param fromperson the person giving the item
     * @param item the item to be shared
     * @param toperson the person receiving the item
     * @return true if successfull.
     */
    public boolean give(Item item, Person fromperson, Person toperson)
    {
        Query query = getEntityManager().createNamedQuery("Item.give");
        query.setParameter("item", item);
        query.setParameter("fromperson", fromperson);
        query.setParameter("toperson", toperson);
        return query.executeUpdate() == 1;
    }

    /**
     * Puts an item into a container.
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
        return query.executeUpdate() == 1;
    }

    /**
     * Retrieves an item from a container.
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
        return query.executeUpdate() == 1;
    }
}
