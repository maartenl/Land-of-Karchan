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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import mmud.database.entities.game.Person;
import mmud.rest.webentities.Fortune;
import mmud.rest.webentities.PublicPerson;

/**
 * Contains all rest calls that are available to the world, without authentication
 * or authorization. You can find them at /karchangame/resources/public.
 * @author maartenl
 */
@Stateless
@LocalBean
@Path("/public")
public class PublicBean
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    /**
     * Returns the entity manager of Hibernate/JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }

    private static final Logger itsLog = Logger.getLogger("mmudrest.public");

    /**
     * Returns a Fortune 100 of players on karchan.
     * The URL: /karchangame/resources/public/fortunes.
     * Can produce both application/xml and application/json.
     */
    @GET
    @Path("fortunes")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<Fortune> fortunes()
    {
        itsLog.entering(this.getClass().getName(), "fortunes");

        List<Fortune> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("Person.getFortunes");
            query.setMaxResults(100);
            List<Person> list = query.getResultList();

            for (Person person : list)
            {
                res.add(new Fortune(person));
            }
        } catch (Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "fortunes", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "fortunes");
        return res;
    }

    /**
     * Returns a List of people currently online.
     * The URL: /karchangame/resources/public/who.
     * Can produce both application/xml and application/json.
     */
    @GET
    @Path("who")
    public List<PublicPerson> who()
    {
        itsLog.entering(this.getClass().getName(), "who");
        List<PublicPerson> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("Person.who");
            List<Person> list = query.getResultList();

            for (Person person : list)
            {
                PublicPerson publicPerson = new PublicPerson();
                publicPerson.name = person.getName();
                publicPerson.title = person.getTitle();
                publicPerson.sleep = person.getSleep() ==1 ? "sleeping" :"";
                publicPerson.area = person.getRoom().getArea().getShortdesc();
                Long now = (new Date()).getTime();
                Long backThen = person.getLastlogin().getTime();
                publicPerson.min = (now-backThen) / 60000;
                publicPerson.sec = ((now-backThen) / 1000) % 60;
                res.add(publicPerson);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "who", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        itsLog.exiting(this.getClass().getName(), "who");
        return res;
    }

}