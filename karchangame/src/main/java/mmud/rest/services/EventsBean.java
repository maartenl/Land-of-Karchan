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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.script.ScriptException;
import javax.ws.rs.core.Response;
import mmud.Constants;
import mmud.database.entities.characters.Administrator;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Event;
import mmud.database.entities.game.Method;
import mmud.database.enums.Filter;
import mmud.exceptions.MudWebException;
import mmud.scripting.Items;
import mmud.scripting.Persons;
import mmud.scripting.Rooms;
import mmud.scripting.RunScript;
import mmud.scripting.World;

/**
 * Takes care of all the events.
 * <img
 * src="doc-files/Eventsbean.png">
 *
 * @startuml doc-files/Eventsbean.png
 * class EventsBean {
 * +events()
 * }
 * @enduml
 * @author maartenl
 */
@Stateless
@LocalBean
public class EventsBean
{

    @EJB
    private PersonBean personBean;
    @EJB
    private GameBean gameBean;
    @EJB
    private ItemBean itemBean;
    @EJB
    private LogBean logBean;
    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    /**
     * Returns the entity manager of Hibernate/JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }
    private static final Logger itsLog = Logger.getLogger(EventsBean.class.getName());

    /**
     * Runs a single event, right now. Used by administrators for testing.
     *
     * @param aUser the administrator running the event
     * @param eventid an event id, a number.
     */
    public void runSingleEvent(Administrator aUser, Integer eventid)
    {
        itsLog.entering(this.getClass().getName(), "runSingleEvent");
        Constants.setFilters(getEntityManager(), Filter.ON);
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
        Rooms rooms = new Rooms(gameBean);
        Items items = new Items(itemBean);
        World world = new World(gameBean);
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
            itsLog.throwing(EventsBean.class.getName(), "events()", ex);
            throw new MudWebException(aUser.getName(), ex.getMessage(), ex, Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Runs every minute, looks up which user-defined event to execute now.
     * So, this takes care of the events that have been dictates by the deputies.
     *
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.InstantiationException
     * @throws java.lang.reflect.InvocationTargetException
     */
    @Schedule(hour = "*", minute = "*/1")
    public void events() throws IllegalAccessException, InstantiationException, InvocationTargetException
    {
        // itsLog.log(Level.INFO, "Events scheduled at time {0}.", new Date());
        // logBean.writeLog(null, "Events scheduled at time " + new Date() + ".");
        Constants.setFilters(getEntityManager(), Filter.ON);
        Query query = getEntityManager().createNamedQuery("Event.list");
        Calendar calendar = Calendar.getInstance();
        query.setParameter("month", calendar.get(Calendar.MONTH));
        query.setParameter("dayofmonth", calendar.get(Calendar.DAY_OF_MONTH));
        query.setParameter("dayofweek", calendar.get(Calendar.DAY_OF_WEEK));
        query.setParameter("hour", calendar.get(Calendar.HOUR_OF_DAY));
        query.setParameter("minute", calendar.get(Calendar.MINUTE));
        List<Event> list = query.getResultList();
        Persons persons = new Persons(personBean);
        Rooms rooms = new Rooms(gameBean);
        Items items = new Items(itemBean);
        World world = new World(gameBean);
        RunScript runScript = new RunScript(persons, rooms, items, world);
        for (Event event : list)
        {
            // itsLog.log(Level.INFO, "Event {0} executed.", event.getEventid());
            logBean.writeLog(null, "Event " + event.getEventid() + " executed.");
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
            } catch (ScriptException | NoSuchMethodException ex)
            {
                // Error occurred: turn this event off!
                // TODO: that's for debugging,...
                // event.setCallable(Boolean.FALSE);
                // log it but keep going with the next event.
                logBean.writeLogException(ex);
                itsLog.throwing(EventsBean.class.getName(), "events()", ex);
            }
        }
    }
    // TODO : scheduler that cleans up people, runs at midnight
    // TODO : scheduler that increments item durability, runs at high noon?
    // TODO : fighting scheduler (second = "*/2")

    /**
     * Started once an hour, and computes who has been idle too long.
     */
    @Schedule(hour = "*", minute = "1")
    private void executeIdleCleanup()
    {
        logBean.writeLog(null, "executeIdleCleanup(): scheduled at time " + new Date() + ".");
        Constants.setFilters(getEntityManager(), Filter.ON);
        Query query = getEntityManager().createNamedQuery("User.who");
        List<User> list = query.getResultList();
        for (User user : list)
        {
            if (user.isIdleTooLong())
            {
                final String message = "executeIdleCleanup(): " + user.getName() + " was idle for " + user.getIdleTime() + " minutes. Deactivated.";
                itsLog.info(message);
                logBean.writeLog(null, message);
                user.getRoom().sendMessageExcl(user, "%SNAME fade%VERB2 slowly from existence.<br/>\r\n");
                user.deactivate();
            }
        }
    }

}
