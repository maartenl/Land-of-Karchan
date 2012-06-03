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
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.BoardMessage;
import mmud.database.entities.game.Guild;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.rest.webentities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all rest calls that are available to the world, without
 * authentication or authorization. You can find them at
 * /karchangame/resources/public.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
@Path("/public")
public class PublicBean
{
    @EJB
    private BoardBean boardBean;

    @PersistenceContext(unitName = "karchangamePU")
    private EntityManager em;


    /**
     * Returns the entity manager of Hibernate/JPA. This is defined in
     * build/web/WEB-INF/classes/META-INF/persistence.xml.
     *
     * @return EntityManager
     */
    protected EntityManager getEntityManager()
    {
        return em;
    }
    private static final Logger itsLog = LoggerFactory.getLogger(PublicBean.class);

    /**
     * Returns a Fortune 100 of players on karchan. The URL:
     * /karchangame/resources/public/fortunes. Can produce both application/xml
     * and application/json.
     */
    @GET
    @Path("fortunes")
    @Produces(
    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public List<Fortune> fortunes()
    {
        itsLog.debug("entering fortunes");
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
            itsLog.debug("throws fortunes", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        itsLog.debug("exiting fortunes");
        return res;
    }

    /**
     * Returns a List of people currently online. The URL:
     * /karchangame/resources/public/who. Can produce both application/xml and
     * application/json.
     */
    @GET
    @Path("who")
    @Produces(
    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public List<PublicPerson> who()
    {
        itsLog.debug("entering who");
        List<PublicPerson> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("User.who");
            List<User> list = query.getResultList();

            for (User person : list)
            {
                PublicPerson publicPerson = new PublicPerson();
                publicPerson.name = person.getName();
                publicPerson.title = person.getTitle();
                publicPerson.sleep = person.getSleep() ? "sleeping" : "";
                publicPerson.area = person.getRoom().getArea().getShortdescription();
                Long now = (new Date()).getTime();
                if (person.getLastlogin() == null)
                {
                    continue;
                }
                Long backThen = person.getLastlogin().getTime();
                publicPerson.min = (now - backThen) / 60000;
                publicPerson.sec = ((now - backThen) / 1000) % 60;
                res.add(publicPerson);
            }
        } catch (Exception e)
        {
            itsLog.debug("throws who", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        itsLog.debug("exiting who");
        return res;
    }

    /**
     * Returns a List of news, recent first. The URL:
     * /karchangame/resources/public/news. Can produce both application/xml and
     * application/json.
     */
    @GET
    @Path("news")
    @Produces(
    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public List<News> news()
    {
        itsLog.debug("entering news");

        List<News> res = new ArrayList<>();
        try
        {
            itsLog.debug("news: getting news");
            List<BoardMessage> list = boardBean.getNews();
            itsLog.debug("news: found " + list.size() + " entries.");
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
            itsLog.debug("news: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        itsLog.debug("exiting news");
        return res;
    }

    /**
     * Returns a List of current active and paid up deputies. The URL:
     * /karchangame/resources/public/status. Can produce both application/xml
     * and application/json.
     */
    @GET
    @Path("status")
    @Produces(
    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public List<PublicPerson> status()
    {
        itsLog.debug("entering status");

        List<PublicPerson> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("User.status");
            List<Person> list = query.getResultList();

            for (Person person : list)
            {
                PublicPerson publicPerson = new PublicPerson();
                publicPerson.name = person.getName();
                publicPerson.title = person.getTitle();
                res.add(publicPerson);
            }
        } catch (Exception e)
        {
            itsLog.debug("status: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        itsLog.debug("exiting status");
        return res;
    }

    /**
     * Returns a list of Guilds. The URL: /karchangame/resources/public/guilds.
     * Can produce both application/xml and application/json.
     */
    @GET
    @Path("guilds")
    @Produces(
    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public List<PublicGuild> guilds()
    {
        itsLog.debug("entering guilds");
        List<PublicGuild> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("Guild.findAll");
            List<Guild> list = query.getResultList();

            for (Guild guild : list)
            {
                PublicGuild newGuild = new PublicGuild();
                newGuild.guildurl = guild.getHomepage();
                newGuild.title = guild.getTitle();
                if (guild.getBoss() == null)
                {
                    itsLog.warn("guilds: no boss found for guild " + guild.getName());
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
            itsLog.debug("guilds: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        itsLog.debug("exiting guilds");
        return res;
    }
    public static final String FAMILYVALUES_CHARACTERSHEET_SQL =
            "select familyvalues.description, toname, characterinfo.name "
            + "from familyvalues, family left join characterinfo on characterinfo.name=  family.toname "
            + "where family.name = ? "
            + "and family.description = familyvalues.id";

    /**
     * Returns all the info of a character. The URL:
     * /karchangame/resources/public/charactersheets/&lt;name&gt;. Can produce
     * both application/xml and application/json.
     */
    @GET
    @Path("charactersheets/{name}")
    @Produces(
    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public PublicPerson charactersheet(@PathParam("name") String name)
    {
        itsLog.debug("entering charactersheet");
        PublicPerson res = new PublicPerson();
        try
        {
            Person person = getEntityManager().find(Person.class, name);
            if (person == null)
            {
                itsLog.debug("charactersheet not found");
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            res.name = person.getName();
            res.title = person.getTitle();
            res.sex = person.getSex().toString();
            res.description = person.getDescription();
            CharacterInfo characterInfo = getEntityManager().find(CharacterInfo.class, person.getName());
            if (characterInfo != null)
            {
                res.imageurl = characterInfo.getImageurl();
                res.homepageurl = characterInfo.getHomepageurl();
                res.dateofbirth = characterInfo.getDateofbirth();
                res.cityofbirth = characterInfo.getCityofbirth();
                res.storyline = characterInfo.getStoryline();
            }
            if (person.getGuild() != null)
            {
                res.guild = person.getGuild().getTitle();
            }

            Query query = getEntityManager().createNamedQuery("Family.findByName");
            query.setParameter("name", person.getName());
            List<Family> list = query.getResultList();
            for (Family fam : list)
            {
                itsLog.debug(fam + "");
                PublicFamily pfam = new PublicFamily();
                pfam.description = fam.getDescription().getDescription();
                pfam.toname = fam.getFamilyPK().getToname();
                res.familyvalues.add(pfam);

            }
        } catch (WebApplicationException e)
        {
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("charactersheet: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.debug(
                "exiting charactersheet");
        return res;
    }

    /**
     * Returns a List of characters and their profiles. The URL:
     * /karchangame/resources/public/charactersheets. Can produce both
     * application/xml and application/json.
     */
    @GET
    @Path("charactersheets")
    @Produces(
    {
        MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON
    })
    public List<PublicPerson> charactersheets()
    {

        itsLog.debug("entering charactersheets");

        List<PublicPerson> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("CharacterInfo.charactersheets");
            List<String> list = query.getResultList();

            for (String name : list)
            {
                PublicPerson person = new PublicPerson();
                person.name = name;
                person.url = "/karchangame/resources/public/charactersheets/" + name;
                res.add(person);
            }
        } catch (Exception e)
        {
            itsLog.debug("charactersheets: throws ", e);
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
        itsLog.debug("exiting charactersheets");
        return res;
    }
}