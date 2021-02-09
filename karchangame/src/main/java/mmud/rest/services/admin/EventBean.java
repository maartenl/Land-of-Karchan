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

import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Event;
import mmud.database.entities.game.Method;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminEvent;

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

import static mmud.rest.services.admin.ValidationUtils.checkValidation;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/events")
public class EventBean // extends AbstractFacade<Event>
{

  private static final Logger LOGGER = Logger.getLogger(EventBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public void create(String json, @Context SecurityContext sc)
  {
    AdminEvent entity = AdminEvent.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    Event newEvent = createEvent(entity, name);
    newEvent.setEventid(entity.eventid);
    create(newEvent, sc);
    logBean.writeDeputyLog(admin, "New event '" + entity.eventid + "' created.");
  }

  private Event createEvent(AdminEvent entity, final String name) throws MudWebException
  {
    Person person = null;
    if (entity.name != null && !entity.name.trim().equals(""))
    {
      person = getEntityManager().find(Person.class, entity.name);
      if (person == null)
      {
        throw new MudWebException(name, "Person " + entity.name + " was not found.", Response.Status.NOT_FOUND);
      }
    }
    Room room = null;
    if (entity.room != null)
    {
      room = getEntityManager().find(Room.class, entity.room);
      if (room == null)
      {
        throw new MudWebException(name, "Room " + entity.room + " was not found.", Response.Status.NOT_FOUND);
      }
    }
    Method method;
    if (entity.methodname != null && !entity.methodname.trim().equals(""))
    {
      method = getEntityManager().find(Method.class, entity.methodname);
      if (method == null)
      {
        throw new MudWebException(name, "Method " + entity.methodname + " was not found.", Response.Status.NOT_FOUND);
      }
    } else
    {
      throw new MudWebException(name, "Method missing!", Response.Status.NOT_FOUND);
    }
    Event newEvent = new Event();
    newEvent.setCallable(entity.callable);
    newEvent.setDayofmonth(entity.dayofmonth);
    newEvent.setDayofweek(entity.dayofweek);
    newEvent.setHour(entity.hour);
    newEvent.setMethod(method);
    newEvent.setMinute(entity.minute);
    newEvent.setMonth(entity.month);
    newEvent.setPerson(person);
    newEvent.setRoom(room);
    return newEvent;
  }

  public void create(Event entity, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    entity.setCreation(LocalDateTime.now());
    entity.setOwner(admin);
    checkValidation(name, entity);
    getEntityManager().persist(entity);
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/json"
    })
  public void edit(@PathParam("id") Integer id, String json, @Context SecurityContext sc)
  {
    AdminEvent entity = AdminEvent.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(entity.eventid))
    {
      throw new MudWebException(name, "Event ids do not match.", Response.Status.BAD_REQUEST);
    }

    Event event = getEntityManager().find(Event.class, entity.eventid);

    if (event == null)
    {
      throw new MudWebException(name, "Event " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Event newEvent = createEvent(entity, name);
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, event);
    event.setCallable(newEvent.getCallable());
    event.setDayofmonth(newEvent.getDayofmonth());
    event.setDayofweek(newEvent.getDayofweek());
    event.setHour(newEvent.getHour());
    event.setMethod(newEvent.getMethod());
    event.setMinute(newEvent.getMinute());
    event.setMonth(newEvent.getMonth());
    event.setPerson(newEvent.getPerson());
    event.setRoom(newEvent.getRoom());
    event.setOwner(OwnerHelper.getNewOwner(entity.owner, admin, getEntityManager()));
    checkValidation(name, event);
    logBean.writeDeputyLog(admin, "Event '" + event.getEventid() + "' updated.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Integer id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Event event = getEntityManager().find(Event.class, id);
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, event);
    getEntityManager().remove(event);
    logBean.writeDeputyLog(admin, "Event '" + event.getEventid() + "' deleted.");
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
    Event item = getEntityManager().find(Event.class, id);
    if (item == null)
    {
      throw new MudWebException(name, "Event " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminEvent(item).toJson();
  }


  @GET
  @Produces(
    {
      "application/json"
    })
  public String findAll()
  {
    List<String> items = getEntityManager().createNativeQuery(AdminEvent.GET_QUERY)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      "application/xml", "application/json"
    })
  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminEvent.GET_QUERY)
      .setMaxResults(pageSize)
      .setFirstResult(offset)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String countREST()
  {
    return String.valueOf(getEntityManager().createNamedQuery("Event.countAll").getSingleResult());
  }

  public EntityManager getEntityManager()
  {
    return em;
  }

}
