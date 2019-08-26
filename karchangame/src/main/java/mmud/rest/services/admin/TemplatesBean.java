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
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Admin;
import mmud.database.entities.web.HistoricTemplate;
import mmud.database.entities.web.Template;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminTemplate;

/**
 * The REST service for dealing with Templates.
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/templates")
public class TemplatesBean
{

  private static final Logger LOGGER = Logger.getLogger(TemplatesBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

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

    Template entity = getEntityManager().find(Template.class, id);

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
    logBean.writeDeputyLog(getAdmin(name), "Template " + id + " updated.");
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
    Template entity = getEntityManager().find(Template.class, id);
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
    TypedQuery<Template> templates = getEntityManager().createNamedQuery("Template.findAll", Template.class);
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
