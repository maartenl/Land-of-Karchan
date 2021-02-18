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
import mmud.database.entities.game.Area;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminArea;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/areas")
public class AreaBean // extends AbstractFacade<Area>
{

  private static final Logger LOGGER = Logger.getLogger(AreaBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  @POST
  @Consumes(
    {
      "application/json"
    })
  public void create(String json, @Context SecurityContext sc)
  {
    AdminArea adminArea = AdminArea.fromJson(json);

    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    Area area = new Area();
    area.setArea(adminArea.area);
    area.setDescription(adminArea.description);
    area.setShortdescription(adminArea.shortdesc);
    area.setCreation(LocalDateTime.now());
    area.setOwner(admin);
    ValidationUtils.checkValidation(name, area);
    logBean.writeDeputyLog(admin, "New area '" + area.getArea() + "' created.");
    getEntityManager().persist(area);
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/xml", "application/json"
    })
  public void edit(@PathParam("id") String id, String json, @Context SecurityContext sc)
  {
    AdminArea adminArea = AdminArea.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminArea.area))
    {
      throw new MudWebException(name, "Area names do not match.", Response.Status.BAD_REQUEST);
    }
    Area area = getEntityManager().find(Area.class, adminArea.area);

    if (area == null)
    {
      throw new MudWebException(name, id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, area);
    area.setDescription(adminArea.description);
    area.setShortdescription(adminArea.shortdesc);
    area.setOwner(OwnerHelper.getNewOwner(adminArea.owner, admin, getEntityManager()));
    ValidationUtils.checkValidation(name, area);
    logBean.writeDeputyLog(admin, "Area '" + area.getArea() + "' updated.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Area area = getEntityManager().find(Area.class, id);
    if (area == null)
    {
      throw new MudWebException(name, "Area " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, area);
    getEntityManager().remove(area);
    logBean.writeDeputyLog(admin, "Area '" + area.getArea() + "' deleted.");
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
    final Area area = getEntityManager().find(Area.class, id);
    if (area == null)
    {
      throw new MudWebException(name, "Area " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminArea(area).toJson();
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminArea.GET_QUERY)).build();
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
    List<String> items = getEntityManager().createNativeQuery(AdminArea.GET_QUERY)
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
    return String.valueOf(getEntityManager().createNamedQuery("Area.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
