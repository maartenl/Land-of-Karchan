/*
 *  Copyright (C) 2012 maartenl
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.BoardMessage;
import mmud.database.entities.game.Guild;
import mmud.exceptions.MudWebException;
import mmud.rest.webentities.Fortune;
import mmud.rest.webentities.News;
import mmud.rest.webentities.PublicGuild;
import mmud.rest.webentities.PublicPerson;
import mmud.rest.webentities.admin.AdminAdmin;
import mmud.services.BoardService;
import mmud.services.IdleUsersService;
import mmud.services.PersonService;
import mmud.services.PublicService;
import mmud.services.websocket.ChatLogEndPoint;

/**
 * Contains all rest calls that are available to the world, without
 * authentication or authorization. You can find them at
 * /karchangame/resources/public.
 *
 * @author maartenl
 */
@Transactional
@Path("/public")
public class PublicRestService
{
  private static final Logger LOGGER = Logger.getLogger(PublicRestService.class.getName());

  @Inject
  private BoardService boardService;

  @Inject
  private PersonService personService;

  @Inject
  private PublicService publicService;

  @Inject
  private IdleUsersService idleUsersService;

  @PersistenceContext(unitName = "karchangamePU")
  private EntityManager em;

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

  /**
   * Returns a Fortune 100 of players on karchan. The URL:
   * /karchangame/resources/public/fortunes.
   *
   * @return a list of fortunes
   */
  @GET
  @Path("fortunes")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public List<Fortune> fortunes()
  {
    LOGGER.finer("entering fortunes");
    List<Fortune> res = new ArrayList<>();
    try
    {
      Query query = getEntityManager().createNamedQuery("User.fortunes");
      query.setMaxResults(100);
      List<Object[]> list = query.getResultList();

      for (Object[] objectarray : list)
      {
        res.add(new Fortune((String) objectarray[0], (Integer) objectarray[1]));
      }
    } catch (Exception e)
    {
      throw new MudWebException(e, Response.Status.BAD_REQUEST);
    }

    LOGGER.finer("exiting fortunes");
    return res;
  }

  /**
   * Returns a List of people currently online. The URL:
   * /karchangame/resources/public/who.
   *
   * @return List of PublicPersons.
   */
  @GET
  @Path("who")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public List<PublicPerson> who(@QueryParam("internal") String internal)
  {
    LOGGER.finer("entering who");
    if (internal != null)
    {
      ChatLogEndPoint.internalpong = internal.equals("on");
    }
    List<PublicPerson> res = new ArrayList<>();
    try
    {
      List<User> list = personService.getActivePlayers();

      for (User person : list)
      {
        if (!person.getVisible())
        {
          continue;
        }
        String idleTime = idleUsersService.getIdleTimeInMinAndSeconds(person.getName());
        PublicPerson publicPerson = new PublicPerson(person, idleTime);
        res.add(publicPerson);
      }
    } catch (Exception e)
    {
      throw new MudWebException(e, Response.Status.BAD_REQUEST);
    }
    LOGGER.finer("exiting who");
    return res;
  }

  @VisibleForTesting
  public void setIdleUsersService(IdleUsersService idleUsersService)
  {
    this.idleUsersService = idleUsersService;
  }

  /**
   * Returns a List of news, recent first. The URL:
   * /karchangame/resources/public/news.
   *
   * @return a list of news items.
   */
  @GET
  @Path("news")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public List<News> news()
  {
    LOGGER.finer("entering news");

    List<News> res = new ArrayList<>();
    try
    {
      LOGGER.finer("news: getting news");
      List<BoardMessage> list = boardService.getNews();
      LOGGER.log(Level.FINER, "news: found {0} entries.", list.size());
      for (BoardMessage message : list)
      {
        News news = new News();
        news.name = message.getPerson().getName();
        news.posttime = message.getPosttime();
        news.message = message.getMessage();
        res.add(news);
      }
    } catch (Exception e)
    {
      throw new MudWebException(e, Response.Status.BAD_REQUEST);
    }

    LOGGER.finer("exiting news");
    return res;
  }

  /**
   * Returns a List of current active and paid up deputies. The URL:
   * /karchangame/resources/public/status. Can produce application/json.
   *
   * @return a List of public deputies.
   */
  @GET
  @Path("status")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public String status()
  {
    LOGGER.finer("entering status");
    List<String> items;
    try
    {
      items = publicService.getDeputies().stream().map(person -> new AdminAdmin(person).toJson()).toList();
    } catch (Exception e)
    {
      throw new MudWebException(e, Response.Status.BAD_REQUEST);
    }

    LOGGER.finer("exiting status");
    return "[" + String.join(",", items) + "]";
  }

  /**
   * Returns a list of Guilds. The URL: /karchangame/resources/public/guilds.
   *
   * @return List of Guilds
   */
  @GET
  @Path("guilds")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public List<PublicGuild> guilds()
  {
    LOGGER.finer("entering guilds");
    List<PublicGuild> res = new ArrayList<>();
    try
    {
      TypedQuery<Guild> query = getEntityManager().createNamedQuery("Guild.findAll", Guild.class);
      List<Guild> list = query.getResultList();

      for (Guild guild : list)
      {
        PublicGuild newGuild = new PublicGuild();
        newGuild.guildurl = guild.getHomepage();
        newGuild.title = guild.getTitle();
        if (guild.getBoss() == null)
        {
          LOGGER.log(Level.INFO, "guilds: no boss found for guild {0}", guild.getName());
        } else
        {
          newGuild.bossname = guild.getBoss().getName();
        }
        newGuild.guilddescription = guild.getDescription();
        newGuild.creation = guild.getCreation();
        res.add(newGuild);
      }
    } catch (Exception e)
    {
      throw new MudWebException(e, Response.Status.BAD_REQUEST);
    }

    LOGGER.finer("exiting guilds");
    return res;
  }

  /**
   * Returns all the info of a character. The URL:
   * /karchangame/resources/public/charactersheets/&lt;name&gt;.
   *
   * @param name the name of the character/player
   * @return all person data that should be visible to the public.
   */
  @GET
  @Path("charactersheets/{name}")
  @Produces(
    {
      MediaType.APPLICATION_JSON
    })
  public PublicPerson charactersheet(@PathParam("name") String name)
  {
    return publicService.charactersheet(name);
  }

  @VisibleForTesting
  public void setPersonBean(PersonService personService)
  {
    this.personService = personService;
  }

  @VisibleForTesting
  public void setBoardBean(BoardService boardService)
  {
    this.boardService = boardService;
  }

  @VisibleForTesting
  public void setPublicService(PublicService publicService)
  {
    this.publicService = publicService;
  }
}
