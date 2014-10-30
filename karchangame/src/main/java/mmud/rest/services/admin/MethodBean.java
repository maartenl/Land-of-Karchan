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

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Method;
import mmud.exceptions.MudWebException;

/**
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/methods")
public class MethodBean extends AbstractFacade<Method>
{

    private static final Logger itsLog = Logger.getLogger(MethodBean.class.getName());

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    public MethodBean()
    {
        super(Method.class);
    }

    @POST
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void create(Method entity, @Context SecurityContext sc)
    {
        itsLog.info("create");
        final String name = sc.getUserPrincipal().getName();
        Admin admin = getEntityManager().find(Admin.class, name);
        itsLog.info(admin.toString());
        entity.setCreation(new Date());
        entity.setOwner(admin);
        itsLog.info(entity.toString());
        checkValidation(name, entity);
        getEntityManager().persist(entity);
    }

    @PUT
    @Path("{alphabet}/{id}")
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void edit(@PathParam("alphabet") String alphabet, @PathParam("id") String id, Method entity, @Context SecurityContext sc)
    {
        itsLog.info("edit");
        final String name = sc.getUserPrincipal().getName();

        Method attribute = find(id);

        if (attribute == null)
        {
            throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
        }
        Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
        attribute.setSrc(entity.getSrc());
        attribute.setOwner(admin);
        checkValidation(name, attribute);
    }

    @DELETE
    @Path("{alphabet}/{id}")
    public void remove(@PathParam("alphabet") String alphabet, @PathParam("id") String id, @Context SecurityContext sc)
    {
        final String name = sc.getUserPrincipal().getName();
        final Method attribute = find(id);
        Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
        super.remove(attribute);
    }

    @DELETE
    @Path("{alphabet}/{id}/owner")
    public void disown(@PathParam("alphabet") String alphabet, @PathParam("id") String id, @Context SecurityContext sc)
    {
        (new OwnerHelper(getEntityManager())).disown(id, sc, Method.class);
    }

    @GET
    @Path("{alphabet}/{id}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public Method find(@PathParam("alphabet") String alphabet, @PathParam("id") String id)
    {
        return super.find(id);
    }

    @GET
    @Override
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Method> findAll()
    {
        return super.findAll();
    }

    @GET
    @Path("{alphabet}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Method> findLike(@PathParam("alphabet") String alphabet)
    {
        itsLog.log(Level.FINER, "findRange {0}", alphabet);
        Query query = getEntityManager().createNamedQuery("Method.findRange");
        query.setParameter("alphabet", alphabet + "%");
        List<Method> result = query.getResultList();
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
