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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mmud.JsonUtils;
import mmud.database.RegularExpressions;
import mmud.database.entities.characters.User;
import mmud.database.entities.web.Image;
import mmud.exceptions.MudException;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.PrivateImage;
import mmud.services.LogService;
import org.karchan.security.Roles;

/**
 * <p>
 * Allows getting pictures and creating new ones. You can find them at
 * /karchangame/resources/private/{playername}/images.</p>
 * <p>
 * The pictures themselves will be put at the url :
 * /images/players/{playername}/{path}.</p>
 *
 * @author maartenl
 */
@DeclareRoles(Roles.PLAYER)
@RolesAllowed(Roles.PLAYER)
@Transactional
@Path("/private/{name}/pictures")
public class PicturesRestService
{

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  @Context
  private SecurityContext securityContext;

  @Inject
  private LogService logService;

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

  private static final Logger LOGGER = Logger.getLogger(PicturesRestService.class.getName());

  private User getUser(String name)
  {
    User person = getEntityManager().find(User.class, name);
    if (person == null)
    {
      throw new MudWebException(name, "User was not found.", "User was not found  (" + name + ")", Response.Status.NOT_FOUND);
    }
    if (!person.isUser())
    {
      throw new MudWebException(name, "User was not a user.", "User was not a user (" + name + ")", Response.Status.BAD_REQUEST);
    }
    return person;
  }

  /**
   * Returns all images of a player. Not all fields are provided, as that would swamp the server.
   * For example, the actual images are not transferred.
   *
   * @param name the player in question
   * @return a list of images in json format
   * @throws WebApplicationException <ul>
   *                                 <li>UNAUTHORIZED, if the authorization failed.</li>
   *                                 <li>NOT_FOUND if this wikipage does not exist.</li>
   *                                 <li>BAD_REQUEST if an unexpected exception crops up.</li></ul>
   */
  @GET
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String getPictures(@PathParam("name") String name)
  {
    String userName = securityContext.getCallerPrincipal().getName();
    if (userName == null || !userName.equals(name))
    {
      throw new MudWebException(name, "Unauthorized", Response.Status.UNAUTHORIZED);
    }

    try
    {
      List<String> items = getEntityManager().createNativeQuery(PrivateImage.GET_QUERY).setParameter(1, name)
        .getResultList();
      return "[" + String.join(",", items) + "]";

    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Returns a single image and its stats of a player.
   *
   * @param name the player in question
   * @param id   the id of the picture to retrieve
   * @return an image in json format
   * @throws WebApplicationException <ul>
   *                                 <li>UNAUTHORIZED, if the authorization failed.</li>
   *                                 <li>NOT_FOUND if this wikipage does not exist.</li>
   *                                 <li>BAD_REQUEST if an unexpected exception crops up.</li></ul>
   */
  @GET
  @Path("/{id}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String getPicture(@PathParam("name") String name, @PathParam("id") Long id)
  {
    LOGGER.fine("getPicture");
    String userName = securityContext.getCallerPrincipal().getName();
    if (userName == null || !userName.equals(name))
    {
      throw new MudWebException(name, "Unauthorized", Response.Status.UNAUTHORIZED);
    }
    if (id == null)
    {
      throw new MudWebException(name, "Image id expected.", Response.Status.NOT_FOUND);
    }
    try
    {
      User owner = getUser(name);

      Image image = getEntityManager().find(Image.class, id);
      if (image.getOwner() != owner)
      {
        throw new MudWebException(name, "Not your image.", Response.Status.UNAUTHORIZED);
      }

      return new PrivateImage(image).toJson();

    } catch (MudWebException | MudException e)
    {
      //ignore
      throw e;
    } catch (Exception e)
    {
      throw new MudWebException(name, e, Response.Status.BAD_REQUEST);
    }
  }

  /**
   * Create a new picture.
   *
   * @param json   the new picture to store in json format.
   * @param player the name of the player
   * @return Response.ok if everything's okay.
   * @throws java.io.UnsupportedEncodingException in case the content cannot be
   *                                              decoded into a byte array.
   * @throws WebApplicationException              UNAUTHORIZED, if the authorization failed.
   *                                              BAD_REQUEST if an unexpected exception crops up.
   */
  @POST
  @Consumes(
    {
      MediaType.APPLICATION_JSON
    })
  public Response createPicture(@PathParam("name") String player, String json) throws UnsupportedEncodingException
  {
    PrivateImage newImage = JsonUtils.fromJson(json, PrivateImage.class);
    String name = securityContext.getCallerPrincipal().getName();
    if (name == null)
    {
      throw new MudWebException(name, "Unauthorized", Response.Status.UNAUTHORIZED);
    }
    if (!name.equals(player))
    {
      throw new MudWebException(name, "Unauthorized", Response.Status.UNAUTHORIZED);
    }

    User user = getUser(name);

    Query queryCheckExistence = getEntityManager().createNamedQuery("Image.findByOwnerAndUrl");
    queryCheckExistence.setParameter("url", newImage.url);
    queryCheckExistence.setParameter("player", user);

    List<Image> list = queryCheckExistence.getResultList();

    if (!list.isEmpty())
    {
      throw new MudWebException(name, "Image already exists.", Response.Status.FORBIDDEN);
    }

    if (newImage.url == null || !newImage.url.startsWith("/"))
    {
      throw new MudWebException(name, "URL did not start with '/'.", Response.Status.BAD_REQUEST);
    }
    if (!RegularExpressions.regExpTest(RegularExpressions.URL_REGEXP, newImage.url))
    {
      throw new MudWebException(name, RegularExpressions.URL_MESSAGE, Response.Status.BAD_REQUEST);
    }

    Image image = new Image();
    image.setUrl(newImage.url);
    image.setOwner(user);
    byte[] decodedString = Base64.getDecoder().decode(newImage.content.getBytes(StandardCharsets.UTF_8));
    if (decodedString == null || decodedString.length == 0)
    {
      throw new MudWebException(name, "No content.", Response.Status.BAD_REQUEST);
    }
    try
    {
      BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedString));
      if (bufferedImage == null)
      {
        throw new MudWebException(name, "That isn't an image. Supported formats are PNG, JPG, BMP and GIF.", Response.Status.BAD_REQUEST);
      }
    } catch (IOException ex)
    {
      throw new MudWebException(name, "Error occurred during parsing of image.", Response.Status.BAD_REQUEST);
    }
    image.setContent(decodedString);
    image.setCreateDate(LocalDateTime.now());
    image.setMimeType(newImage.mimeType);
    getEntityManager().persist(image);
    logService.writeLog(user, "Picture added with url " + image.getUrl());
    return Response.ok().build();
  }


  /**
   * Delete an existing picture. Due to the way this was setup, there is a lot of browser caching involved.
   * This means a picture is cached in your browser, therefore a deleted picture might still be available (in your
   * browser).
   *
   * @param player the name of the player
   * @return Response.ok if everything's okay.
   * @throws WebApplicationException UNAUTHORIZED, if the authorization failed.
   *                                 BAD_REQUEST if an unexpected exception crops up.
   */
  @DELETE
  @Path("{id}")
  public Response deletePicture(@PathParam("name") String player, @PathParam("id") Long id)
  {
    String name = securityContext.getCallerPrincipal().getName();
    if (name == null)
    {
      throw new MudWebException(name, "Unauthorized", Response.Status.UNAUTHORIZED);
    }
    if (!name.equals(player))
    {
      throw new MudWebException(name, "Unauthorized", Response.Status.UNAUTHORIZED);
    }
    if (id == null)
    {
      throw new MudWebException(name, "Id of image invalid.", Response.Status.NOT_FOUND);
    }

    User user = getUser(name);

    Query queryCheckExistence = getEntityManager().createNamedQuery("Image.findByOwnerAndId");
    queryCheckExistence.setParameter("id", id);
    queryCheckExistence.setParameter("player", user);

    List<Image> list = queryCheckExistence.getResultList();

    if (list.isEmpty())
    {
      throw new MudWebException(name, "Image with id " + id + " not found.", Response.Status.FORBIDDEN);
    }

    Image image = list.get(0);
    getEntityManager().remove(image);
    logService.writeLog(user, "Picture " + image.getId() + " removed. (" + image.getUrl() + ")");
    return Response.ok().build();
  }
}
