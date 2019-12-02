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
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Method;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminUserCommand;

/**
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@LocalBean
@Path("/administration/commands")
public class UserCommandsBean //implements AdminRestService<String>
{

  private static final Logger LOGGER = Logger.getLogger(UserCommandsBean.class.getName());

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
    userCommand.setOwner(admin);
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
  public String findAll(@Context UriInfo info)
  {
    String owner = info.getQueryParameters().getFirst("owner");
    List<UserCommand> userCommands = null;
    if (owner == null)
    {
      userCommands = getEntityManager().createNamedQuery("UserCommand.findAll")
              .getResultList();
    } else
    {
      userCommands = getEntityManager().createNamedQuery("UserCommand.findAllByOwner")
              .setParameter("owner", owner)
              .getResultList();
    }
    List<AdminUserCommand> adminUserCommands = userCommands.stream().map(userCommand -> new AdminUserCommand(userCommand)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminUserCommands);
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
    String owner = info.getQueryParameters().getFirst("owner");
    List<UserCommand> userCommands = null;
    if (owner == null)
    {
      userCommands = getEntityManager().createNamedQuery("UserCommand.findAll")
              .setMaxResults(pageSize)
              .setFirstResult(offset)
              .getResultList();
    } else
    {
      userCommands = getEntityManager().createNamedQuery("UserCommand.findAllByOwner")
              .setParameter("owner", owner)
              .setMaxResults(pageSize)
              .setFirstResult(offset)
              .getResultList();
    }
    List<AdminUserCommand> adminUserCommands = userCommands.stream().map(userCommand -> new AdminUserCommand(userCommand)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminUserCommands);
  }

  @GET
  @Path("count")
  @Produces("text/plain")

  public String count(@Context UriInfo info)
  {
    String owner = info.getQueryParameters().getFirst("owner");
    if (owner == null)
    {
      return String.valueOf(getEntityManager().createNamedQuery("UserCommand.countAll").getSingleResult());
    }
    return String.valueOf(getEntityManager().createNamedQuery("UserCommand.countAllByOwner").setParameter("owner", owner).getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
