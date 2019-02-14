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


import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import mmud.database.entities.game.Worldattribute;
import mmud.exceptions.MudWebException;

/**
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/worldattributes")
public class WorldattributesBean extends AbstractFacade<Worldattribute>
{

    private static final Logger itsLog = Logger.getLogger(WorldattributesBean.class.getName());

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;

    public WorldattributesBean()
    {
        super(Worldattribute.class);
    }

    @POST
    @Override
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void create(Worldattribute entity, @Context SecurityContext sc)
    {
        itsLog.info("create");
        final String name = sc.getUserPrincipal().getName();
        Admin admin = getEntityManager().find(Admin.class, name);
        itsLog.info(admin.toString());
        entity.setCreation(LocalDateTime.now());
        entity.setOwner(admin);
        itsLog.info(entity.toString());
        checkValidation(name, entity);
        getEntityManager().persist(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes(
            {
                "application/xml", "application/json"
            })
    public void edit(@PathParam("id") String id, Worldattribute entity, @Context SecurityContext sc)
    {
        itsLog.info("edit");
        final String name = sc.getUserPrincipal().getName();

        Worldattribute attribute = find(id);

        if (attribute == null)
        {
            throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
        }
        Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
        attribute.setContents(entity.getContents());
        attribute.setType(entity.getType());
        attribute.setOwner(admin);
        checkValidation(name, attribute);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id, @Context SecurityContext sc)
    {
        final String name = sc.getUserPrincipal().getName();
        final Worldattribute attribute = find(id);
        Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
        remove(attribute);
    }

    @DELETE
    @Path("{id}/owner")
    public void disown(@PathParam("id") String id, @Context SecurityContext sc)
    {
        (new OwnerHelper(getEntityManager())).disown(id, sc, Worldattribute.class);
    }

    @GET
    @Path("{id}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public Worldattribute find(@PathParam("id") String id)
    {
        return super.find(id);
    }

    @GET
    @Override
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Worldattribute> findAll()
    {
        final List<Worldattribute> collection = super.findAll();
        return collection;
    }

    @GET
    @Path("{from}/{to}")
    @Produces(
            {
                "application/xml", "application/json"
            })
    public List<Worldattribute> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to)
    {
        return super.findRange(new int[]
        {
            from, to
        });
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
