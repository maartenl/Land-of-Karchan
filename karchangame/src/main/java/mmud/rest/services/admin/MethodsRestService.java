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

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
import mmud.database.entities.game.Method;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminMethod;
import mmud.rest.webentities.admin.AdminUserCommand;
import mmud.services.LogService;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/methods")
public class MethodsRestService
{

  private static final Logger LOGGER = Logger.getLogger(MethodsRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogService logService;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public void create(String json, @Context SecurityContext sc)
  {
    AdminMethod adminMethod = AdminMethod.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    Method method = new Method();
    method.setName(adminMethod.name);
    method.setSrc(adminMethod.src);
    method.setCreation(LocalDateTime.now());
    method.setOwner(admin);
    ValidationUtils.checkValidation(name, method);
    getEntityManager().persist(method);
    logService.writeDeputyLog(admin, "New method '" + method.getName() + "' created.");
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/json"
    })
  public void edit(@PathParam("id") String id, String json, @Context SecurityContext sc)
  {
    AdminMethod adminMethod = AdminMethod.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminMethod.name))
    {
      throw new MudWebException(name, "Method names do not match.", Response.Status.BAD_REQUEST);
    }
    Method method = getEntityManager().find(Method.class, adminMethod.name);

    if (method == null)
    {
      throw new MudWebException(name, "Method " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, method);
    method.setSrc(adminMethod.src);
    method.setOwner(OwnerHelper.getNewOwner(adminMethod.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, method);
  }

  @DELETE
  @Path("{id}")

  public void remove(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Method method = getEntityManager().find(Method.class, id);
    if (method == null)
    {
      throw new MudWebException(name, "Method " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, method);
    getEntityManager().remove(method);
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
    Method method = getEntityManager().find(Method.class, id);
    if (method == null)
    {
      throw new MudWebException(name, "Method " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminMethod(method).toJson();
  }

  @GET
  @Path("{id}/commands")
  @Produces(
    {
      "application/json"
    })

  public String getCommands(@PathParam("id") String id,
                            @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final List<UserCommand> userCommands = getEntityManager().createNamedQuery("UserCommand.findByMethodName").setParameter("methodname", id).getResultList();
    List<AdminUserCommand> adminCommands = userCommands.stream().map(AdminUserCommand::new).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminCommands);
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminMethod.GET_QUERY)).build();
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      "application/json"
    })

  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize
  )
  {
    List<String> methods = getEntityManager().createNativeQuery(AdminMethod.GET_QUERY)
      .setMaxResults(pageSize)
      .setFirstResult(offset)
      .getResultList();
    return "[" + String.join(",", methods) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")

  public String count(@Context UriInfo info)
  {
    return String.valueOf(getEntityManager().createNamedQuery("Method.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
