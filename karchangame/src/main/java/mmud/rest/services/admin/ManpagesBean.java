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


import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Help;
import mmud.database.entities.game.UnbanTable;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminArea;
import mmud.rest.webentities.admin.AdminManpage;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/manpages")
public class ManpagesBean // extends AbstractFacade<Area>
{

  private static final Logger LOGGER = Logger.getLogger(ManpagesBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public String create(String json, @Context SecurityContext sc)
  {
    AdminManpage adminManpage = AdminManpage.fromJson(json);

    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    final Help existingItem = getEntityManager().find(Help.class, adminManpage.command);
    if (existingItem != null)
    {
      throw new MudWebException(name, "Manpage for " + adminManpage.command + " already exists.", Response.Status.PRECONDITION_FAILED);
    }

    Help help = new Help();
    help.setCommand(adminManpage.command);
    help.setContents(adminManpage.contents);
    help.setSynopsis(adminManpage.synopsis);
    help.setSeealso(adminManpage.seealso);
    help.setExample1(adminManpage.example1);
    help.setExample1a(adminManpage.example1a);
    help.setExample1b(adminManpage.example1b);
    help.setExample2(adminManpage.example2);
    help.setExample2a(adminManpage.example2a);
    help.setExample2b(adminManpage.example2b);
    help.setExample2c(adminManpage.example2c);
    ValidationUtils.checkValidation(name, help);
    logBean.writeDeputyLog(admin, "New manpage '" + help.getCommand() + "' created.");
    getEntityManager().persist(help);
    return help.getCommand();
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/xml", "application/json"
    })
  public void edit(@PathParam("id") String id, String json, @Context SecurityContext sc)
  {
    AdminManpage adminManpage = AdminManpage.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminManpage.command))
    {
      throw new MudWebException(name, "Manpage commands do not match.", Response.Status.BAD_REQUEST);
    }
    Help help = getEntityManager().find(Help.class, adminManpage.command);
    if (help == null)
    {
      throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = getEntityManager().find(Admin.class, name);
    help.setContents(adminManpage.contents);
    help.setSynopsis(adminManpage.synopsis);
    help.setSeealso(adminManpage.seealso);
    help.setExample1(adminManpage.example1);
    help.setExample1a(adminManpage.example1a);
    help.setExample1b(adminManpage.example1b);
    help.setExample2(adminManpage.example2);
    help.setExample2a(adminManpage.example2a);
    help.setExample2b(adminManpage.example2b);
    help.setExample2c(adminManpage.example2c);
    ValidationUtils.checkValidation(name, help);
    logBean.writeDeputyLog(admin, "Manpage '" + help.getCommand() + "' updated.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Help help = getEntityManager().find(Help.class, id);
    if (help == null)
    {
      throw new MudWebException(name, "Manpage " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = getEntityManager().find(Admin.class, name);
    getEntityManager().remove(help);
    logBean.writeDeputyLog(admin, "Manpage '" + help.getCommand() + "' deleted.");
  }

  @GET
  @Path("{id}")
  @Produces(
    {
      "application/json"
    })
  public String find(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Help help = getEntityManager().find(Help.class, id);
    if (help == null)
    {
      throw new MudWebException(name, "Manpage " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminManpage(help).toJson();
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public String findAll(@Context UriInfo info)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminManpage.GET_QUERY)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      "application/json"
    })
  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminManpage.GET_QUERY)
      .setMaxResults(pageSize)
      .setFirstResult(offset)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String count(@Context UriInfo info)
  {
    return String.valueOf(getEntityManager().createNamedQuery("Help.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
