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

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.game.Admin;
import mmud.database.entities.web.HistoricTemplate;
import mmud.database.entities.web.HtmlTemplate;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminTemplate;
import mmud.services.LogService;

/**
 * The REST service for dealing with Templates.
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")

@Path("/administration/templates")
public class TemplatesRestService
{

  private static final Logger LOGGER = Logger.getLogger(TemplatesRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogService logService;

  @PUT
  @Path("{id}")
  @Consumes(
          {
            "application/json"
          })
  public void edit(@PathParam("id") Long id, AdminTemplate template, @Context SecurityContext sc)
  {
    LOGGER.info("edit");
    final String name = sc.getUserPrincipal().getName();

    HtmlTemplate entity = getEntityManager().find(HtmlTemplate.class, id);

    if (entity == null)
    {
      throw new MudWebException(name, "Template " + id + " not found.", Response.Status.NOT_FOUND);
    }
    HistoricTemplate hisTemplate = new HistoricTemplate(entity);
    ValidationUtils.checkValidation(name, hisTemplate);
    getEntityManager().persist(hisTemplate);
    entity.setContent(template.content);
    entity.setModified(LocalDateTime.now());
    entity.setName(template.name);
    entity.setEditor(name);
    entity.setComment(template.comment);
    entity.increaseVersion();
    ValidationUtils.checkValidation(name, entity);
    logService.writeDeputyLog(getAdmin(name), "Template " + id + " updated.");
  }

  @GET
  @Path("{id}")
  @Produces(
          {
            "application/json"
          })
  public AdminTemplate find(@PathParam("id") Long id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    HtmlTemplate entity = getEntityManager().find(HtmlTemplate.class, id);
    if (entity == null)
    {
      throw new MudWebException(name, "Template " + id + " not found.", Response.Status.NOT_FOUND);
    }
    return new AdminTemplate(entity);
  }

  @GET
  @Produces(
          {
            "application/json"
          })
  public List<AdminTemplate> findAll(@Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    if (admin == null)
    {
      throw new MudWebException(name, "Admin " + name + " not found.", Response.Status.NOT_FOUND);
    }
    TypedQuery<HtmlTemplate> templates = getEntityManager().createNamedQuery("HtmlTemplate.findAll", HtmlTemplate.class);
    return templates.getResultList().stream().map(x -> new AdminTemplate(x)).collect(Collectors.toList());
  }

  protected EntityManager getEntityManager()
  {
    return em;
  }

  private Admin getAdmin(String name)
  {
    Admin person = getEntityManager().find(Admin.class, name);
    if (person == null)
    {
      throw new MudWebException(name, "Admin was not found.", "Admin was not found  (" + name + ")", Response.Status.BAD_REQUEST);
    }
    return person;
  }

}
