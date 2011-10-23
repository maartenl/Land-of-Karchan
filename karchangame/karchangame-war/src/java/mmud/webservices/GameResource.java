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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import mmud.beans.GameBeanLocal;

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

    private static final Logger itsLog = Logger.getLogger("mmudrest");

    GameBeanLocal gameBean = lookupGameBeanLocal();
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
    @Produces("application/json")
    public String getJson()
    {
        itsLog.entering(this.getClass().getName(), "getJson");
        String result = null;
        try
        {
            GameBeanLocal example = lookupGameBeanLocal();
            InitialContext initialContext = new InitialContext();
            example = (GameBeanLocal) initialContext.lookup("java:global/karchangame/karchangame-ejb/GameBean");
            if (example == null)
            {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            result = example.helloWorld();
        } catch (Exception ex)
        {
            itsLog.throwing(this.getClass().getName(), "getJson", ex);
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        itsLog.exiting(this.getClass().getName(), "getJson");
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
     * @return an instance of java.lang.String PathParam
     * http://localhost:8080/karchangame-war/resources/game/Karn/sessionpassword?password=simple
     */
    @GET
    @Path("sessionpassword")
    @Produces("application/json")
    public String getSessionPassword(@QueryParam("name") String name, @QueryParam("password") String password)
    {
        itsLog.entering(this.getClass().getName(), "getSessionPassword");
        String sessionpwd = null;
        try
        {
            GameBeanLocal example = lookupGameBeanLocal();
            InitialContext initialContext = new InitialContext();
            example = (GameBeanLocal) initialContext.lookup("java:global/karchangame/karchangame-ejb/GameBean");
            if (example == null)
            {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }
            sessionpwd = example.helloWorld();//getSessionPassword(name, password);
        } catch (Exception ex)
        {
            itsLog.throwing(this.getClass().getName(), "getSessionPassword", ex);
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        itsLog.exiting(this.getClass().getName(), "getSessionPassword");
        return sessionpwd;
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
        return gbl;
    }
}
