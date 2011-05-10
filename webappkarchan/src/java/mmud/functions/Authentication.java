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
import mmud.webservices.webentities.MmudMail;

/**
 *
 * Checks if the credentials provided are authorised for certain stuff.
 * @author maartenl
 */
public class Authentication {

    private static Logger itsLog = Logger.getLogger("mmudrest");

    public static final String FINDUSER_SQL = "select 1 from mm_usertable where name = ? and (god = 0 or god = 1)";

    public static final String AUTHORIZE_SQL = "select 1 from mm_usertable where name = ? and mm_usertable.lok is not null and trim(mm_usertable.lok) <> \"\" and mm_usertable.lok = ? and (god = 0 or god = 1)";

    /**
     * This method should be called preceding to each method that is part of this
     * webservice.
     * @param con the connection to the database
     * @param name the name to identify the person
     * @param lok the hash code to authorize the person
     * @throws WebApplicationException UNAUTHORIZED, if the authorisation failed.
     * BAD_REQUEST if an unexpected exception crops up.
     */
    public void authentication(Connection con, String name, String lok)
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
    public void verify(Connection con, String name)
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
