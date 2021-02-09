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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminAdmin;

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
@Path("/administration/administrators")
public class AdminBean
{

  private static final Logger LOGGER = Logger.getLogger(AdminBean.class.getName());

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

  /**
   * Returns single information of the currently logged in deputy. The URL:
   * /karchangame/resources/administration/administrators. Can produce application/json.
   *
   * @return a deputy.
   */
  @GET
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String status(@Context SecurityContext sc)
  {
    LOGGER.finer("entering status");
    final String name = sc.getUserPrincipal().getName();
    Admin admin;
    User person;
    try
    {
      admin = getEntityManager().createNamedQuery("Admin.findByName", Admin.class).setParameter("name", name).getSingleResult();
      person = getEntityManager().createNamedQuery("User.findByName", User.class).setParameter("name", name).getSingleResult();
    } catch (Exception e)
    {
      throw new MudWebException(e, Response.Status.BAD_REQUEST);
    }
    if (admin == null) {
      throw new MudWebException(name, "Admin " + name + " not found.", "Admin " + name + " not found.", Response.Status.NOT_FOUND);
    }
    if (person == null) {
      throw new MudWebException(name, "Admin " + name + " not found.", "Admin " + name + " not found.", Response.Status.NOT_FOUND);
    }
    LOGGER.finer("exiting status");
    return new AdminAdmin(person, admin).toJson();
  }

  protected EntityManager getEntityManager()
  {
    return em;
  }

}
