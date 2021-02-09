/*
 * Copyright (C) 2014 maartenl
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
package mmud.rest.services.admin;

import mmud.database.entities.Ownage;
import mmud.database.entities.game.Admin;
import mmud.exceptions.MudWebException;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * @author maartenl
 */
class OwnerHelper
{

  private EntityManager em;

  public static Admin getNewOwner(String newowner, Admin admin, EntityManager entityManager)
  {
    if (newowner == null || newowner.trim().equals(""))
    {
      return null;
    }
    Admin newadmin = entityManager.find(Admin.class, newowner);
    if (newadmin == null)
    {
      throw new MudWebException(admin.getName(), "Admin (" + newowner + ") was not found.", "Admin (" + newowner + ") was not found.", Response.Status.NOT_FOUND);
    }
    return admin;
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

  OwnerHelper(EntityManager em)
  {
    this.em = em;
  }

  /**
   * Sees if the attribute is owned by the deputy with that name. Otherwise, throws a {@link MudWebException}.
   *
   * @param name      the name of the admin/deputy.
   * @param attribute the attribute to check
   * @return the admin/deputy
   * @throws MudWebException if not the owner of the attribute.
   */
  Admin authorize(final String name, Ownage attribute) throws MudWebException
  {
    Admin admin = getEntityManager().find(Admin.class, name);
    if (attribute.getOwner() != null && !attribute.getOwner().getName().equals(name))
    {
      throw new MudWebException(name, name + " is not the owner of the object. " + attribute.getOwner().getName() + " is.", Response.Status.UNAUTHORIZED);
    }
    return admin;
  }

  /**
   * Remove ownership of a certain attribute, setting the property deputy to null.
   *
   * @param id          the id of the object
   * @param sc          the security context
   * @param entityClass the entity class (can be a lot of things, really)
   * @param <T>         template which extends {@link Ownage}.
   */
  <T extends Ownage> void disown(Object id, SecurityContext sc, Class<T> entityClass)
  {
    final String name = sc.getUserPrincipal().getName();

    T attribute = getEntityManager().find(entityClass, id);

    if (attribute == null)
    {
      throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = authorize(name, attribute);
    attribute.setOwner(null);
  }

}
