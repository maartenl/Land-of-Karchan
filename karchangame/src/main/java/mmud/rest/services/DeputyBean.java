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
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.Constants;
import mmud.database.entities.game.Method;
import mmud.database.enums.Filter;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.DeputyScript;

/**
 * Contains all rest calls that are available to deputies for adnministration. You can find them at
 * /karchangame/resources/administration.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
@Path("/administration")
public class DeputyBean
{

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    public DeputyBean()
    {
        // empty constructor, I don't know why, but it's necessary.
    }

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

    private static final Logger itsLog = Logger.getLogger(DeputyBean.class.getName());

    @GET
    @Path("scripts")
    @Produces(
            {
                MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
            })
    public List<DeputyScript> scripts()
    {
        itsLog.finer("entering scripts");
        Constants.setFilters(getEntityManager(), Filter.OFF);
        List<DeputyScript> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("Method.findAll");
            List<Method> list = query.getResultList();
            for (Method method : list)
            {
                res.add(new DeputyScript(method));
            }
        } catch (Exception e)
        {
            itsLog.throwing("DeputyBean", "scripts", e);
            throw new MudWebException(e, Response.Status.BAD_REQUEST);
        }
        itsLog.finer("exiting scripts");
        return res;
    }

}
