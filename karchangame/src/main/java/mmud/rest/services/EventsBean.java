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

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Event;
import mmud.database.entities.game.Method;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudWebException;
import mmud.scripting.Items;
import mmud.scripting.ItemsInterface;
import mmud.scripting.Persons;
import mmud.scripting.Rooms;
import mmud.scripting.RoomsInterface;
import mmud.scripting.RunScript;
import mmud.scripting.World;
import mmud.scripting.WorldInterface;
import mmud.services.CommunicationService;
import mmud.services.IdleUsersService;

/**
 * Takes care of all the events.
 * <img
 * src="doc-files/Eventsbean.png">
 *
 * @author maartenl
 * @startuml doc-files/Eventsbean.png class EventsBean { +events() }
 * @enduml
 */
@Stateless
@Path("/crontab/events")
public class EventsBean
{

  public static final String LOCALHOST = "localhost";
  public static final String ONLY_LOCALHOST_MAY_ACCESS_THIS = "Only localhost may access this.";
  @EJB
  private PersonBean personBean;
  @EJB
  private GameBean gameBean;
  @EJB
  private ItemBean itemBean;

  @Inject
  private IdleUsersService idleUsersService;

  @EJB
  private LogBean logBean;
  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

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

  private static final Logger LOGGER = Logger.getLogger(EventsBean.class.getName());

  @GET
  @Path("{eventid}")
  public String runSingleEvent(@PathParam("eventid") Integer eventid, @Context HttpServletRequest request)
  {
    if (!request.getRequestURL().toString().contains(LOCALHOST))
    {
      throw new MudWebException("root", ONLY_LOCALHOST_MAY_ACCESS_THIS, Response.Status.FORBIDDEN);
    }
    Object[] objects = {eventid, request.getRequestURL()};
    LOGGER.log(Level.INFO, "Run single event {0}. (accessed {1})", objects);
    if (eventid >= 0)
    {
      runSingleEvent(null, eventid);
    }
    return "Ok";
  }

  /**
   * Runs a single event, right now. Used by administrators for testing.
   *
   * @param aUser   the administrator running the event
   * @param eventid an event id, a number.
   */
  public void runSingleEvent(Administrator aUser, Integer eventid)
  {
    LOGGER.entering(this.getClass().getName(), "runSingleEvent");
    if (eventid == null)
    {
      throw new MudWebException(null, "Event id was empty.", Response.Status.BAD_REQUEST);
    }
    Event event = getEntityManager().find(Event.class, eventid);
    if (event == null)
    {
      throw new MudWebException(null, "Event was not found.", Response.Status.NOT_FOUND);
    }
    Persons persons = new Persons(personBean);
    Rooms rooms = new Rooms(new RoomsInterface()
    {
      @Override
      public Room find(Long id)
      {
        return gameBean.find(id);
      }
    });
    Items items = new Items(new ItemsInterface()
    {
      @Override
      public Item createItem(long itemdefnr)
      {
        return itemBean.createItem(itemdefnr);
      }
    });
    World world = new World(new WorldInterface()
    {
      @Override
      public String getAttribute(String name)
      {
        return gameBean.getAttribute(name);
      }
    });
    RunScript runScript = new RunScript(persons, rooms, items, world);
    Method method = event.getMethod();
    try
    {
      if (event.getRoom() != null)
      {
        boolean result = runScript.run(event.getRoom(), method.getSrc());
      } else if (event.getPerson() != null)
      {
        boolean result = runScript.run(event.getPerson(), method.getSrc());
      } else
      {
        boolean result = runScript.run(method.getSrc());
      }
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | ScriptException | NoSuchMethodException ex)
    {
      // Error occurred: turn this event off!
      // TODO: that's for debugging,...
      // event.setCallable(Boolean.FALSE);
      // log it but keep going with the next event.
      logBean.writeLogException(ex);
      LOGGER.throwing(EventsBean.class.getName(), "events()", ex);
      throw new MudWebException(aUser == null ? null : aUser.getName(), ex.getMessage(), ex, Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Runs every minute, looks up which user-defined event to execute now. So,
   * this takes care of the events that have been dictated by the deputies.
   *
   * @return always "400"
   * @throws java.lang.IllegalAccessException            an exception because of javascript
   * @throws java.lang.InstantiationException            an exception because of javascript
   * @throws java.lang.reflect.InvocationTargetException an exception because of javascript
   */
  @GET
  public Response events(@Context HttpServletRequest request) throws IllegalAccessException, InstantiationException, InvocationTargetException
  {
    if (!request.getRequestURL().toString().contains(LOCALHOST))
    {
      throw new MudWebException("root", ONLY_LOCALHOST_MAY_ACCESS_THIS, Response.Status.FORBIDDEN);
    }
    if (idleUsersService.isNobodyPlaying())
    {
      return Response.ok().build();
    }
    Object[] objects = {LocalDateTime.now(), request.getRequestURL()};
    LOGGER.log(Level.INFO, "Events scheduled at time {0}. (accessed {1})", objects);
    // logBean.writeLog(null, "Events scheduled at time " + LocalDateTime.now() + ".");
    Query query = getEntityManager().createNamedQuery("Event.list");
    Calendar calendar = Calendar.getInstance();
    query.setParameter("month", calendar.get(Calendar.MONTH));
    query.setParameter("dayofmonth", calendar.get(Calendar.DAY_OF_MONTH));
    query.setParameter("dayofweek", calendar.get(Calendar.DAY_OF_WEEK));
    query.setParameter("hour", calendar.get(Calendar.HOUR_OF_DAY));
    query.setParameter("minute", calendar.get(Calendar.MINUTE));
    List<Event> list = query.getResultList();
    Persons persons = new Persons(personBean);
    Rooms rooms = new Rooms(new RoomsInterface()
    {
      @Override
      public Room find(Long id)
      {
        return gameBean.find(id);
      }
    });
    Items items = new Items(new ItemsInterface()
    {
      @Override
      public Item createItem(long itemdefnr)
      {
        return itemBean.createItem(itemdefnr);
      }
    });
    World world = new World(new WorldInterface()
    {
      @Override
      public String getAttribute(String name)
      {
        return gameBean.getAttribute(name);
      }
    });
    RunScript runScript = new RunScript(persons, rooms, items, world);
    for (Event event : list)
    {
      // LOGGER.log(Level.INFO, "Event {0} executed.", event.getEventid());
      logBean.writeLog("Event " + event.getEventid() + " executed.");
      Method method = event.getMethod();
      if (event.getRoom() != null)
      {
        try
        {
          boolean result = runScript.run(event.getRoom(), method.getSrc());
        } catch (ScriptException | NoSuchMethodException ex)
        {
          // Error occurred: turn this event off!
          // TODO: that's for debugging,...
          // event.setCallable(Boolean.FALSE);
          // log it but keep going with the next event.
          logBean.writeLogException(ex);
          LOGGER.throwing(EventsBean.class.getName(), String.format("events(room=%d, method=%s)", event.getRoom().getId(), method.getName()), ex);
        }
      } else if (event.getPerson() != null)
      {
        try
        {
          boolean result = runScript.run(event.getPerson(), method.getSrc());
        } catch (ScriptException | NoSuchMethodException ex)
        {
          // Error occurred: turn this event off!
          // TODO: that's for debugging,...
          // event.setCallable(Boolean.FALSE);
          // log it but keep going with the next event.
          logBean.writeLogException(ex);
          LOGGER.throwing(EventsBean.class.getName(), String.format("events(person=%s, method=%s)", event.getPerson().getName(), method.getName()), ex);
        }
      } else
      {
        try
        {
          boolean result = runScript.run(method.getSrc());
        } catch (ScriptException | NoSuchMethodException ex)
        {
          // Error occurred: turn this event off!
          // TODO: that's for debugging,...
          // event.setCallable(Boolean.FALSE);
          // log it but keep going with the next event.
          logBean.writeLogException(ex);
          LOGGER.throwing(EventsBean.class.getName(), String.format("events(method=%s)", method.getName()), ex);
        }
      }
    }
    return Response.ok().build();
  }
  // TODO : scheduler that cleans up people, runs at midnight
  // TODO : scheduler that increments item durability, runs at high noon?
  // TODO : fighting scheduler (second = "*/2") -> bad idea, fix this differently, with a dedicated server thing

  /**
   * Started once an hour, and computes who has been idle too long.
   *
   * @return always "400".
   */
  @GET
  @Path("idles")
  public Response executeIdleCleanup(@Context HttpServletRequest request)
  {
    if (!request.getRequestURL().toString().contains(LOCALHOST))
    {
      throw new MudWebException("root", ONLY_LOCALHOST_MAY_ACCESS_THIS, Response.Status.FORBIDDEN);
    }
    LOGGER.log(Level.INFO, "Idle cleanup. (accessed {0})", request.getRequestURL());
    var query = getEntityManager().createNamedQuery("User.who");
    List<User> list = query.getResultList();
    List<String> idleUsers = idleUsersService.getIdleUsers();
    for (User user : list)
    {
      if (idleUsers.contains(user.getName()))
      {
        final String message = "executeIdleCleanup(): " + user.getName() + " was idle for " + idleUsersService.getIdleTime(user.getName()) + " minutes. Deactivated.";
        LOGGER.info(message);
        logBean.writeLog(message);
        CommunicationService.getCommunicationService(user.getRoom()).sendMessageExcl(user, "%SNAME fade%VERB2 slowly from existence.<br/>\r\n");
        user.deactivate();
      }
    }
    return Response.ok().build();
  }

}
