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
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminRoom;

/**
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/rooms")
public class RoomsBean
{

  private static final Logger LOGGER = Logger.getLogger(RoomsBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @POST
  @Consumes(
          {
            "application/json"
          })
  public void create(String json, @Context SecurityContext sc)
  {
    AdminRoom adminRoom = AdminRoom.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    Room room = new Room();
    room.setContents(adminRoom.contents);
    if (adminRoom.area != null)
    {
      Area area = getEntityManager().find(Area.class, adminRoom.area);
      room.setArea(area);
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
  @Produces(
          {
            "application/json"
          })
  public String findAll()
  {
    LOGGER.info("findAll");
    final List<Room> rooms = getEntityManager().createNamedQuery("Room.findAll").getResultList();
    List<AdminRoom> adminRooms = rooms.stream().map(room -> new AdminRoom(room)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminRooms);
  }

  @GET
  @Path("{from}/{to}")
  @Produces(
          {
            "application/json"
          })
  public String findRange(@PathParam("from") Integer from,
          @PathParam("to") Integer to
  )
  {
    final List<Room> rooms = getEntityManager().createNamedQuery("Room.findAll")
            .setMaxResults(to - from + 1)
            .setFirstResult(from)
            .getResultList();
    List<AdminRoom> adminRooms = rooms.stream().map(room -> new AdminRoom(room)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminRooms);
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String countREST()
  {
    return String.valueOf(getEntityManager().createNamedQuery("Room.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
