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
import java.util.stream.Collectors;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.json.bind.JsonbBuilder;
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
import mmud.database.entities.game.Method;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminMethod;

/**
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@LocalBean
@Path("/administration/methods")
public class MethodsBean //implements AdminRestService<String>
{

  private static final Logger LOGGER = Logger.getLogger(MethodsBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @POST
  @Consumes(
          {
            "application/json"
          })

  public void create(String json, @Context SecurityContext sc)
  {
    LOGGER.info("create");
    AdminMethod adminMethod = AdminMethod.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);

    Method method = new Method();
    method.setName(adminMethod.name);
    method.setSrc(adminMethod.src);
    method.setCreation(LocalDateTime.now());
    method.setOwner(admin);
    LOGGER.info(method.toString());
    ValidationUtils.checkValidation(name, method);
    getEntityManager().persist(method);
  }

  @PUT
  @Path("{id}")
  @Consumes(
          {
            "application/json"
          })

  public void edit(@PathParam("id") String id, String json, @Context SecurityContext sc)
  {
    LOGGER.info("edit");
    AdminMethod adminMethod = AdminMethod.fromJson(json);
    final String name = sc.getUserPrincipal().getName();
    if (!id.equals(adminMethod.name))
    {
      throw new MudWebException(name, "Method names do not match.", Response.Status.BAD_REQUEST);
    }
    Method method = getEntityManager().find(Method.class, adminMethod.name);

    if (method == null)
    {
      throw new MudWebException(name, "Method " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, method);
    method.setSrc(adminMethod.src);
    method.setOwner(admin);
    ValidationUtils.checkValidation(name, method);
  }

  @DELETE
  @Path("{id}")

  public void remove(@PathParam("id") String id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Method method = getEntityManager().find(Method.class, id);
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, method);
    getEntityManager().remove(method);
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
    Method method = getEntityManager().find(Method.class, id);
    if (method == null)
    {
      throw new MudWebException(name, "Method " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminMethod(method).toJson();
  }

  @GET

  @Produces(
          {
            "application/json"
          })
  public String findAll()
  {
    LOGGER.info("findAll");
    final List<Method> methods = getEntityManager().createNamedQuery("Method.findAll").getResultList();
    List<AdminMethod> adminMethods = methods.stream().map(method -> new AdminMethod(method)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminMethods);
  }

  @GET
  @Path("{offset}/{pageSize}")
  @Produces(
          {
            "application/json"
          })

  public String findRange(@PathParam("offset") Integer offset,
          @PathParam("pageSize") Integer pageSize
  )
  {
    final List<Method> methods = getEntityManager().createNamedQuery("Method.findAll")
            .setMaxResults(pageSize)
            .setFirstResult(offset)
            .getResultList();
    List<AdminMethod> adminMethods = methods.stream().map(method -> new AdminMethod(method)).collect(Collectors.toList());
    return JsonbBuilder.create().toJson(adminMethods);
  }

  @GET
  @Path("count")
  @Produces("text/plain")

  public String count()
  {
    return String.valueOf(getEntityManager().createNamedQuery("Method.countAll").getSingleResult());
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

}
