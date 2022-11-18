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
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminUserCommand;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/commands")
public class UserCommandsRestService
{

  private static final Logger LOGGER = Logger.getLogger(UserCommandsRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @POST
  @Consumes(
    {
            "application/json"
          })

  public void create(String json, @Context SecurityContext sc)
  {
    AdminUserCommand adminUserCommand = AdminUserCommand.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    Room room = null;
    if (adminUserCommand.room != null)
    {
      room = getEntityManager().find(Room.class, adminUserCommand.room);
      if (room == null)
      {
        throw new MudWebException(name, "Room " + adminUserCommand.room + " not found.", Response.Status.NOT_FOUND);
      }
    }
    Method method = null;
    if (adminUserCommand.methodName != null)
    {
      method = getEntityManager().find(Method.class, adminUserCommand.methodName);
      if (method == null)
      {
        throw new MudWebException(name, "Method " + adminUserCommand.methodName + " not found.", Response.Status.NOT_FOUND);
      }
    } else
    {
      throw new MudWebException(name, "Method not provided.", Response.Status.NOT_FOUND);
    }

    UserCommand userCommand = new UserCommand();
    userCommand.setCallable(adminUserCommand.callable);
    userCommand.setCommand(adminUserCommand.command);
    userCommand.setRoom(room);
    userCommand.setMethodName(method);
    userCommand.setCreation(LocalDateTime.now());
    userCommand.setOwner(admin);

    ValidationUtils.checkValidation(name, userCommand);
    getEntityManager().persist(userCommand);
  }

  @PUT
  @Path("{id}")
  @Consumes(
          {
            "application/json"
          })

  public void edit(@PathParam("id") Integer id, String json, @Context SecurityContext sc)
  {
    AdminUserCommand adminUserCommand = AdminUserCommand.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminUserCommand.id))
    {
      throw new MudWebException(name, "UserCommand ids do not match.", Response.Status.BAD_REQUEST);
    }
    Room room = null;
    if (adminUserCommand.room != null)
    {
      room = getEntityManager().find(Room.class, adminUserCommand.room);
      if (room == null)
      {
        throw new MudWebException(name, "Room " + adminUserCommand.room + " not found.", Response.Status.NOT_FOUND);
      }
    }
    Method method = null;
    if (adminUserCommand.methodName != null)
    {
      method = getEntityManager().find(Method.class, adminUserCommand.methodName);
      if (method == null)
      {
        throw new MudWebException(name, "Method " + adminUserCommand.methodName + " not found.", Response.Status.NOT_FOUND);
      }
    } else
    {
      throw new MudWebException(name, "Method not provided.", Response.Status.NOT_FOUND);
    }
    UserCommand userCommand = getEntityManager().find(UserCommand.class, adminUserCommand.id);

    if (userCommand == null)
    {
      throw new MudWebException(name, "UserCommand " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, userCommand);
    userCommand.setCommand(adminUserCommand.command);
    userCommand.setMethodName(method);
    userCommand.setRoom(room);
    userCommand.setCallable(adminUserCommand.callable);
    userCommand.setOwner(OwnerHelper.getNewOwner(adminUserCommand.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, userCommand);
  }

  @DELETE
  @Path("{id}")

  public void remove(@PathParam("id") Integer id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final UserCommand userCommand = getEntityManager().find(UserCommand.class, id);
    if (userCommand == null)
    {
      throw new MudWebException(name, "UserCommand " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, userCommand);
    getEntityManager().remove(userCommand);
  }

  @GET
  @Path("{id}")
  @Produces(
          {
            "application/json"
          })

  public String find(@PathParam("id") Integer id, @Context SecurityContext sc)
  {

    final String name = sc.getUserPrincipal().getName();
    UserCommand userCommand = getEntityManager().find(UserCommand.class, id);
    if (userCommand == null)
    {
      throw new MudWebException(name, "UserCommand " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminUserCommand(userCommand).toJson();
  }

  @GET

  @Produces(
    {
      "application/json"
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminUserCommand.GET_QUERY)).build();
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
    List<String> userCommands = null;
    userCommands = getEntityManager().createNativeQuery(AdminUserCommand.GET_QUERY)
            .setMaxResults(pageSize)
            .setFirstResult(offset)
            .getResultList();
    return "[" + userCommands.stream().collect(Collectors.joining(",")) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")

  public String count(@Context UriInfo info)
  {
    return String.valueOf(getEntityManager().createNamedQuery("UserCommand.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
