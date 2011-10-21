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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.enterprise.context.RequestScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
@RequestScoped
public class GameResource
{

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
        GameBeanLocal example;
        try {
            InitialContext initialContext = new InitialContext();

            // INFO: Portable JNDI names for EJB MyFirstBean :
            // java:global/karchan_core/karchan_core-ejb/ConverterBean
            example = (GameBeanLocal) initialContext.lookup("java:global/karchangame/karchangame-ejb/GameBean");
            return example.helloWorld();
        } catch (NamingException ex) {
            Logger.getLogger(GameResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
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
}
