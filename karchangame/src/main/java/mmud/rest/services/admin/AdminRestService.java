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

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminAdmin;
import mmud.services.AdminService;

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
@Transactional
@Path("/administration/administrators")
public class AdminRestService
{

  private static final Logger LOGGER = Logger.getLogger(AdminRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private AdminService adminService;

  @Context
  private SecurityContext sc;

  /**
   * Returns a list of currently valid administrators.
   *
   * @return list of administrators
   */
  @RolesAllowed("player")
  public List<Admin> getAdministrators()
  {
    return adminService.getAdministrators();
  }

  @RolesAllowed("player")
  public Optional<Admin> getAdministrator(String name)
  {
    return adminService.getAdministrator(name);
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
  public String status()
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
