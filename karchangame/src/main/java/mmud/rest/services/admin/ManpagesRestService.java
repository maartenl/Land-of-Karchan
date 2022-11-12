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
import java.util.logging.Logger;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Help;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminManpage;
import mmud.services.LogBean;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")

@Path("/administration/manpages")
public class ManpagesRestService // extends AbstractFacade<Area>
{

  private static final Logger LOGGER = Logger.getLogger(ManpagesRestService.class.getName());

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
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminManpage.GET_QUERY)).build();
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
