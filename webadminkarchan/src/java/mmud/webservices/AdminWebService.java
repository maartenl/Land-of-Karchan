/*-----------------------------------------------------------------------
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
Appelhof 27
5345 KA Oss
Nederland
Europe
maarten_l@yahoo.com
-----------------------------------------------------------------------*/

package mmud.webservices;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import mmud.webservices.webentities.Character;
import mmud.webservices.webentities.LogonMessage;
import mmud.webservices.webentities.Result;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import mmud.webservices.webentities.Results;
import mmud.webservices.webentities.Room;

/**
 * The Java class will be hosted at the URI path "/karchan/admin/resources/admin"
 * @author maartenl
 */
@Path("admin")
@Consumes("application/json")
@Produces("application/json")
public class AdminWebService {

    private Logger itsLog = Logger.getLogger("mmudrest");

    /**
     * Just for testing.
     * @return A string with "Hello World".
     */
    @GET
    @Path("helloworld")
    public String getClichedMessage() {
        // Return some cliched textual content
        itsLog.entering(this.getClass().getName(), "getClichedMessage");
        return "Hello World";
    }

    /**
     * Returns the latest Logon message.
     * @return JSON formatted Result object, in the form:
     * {success: "true", data: {name:"Karn",datetime:"2008-08-08:00.00.00",message:"Mine!"}}
     * or
     * {success: "false", errorMessage:"Houston, we have a problem."}
     */
    @GET
    @Path("logonmessage")
    public Result getLogonMessage()
    {
        itsLog.entering(this.getClass().getName(), "getLogonMessage");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        Result result = new Result();

        try
        {
            con = getDatabaseConnection();

            stmt=con.prepareStatement("select * from mm_boardmessages where boardid = 2 order by posttime desc limit 1");
            rst=stmt.executeQuery();
            if (rst.next())
            {
                result = new Result(new LogonMessage(rst.getString("name"),
                        rst.getString("message"),
                        rst.getTimestamp("posttime")));
            }
            rst.close();
            stmt.close();
            con.close();
        }
        catch(Exception e)
        {
            result.setErrorMessage(e.getMessage());
            itsLog.throwing(this.getClass().getName(), "getLogonMessage", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }
        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "getLogonMessage " + result.toString());
        return result;
    }

    /**
     * Creates a brand new logon message!
     * @param aNewMessage contains the name, message and datetime in JSON format, for example:
     * {name:"Karn",datetime:"2008-08-08:00.00.00",message:"Mine!"}. Be aware, that <i>none</i>
     * of the fields are actually used, except the 'message'. Datetime is defaulted to "Now()"
     * and the name is retrieved from the SecurityContext.
     * @param context the security context.
     */
    @POST
    @Path("logonmessage")
    public void addLogonMessage(LogonMessage aNewMessage, @Context SecurityContext context)
    {
        itsLog.entering(this.getClass().getName(), "addLogonMessage");
        Connection con=null;
        PreparedStatement stmt=null;

        try
        {
            con = getDatabaseConnection();
            String userName = context.getUserPrincipal().getName();

            stmt=con.prepareStatement("insert into mm_boardmessages (boardid, name, posttime, message, removed) values(2, ?, now(), ?, 0)");
            stmt.setString(1, userName);
            stmt.setString(2, aNewMessage.getMessage());
            if (stmt.executeUpdate() != 1)
            {
                throw new NullPointerException("Problems creating new logon message");
            }
        }
        catch(Exception e)
        {
            Result res = new Result();
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            itsLog.throwing(this.getClass().getName(), "addLogonMessage", e);
        }
        finally
        {
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }
        itsLog.exiting(this.getClass().getName(), "addLogonMessage");
    }

    /**
     * Change the existing Logon message.
     * @param aChangedMessage the new message, in the following format:
     * {name:"Karn",datetime:"2008-08-08:00.00.00",message:"Mine!"}.
     * @param context the security context.
     */
    @PUT
    @Path("logonmessage")
    public Result changeLogonMessage(LogonMessage aChangedMessage, @Context SecurityContext context)
    {
        itsLog.entering(this.getClass().getName(), "changeLogonMessage");
        Connection con=null;
        PreparedStatement stmt=null;
        String userName = context.getUserPrincipal().getName();
        Result res = new Result();

        if (!userName.equals(aChangedMessage.getName()))
        {
            return new Result("Trying to change a logonmessage that is not yours.");
        }

        try
        {
            con = getDatabaseConnection();

            stmt=con.prepareStatement("update mm_boardmessages set posttime=now(), message = ? " +
                    "where boardid = 2 and name = ? and posttime = ?");
            stmt.setString(1, aChangedMessage.getMessage());
            stmt.setString(2, userName);
            stmt.setTimestamp(3, new java.sql.Timestamp(aChangedMessage.getDatetime().getTime()));
            if (stmt.executeUpdate() != 1)
            {
                throw new NullPointerException("Problems changing logon message. Logon message not found or not yours.");
            }
        }
        catch(Exception e)
        {
            res = new Result();
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            itsLog.throwing(this.getClass().getName(), "changeLogonMessage", e);
        }
        finally
        {
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }
        itsLog.exiting(this.getClass().getName(), "changeLogonMessage");
        return res;
    }

    /**
     * Returns a list of character names.
     * @return JSON formatted array of Strings, in the form:
     * {success: "true", data: {name:"Karn",etc...}}
     * or
     * {success: "false", errorMessage:"Houston, we have a problem."}
     */
    @GET
    @Path("characters")
    public Results getCharacters()
    {
        itsLog.entering(this.getClass().getName(), "getCharacters");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        ArrayList<String> result = new ArrayList<String>();
        Results res = null;

        try
        {
            con = getDatabaseConnection();

            stmt=con.prepareStatement("select name from mm_usertable limit 100");
            rst=stmt.executeQuery();
            while (rst.next())
            {
                result.add(rst.getString("name"));
            }
            rst.close();
            stmt.close();
            con.close();
            res = new Results(result);
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "getCharacters", e);
            res = new Results(e.getMessage());
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }
        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "getCharacters: result= " + result.size());
        return res;
    }


    /**
     * Returns the character.
     * @return JSON formatted Result object, in the form:
     * {success: "true", data: {name:"Karn",etc...}}
     * or
     * {success: "false", errorMessage:"Houston, we have a problem."}
     */
    @GET
    @Path("characters/{name}")
    public Result getCharacter(@PathParam("name") String name)
    {
        itsLog.entering(this.getClass().getName(), "getCharacter " + name);
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        Character result = null;
        Result res = new Result();

        try
        {
            con = getDatabaseConnection();

            stmt=con.prepareStatement("select * from mm_usertable where name = ?");
            stmt.setString(1, name);
            rst=stmt.executeQuery();
            if (rst.next())
            {
                result = new Character(rst.getString("name"),
                        rst.getString("password"),
                        rst.getString("address"),
                        rst.getString("title"),
                        rst.getString("realname"),
                        rst.getString("email"),
                        rst.getString("race"),
                        rst.getString("sex"),
                        rst.getString("age"),
                        rst.getString("length"),
                        rst.getString("width"),
                        rst.getString("complexion"),
                        rst.getString("eyes"),
                        rst.getString("face"),
                        rst.getString("hair"),
                        rst.getString("beard"),
                        rst.getString("arm"),
                        rst.getString("leg"),
                        rst.getString("notes")
                        );

                itsLog.info("getCharacter: Found character: " + result.toString());
            }
            rst.close();
            stmt.close();
            con.close();
        }
        catch(Exception e)
        {
            res.setSuccess(false);
            res.setErrorMessage(e.getMessage());
            itsLog.throwing(this.getClass().getName(), "getCharacter", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }


        if (result == null)
        {
            res = new Result("Character " + name + " not found.");
            itsLog.exiting(this.getClass().getName(), "getCharacter " + res);
            return res;
        }
        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        res = new Result(result);
        itsLog.exiting(this.getClass().getName(), "getCharacter " + res);
        return res;
    }


    /**
     * Returns a list of character names.
     * @return JSON formatted array of Strings, in the form:
     * {success: "true", data: {name:"Karn",etc...}}
     * or
     * {success: "false", errorMessage:"Houston, we have a problem."}
     */
    @GET
    @Path("rooms")
    public Results getRooms()
    {
        itsLog.entering(this.getClass().getName(), "getRooms");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        ArrayList<String> result = new ArrayList<String>();
        Results res = null;

        try
        {
            con = getDatabaseConnection();

            stmt=con.prepareStatement("select id from mm_rooms limit 100");
            rst=stmt.executeQuery();
            while (rst.next())
            {
                result.add(rst.getString("id"));
            }
            rst.close();
            stmt.close();
            con.close();
            res = new Results(result);
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "getRooms", e);
            res = new Results(e.getMessage());
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }
        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "getRooms: result= " + result.size());
        return res;
    }

    /**
     * Returns the room details.
     * @return JSON formatted Result object, in the form:
     * {success: "true", data: {id:"3",title:"Our house"}}
     * or
     * {success: "false", errorMessage:"Houston, we have a problem."}
     */
    @GET
    @Path("rooms/{id}")
    public Result getRoom(@PathParam("id") int id)
    {
        itsLog.entering(this.getClass().getName(), "getRoom");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        Result result = new Result();

        try
        {
            con = getDatabaseConnection();

            stmt=con.prepareStatement("select * from mm_rooms where id = ?");
            stmt.setInt(1, id);
            rst=stmt.executeQuery();
            if (rst.next())
            {
                result = new Result(new Room(rst.getInt("id"),
                        rst.getString("title"),
                        rst.getString("picture"),
                        rst.getString("area"),
                        rst.getTimestamp("creation"),
                        rst.getString("contents"),
                        rst.getString("owner"),
                        rst.getInt("west"),
                        rst.getInt("east"),
                        rst.getInt("north"),
                        rst.getInt("south"),
                        rst.getInt("up"),
                        rst.getInt("down")
                        ));
            }
            rst.close();
            stmt.close();
            con.close();
        }
        catch(Exception e)
        {
            result.setErrorMessage(e.getMessage());
            itsLog.throwing(this.getClass().getName(), "getRoom", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
        }
        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "getRoom " + result.toString());
        return result;
    }

    private Connection getDatabaseConnection() throws SQLException, NamingException {
        Connection con;
        javax.naming.Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
        con = ds.getConnection();
        return con;
    }
}
