/*
 * Copyright (C) 2015 maartenl
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

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import mmud.database.entities.game.Admin;
import mmud.exceptions.MudException;

/**
 *
 * @author maartenl
 */
@Stateless
public class IdentificationBean
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    @Resource
    private SessionContext context;

    /**
     *
     * @return the current user, for example "Karn".
     */
    public String getCurrentUser()
    {
        String currentUser = context.getCallerPrincipal().getName();
        return currentUser;
    }

    /**
     * TODO: check validUntil.
     *
     * @return the admin identified by the current user, or null if no
     * proper admin can be found.
     */
    public Admin getCurrentAdmin()
    {
        String currentUser = context.getCallerPrincipal().getName();
        Admin admin = em.find(Admin.class, currentUser);
        if (admin == null)
        {
            throw new MudException("admin " + currentUser + " not found.");
        }
        return admin;
    }
}
