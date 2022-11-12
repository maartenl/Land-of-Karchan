/*
 * Copyright (C) 2019 maartenl
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
package org.karchan;

import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;

/**
 *
 * @author maartenl
 */
public class MailChecker
{
  private static final Logger LOGGER = Logger.getLogger(MailChecker.class.getName());

  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;

  public boolean checkMail(String name)
  {
    LOGGER.entering(this.getClass().getName(), "checkMail");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    Query query = entityManager.createNativeQuery("SELECT count(m.id) FROM mm_mailtable m WHERE m.newmail = 1 and m.deleted = 0 and m.toname = ?");
    // named parameters are not defined in the JPA spec for Native queries.
    query.setParameter(1, name);
    return !query.getSingleResult().equals(0L);
  }
}
