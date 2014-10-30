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

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import mmud.database.entities.Ownage;
import mmud.database.entities.game.Admin;
import mmud.exceptions.MudWebException;

/**
 *
 * @author maartenl
 */
class OwnerHelper
{

    private EntityManager em;

    private EntityManager getEntityManager()
    {
        return em;
    }

    OwnerHelper(EntityManager em)
    {
        this.em = em;
    }

    Admin authorize(final String name, Ownage attribute) throws MudWebException
    {
        Admin admin = getEntityManager().find(Admin.class, name);
        if (attribute.getOwner() != null && !attribute.getOwner().getName().equals(name))
        {
            throw new MudWebException(name, name + " is not the owner of the object. " + attribute.getOwner().getName() + " is.", Response.Status.UNAUTHORIZED);
        }
        return admin;
    }

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
