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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.Constants;
import mmud.database.RegularExpressions;
import mmud.database.entities.characters.User;
import mmud.database.entities.web.Image;
import mmud.exceptions.MudWebException;
import org.karchan.security.Roles;
import mmud.rest.webentities.PrivateImage;

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
@Stateless
@LocalBean
@Path("/private/{name}/pictures")
public class PicturesBean
{

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

  @Inject
  private SecurityContext securityContext;

  @Inject
  private LogBean logBean;

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

  private static final Logger LOGGER = Logger.getLogger(PicturesBean.class.getName());

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
   * Returns all images of a player.
   *
   * @param name the player in question
   * @return a list of images in json format
   * @throws WebApplicationException <ul>
   * <li>UNAUTHORIZED, if the authorization failed.</li>
   * <li>NOT_FOUND if this wikipage does not exist.</li>
   * <li>BAD_REQUEST if an unexpected exception crops up.</li></ul>
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
      User owner = getUser(name);

      Query query = getEntityManager().createNamedQuery("Image.findByOwner");
      query.setParameter("player", owner);

      List<Image> list = query.getResultList();

      List<PrivateImage> result = list.stream().map(f -> new PrivateImage(f)).collect(Collectors.toList());

      return JsonbBuilder.create().toJson(result);

    } catch (WebApplicationException e)
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
   * @param json the new picture to store in json format.
   * @param player the name of the player
   * @return Response.ok if everything's okay.
   * @throws java.io.UnsupportedEncodingException in case the content cannot be
   * decoded into a byte array.
   * @throws WebApplicationException UNAUTHORIZED, if the authorization failed.
   * BAD_REQUEST if an unexpected exception crops up.
   */
  @POST
  @Consumes(
          {
            MediaType.APPLICATION_JSON
          })
  public Response createPicture(@PathParam("name") String player, String json) throws UnsupportedEncodingException
  {
    Jsonb jsonb = JsonbBuilder.create();
    PrivateImage newImage = jsonb.fromJson(json, PrivateImage.class);

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
    byte[] decodedString = Base64.getDecoder().decode(newImage.content.getBytes("UTF-8"));
    if (decodedString == null)
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
    if (decodedString == null)
    {
      throw new MudWebException(name, "No content.", Response.Status.NO_CONTENT);
    }
    image.setContent(decodedString);
    image.setCreateDate(LocalDateTime.now());
    image.setMimeType(newImage.mimeType);
    getEntityManager().persist(image);
    logBean.writeLog(user, "Picture added with url " + image.getUrl());
    return Response.ok().build();
  }
}
