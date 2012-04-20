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
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.database.entities.game.Person;
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
     * This method should be called to verify that the target of a certain
     * action is indeed a proper authenticated user.
     *
     * @param lok session password
     * @param name the name to identify the person
     * @throws WebApplicationException NOT_FOUND, if the user is either not
     * found or is not a proper user. BAD_REQUEST if an unexpected exception
     * crops up or provided info is really not proper. UNAUTHORIZED if session
     * passwords do not match.
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
     * Creates a new character, suitable for playing.
     *
     * @param name the name of the user
     * @param person the data of the new character
     * @throws BAD_REQUEST if an unexpected exception
     * crops up.
     */
    @POST
    @Path("{name}/create")
    @Produces(

    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public Response createCharacter(@PathParam("name") String name, PrivatePerson person)
    {
        itsLog.debug("entering createCharacter");
        List<PrivateMail> res = new ArrayList<>();
        try
        {
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("createCharacter: throws ", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
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
    public Response logon(@PathParam("name") String name, @QueryParam("lok") String lok)
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
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
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
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
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
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
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
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return Response.ok().build();
    }
}
