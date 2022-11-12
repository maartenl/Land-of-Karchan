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
package mmud.services;

import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mmud.database.entities.characters.Person;
import mmud.rest.services.PrivateRestService;

/**
 * TODO : add all mail here instead of in {@link PrivateRestService}. use Path:{name}/mail
 *
 * @author maartenl
 */


public class MailService
{

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

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

  private static final Logger LOGGER = Logger.getLogger(MailService.class.getName());

    public boolean hasNewMail(Person person)
    {
      var query = getEntityManager().createNamedQuery("Mail.hasnewmail");
        query.setParameter("name", person);

        Long count = (Long) query.getSingleResult();
        if (count > 0)
        {
            return true;
        }
        return false;
    }
}
