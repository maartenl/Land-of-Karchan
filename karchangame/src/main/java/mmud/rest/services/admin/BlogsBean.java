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
import mmud.database.entities.web.Blog;
import mmud.exceptions.MudWebException;
import mmud.rest.services.LogBean;
import mmud.rest.webentities.admin.AdminBlog;

/**
 * The REST service for dealing with Blogs.
 *
 * @author maartenl
 */
@DeclareRoles("deputy")
@RolesAllowed("deputy")
@Stateless
@Path("/administration/blogs")
public class BlogsBean extends AbstractFacade<Blog>
{

  private static final Logger LOGGER = Logger.getLogger(BlogsBean.class.getName());

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private LogBean logBean;

  public BlogsBean()
  {
    super(Blog.class);
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

  @POST
  @Consumes(
          {
            "application/json"
          })
  @Produces(
          {
            "application/json"
          })
  public AdminBlog create(AdminBlog blog, @Context SecurityContext sc)
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
    checkValidation(name, entity);
    getEntityManager().persist(entity);
    getEntityManager().flush();
    logBean.writeDeputyLog(getAdmin(name), "Blog " + entity.getUrlTitle() + " created.");
    return new AdminBlog(entity);
  }

  @PUT
  @Path("{id}")
  @Consumes(
          {
            "application/json"
          })
  public void edit(@PathParam("id") Long id, AdminBlog blog, @Context SecurityContext sc)
  {
    LOGGER.info("edit");
    final String name = sc.getUserPrincipal().getName();

    Blog entity = super.find(id);

    if (entity == null)
    {
      throw new MudWebException(name, "Blog " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, entity);
    entity.setContents(blog.contents);
    entity.setModifiedDate(LocalDateTime.now());
    entity.setTitle(blog.title);
    entity.setUrlTitle(blog.urlTitle);
    checkValidation(name, entity);
    logBean.writeDeputyLog(getAdmin(name), "Blog " + entity.getUrlTitle() + " edited.");
  }

  @DELETE
  @Path("{id}")
  public void remove(@PathParam("id") Long id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    final Blog blog = super.find(id);
    if (blog == null)
    {
      throw new MudWebException(name, "Blog " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, blog);
    logBean.writeDeputyLog(getAdmin(name), "Blog " + blog.getUrlTitle() + " removed.");
    remove(blog);
  }

  @GET
  @Path("{id}")
  @Produces(
          {
            "application/json"
          })
  public AdminBlog find(@PathParam("id") Long id, @Context SecurityContext sc)
  {
    final String name = sc.getUserPrincipal().getName();
    Blog entity = super.find(id);
    if (entity == null)
    {
      throw new MudWebException(name, "Blog " + id + " not found.", Response.Status.NOT_FOUND);
    }
    Admin admin = (new OwnerHelper(getEntityManager())).authorize(name, entity);
    return new AdminBlog(entity);
  }

  @GET
  @Produces(
          {
            "application/json"
          })
  public List<AdminBlog> findAll(@Context SecurityContext sc)
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

  @Override
  protected EntityManager getEntityManager()
  {
    return em;
  }

  @Override
  public void create(Blog entity, SecurityContext sc)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
