/*
-----------------------------------------------------------------------
svninfo: $Id: charactersheets.php 1078 2006-01-15 09:25:36Z maartenl $
Maarten's Mud, WWW-based MUD using MYSQL
Copyright (C) 1998  Maarten van Leunen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

Maarten van Leunen
Regent Bannenbergstraat 44
5272 BR Sint Michielsgestel
Nederland
Europe
maarten_l@yahoo.com
-----------------------------------------------------------------------
*/
package mmud.webservices;

import mmud.webservices.webentities.CharacterInfo;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import mmud.webservices.webentities.DisplayResult;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import mmud.functions.CharacterSheets;
import mmud.functions.mail.Mail;
import mmud.webservices.webentities.MmudMail;

/**
 * This REST service is used for private players only. This means all methods
 * must use an Authorisation method first.
 * TODO: get a filter in here to do this automatically.
 * The REST service will be hosted at the URI path "/karchan/resources/private"
 * @author maartenl
 */
@Path("private")
@Consumes("application/json")
@Produces("application/json")
public class PrivateResource {



    private Logger itsLog = Logger.getLogger("mmudrest");

    /** Creates a new instance of PrivateResource */
    public PrivateResource() {
    }

    /**
     * Retrieves representation of an instance of mmud.webservices.PrivateResource
     * @return an instance of java.lang.String
     */
    @GET
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PublicResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    public void putJson(String content) {
    }

    /**
     * Returns a String in the DisplayResult set to "Hello, world!".
     */
    @GET
    @Path("helloworld")
    public DisplayResult helloWorld()
    {
        itsLog.entering(this.getClass().getName(), "helloWorld");
        DisplayResult res = new DisplayResult();
        res.setErrorMessage(null);
        res.setSuccess(true);
        res.setData("Hello, world!");
        itsLog.exiting(this.getClass().getName(), "helloWorld");
        return res;
    }

    /**
     * Returns a List of your mail, with a maximum of 20 mails.
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @param offset the offset, default is 0 if not provided.
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @GET
    @Path("{name}/mail")
    public List<MmudMail> listMail(@PathParam("name") String name, @QueryParam("offset") int offset, @QueryParam("lok") String lok)
    {
        itsLog.entering(this.getClass().getName(), "listMail");
        return (new Mail()).listMail(name, offset, lok);
    }

    /**
     * Returns a boolean, indicating if you have new mail. (i.e. mail since you last checked)
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @GET
    @Path("{name}/newmail")
    public Response newMail(@PathParam("name") String name, @QueryParam("lok") String lok)
    {
        itsLog.entering(this.getClass().getName(), "newMail");
        Boolean result = (new Mail()).hasNewMail(name, lok);
        if (!result)
        {
            return Response.noContent().build();
        }
        return Response.ok().build();
    }

    /**
     * Compose a new mail.
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @POST
    @Path("{name}/mail")
    public Response newMail(MmudMail newMail, @PathParam("name") String name)
    {
        (new Mail()).newMail(newMail, name);
        return Response.ok().build();
    }

    /**
     * Returns a single mail based by id.
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @param id the id of the mail to get
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @GET
    @Path("{name}/mail/{id}")
    public MmudMail getMail(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("id") long id)
    {
        return (new Mail()).getMail(name, lok, id);
    }

     /**
     * Creates an item instance (and, if required, an item definition)
     * representing
     * an in-game version of a single mail based by id.
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @param id the id of the mail to get
     * @param item the kind of item that is to be made.
     * @see PrivateResource#ITEMS
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @GET
    @Path("{name}/mail/{id}/createMailItem/{item}")
    public Response createMailItem(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("id") long id, @PathParam("item") int item)
    {
        (new Mail()).createMailItem(name, lok, id, item);
        return Response.ok().build();
    }

    /**
     * Deletes a single mail based by id.
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @param id the id of the mail to delete
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @DELETE
    @Path("{name}/mail/{id}")
    public Response deleteMail(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("id") long id)
    {
        (new Mail()).deleteMail(name, lok, id);
        return Response.ok().build();
    }

    /**
     * Adds or updates your current character info.
     * @param name the name of the user
     * @param toname the name of the user you are related to
     * @param description the description of the family relation, for example "mother".
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @PUT
    @Path("{name}/charactersheet")
    public Response updateCharacterSheet(@PathParam("name") String name, CharacterInfo cinfo)
    {
        cinfo.setName(name);
        (new CharacterSheets()).updateCharactersheet(cinfo);
        return Response.ok().build();
    }

    /**
     * Add or updates some family values from your family tree.
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @param toname the name of the user you are related to
     * @param description the description of the family relation, for example "mother".
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @PUT
    @Path("{name}/charactersheet/familyvalues/{toname}/{description}")
    public Response updateFamilyvalues(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("toname") String toname, @PathParam("description") Integer description)
    {
        (new CharacterSheets()).updateFamilyValues(name, toname, lok, description);
        return Response.ok().build();
    }


    /**
     * Deletes some family values from your family tree.
     * @param lok the hash to use for verification of the user, is the lok setting
     * in the cookie when logged onto the game.
     * @param name the name of the user
     * @param toname the name of the user you are related to
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    @DELETE
    @Path("{name}/charactersheet/familyvalues/{toname}")
    public Response deleteFamilyvalues(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("toname") String toname)
    {
        (new CharacterSheets()).deleteFamilyValues(name, toname, lok);
        return Response.ok().build();
    }





}
