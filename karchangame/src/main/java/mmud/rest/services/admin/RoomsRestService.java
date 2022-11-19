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
import jakarta.persistence.Query;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.database.entities.game.Room;
import mmud.database.entities.game.UserCommand;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminRoom;
import mmud.rest.webentities.admin.AdminUserCommand;
import mmud.services.RoomsService;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.config.ResultSetConcurrency;
import org.eclipse.persistence.config.ResultSetType;
import org.eclipse.persistence.queries.ScrollableCursor;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/rooms")
public class RoomsRestService
{

  private static final Logger LOGGER = Logger.getLogger(RoomsRestService.class.getName());
  public static final String DESCRIPTION = "description";

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private RoomsService roomsService;

  @Context
  private SecurityContext sc;

  @POST
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })

  public Long create(String json)
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
      MediaType.APPLICATION_JSON
    })

  public void edit(@PathParam("id") Long id, String json)
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
    room.setOwner(OwnerHelper.getNewOwner(adminRoom.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, room);
  }

  @DELETE
  @Path("{id}")

  public void remove(@PathParam("id") Long id)
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
      MediaType.APPLICATION_JSON
    })
  public String find(@PathParam("id") Long id)
  {
    final String name = sc.getUserPrincipal().getName();
    Room room = roomsService.find(id);
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
      MediaType.APPLICATION_JSON
    })

  public String getCommands(@PathParam("id") Long id)
  {
    final String name = sc.getUserPrincipal().getName();
    final List<UserCommand> userCommands = getEntityManager().createNamedQuery("UserCommand.findByRoom").setParameter("room", id).getResultList();
    List<AdminUserCommand> adminCommands = userCommands.stream().map(command -> new AdminUserCommand(command)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminCommands);
  }

  @GET
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })

  public Response findAll(@Context UriInfo info)
  {
    LOGGER.info("findAll");
    String description = info.getQueryParameters().getFirst(DESCRIPTION);
    if ("null".equals(description))
    {
      description = null;
    }
    if (description == null)
    {
      return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminRoom.GET_QUERY)).build();
    }
    Query query = getEntityManager().createNativeQuery(AdminRoom.GET_SEARCH_QUERY);
    query.setHint(QueryHints.CURSOR, HintValues.TRUE);
    query.setHint(QueryHints.CURSOR_INITIAL_SIZE, 100);
    query.setHint(QueryHints.CURSOR_PAGE_SIZE, 100);
    query.setHint(QueryHints.RESULT_SET_CONCURRENCY, ResultSetConcurrency.ReadOnly);
    query.setHint(QueryHints.RESULT_SET_TYPE, ResultSetType.ForwardOnly);
    query.setParameter(1, "%" + description + "%");
    @SuppressWarnings("unchecked")
    ScrollableCursor cursor = (ScrollableCursor) query.getSingleResult();
    return Response.ok(StreamerHelper.getStream(cursor)).build();
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })

  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize
  )
  {
    String description = info.getQueryParameters().getFirst(DESCRIPTION);
    if ("null".equals(description))
    {
      description = null;
    }
    final List<String> rooms;
    if (description == null)
    {
      rooms = getEntityManager().createNativeQuery(AdminRoom.GET_QUERY)
        .setMaxResults(pageSize)
        .setFirstResult(offset)
        .getResultList();
    } else
    {
      rooms = getEntityManager().createNativeQuery(AdminRoom.GET_SEARCH_QUERY)
        .setParameter(1, "%" + description + "%")
        .setMaxResults(pageSize)
        .setFirstResult(offset)
        .getResultList();
    }
    return "[" + String.join(",", rooms) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")

  public String count(@Context UriInfo info)
  {
    String description = info.getQueryParameters().getFirst(DESCRIPTION);
    if ("null".equals(description))
    {
      description = null;
    }
    if (description == null)
    {
      return String.valueOf(getEntityManager().createNamedQuery("Room.countAll").getSingleResult());
    }
    return String.valueOf(getEntityManager().createNamedQuery("Room.countAllByDescription").setParameter(DESCRIPTION, "%" + description + "%").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
