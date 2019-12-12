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
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminRoom;
import mmud.rest.webentities.admin.AdminUserCommand;

/**
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/rooms")
@LocalBean
public class RoomsBean //implements AdminRestService<Long>
{

  private static final Logger LOGGER = Logger.getLogger(RoomsBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @POST
  @Consumes(
          {
            "application/json"
          })

  public Long create(String json, @Context SecurityContext sc)
  {
    AdminRoom adminRoom = AdminRoom.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    Room room = new Room();
    if (adminRoom.id == null)
    {
      room.setId(getEntityManager().createNamedQuery("Room.findMaxId", Long.class).getSingleResult() + 1);
    } else
    {
      if (getEntityManager().find(Room.class, adminRoom.id) != null)
      {
        throw new MudWebException(name, "Room " + adminRoom.id + " already exists.", Response.Status.FOUND);
      }
      room.setId(adminRoom.id);
    }
    room.setContents(adminRoom.contents);
    if (adminRoom.area != null)
    {
      Area area = getEntityManager().find(Area.class, adminRoom.area);
      if (area == null)
      {
        throw new MudWebException(name, "Area " + adminRoom.area + " not found.", Response.Status.NOT_FOUND);
      }
      room.setArea(area);
    } else
    {
      throw new MudWebException(name, "Area cannot be null.", Response.Status.BAD_REQUEST);
    }
    if (adminRoom.down != null)
    {
      Room down = getEntityManager().find(Room.class, adminRoom.down);
      room.setDown(down);
    }
    if (adminRoom.up != null)
    {
      Room up = getEntityManager().find(Room.class, adminRoom.up);
      room.setUp(up);
    }
    if (adminRoom.east != null)
    {
      Room east = getEntityManager().find(Room.class, adminRoom.east);
      room.setEast(east);
    }
    if (adminRoom.west != null)
    {
      Room west = getEntityManager().find(Room.class, adminRoom.west);
      room.setWest(west);
    }
    if (adminRoom.south != null)
    {
      Room south = getEntityManager().find(Room.class, adminRoom.south);
      room.setSouth(south);
    }
    if (adminRoom.north != null)
    {
      Room north = getEntityManager().find(Room.class, adminRoom.north);
      room.setNorth(north);
    }
    room.setPicture(adminRoom.picture);
    room.setTitle(adminRoom.title);
    room.setCreation(LocalDateTime.now());
    room.setOwner(admin);
    ValidationUtils.checkValidation(name, room);
    getEntityManager().persist(room);
    return room.getId();
  }

  @PUT
  @Path("{id}")
  @Consumes(
          {
            "application/json"
          })

  public void edit(@PathParam("id") Long id, String json, @Context SecurityContext sc)
  {
    AdminRoom adminRoom = AdminRoom.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminRoom.id))
    {
      throw new MudWebException(name, "Room ids do not match.", Response.Status.BAD_REQUEST);
    }
    Room room = getEntityManager().find(Room.class, adminRoom.id);

    if (room == null)
    {
      throw new MudWebException(name, "Room " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, room);
    room.setContents(adminRoom.contents);
    if (adminRoom.area != null)
    {
      Area area = getEntityManager().find(Area.class, adminRoom.area);
      room.setArea(area);
    } else
    {
      room.setArea(null);
    }
    if (adminRoom.down != null)
    {
      Room down = getEntityManager().find(Room.class, adminRoom.down);
      room.setDown(down);
    } else
    {
      room.setDown(null);
    }
    if (adminRoom.up != null)
    {
      Room up = getEntityManager().find(Room.class, adminRoom.up);
      room.setUp(up);
    } else
    {
      room.setUp(null);
    }
    if (adminRoom.east != null)
    {
      Room east = getEntityManager().find(Room.class, adminRoom.east);
      room.setEast(east);
    } else
    {
      room.setEast(null);
    }
    if (adminRoom.west != null)
    {
      Room west = getEntityManager().find(Room.class, adminRoom.west);
      room.setWest(west);
    } else
    {
      room.setWest(null);
    }
    if (adminRoom.south != null)
    {
      Room south = getEntityManager().find(Room.class, adminRoom.south);
      room.setSouth(south);
    } else
    {
      room.setSouth(null);
    }
    if (adminRoom.north != null)
    {
      Room north = getEntityManager().find(Room.class, adminRoom.north);
      room.setNorth(north);
    } else
    {
      room.setNorth(null);
    }
    room.setPicture(adminRoom.picture);
    room.setTitle(adminRoom.title);
    if (adminRoom.owner == null)
    {
      room.setOwner(null);
    } else
    {
      room.setOwner(admin);
    }
    ValidationUtils.checkValidation(name, room);
  }

  @DELETE
  @Path("{id}")

  public void remove(@PathParam("id") Long id,
          @Context SecurityContext sc
  )
  {
    final String name = sc.getUserPrincipal().getName();
    Room room = getEntityManager().find(Room.class, id);
    if (room == null)
    {
      throw new MudWebException(name, "Room " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, room);
    getEntityManager().remove(room);
  }

  @GET
  @Path("{id}")
  @Produces(
          {
            "application/json"
          })

  public String find(@PathParam("id") Long id,
          @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Room room = getEntityManager().find(Room.class, id);
    if (room == null)
    {
      throw new MudWebException(name, "Room " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminRoom(room).toJson();
  }

  @GET
  @Path("{id}/commands")
  @Produces(
          {
            "application/json"
          })

  public String getCommands(@PathParam("id") Long id,
          @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final List<UserCommand> userCommands = getEntityManager().createNamedQuery("UserCommand.findByRoom").setParameter("room", id).getResultList();
    List<AdminUserCommand> adminCommands = userCommands.stream().map(command -> new AdminUserCommand(command)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminCommands);
  }

  @GET
  @Produces(
          {
            "application/json"
          })

  public String findAll(@Context UriInfo info)
  {
    LOGGER.info("findAll");
    String owner = info.getQueryParameters().getFirst("owner");
    final List<String> rooms = getEntityManager().createNativeQuery(AdminRoom.GET_QUERY)
            .setParameter(1, owner)
            .getResultList();
    return "[" + rooms.stream().collect(Collectors.joining(",")) + "]";
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
    final List<String> rooms = getEntityManager().createNativeQuery(AdminRoom.GET_QUERY)
            .setParameter(1, owner)
            .setMaxResults(pageSize)
            .setFirstResult(offset)
            .getResultList();
    return "[" + rooms.stream().collect(Collectors.joining(",")) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")

  public String count(@Context UriInfo info)
  {
    String owner = info.getQueryParameters().getFirst("owner");
    if (owner == null)
    {
      return String.valueOf(getEntityManager().createNamedQuery("Room.countAll").getSingleResult());
    }
    return String.valueOf(getEntityManager().createNamedQuery("Room.countAllByOwner").setParameter("owner", owner).getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
