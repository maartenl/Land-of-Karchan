/*
 *  Copyright (C) 2012 maartenl
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
package mmud.rest.services;

import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;
import mmud.JsonUtils;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.Transform;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.PrivateRoom;
import mmud.services.LogService;
import mmud.services.PlayerAuthenticationService;

import java.util.logging.Logger;

/**
 * Contains all rest calls used by the graphical client, with
 * authentication and authorization. You can find them at
 * /karchangame/resources/graphics.
 *
 * @author maartenl
 */
@DeclareRoles("player")
@RolesAllowed("player")
@Path("/graphics")
@Transactional
public class GraphicsRestService
{

  @Context
  private SecurityContext context;

  @Inject
  private LogService logService;

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private PlayerAuthenticationService playerAuthenticationService;

  public GraphicsRestService()
  {
    // empty constructor, I don't know why, but it's necessary.
  }

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  protected EntityManager getEntityManager()
  {
    return em;
  }

  private static final Logger LOGGER = Logger.getLogger(GraphicsRestService.class.getName());

  /**
   * Returns the transform of a person.
   *
   * @param name the name of the user
   * @return a JSON-ified {@link mmud.database.entities.characters.Transform} object.
   */
  @GET
  @Path("persons/{name}/transform")
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public String getTransform(@PathParam("name") String name)
  {
    LOGGER.finer("entering getTransform");
    Person person;
    try
    {
      person = getPerson(name);
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Status.BAD_REQUEST);
    }
    logService.writeLog(person, "Transform retrieved.");
    return JsonUtils.toJson(person.getTransform());
  }

  /**
   * Adds or updates your current character transform (position, rotation, scale).
   *
   * @param name the name of the user
   * @param json the object containing the new stuff to update.
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @PUT
  @Path("persons/{name}/transform")
  @Consumes(
      {
          MediaType.APPLICATION_JSON
      })
  public Response setTransform(@PathParam("name") String name, String json)
  {
    LOGGER.finer("entering setTransform");
    Transform transform = JsonUtils.fromJson(json, Transform.class);

    Person person = getPerson(name);
    person.setTransform(transform);
    logService.writeLog(person, "Transform changed.");

    return createResponse();
  }


  /**
   * Retrieved the room number where a person is.
   *
   * @param name the name of the user
   * @return room.
   */
  @GET
  @Path("persons/{name}/room")
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public String getRoom(@PathParam("name") String name)
  {
    LOGGER.finer("entering getRoom");
    Person person;
    try
    {
      person = getPerson(name);
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Status.BAD_REQUEST);
    }
    return new PrivateRoom(person.getRoom()).toJson();
  }

  /**
   * Retrieved the room by room number.
   *
   * @param id the id of the room
   * @return room.
   */
  @GET
  @Path("rooms/{id}")
  @Produces(
      {
          MediaType.APPLICATION_JSON
      })
  public String getRoom(@PathParam("id") Long id)
  {
    LOGGER.finer("entering getRoom(id)");
    Room room;
    try
    {
      room = getEntityManager().find(Room.class, id);
      if (room == null)
      {
        throw new MudWebException(null, "Room " + id + " not found.", Status.NOT_FOUND);
      }
    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(null, e, Status.BAD_REQUEST);
    }
    return new PrivateRoom(room).toJson();
  }

  /**
   * Adds or updates your current characters room.
   *
   * @param name the name of the user
   * @param json the object containing the new stuff to update.
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation
   *                                 failed. BAD_REQUEST if an unexpected exception crops up.
   */
  @PUT
  @Path("persons/{name}/room")
  @Consumes(
      {
          MediaType.APPLICATION_JSON
      })
  public Response setRoom(@PathParam("name") String name, String json)
  {
    LOGGER.finer("entering setRoom");
    PrivateRoom privateRoom = JsonUtils.fromJson(json, PrivateRoom.class);

    if (privateRoom.roomid() == null)
    {
      throw new MudWebException(name, "Room id not set.", Status.BAD_REQUEST);
    }

    Room room = getEntityManager().find(Room.class, privateRoom.roomid());
    if (room == null)
    {
      throw new MudWebException(name, "Room " + privateRoom.roomid() + " not found.", Status.NOT_FOUND);
    }

    Person person = getPerson(name);
    person.setRoom(room);
    return createResponse();
  }

  private User authenticate(String name)
  {
    return playerAuthenticationService.authenticate(name, context);
  }

  private Person getPerson(String name)
  {
    var toperson = getEntityManager().find(Person.class, name);
    if (toperson == null)
    {
      throw new MudWebException(name, name + " was not found.", "User was not found (" + name + ")", Status.NOT_FOUND);
    }
    return toperson;
  }

  protected Response createResponse()
  {
    return Response.ok().build();
  }

  @VisibleForTesting
  public void setLogService(LogService logService)
  {
    this.logService = logService;
  }

  @VisibleForTesting
  public void setPlayerAuthenticationService(PlayerAuthenticationService playerAuthenticationService)
  {
    this.playerAuthenticationService = playerAuthenticationService;
  }
}
