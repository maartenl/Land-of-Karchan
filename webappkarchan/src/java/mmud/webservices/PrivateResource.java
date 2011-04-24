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
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import mmud.webservices.webentities.DisplayResult;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import mmud.webservices.webentities.MmudMail;
import org.codehaus.jettison.json.JSONObject;

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

    public static final String LISTMAIL_SQL = "select mm_mailtable.* from mm_mailtable, mm_usertable where toname = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? order by id limit ?, 20";

    public static final String GETMAIL_SQL = "select mm_mailtable.* from mm_mailtable, mm_usertable where toname = ? and mm_mailtable.id = ? and mm_usertable.name = mm_mailtable.toname and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ?";

    public static final String NEWMAIL_SQL = "insert into mm_mailtable (name, toname, subject, body) values(?, ?, ?, ?)";

    public static final String AUTHORIZE_SQL = "select 1 from mm_usertable where name = ? and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ?";

    @Context
    private UriInfo context;

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
        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
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
        //JSONArray res = new JSONArray();
        List<MmudMail> res = new ArrayList<MmudMail>();
        try
        {

            con = getDatabaseConnection();

            authentication(con, name, lok);

            stmt=con.prepareStatement(LISTMAIL_SQL);
            stmt.setString(1, name);
            stmt.setString(2, lok);
            stmt.setInt(3, offset);
            rst=stmt.executeQuery();
            while(rst.next())
            {
                MmudMail mail = new MmudMail(null, rst.getString("toname"), rst.getString("name"), rst.getString("subject"), rst.getString("body"),
                        rst.getLong("id"), rst.getBoolean("haveread"), rst.getBoolean("newmail"), rst.getDate("whensent"));
/*
                JSONObject myJSONObject = new JSONObject();
                myJSONObject.put("id", rst.getInt("id"));
                myJSONObject.put("name", rst.getString("name"));
                myJSONObject.put("toname", rst.getString("toname"));
                myJSONObject.put("haveread", rst.getBoolean("haveread"));
                myJSONObject.put("newmail", rst.getBoolean("newmail"));
                myJSONObject.put("whensent", rst.getDate("whensent"));
                myJSONObject.put("subject", rst.getString("subject"));
                myJSONObject.put("body", rst.getString("body"));
                res.put(myJSONObject);*/
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

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
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

            con = getDatabaseConnection();

            authentication(con, name, lok);

            stmt=con.prepareStatement(NEWMAIL_SQL); // name, toname, subject, body
            stmt.setString(1, name);
            stmt.setString(2, newMail.getToname());
            stmt.setString(3, newMail.getSubject());
            stmt.setString(4, newMail.getBody());
            itsLog.warning("newMail: " + newMail);

            int result = 1; // stmt.executeUpdate();
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
    public JSONObject getMail(@PathParam("name") String name, @QueryParam("lok") String lok, @PathParam("id") long id)
    {
        itsLog.entering(this.getClass().getName(), "getMail");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONObject res = new JSONObject();
        try
        {
            con = getDatabaseConnection();
            authentication(con, name, lok);

            stmt=con.prepareStatement(GETMAIL_SQL);
            stmt.setString(1, name);
            stmt.setLong(2, id);
            stmt.setString(3, lok);
            rst=stmt.executeQuery();
            while(rst.next())
            {
                res.put("id", rst.getInt("id"));
                res.put("name", rst.getString("name"));
                res.put("toname", rst.getString("toname"));
                res.put("haveread", rst.getBoolean("haveread"));
                res.put("newmail", rst.getBoolean("newmail"));
                res.put("whensent", rst.getDate("whensent"));
                res.put("subject", rst.getString("subject"));
                res.put("body", rst.getString("body"));
            }
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

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "getMail");
        return res;
    }

    private Connection getDatabaseConnection() throws SQLException, NamingException {
        itsLog.finest(this.getClass().getName() + ": connection with database opened.");
        Connection con;
        javax.naming.Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
        con = ds.getConnection();
        return con;
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
}
