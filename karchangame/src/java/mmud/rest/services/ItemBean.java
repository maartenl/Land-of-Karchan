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

}
