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

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import mmud.webservices.webentities.DisplayResult;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
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

    /**
     * Contains the item ids of the different items that represent letters/mail.
     * The readdescription of said letters looks a little like the following:
     * <p>"stuffletterhead
     * letterbody
     * letterfooter"</p>
     * That way, the letterhead, letterbody and letterfooter are automatically
     * replaced.
     */
    public static final int[] ITEMS = {
        8008,
        8009,
        8010,
        8011,
        8012,
        8013,
        8014,
        8015
    };


    public static final String LISTMAIL_SQL = "select mm_mailtable.* from mm_mailtable, mm_usertable where toname = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and mm_mailtable.deleted <> 1 order by id desc limit ?, 20";

    public static final String GETMAIL_SQL = "select mm_mailtable.* from mm_mailtable, mm_usertable where toname = ? and mm_mailtable.id = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and mm_mailtable.deleted <> 1 ";

    public static final String DELETEMAIL_SQL = "update mm_mailtable, mm_usertable set deleted = 1 where toname = ? and mm_mailtable.id = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and mm_mailtable.deleted <> 1";

    public static final String NEWMAIL_SQL = "insert into mm_mailtable (name, toname, subject, body, whensent, newmail, haveread) values(?, ?, ?, ?, now(), 1, 0)";

    public static final String AUTHORIZE_SQL = "select 1 from mm_usertable where name = ? and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and (god = 0 or god = 1)";

    public static final String FINDUSER_SQL = "select 1 from mm_usertable where name = ? and (god = 0 or god = 1)";

    public static final String GETMAXIDITEMDEF_SQL = "select (max(id)) as max from mm_items";

    public static final String SETIDITEMDEF_IN_MAIL_SQL = "update mm_mailtable set item_id = ? where id = ?";

    public static final String CHECKITEMDEF_SQL = "select 1 from mm_items where id = ?";

    public static final String CREATEMAILITEM_IN_INVENTORY_SQL = "insert into mm_charitemtable (id, belongsto) values(?, ?)";
    public static final String CREATEMAILITEM_INSTANCE_SQL = "insert into mm_itemtable (itemid, owner) values(?, \"Karn\")";

    public static final String CREATEMAILITEMDEF_SQL = "insert into mm_items " +
            "(id, name, adject1, adject2, adject3, getable, dropable, visible, description, readdescr, copper, owner, notes) " +
            "select ?, name, adject1, adject2, adject3, getable, dropable, visible, description, " +
            "replace(replace(replace(readdescr, \"letterhead\", ?), \"letterbody\", ?), \"letterfooter\", ?), " +
            "copper, owner, notes " +
            "from mm_items where id = ?";

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
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        List<MmudMail> res = new ArrayList<MmudMail>();
        try
        {

            con = Utils.getDatabaseConnection();

            authentication(con, name, lok);

            stmt=con.prepareStatement(LISTMAIL_SQL);
            stmt.setString(1, name);
            stmt.setString(2, lok);
            stmt.setInt(3, offset);
            rst=stmt.executeQuery();
            while(rst.next())
            {
                MmudMail mail = new MmudMail(null, rst.getString("toname"), rst.getString("name"), rst.getString("subject"), rst.getString("body"),
                        rst.getLong("id"), rst.getBoolean("haveread"), rst.getBoolean("newmail"), rst.getDate("whensent"),
                        rst.getBoolean("deleted"), rst.getInt("item_id"));
                res.add(mail);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "listMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "listMail");
        return res;
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
        itsLog.entering(this.getClass().getName(), "newMail");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        try
        {
            String lok = newMail.getLok();

            con = Utils.getDatabaseConnection();

            authentication(con, name, lok);
            verify(con, newMail.getToname());

            stmt=con.prepareStatement(NEWMAIL_SQL); // name, toname, subject, body
            stmt.setString(1, name);
            stmt.setString(2, Utils.security(newMail.getToname()));
            stmt.setString(3, Utils.security(newMail.getSubject()));
            stmt.setString(4, Utils.security(newMail.getBody()));

            int result = stmt.executeUpdate();
            if (result != 1)
            {
                itsLog.severe("Unable to store new mud mail.");
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "newMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "newMail");
        return Response.ok().build();
    }

    private MmudMail getMyMail(Connection con, String name, Long id, String lok)
    {
        PreparedStatement stmt=null;
        ResultSet rst=null;
        try
        {
            stmt=con.prepareStatement(GETMAIL_SQL);
            stmt.setString(1, name);
            stmt.setLong(2, id);
            stmt.setString(3, lok);
            rst=stmt.executeQuery();
            if(rst.next())
            {
                MmudMail result = new MmudMail(null, rst.getString("toname"), rst.getString("name"), rst.getString("subject"), rst.getString("body"), rst.getLong("id"),
                        rst.getBoolean("haveread"), rst.getBoolean("newmail"), rst.getDate("whensent"), rst.getBoolean("deleted"), rst.getInt("item_id"));

                return result;
            }
            else
            {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "getMyMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + "getMyMail: resultset closed.");
        }
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
        itsLog.entering(this.getClass().getName(), "getMail");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        MmudMail res = null;
        try
        {
            con = Utils.getDatabaseConnection();
            authentication(con, name, lok);

            res = getMyMail(con, name, id, lok);
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "getMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "getMail");
        return res;
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
        itsLog.entering(this.getClass().getName(), "createMailItem");
        if (item >= ITEMS.length && item < 0)
        {
            itsLog.entering(this.getClass().getName(), "createMailItem: wrong item def");
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        MmudMail res = null;
        try
        {
            con = Utils.getDatabaseConnection();
            itsLog.entering(this.getClass().getName(), "createMailItem: authentication");
            authentication(con, name, lok);

            // get the sepcific mail with id {id}
            itsLog.entering(this.getClass().getName(), "createMailItem: getMail");
            res = getMyMail(con, name, id, lok);

            int item_id = 0;
            if (res.getItem_id() == null || res.getItem_id() == 0)
            {
                int max_id = 0;
                // retrieve max item_id
                itsLog.entering(this.getClass().getName(), "createMailItem: retrieve max id");
                stmt=con.prepareStatement(GETMAXIDITEMDEF_SQL);
                rst=stmt.executeQuery();
                if (rst.next())
                {
                    max_id = rst.getInt("max");
                }
                else
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: no max itemdefsid determined");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
                // create item definition
                itsLog.entering(this.getClass().getName(), "createMailItem: create item definition");
                stmt=con.prepareStatement(CREATEMAILITEMDEF_SQL);
                stmt.setInt(1, max_id + 1);
                stmt.setString(2, "<div id=\"karchan_letterhead\">" + res.getSubject() + "</div>");
                stmt.setString(3, "<div id=\"karchan_letterbody\">" + res.getBody() + "</div>");
                stmt.setString(4, "<div id=\"karchan_letterfooter\">" + res.getName() + "</div>");
                stmt.setInt(5, ITEMS[item]);
                int result = stmt.executeUpdate();
                if (result != 1)
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: could not create itemdef");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }

                // set item definition into the mail
                itsLog.entering(this.getClass().getName(), "createMailItem: set itemdefinition into the mail");
                stmt=con.prepareStatement(SETIDITEMDEF_IN_MAIL_SQL);
                stmt.setInt(1, max_id + 1);
                stmt.setLong(2, id);
                result = stmt.executeUpdate();
                if (result != 1)
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: could not update item_id in mail");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
                item_id = max_id + 1;
            }
            else
            {
                // retrieve item definition
                itsLog.entering(this.getClass().getName(), "createMailItem: retrieve item def");
                stmt=con.prepareStatement(CHECKITEMDEF_SQL);
                stmt.setLong(1, res.getItem_id());
                rst=stmt.executeQuery();
                if (!rst.next())
                {
                    itsLog.entering(this.getClass().getName(), "createMailItem: no itemdef found");
                    throw new WebApplicationException(Response.Status.BAD_REQUEST);
                }
                item_id = res.getItem_id();
            }

            if (item_id == 0)
            {
                itsLog.entering(this.getClass().getName(), "createMailItem: could not get itemdef");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

            // create item instance
            itsLog.entering(this.getClass().getName(), "createMailItem: create instance object " + item_id + " " + name);
            stmt=con.prepareStatement(CREATEMAILITEM_INSTANCE_SQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, item_id);
            int result = stmt.executeUpdate();
            if (result != 1)
            {
                itsLog.entering(this.getClass().getName(), "createMailItem: could not create item instance");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
            ResultSet keys = stmt.getGeneratedKeys();
            keys.next();
            int key = keys.getInt(1);

            // put item instance into inventory
            itsLog.entering(this.getClass().getName(), "createMailItem: create inventory object " + key + " " + name);
            stmt=con.prepareStatement(CREATEMAILITEM_IN_INVENTORY_SQL);
            stmt.setInt(1, key);
            stmt.setString(2, name);
            result = stmt.executeUpdate();
            if (result != 1)
            {
                itsLog.entering(this.getClass().getName(), "createMailItem: could not add item instance to inventory");
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }

        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "createMailItem", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "createMailItem");
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
        itsLog.entering(this.getClass().getName(), "deleteMail");
        Connection con=null;
        PreparedStatement stmt=null;
        try
        {
            con = Utils.getDatabaseConnection();
            authentication(con, name, lok);

            stmt=con.prepareStatement(DELETEMAIL_SQL);
            stmt.setString(1, name);
            stmt.setLong(2, id);
            stmt.setString(3, lok);
            int rst = stmt.executeUpdate();
            if (rst != 1)
            {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "deleteMail", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        itsLog.exiting(this.getClass().getName(), "deleteMail");
        return Response.ok().build();
    }


    /**
     * This method should be called preceding to each method that is part of this
     * webservice.
     * @param con the connection to the database
     * @param name the name to identify the person
     * @param lok the hash code to authorize the person
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    private void authentication(Connection con, String name, String lok)
    {
        if (lok == null || "".equals(lok.trim()))
        {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        if (name == null || "".equals(name.trim()))
        {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        ResultSet rst=null;
        PreparedStatement stmt=null;
        try
        {
            stmt=con.prepareStatement(AUTHORIZE_SQL);
            stmt.setString(1, name);
            stmt.setString(2, lok);
            rst=stmt.executeQuery();
            if (!rst.next())
            {
                throw new WebApplicationException(Response.Status.UNAUTHORIZED);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "authentication", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": resultset/statement with database closed.");
        }
    }

    /**
     * This method should be called to verify that the target of a certain
     * action is indeed a proper user.
     * @param con the connection to the database
     * @param name the name to identify the person
     * @throws WebApplicationException NOT_FOUND, if the user is either
     * not found or is not a proper user.
     * BAD_REQUEST if an unexpected exception crops up or provided info
     * is really not proper.
     */
    private void verify(Connection con, String name)
    {
        if (name == null || "".equals(name.trim()))
        {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        ResultSet rst=null;
        PreparedStatement stmt=null;
        try
        {
            stmt=con.prepareStatement(FINDUSER_SQL);
            stmt.setString(1, name);
            rst=stmt.executeQuery();
            if (!rst.next())
            {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
        }
        catch(WebApplicationException e)
        {
            //ignore
            throw e;
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "authentication", e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": resultset/statement with database closed.");
        }
    }



}
