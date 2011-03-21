/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.webservices;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import mmud.webservices.webentities.DisplayResult;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.ws.rs.core.Context;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * The REST service will be hosted at the URI path "/karchan/resources/admin"
 * @author maartenl
 */
@Path("public")
@Consumes("application/json")
@Produces("application/json")
public class PublicResource {
    @Context
    private UriInfo context;

    private Logger itsLog = Logger.getLogger("mmudrest");

    /** Creates a new instance of PublicResource */
    public PublicResource() {
    }

    /**
     * Retrieves representation of an instance of mmud.webservices.PublicResource
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
     * Returns a List of people currently online.
     */
    @GET
    @Path("who")
    public JSONArray who()
    {
        itsLog.entering(this.getClass().getName(), "who");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONArray res = new JSONArray();
        try
        {

            con = getDatabaseConnection();

            stmt=con.prepareStatement("select name, mm_usertable.title, sleep, 	floor((unix_timestamp(NOW())-unix_timestamp(lastlogin)) / 60) as min,	((unix_timestamp(NOW())-unix_timestamp(lastlogin)) % 60) as sec,	if (mm_area.area <> \"Main\", concat(\" in \" , mm_area.shortdesc), \"\") as area	from mm_usertable, mm_rooms, mm_area 	where god<=1 and active=1 and mm_rooms.id = mm_usertable.room and	mm_rooms.area = mm_area.area");
            rst=stmt.executeQuery();
            while(rst.next())
            {
                JSONObject myJSONObject = new JSONObject();
                myJSONObject.put("name", rst.getString("name"));
                myJSONObject.put("title", rst.getString("title"));
                myJSONObject.put("sleep", (rst.getInt("sleep") != 1 ? "" : "sleeping"));
                myJSONObject.put("area", rst.getString("area"));
                myJSONObject.put("min", rst.getString("min"));
                myJSONObject.put("sec", rst.getString("sec"));
                res.put(myJSONObject);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "who", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "who");
        return res;
    }


    /**
     * Returns a List of news, recent first.
     */
    @GET
    @Path("news")
    public JSONArray news()
    {
        itsLog.entering(this.getClass().getName(), "news");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONArray res = new JSONArray();
        try
        {

            con = getDatabaseConnection();

            stmt=con.prepareStatement("select mm_boardmessages.name, date_format(posttime, \"%W, %M %e %Y, %H:%i\") as posttime, message from mm_boardmessages, mm_boards where boardid=id and	mm_boards.name = \"logonmessage\" order by mm_boardmessages.posttime desc limit 10");
            rst=stmt.executeQuery();
            while(rst.next())
            {
                JSONObject myJSONObject = new JSONObject();
                myJSONObject.put("name", rst.getString("name"));
                myJSONObject.put("posttime", rst.getString("posttime"));
                myJSONObject.put("message", rst.getString("message"));
                res.put(myJSONObject);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "news", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "news");
        return res;
    }

    /**
     * Returns a List of current active and paid up deputies.
     */
    @GET
    @Path("status")
    public JSONArray status()
    {
        itsLog.entering(this.getClass().getName(), "status");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONArray res = new JSONArray();
        try
        {

            con = getDatabaseConnection();

            stmt=con.prepareStatement("select mm_admin.name, title from mm_admin, mm_usertable where mm_admin.name = mm_usertable.name and mm_admin.validuntil > now()");
            rst=stmt.executeQuery();
            while(rst.next())
            {
                JSONObject myJSONObject = new JSONObject();
                myJSONObject.put("name", rst.getString("name"));
                myJSONObject.put("title", rst.getString("title"));
                res.put(myJSONObject);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "status", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "status");
        return res;
    }


    /**
     * Returns a List of characters and their profiles.
     */
    @GET
    @Path("charactersheets")
    public JSONArray charactersheets()
    {
        itsLog.entering(this.getClass().getName(), "charactersheets");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONArray res = new JSONArray();
        try
        {

            con = getDatabaseConnection();

            stmt=con.prepareStatement("select mm_usertable.name from characterinfo, mm_usertable where mm_usertable.name=characterinfo.name order by mm_usertable.name");
            rst=stmt.executeQuery();
            while(rst.next())
            {
                JSONObject myJSONObject = new JSONObject();
                myJSONObject.put("name", rst.getString("name"));
                myJSONObject.put("url", "/resources/public/charactersheets/" + rst.getString("name"));
                res.put(myJSONObject);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "charactersheets", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "charactersheets");
        return res;
    }


    /**
     * Returns a Fortune 500 of players on karchan.
     */
    @GET
    @Path("fortunes")
    public JSONArray fortunes()
    {
        itsLog.entering(this.getClass().getName(), "fortunes");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONArray res = new JSONArray();
        try
        {

            con = getDatabaseConnection();

            stmt=con.prepareStatement("select name, floor(copper/100) as gold, floor((copper % 100)/10) as silver, copper % 10 as copper	from mm_usertable	where god<=1	order by gold desc, silver desc, copper desc, name asc	limit 100");
            rst=stmt.executeQuery();
            while(rst.next())
            {
                JSONObject myJSONObject = new JSONObject();
                myJSONObject.put("name", rst.getString("name"));
                myJSONObject.put("gold", rst.getString("gold"));
                myJSONObject.put("silver", rst.getString("silver"));
                myJSONObject.put("copper", rst.getString("copper"));
                res.put(myJSONObject);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "fortunes", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "fortunes");
        return res;
    }

    /**
     * Returns a list of Guilds
     */
    @GET
    @Path("guilds")
    public JSONArray guilds()
    {
        itsLog.entering(this.getClass().getName(), "guilds");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONArray res = new JSONArray();
        try
        {

            con = getDatabaseConnection();

            stmt=con.prepareStatement("select *, date_format(creation, \"%Y-%m-%d %T\") as creation2 from mm_guilds order by title");
            rst=stmt.executeQuery();
            while(rst.next())
            {
                JSONObject myJSONObject = new JSONObject();
                myJSONObject.put("guildurl", rst.getString("guildurl"));
                myJSONObject.put("title", rst.getString("title"));
                myJSONObject.put("bossname", rst.getString("bossname"));
                myJSONObject.put("guilddescription", rst.getString("guilddescription"));
                myJSONObject.put("creation", rst.getString("creation2"));
                //myJSONObject.put("", rst.getString(""));
                //myJSONObject.put("", rst.getString(""));
                //myJSONObject.put("", rst.getString(""));
                //myJSONObject.put("", rst.getString(""));
                res.put(myJSONObject);
            }
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "guilds", e);
        }
        finally
        {
            if (rst != null) {try {rst.close();} catch (Exception e){}}
            if (stmt != null) {try {stmt.close();} catch (Exception e){}}
            if (con != null) {try {con.close();} catch (Exception e){}}
            itsLog.finest(this.getClass().getName() + ": connection with database closed.");
        }

        // ResponseBuilder rb = request.evaluatePreconditions(lastModified, et);
        itsLog.exiting(this.getClass().getName(), "guilds");
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
}
