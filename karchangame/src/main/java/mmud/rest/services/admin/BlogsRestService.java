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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import mmud.database.entities.game.Admin;
import mmud.database.entities.web.Blog;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.admin.AdminBlog;
import mmud.services.LogService;

/**
 * The REST service for dealing with Blogs.
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Transactional
@Path("/administration/blogs")
public class BlogsRestService
{

  private static final Logger LOGGER = Logger.getLogger(BlogsRestService.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Context
  private SecurityContext sc;

  @Inject
  private LogService logService;

  private Admin getAdmin(String name)
  {
    Admin person = getEntityManager().find(Admin.class, name);
    if (person == null)
    {
      throw new MudWebException(name, "Admin was not found.", "Admin was not found  (" + name + ")", Response.Status.BAD_REQUEST);
    }
    return person;
  }

  @POST
  @Consumes(
    {
      "application/json"
    })
  @Produces(
    {
      "application/json"
    })
  public AdminBlog create(AdminBlog blog)
  {
    LOGGER.info("create");
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    Blog entity = new Blog();
    entity.setOwner(admin);
    entity.setCreateDate(LocalDateTime.now());
    entity.setModifiedDate(entity.getCreateDate());
    entity.setTitle(blog.title);
    entity.setUrlTitle(blog.urlTitle);
    entity.setContents(blog.contents);
    ValidationUtils.checkValidation(name, entity);
    getEntityManager().persist(entity);
    getEntityManager().flush();
    logService.writeDeputyLog(getAdmin(name), "Blog " + entity.getUrlTitle() + " created.");
    return new AdminBlog(entity);
  }

  @PUT
  @Path("{id}")
  @Consumes(
    {
      "application/json"
    })
  public void edit(@PathParam("id") Long id, AdminBlog blog)
  {
    LOGGER.info("edit");
    final String name = sc.getUserPrincipal().getName();

    Blog entity = em.find(Blog.class, id);

    if (entity == null)
    {
      throw new MudWebException(name, "Blog " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, entity);
    entity.setContents(blog.contents);
    entity.setModifiedDate(LocalDateTime.now());
    entity.setTitle(blog.title);
    entity.setUrlTitle(blog.urlTitle);
    ValidationUtils.checkValidation(name, entity);
    logService.writeDeputyLog(getAdmin(name), "Blog " + entity.getUrlTitle() + " edited.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Long id)
  {
    final String name = sc.getUserPrincipal().getName();
    final Blog blog = em.find(Blog.class, id);
    if (blog == null)
    {
      throw new MudWebException(name, "Blog " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, blog);
    logService.writeDeputyLog(getAdmin(name), "Blog " + blog.getUrlTitle() + " removed.");
    em.remove(blog);
  }

  @GET
  @Path("{id}")
  @Produces(
    {
      "application/json"
    })
  public AdminBlog find(@PathParam("id") Long id)
  {
    final String name = sc.getUserPrincipal().getName();
    Blog entity = em.find(Blog.class, id);
    if (entity == null)
    {
      throw new MudWebException(name, "Blog " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, entity);
    return new AdminBlog(entity);
  }

  private EntityManager getEntityManager()
  {
    return em;
  }

  @GET
  @Produces(
    {
      "application/json"
    })
  public List<AdminBlog> findAll()
  {
    final String name = sc.getUserPrincipal().getName();
    Admin admin = getEntityManager().find(Admin.class, name);
    if (admin == null)
    {
      throw new MudWebException(name, "Admin " + name + " not found.", Response.Status.NOT_FOUND);
    }
    TypedQuery<Blog> blogs = getEntityManager().createNamedQuery("Blog.find", Blog.class);
    blogs.setParameter("user", admin);
    return blogs.getResultList().stream().map(x -> new AdminBlog(x)).collect(Collectors.toList());
  }

}
