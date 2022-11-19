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

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Area;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminArea;
import mmud.services.LogService;

/**
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/areas")
public class AreaRestService // extends AbstractFacade<Area>
{

  private static final Logger LOGGER = Logger.getLogger(AreaRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogService logService;

  @Context
  private SecurityContext sc;

  @POST
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public void create(String json)
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
    logService.writeDeputyLog(admin, "New area '" + area.getArea() + "' created.");
    getEntityManager().persist(area);
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public void edit(@PathParam("id") String id, String json)
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
    logService.writeDeputyLog(admin, "Area '" + area.getArea() + "' updated.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") String id)
  {
    final String name = sc.getUserPrincipal().getName();
    final Area area = getEntityManager().find(Area.class, id);
    if (area == null)
    {
      throw new MudWebException(name, "Area " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, area);
    getEntityManager().remove(area);
    logService.writeDeputyLog(admin, "Area '" + area.getArea() + "' deleted.");
  }

  @GET
  @Path("{id}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String find(@PathParam("id") String id)
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
      MediaType.APPLICATION_JSON
    })
  public Response findAll(@Context UriInfo info)
  {
    return Response.ok(StreamerHelper.getStream(getEntityManager(), AdminArea.GET_QUERY)).build();
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
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
