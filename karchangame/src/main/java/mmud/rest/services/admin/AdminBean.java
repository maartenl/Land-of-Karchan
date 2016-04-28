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

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import mmud.Constants;
import mmud.database.entities.game.Admin;
import mmud.database.enums.Filter;

/**
 * Currently the only thing it is used for is for a player to reset the owner
 * of his character.
 *
 * @author maartenl
 */
@DeclareRoles(
        {
            "deputy", "god", "player"
        })
@RolesAllowed("deputy")
@Stateless
public class AdminBean
{

    private static final Logger itsLog = Logger.getLogger(AdminBean.class.getName());

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    /**
     * Returns a list of currently valid administrators.
     *
     * @return list of administrators
     */
    @RolesAllowed("player")
    public List<Admin> getAdministrators()
    {
        Constants.setFilters(getEntityManager(), Filter.OFF);
        Query query = getEntityManager().createNamedQuery("Admin.findValid");
        List<Admin> list = query.getResultList();
        return list;
    }

    /**
     * Returns the found valid administrator with that name.
     *
     * @param name the name of the administrator to look for, case insensitive.
     * @return An optional which is either empty (not found) or contains
     * the administrator.
     */
    @RolesAllowed("player")
    public Optional<Admin> getAdministrator(String name)
    {
        return getAdministrators().stream().filter(x -> x.getName().equalsIgnoreCase(name)).findFirst();
    }

    protected EntityManager getEntityManager()
    {
        return em;
    }

}
