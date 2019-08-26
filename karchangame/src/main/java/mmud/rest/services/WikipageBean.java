/*
 *  Copyright (C) 2019 maartenl
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
package mmud.rest.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.Constants;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.PrivateWikipage;
import org.karchan.security.Roles;
import mmud.database.entities.web.Wikipage;
import mmud.database.entities.web.WikipageHistory;

/**
 * Allows getting wikipages, creating new ones and editing them. You can find
 * them at /karchangame/resources/wikipages.
 *
 * @author maartenl
 */
@DeclareRoles(Roles.PLAYER)
@RolesAllowed(Roles.PLAYER)
@Stateless
@LocalBean
@Path("/wikipages")
public class WikipageBean
{

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private SecurityContext securityContext;

  /**
   * Returns the entity manager of JPA. This is defined in
   * build/web/WEB-INF/classes/META-INF/persistence.xml.
   *
   * @return EntityManager
   */
  protected EntityManager getEntityManager()
  {
    return em;
  }
  private static final Logger LOGGER = Logger.getLogger(WikipageBean.class.getName());

  /**
   * Returns a wikipage with that specific title.
   *
   * @param title the (unique) title of the wikipage
   * @return a wikipage in json format
   * @throws WebApplicationException <ul>
   * <li>UNAUTHORIZED, if the authorization failed.</li>
   * <li>NOT_FOUND if this wikipage does not exist.</li>
   * <li>BAD_REQUEST if an unexpected exception crops up.</li></ul>
   */
  @GET
  @Path("{title}")
  @Produces(
          {
            MediaType.APPLICATION_JSON
          })
  public String getWikipage(@PathParam("title") String title)
  {
    try
    {
      String namedQuery = securityContext.isCallerInRole(Roles.DEPUTY)
              ? "Wikipage.findByTitleAuthorized"
              : "Wikipage.findByTitle";
      Query query = getEntityManager().createNamedQuery(namedQuery);
      query.setParameter("title", title);

      List<Wikipage> list = query.getResultList();

      if (list.isEmpty())
      {
        throw new MudWebException(null, "Wikipage was not found.", Response.Status.NOT_FOUND);
      }

      return new PrivateWikipage(list.get(0)).toJson();

    } catch (MudWebException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(title, e, Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Create a new wikipage.
   *
   * @param json the new wikipage to store in json format.
   * @return Response.ok if everything's okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorization failed.
   * BAD_REQUEST if an unexpected exception crops up.
   */
  @POST
  @Consumes(
          {
            MediaType.APPLICATION_JSON
          })
  public Response createWikipage(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    PrivateWikipage newWikipage = jsonb.fromJson(json, PrivateWikipage.class);
    Query queryCheckExistence = getEntityManager().createNamedQuery("Wikipage.checkExistenceOfWikipage");
    queryCheckExistence.setParameter("title", newWikipage.title);

    List<Wikipage> list = queryCheckExistence.getResultList();
    final String name = securityContext.getCallerPrincipal().getName();

    if (!list.isEmpty())
    {
      throw new MudWebException(name, "Wikipage already exists.", Response.Status.FORBIDDEN);
    }

    if (!securityContext.isCallerInRole(Roles.DEPUTY) && newWikipage.administration)
    {
      throw new MudWebException(name, "Creation of administrative wikipages not allowed.", Response.Status.UNAUTHORIZED);
    }

    Wikipage parent = null;

    if (newWikipage.parentTitle != null && !"".equals(newWikipage.parentTitle.trim()))
    {
      Query query = getEntityManager().createNamedQuery("Wikipage.findByTitleAuthorized");
      query.setParameter("title", newWikipage.parentTitle);

      List<Wikipage> parents = query.getResultList();

      if (parents.isEmpty())
      {
        throw new MudWebException(name, "Parent wikipage not found.", Response.Status.NOT_FOUND);
      }
      parent = parents.get(0);

      if (parent.getAdministration() != newWikipage.administration)
      {
        throw new MudWebException(name, "Parent wikipage does not have the same rights.", Response.Status.FORBIDDEN);
      }
    }
    if (!Constants.regExpTest(Constants.COMMENTS_REGEXP, newWikipage.comment))
    {
      throw new MudWebException(name, Constants.COMMENTS_MESSAGE, Response.Status.BAD_REQUEST);
    }
    if (!Constants.regExpTest(Constants.WIKIPAGE_TITLE_REGEXP, newWikipage.title))
    {
      throw new MudWebException(name, Constants.WIKIPAGE_TITLE_MESSAGE, Response.Status.BAD_REQUEST);
    }

    Wikipage wikipage = new Wikipage();
    wikipage.setTitle(newWikipage.title);
    wikipage.setSummary(newWikipage.summary);
    wikipage.setContent(newWikipage.content);
    wikipage.setCreateDate(LocalDateTime.now());
    wikipage.setModifiedDate(LocalDateTime.now());
    wikipage.setVersion("1.0");
    wikipage.setName(name);
    wikipage.setParentTitle(parent);
    wikipage.setAdministration(newWikipage.administration);
    wikipage.setComment(newWikipage.comment);
    wikipage.setOrdering(newWikipage.ordering);
    getEntityManager().persist(wikipage);

    return Response.ok().build();
  }

  /**
   * Adds or updates a wikipage.
   *
   * @param title the (unique) title of the wikpage, should not exist yet.
   * @param json the updated wikipage to store in json format.
   * @return Response.ok if everything is okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
   * BAD_REQUEST if an unexpected exception crops up.
   */
  @PUT
  @Path("{title}")
  @Consumes(
          {
            MediaType.APPLICATION_JSON
          })
  public Response updateWikipage(@PathParam("title") String title, String json)
  {
    String name = securityContext.getCallerPrincipal().getName();
    Jsonb jsonb = JsonbBuilder.create();
    PrivateWikipage privateWikipage = jsonb.fromJson(json, PrivateWikipage.class);
    try
    {
      String namedQuery = securityContext.isCallerInRole(Roles.DEPUTY)
              ? "Wikipage.findByTitleAuthorized"
              : "Wikipage.findByTitle";
      Query queryCheckExistence = getEntityManager().createNamedQuery(namedQuery);
      queryCheckExistence.setParameter("title", title);

      List<Wikipage> list = queryCheckExistence.getResultList();

      if (list.isEmpty())
      {
        throw new MudWebException(name, "Wikipage not found.", Response.Status.NOT_FOUND);
      }
      Wikipage wikipage = list.get(0);

      if (!securityContext.isCallerInRole(Roles.DEPUTY) && wikipage.getAdministration())
      {
        throw new MudWebException(null, "Editing of administrative wikipages not allowed.", Response.Status.UNAUTHORIZED);
      }

      Wikipage parent = null;

      if (privateWikipage.parentTitle != null && !"".equals(privateWikipage.parentTitle.trim()))
      {
        Query query = getEntityManager().createNamedQuery("Wikipage.findByTitleAuthorized");
        query.setParameter("title", privateWikipage.parentTitle);

        List<Wikipage> parents = query.getResultList();

        if (parents.isEmpty())
        {
          throw new MudWebException(name, "Parent wikipage not found.", Response.Status.NOT_FOUND);
        }
        parent = parents.get(0);
        if (parent.getAdministration() != wikipage.getAdministration())
        {
          throw new MudWebException(null, "Parent wikipage does not have the same rights.", Response.Status.FORBIDDEN);
        }
        if (!Constants.regExpTest(Constants.WIKIPAGE_TITLE_REGEXP, privateWikipage.parentTitle))
        {
          throw new MudWebException(name, Constants.WIKIPAGE_TITLE_MESSAGE, Response.Status.BAD_REQUEST);
        }
      }
      if (!Constants.regExpTest(Constants.COMMENTS_REGEXP, privateWikipage.comment))
      {
        throw new MudWebException(name, Constants.COMMENTS_MESSAGE, Response.Status.BAD_REQUEST);
      }
      if (!Constants.regExpTest(Constants.WIKIPAGE_TITLE_REGEXP, privateWikipage.title))
      {
        throw new MudWebException(name, Constants.WIKIPAGE_TITLE_MESSAGE, Response.Status.BAD_REQUEST);
      }

      WikipageHistory historie = new WikipageHistory(wikipage);
      getEntityManager().persist(historie);
      wikipage.setContent(privateWikipage.content);
      // createDate not changed, obviously.
      // title not changed, obviously.
      // administration not changed, obviously.
      wikipage.setModifiedDate(LocalDateTime.now());
      wikipage.setName(name);
      wikipage.setSummary(privateWikipage.summary);
      wikipage.increaseVersion();
      wikipage.setComment(privateWikipage.comment);
      wikipage.setOrdering(privateWikipage.ordering);
      wikipage.setParentTitle(parent);

    } catch (MudWebException e)
    {
      //ignore and rethrow
      throw e;
    } catch (Exception e)
    {
      // anything else, throw a BAD REQUEST
      throw new MudWebException(name, e, Response.Status.BAD_REQUEST);
    }
    return Response.ok().build();
  }
}
