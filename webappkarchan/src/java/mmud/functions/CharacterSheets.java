/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.functions;

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
import mmud.functions.mail.Mail;
import mmud.webservices.webentities.MmudMail;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * Everything from retrieving to changing charactersheets.
 * @author maartenl
 */
public class CharacterSheets {

    public static final String CHARACTERSHEETS_SQL = "select mm_usertable.name from characterinfo, mm_usertable where mm_usertable.name=characterinfo.name order by mm_usertable.name";

    public static final String CHARACTERSHEET_SQL = "select mm_usertable.name, title, sex, concat(age, if(length = \'none\', \'\', concat(\', \',length)),if(width = \'none\', \'\', concat(\', \',width)), if(complexion = \'none\', \'\', concat(\', \',complexion)),        if(eyes = \'none\', \'\', concat(\', \',eyes)),        if(face = \'none\', \'\', concat(\', \',face)),        if(hair = \'none\', \'\', concat(\', \',hair)),        if(beard = \'none\', \'\', concat(\', \',beard)),        if(arm = \'none\', \'\', concat(\', \',arm)),        if(leg = \'none\', \'\', concat(\', \',leg)),        \' \', sex, \' \', race) as description,         concat(\'<IMG SRC=\"\',imageurl,\'\">\') as imageurl,         guild,   homepageurl,         dateofbirth, cityofbirth, mm_usertable.lastlogin, storyline         from mm_usertable, characterinfo         where mm_usertable.name = ? and mm_usertable.name = characterinfo.name";

    public static final String FAMILYVALUES_CHARACTERSHEET_SQL =
            "select familyvalues.description, toname, characterinfo.name " +
            "from family, familyvalues, characterinfo " +
            "where family.name = ? " +
            "and family.description = familyvalues.id and " +
            "characterinfo.name = family.toname";

    private static Logger itsLog = Logger.getLogger("mmudrest");

    /**
     * Returns a List of characters and their profiles.
     */
    public JSONArray charactersheets()
    {
        itsLog.entering(this.getClass().getName(), "charactersheets");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONArray res = new JSONArray();
        try
        {

            con = Utils.getDatabaseConnection();

            stmt=con.prepareStatement(CHARACTERSHEETS_SQL);
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
        itsLog.exiting(this.getClass().getName(), "charactersheets");
        return res;
    }
        /**
     * Returns all the info of a character.
     */
    public JSONObject charactersheet(String name)
    {
        itsLog.entering(this.getClass().getName(), "charactersheet");
        Connection con=null;
        ResultSet rst=null;
        PreparedStatement stmt=null;
        JSONObject res = new JSONObject();
        try
        {

            con = Utils.getDatabaseConnection();

            stmt=con.prepareStatement(CHARACTERSHEET_SQL);
            stmt.setString(1, name);
            rst=stmt.executeQuery();
            while(rst.next())
            {
                res.put("name", rst.getString("name"));
                res.put("title", rst.getString("title"));
                res.put("sex", rst.getString("sex"));
                res.put("description", rst.getString("description"));
                res.put("imageurl", rst.getString("imageurl"));
                res.put("guild", rst.getString("guild"));
                res.put("homepageurl", rst.getString("homepageurl"));
                res.put("dateofbirth", rst.getString("dateofbirth"));
                res.put("cityofbirth", rst.getString("cityofbirth"));
                res.put("lastlogin", rst.getString("lastlogin"));
                res.put("storyline", rst.getString("storyline"));
            }
            stmt=con.prepareStatement(FAMILYVALUES_CHARACTERSHEET_SQL);
            stmt.setString(1, name);
            rst=stmt.executeQuery();
            JSONArray familyvalues = new JSONArray();
            while(rst.next())
            {
                JSONObject fvalue = new JSONObject();
                fvalue.put("name", rst.getString("name"));
                fvalue.put("description", rst.getString("description"));
                fvalue.put("toname", rst.getString("toname"));
                familyvalues.put(fvalue);
            }
            res.put("familyvalues", familyvalues);
        }
        catch(Exception e)
        {
            itsLog.throwing(this.getClass().getName(), "charactersheet", e);
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
        itsLog.exiting(this.getClass().getName(), "charactersheet");
        return res;
    }

}
