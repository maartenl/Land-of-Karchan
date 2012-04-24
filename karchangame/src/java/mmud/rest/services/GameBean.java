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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.Utils;
import mmud.database.entities.game.Person;
import mmud.database.entities.game.Room;
import mmud.database.enums.Sex;
import mmud.rest.webentities.PrivateDisplay;
import mmud.rest.webentities.PrivateLog;
import mmud.rest.webentities.PrivateMail;
import mmud.rest.webentities.PrivatePerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes care of all the game-related functions.
 * <img
 * src="../../../images/Gamebean.png">
 *
 * @startuml Gamebean.png
 * (*) --> "Create character"
 * --> "Login"
 * --> "Play"
 * --> "Retrieve log"
 * --> "Play"
 * --> "Quit"
 * -->(*)
 * @enduml
 * @author maartenl
 */
@Stateless
@LocalBean
@Path("/game")
public class GameBean
{

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
    private static final Logger itsLog = LoggerFactory.getLogger(GameBean.class);

    /**
     * <p>This method should be called to verify that the target of a certain
     * action is indeed a proper authenticated user.</p>
     * <p><img
     * src="../../../images/Gamebean_authenticate.png"></p>
     *
     * @param lok session password
     * @param name the name to identify the person
     * @throws WebApplicationException NOT_FOUND, if the user is either not
     * found or is not a proper user. BAD_REQUEST if an unexpected exception
     * crops up or provided info is really not proper. UNAUTHORIZED if session
     * passwords do not match.
     * @startuml Gamebean_authenticate.png
     * (*) --> "find character"
     * --> "character found"
     * --> "character == user"
     * --> "session password is good"
     * -->(*)
     * @enduml
     */
    private Person authenticate(String name, String lok)
    {
        Person person = getEntityManager().find(Person.class, name);
        if (person == null)
        {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (!person.isUser())
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (!person.verifySessionPassword(lok))
        {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        return person;
    }

    /**
     * <p>This method should be called to verify that the target of a certain
     * action is a user with the appropriate password.</p>
     * <p><img
     * src="../../../images/Gamebean_authenticateWithPassword.png"></p>
     *
     * @param password real password
     * @param name the name to identify the person
     * @throws BAD_REQUEST if an unexpected exception
     * crops up or provided info is really not proper. UNAUTHORIZED if session
     * passwords do not match or user not found.
     * @startuml Gamebean_authenticateWithPassword.png
     * (*) --> "find character"
     * --> "character found"
     * --> "character == user"
     * --> "password is good"
     * -->(*)
     * @enduml
     */
    protected Person authenticateWithPassword(String name, String password)
    {
        Person person = getEntityManager().find(Person.class, name);
        if (person == null)
        {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        if (!person.isUser())
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Query query = getEntityManager().createNamedQuery("Person.authorise");
        query.setParameter("name", name);
        query.setParameter("password", password);
        List<Person> persons = query.getResultList();
        if (persons.isEmpty())
        {
            throw new WebApplicationException(new RuntimeException("name was " + name + " password " + password), Response.Status.UNAUTHORIZED);
        }
        if (persons.size() > 1)
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        person = persons.get(0);
        if (!person.isUser())
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return person;
    }

    /**
     * <p>Checks to see if a person is banned from playing.</p>
     * <p><img
     * src="../../../images/Gamebean_isBanned.png"></p>
     *
     * @param name the name of the person
     * @param address the ip address the person is playing from
     * @return true if banned, false otherwise.
     * @startuml Gamebean_isBanned.png
     * (*) --> "check silly names"
     * --> "check unbanned"
     * --> "check address banned"
     * --> "check name banned"
     * -->(*)
     * @enduml
     */
    public boolean isBanned(String name, String address)
    {
        // check silly names
        Query query = getEntityManager().createNamedQuery("SillyName.findByName");
        query.setParameter("name", name);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty())
        {
            // silly name found!
            return true;
        }

        // check unbanned names
        query = getEntityManager().createNamedQuery("UnbanTable.findByName");
        query.setParameter("name", name);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty())
        {
            // unbanned name found!
            return false;
        }

        // check address banned
        String address2 = "bogushostman!";
        try
        {
            InetAddress inetAddress = InetAddress.getByName(address2);
            address2 = inetAddress.getHostName();
        } catch (UnknownHostException e)
        {
            // ignore this.
        }
        query = getEntityManager().createNamedQuery("BanTable.find");
        query.setParameter("address", address);
        query.setParameter("address2", address2);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty())
        {
            // banned address found!
            return true;
        }

        // check name banned
        query = getEntityManager().createNamedQuery("BannedName.find");
        query.setParameter("name", name);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty())
        {
            // banned name found!
            return true;
        }

        return false;
    }

    /**
     * <p>Creates a new character, suitable for playing.</p>
     * <p><img
     * src="../../../images/Gamebean_create.png"></p>
     * @param name the name of the user
     * @param pperson the data of the new character
     * @return NO_CONTENT if the game is offline for maintenance.
     * @throws BAD_REQUEST if an unexpected exception
     * crops up or something could not be validated.
     * @startuml Gamebean_create.png
     * (*) --> "check for offline"
     * --> "check presence of data"
     * --> "check name == pperson.name"
     * --> "check password == password2"
     * --> "check isBanned"
     * --> "check already person"
     * --> "create person"
     * -->(*)
     * @enduml
     */
    @POST
    @Path("{name}")
    @Produces(




    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public Response create(@Context HttpServletRequest requestContext, @PathParam("name") String name, PrivatePerson pperson)
    {
        itsLog.debug("entering create");
        String address = requestContext.getRemoteAddr().toString();
        try
        {

            if (Utils.isOffline())
            {
                // game offline
                return Response.noContent().build();
            }
            if (pperson == null)
            {
                // no data provided
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            if (name == null || !pperson.name.equals(name))
            {
                // wrong data provided
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            if (pperson.password == null || !pperson.password.equals(pperson.password2))
            {
                // passwords do not match
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            Person person = new Person();
            person.setName(name);
            person.setPassword(pperson.password);
            if (isBanned(name, address))
            {
                // is banned
                throw new WebApplicationException(Response.Status.FORBIDDEN);
            }
            Person foundPerson = getEntityManager().find(Person.class, name);
            if (foundPerson != null)
            {
                // already a person
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            // everything's cool! Let's do this!
            person.setActive(false);
            person.setTitle(pperson.title);
            person.setRealname(pperson.realname);
            person.setEmail(pperson.email);
            person.setAddress(address);

            person.setSex(Sex.createFromString(pperson.sex));
            person.setRace(pperson.race);
            person.setAge(pperson.age);
            person.setHeight(pperson.height);
            person.setWidth(pperson.width);
            person.setComplexion(pperson.complexion);
            person.setEyes(pperson.eyes);
            person.setFace(pperson.face);
            person.setHair(pperson.hair);
            person.setBeard(pperson.beard);
            person.setArm(pperson.arm);
            person.setLeg(pperson.leg);
            person.setBirth(new Date());
            person.setCreation(new Date());
            person.setRoom(getEntityManager().find(Room.class, Room.STARTERS_ROOM));
            getEntityManager().persist(person);
            // TODO automatically add a welcome mail.
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (ConstraintViolationException e)
        {
            StringBuilder buffer = new StringBuilder("ConstraintViolationException:");
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                buffer.append(violation);
            }
            throw new RuntimeException(buffer.toString(), e);

        } catch (javax.persistence.PersistenceException f)
        {
            ConstraintViolationException e = (ConstraintViolationException) f.getCause();
            StringBuilder buffer = new StringBuilder("PersistenceException:");
            for (ConstraintViolation<?> violation : e.getConstraintViolations())
            {
                buffer.append(violation);
            }
            throw new RuntimeException(buffer.toString(), e);
        } catch (Exception e)
        {
            itsLog.debug("create: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }

    /**
     * Deletes a character, permanently. Use with extreme caution.
     *
     * @param name the name of the user
     * @param person the data of the new character
     * @throws BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @DELETE
    @Path("{name}")
    @Produces(



    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public Response delete(@PathParam("name") String name, @QueryParam("password") String password, @QueryParam("password2") String password2)
    {
        itsLog.debug("entering delete");
        if (password == null || !password.equals(password2))
        {
            // passwords do not match
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        Person person = authenticateWithPassword(name, password);
        try
        {
            getEntityManager().remove(person);
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("delete: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }

    /**
     * Logs a character in, to start playing.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @throws BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @POST
    @Path("{name}/logon")
    @Produces(











    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public Response logon(@PathParam("name") String name, @QueryParam("password") String password)
    {
        itsLog.debug("entering logon");
        List<PrivateMail> res = new ArrayList<>();
        try
        {
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("logon: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }

    /**
     * Logs a character in, to start playing.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @throws BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @POST
    @Path("{name}/play")
    @Produces(


















    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public PrivateDisplay play(@PathParam("name") String name, @QueryParam("lok") String lok, @QueryParam("command") String command)
    {
        itsLog.debug("entering play");
        List<PrivateMail> res = new ArrayList<>();
        try
        {
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("play: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return null;
    }

    /**
     * Retrieves the log of a player.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param offset the offset from whence to read the log
     * @return returns the log
     * @see PrivateLog
     * @throws BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @GET
    @Path("{name}/log")
    @Produces(


















    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public PrivateLog retrieveLog(@PathParam("name") String name, @QueryParam("lok") String lok, @QueryParam("offset") Integer offset)
    {
        itsLog.debug("entering retrieveLog");
        List<PrivateMail> res = new ArrayList<>();
        try
        {
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("retrieveLog: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return null;
    }

    /**
     * Stops a playing character from playing.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @throws BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @GET
    @Path("{name}/quit")
    @Produces(


















    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public Response quit(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.debug("entering quit");
        List<PrivateMail> res = new ArrayList<>();
        try
        {
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("quit: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }
}
