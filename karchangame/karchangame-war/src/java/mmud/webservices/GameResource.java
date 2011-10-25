/*
 * Copyright (C) 2011 maartenl
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
package mmud.webservices;

import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import mmud.beans.CommandOutput;
import mmud.beans.GameBeanLocal;
import mmud.exceptions.AuthenticationException;
import mmud.exceptions.NotFoundException;

/**
 * REST Web Service for the game.
 * Url at http://localhost:8080/karchangame-war/resources/game
 *
 * @author maartenl
 */
@Path("game")
@Consumes("application/json")
@Produces("application/json")
@RequestScoped
public class GameResource
{

    private static final String VERSION = "version 1:";

    private static final Logger itsLog = Logger.getLogger("mmudrest");
    GameBeanLocal gameBean;

    @Context
    private UriInfo context;

    /** Creates a new instance of GameResource */
    public GameResource()
    {
    }

    /**
     * Retrieves representation of an instance of mmud.webservices.GameResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("helloworld")
    @Produces("application/json")
    public String helloworld()
    {
        itsLog.entering(this.getClass().getName(), "helloworld");
        String result = null;
        try
        {
            GameBeanLocal example = lookupGameBeanLocal();
            result = example.helloWorld();
        } catch (Exception ex)
        {
            itsLog.throwing(this.getClass().getName(), "helloworld", ex);
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        itsLog.exiting(this.getClass().getName(), "helloworld");
        return result;
    }

    /**
     * PUT method for updating or creating an instance of GameResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content)
    {
    }

    /**
     * Retrieves representation of an instance of mmud.webservices.GameResource
     * @return an instance of java.lang.String
     * http://localhost:8080/karchangame-war/resources/game/Karn/sessionpassword?password=simple
     */
    @GET
    @Path("{name: [a-zA-Z]{3,}}/sessionpassword")
    @Produces("application/json")
    public String getSessionPassword(@PathParam("name") String name, @QueryParam("password") String password)
    {
        itsLog.entering(this.getClass().getName(), VERSION + "getSessionPassword");
        String sessionpwd = null;
        try
        {
            GameBeanLocal example = lookupGameBeanLocal();
            sessionpwd = example.getSessionPassword(name, password);
        } catch (Exception ex)
        {
            itsLog.throwing(this.getClass().getName(), VERSION + "getSessionPassword", ex);
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        itsLog.exiting(this.getClass().getName(), VERSION + "getSessionPassword");
        return sessionpwd;
    }

    /**
     *
     * @param name the name of the player entering the game, requires at least 3 characters, upper and lower case allowed.
     * @param password can be anything, but needs at least 5 characters.
     * @return a CommandOutput containing the result
     */
    @GET
    @Path("{name: [a-zA-Z]{3,}}/enter")
    @Produces("application/json")
    public CommandOutput enter(@PathParam("name") final String name, @QueryParam("password") final String password)
    {
        itsLog.entering(this.getClass().getName(), VERSION + "enter name=" + name + " password=" + password);
        if (password.length() < 5)
        {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        CommandOutput commandOutput = null;
        try
        {
            GameBeanLocal example = lookupGameBeanLocal();
            commandOutput = example.enter(name, password);
        } catch (NotFoundException ex)
        {
            itsLog.throwing(this.getClass().getName(), VERSION + "enter", ex);
            throw new WebApplicationException(Status.NOT_FOUND);

        } catch (AuthenticationException ex)
        {
            itsLog.throwing(this.getClass().getName(), VERSION + "enter", ex);
            throw new WebApplicationException(Status.UNAUTHORIZED);

        } catch (Exception ex)
        {
            itsLog.throwing(this.getClass().getName(), VERSION + "enter", ex);
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        itsLog.exiting(this.getClass().getName(), VERSION + "enter");
        return commandOutput;
    }

    private GameBeanLocal lookupGameBeanLocal()
    {
        itsLog.entering(this.getClass().getName(), "lookupGameBeanLocal");
        GameBeanLocal gbl = null;
        try
        {
            javax.naming.Context c = new InitialContext();
            gbl = (GameBeanLocal) c.lookup("java:global/karchangame/karchangame-ejb/GameBean!mmud.beans.GameBeanLocal");
        } catch (NamingException ne)
        {
            itsLog.throwing(this.getClass().getName(), "lookupGameBeanLocal", ne);
            throw new RuntimeException(ne);
        }
        itsLog.exiting(this.getClass().getName(), "lookupGameBeanLocal");
        if (gbl == null)
        {
            throw new NullPointerException("unable to retrieve GameBean");
        }
        return gbl;
    }
}
