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
import javax.inject.Inject;
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
import javax.ws.rs.core.UriInfo;

import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Worldattribute;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminWorldattribute;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/worldattributes")
public class WorldattributesBean
{

  private static final Logger LOGGER = Logger.getLogger(WorldattributesBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public String create(String json, @Context SecurityContext sc)
  {
    AdminWorldattribute adminWorldattribute = AdminWorldattribute.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    final Worldattribute existingItem = getEntityManager().find(Worldattribute.class, adminWorldattribute.name);
    if (existingItem != null)
    {
      throw new MudWebException(name, "Worldattribute for " + adminWorldattribute.name + " already exists.", Response.Status.PRECONDITION_FAILED);
    }
    Worldattribute entity = new Worldattribute();
    entity.setName(adminWorldattribute.name);
    entity.setType(adminWorldattribute.type);
    entity.setContents(adminWorldattribute.contents);
    entity.setCreation(LocalDateTime.now());
    entity.setOwner(admin);
    ValidationUtils.checkValidation(name, entity);
    getEntityManager().persist(entity);
    logBean.writeDeputyLog(admin, "New worldattribute '" + adminWorldattribute.name + "' created.");
    return entity.getName();
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/json"
    })
  public void edit(@PathParam("id") String id, String json, @Context SecurityContext sc)
  {
    AdminWorldattribute adminWorldattribute = AdminWorldattribute.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminWorldattribute.name))
    {
      throw new MudWebException(name, "Worldattribute ids do not match.", Response.Status.BAD_REQUEST);
    }
    Worldattribute attribute = getEntityManager().find(Worldattribute.class, id);
    if (attribute == null)
    {
      throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
    }

    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
    attribute.setContents(adminWorldattribute.contents);
    attribute.setType(adminWorldattribute.type);
    attribute.setOwner(OwnerHelper.getNewOwner(adminWorldattribute.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, attribute);
    logBean.writeDeputyLog(admin, "Worldattribute '" + id + "' updated.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Worldattribute attribute = getEntityManager().find(Worldattribute.class, id);
    if (attribute == null)
    {
      throw new MudWebException(name, "Worldattribute " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, attribute);
    getEntityManager().remove(attribute);
    logBean.writeDeputyLog(admin, "Worldattribute '" + id + "' deleted.");
  }

  @GET
  @Path("{id}")
  @Produces(
    {
      "application/json"
    })
  public String find(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Worldattribute worldattribute = getEntityManager().find(Worldattribute.class, id);
    if (worldattribute == null)
    {
      throw new MudWebException(name, "Worldattribute " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminWorldattribute(worldattribute).toJson();
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminWorldattribute.GET_QUERY)).build();
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      "application/json"
    })
  public String findRange(@Context UriInfo info, @PathParam("offset") Integer offset,
                          @PathParam("pageSize") Integer pageSize)
  {
    List<String> items = getEntityManager().createNativeQuery(AdminWorldattribute.GET_QUERY)
      .setMaxResults(pageSize)
      .setFirstResult(offset)
      .getResultList();
    return "[" + String.join(",", items) + "]";
  }

  @GET
  @Path("count")
  @Produces("text/plain")
  public String count(@Context UriInfo info)
  {
    return String.valueOf(getEntityManager().createNamedQuery("Worldattribute.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
