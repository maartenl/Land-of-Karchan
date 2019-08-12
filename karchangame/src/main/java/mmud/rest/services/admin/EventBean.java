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
import java.util.ArrayList;

import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
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
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Event;
import mmud.database.entities.game.Method;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminEvent;

/**
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/events")
public class EventBean extends AbstractFacade<Event>
{

  private static final Logger LOGGER = Logger.getLogger(EventBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  public EventBean()
  {
    super(Event.class);
  }

  @POST
  @Consumes(
          {
            "application/xml", "application/json"
          })
  public void create(AdminEvent entity, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Event newEvent = createEvent(entity, name);
    newEvent.setEventid(entity.eventid);
    create(newEvent, sc);
  }

  private Event createEvent(AdminEvent entity, final String name) throws MudWebException
  {
    Person person = null;
    if (entity.person != null && !entity.person.trim().equals(""))
    {
      person = getEntityManager().find(Person.class, entity.person);
      if (person == null)
      {
        throw new MudWebException(name, "Person " + entity.person + " was not found.", Response.Status.NOT_FOUND);
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
    Method method = null;
    if (entity.method != null && !entity.method.trim().equals(""))
    {
      method = getEntityManager().find(Method.class, entity.method);
      if (method == null)
      {
        throw new MudWebException(name, "Method " + entity.method + " was not found.", Response.Status.NOT_FOUND);
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

  @Override
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
            "application/xml", "application/json"
          })

  public void edit(@PathParam("id") Integer id, AdminEvent entity, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Event newEvent = createEvent(entity, name);
    edit(id, newEvent, sc);
  }

  public void edit(@PathParam("id") Integer id, Event entity, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Event attribute = super.find(id);

    if (attribute == null)
    {
      throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
    attribute.setCallable(entity.getCallable());
    attribute.setDayofmonth(entity.getDayofmonth());
    attribute.setDayofweek(entity.getDayofweek());
    attribute.setHour(entity.getHour());
    attribute.setMethod(entity.getMethod());
    attribute.setMinute(entity.getMinute());
    attribute.setMonth(entity.getMonth());
    attribute.setPerson(entity.getPerson());
    attribute.setRoom(entity.getRoom());
    attribute.setOwner(admin);
    checkValidation(name, attribute);
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Integer id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Event attribute = super.find(id);
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
    super.remove(attribute);
  }

  @GET
  @Path("{id}")
  @Produces(
          {
            "application/xml", "application/json"
          })
  public AdminEvent find(@PathParam("id") Integer id)
  {
    Event event = super.find(id);
    if (event == null)
    {
      return null;
    }
    AdminEvent result = getAdminEvent(event);
    return result;
  }

  private AdminEvent getAdminEvent(Event event)
  {
    AdminEvent result = new AdminEvent();
    result.callable = event.getCallable();
    result.creation = event.getCreation();
    result.dayofmonth = event.getDayofmonth();
    result.dayofweek = event.getDayofweek();
    result.eventid = event.getEventid();
    result.hour = event.getHour();
    result.method = event.getMethod().getName();
    result.minute = event.getMinute();
    result.month = event.getMonth();
    result.owner = event.getOwner();
    result.person = event.getPerson() != null ? event.getPerson().getName() : null;
    result.room = event.getRoom() != null ? event.getRoom().getId() : null;
    return result;
  }

  @DELETE
  @Path("{id}/owner")
  public void disown(@PathParam("id") Integer id, @Context SecurityContext sc)
  {
    (new OwnerHelper(getEntityManager())).disown(id, sc, Event.class);
  }

  @GET
  @Produces(
          {
            "application/xml", "application/json"
          })
  public List<AdminEvent> findAllAdminEvents()
  {
    LOGGER.info("findAll");
    List<Event> all = super.findAll();
    final List<AdminEvent> result = new ArrayList<>();
    for (Event event : all)
    {
      result.add(getAdminEvent(event));
    }
    return result;
  }

  @GET
  @Path("{from}/{to}")
  @Produces(
          {
            "application/xml", "application/json"
          })
  public List<AdminEvent> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
  {
    List<Event> all = super.findRange(new int[]
    {
      from, to
    });
    final List<AdminEvent> result = new ArrayList<>();
    for (Event event : all)
    {
      result.add(getAdminEvent(event));
    }
    return result;
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String countREST()
  {
    return String.valueOf(super.count());
  }

  @Override
  protected EntityManager getEntityManager()
  {
    return em;
  }

}
