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
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Mail;
import mmud.database.entities.game.Person;
import mmud.database.entities.web.CharacterInfo;
import mmud.rest.webentities.PrivateMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains all rest calls that are available to a specific character, with
 * authentication and authorization. You can find them at
 * /karchangame/resources/private.
 *
 * @author maartenl
 */
@Stateless
@LocalBean
@Path("/private")
public class PrivateBean
{

    /**
     * Indicates that only 20 mails may be retrieved per time (basically a
     * page).
     */
    public static final int MAX_MAILS = 20;
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
    private static final Logger itsLog = LoggerFactory.getLogger(PrivateBean.class);

    private Person authenticate(String name, String lok)
    {
        Person person = getEntityManager().find(Person.class, name);
        if (person == null)
        {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
        if (!person.verifySessionPassword(lok))
        {
            throw new WebApplicationException(Status.UNAUTHORIZED);
        }
        return person;
    }

    /**
     * Returns a List of your mail, with a maximum of 20 mails and an offset for
     * paging.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param offset the offset, default is 0 if not provided.
     * @throws WebApplicationException <ul><li>UNAUTHORIZED, if the
     * authorisation failed. </li><li>BAD_REQUEST if an unexpected exception
     * crops up.</li></ul>
     */
    @GET
    @Path("{name}/mail")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public List<PrivateMail> listMail(@PathParam("name") String name, @QueryParam("offset") Integer offset, @QueryParam("lok") String lok)
    {
        itsLog.debug("entering listMail");
        Person person = authenticate(name, lok);
        List<PrivateMail> res = new ArrayList<>();
        try
        {
            Query query = getEntityManager().createNamedQuery("Mail.listmail");
            query.setParameter("name", person);
            query.setMaxResults(MAX_MAILS);
            if (offset != null)
            {
                query.setFirstResult(offset);
            }

            List<Mail> list = query.getResultList();
            for (Mail mail : list)
            {

                PrivateMail pmail = new PrivateMail();
                pmail.name = mail.getName().getName();
                pmail.toname = mail.getToname().getName();
                pmail.subject = mail.getSubject();
                pmail.body = mail.getBody();
                pmail.id = mail.getId();
                pmail.haveread = mail.getHaveread();
                pmail.newmail = mail.getNewmail();
                pmail.whensent = mail.getWhensent();
                pmail.deleted = false;
                pmail.item_id = mail.getItemId() != null ? mail.getItemId().getId() : null;
                res.add(pmail);
            }

            // turn off the "newmail" sign.
            query = getEntityManager().createNamedQuery("Mail.nonewmail");
            query.setParameter("name", person);
            query.executeUpdate();
        } catch (WebApplicationException e)
        {
            //ignore
            throw e;
        } catch (Exception e)
        {
            itsLog.debug("listMail: throws ", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return res;
    }

    /**
     * Returns a boolean, indicating if you have new mail. (i.e. mail since you
     * last checked)
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @GET
    @Path("{name}/newmail")
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public Response newMail(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.debug("entering newMail");
        Person person = authenticate(name, lok);
        boolean result = false;
        try
        {
            Query query = getEntityManager().createNamedQuery("Mail.hasnewmail");
            query.setParameter("name", person);

            Long count = (Long) query.getSingleResult();
            if (count > 0)
            {
                result = true;
            }
        } catch (Exception e)
        {
            itsLog.debug("newMail: throws ", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        if (!result)
        {
            return Response.noContent().build();
        }
        return Response.ok().build();
    }

    /**
     * Compose a new mail.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @POST
    @Path("{name}/mail")
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public Response newMail(PrivateMail newMail, @PathParam("name") String name)
    {
        // (new Mail()).newMail(newMail, name);
        return Response.ok().build();
    }

    /**
     * Returns a single mail based by id.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param id the id of the mail to get
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @GET
    @Path("{name}/mail/{id}")
    @Produces(
    {
        "application/xml", "application/json"
    })
    public PrivateMail getMail(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("id") long id)
    {
        return new PrivateMail();//(new Mail()).getMail(name, lok, id);
    }

    /**
     * Creates an item instance (and, if required, an item definition)
     * representing an in-game version of a single mail based by id.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param id the id of the mail to get
     * @param item the kind of item that is to be made.
     * @see PrivateResource#ITEMS
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @GET
    @Path("{name}/mail/{id}/createMailItem/{item}")
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public Response createMailItem(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("id") long id, @PathParam("item") int item)
    {
        //(new Mail()).createMailItem(name, lok, id, item);
        return Response.ok().build();
    }

    /**
     * Deletes a single mail based by id.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param id the id of the mail to delete
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @DELETE
    @Path("{name}/mail/{id}")
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public Response deleteMail(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("id") long id)
    {
        //(new Mail()).deleteMail(name, lok, id);
        return Response.ok().build();
    }

    /**
     * Adds or updates your current character info.
     *
     * @param name the name of the user
     * @param toname the name of the user you are related to
     * @param description the description of the family relation, for example
     * "mother".
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @PUT
    @Path("{name}/charactersheet")
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public Response updateCharacterSheet(@PathParam("name") String name, CharacterInfo cinfo)
    {
        //cinfo.setName(name);
        //(new CharacterSheets()).updateCharactersheet(cinfo);
        return Response.ok().build();
    }

    /**
     * Add or updates some family values from your family tree.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param toname the name of the user you are related to
     * @param description the description of the family relation, for example
     * "mother".
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @PUT
    @Path("{name}/charactersheet/familyvalues/{toname}/{description}")
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public Response updateFamilyvalues(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("toname") String toname, @PathParam("description") Integer description)
    {
        //(new CharacterSheets()).updateFamilyValues(name, toname, lok, description);
        return Response.ok().build();
    }

    /**
     * Deletes some family values from your family tree.
     *
     * @param lok the hash to use for verification of the user, is the lok
     * setting in the cookie when logged onto the game.
     * @param name the name of the user
     * @param toname the name of the user you are related to
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation
     * failed. BAD_REQUEST if an unexpected exception crops up.
     */
    @DELETE
    @Path("{name}/charactersheet/familyvalues/{toname}")
    @Consumes(
    {
        "application/xml", "application/json"
    })
    public Response deleteFamilyvalues(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("toname") String toname)
    {
        //(new CharacterSheets()).deleteFamilyValues(name, toname, lok);
        return Response.ok().build();
    }
}