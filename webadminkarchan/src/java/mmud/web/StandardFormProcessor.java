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

package mmud.web;

import javax.naming.InitialContext;
import java.io.PrintWriter;
import java.io.IOException;
import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Enumeration;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

/**
 * Standard class for processing the different administrative possibiities
 * of the mmud web pages.
 *
 * @author Maarten van Leunen
 */
public class StandardFormProcessor
{
    private Connection itsConnection;

    private String itsTableName;

    private String[] itsColumns;

    private String[] itsDisplay;

    private String itsPlayerName;

    /**
     * Initialises the object with a connection to the database.
     */
    public StandardFormProcessor(String aTableName, String aPlayerName)
    {
        if (aTableName == null || aTableName.trim().equals(""))
        {
            throw new RuntimeException("aTableName is empty.");
        }
        itsTableName = aTableName;
        itsPlayerName = aPlayerName;

        Context ctx = null;
        try
        {
            ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("jdbc/mmud");
            itsConnection = ds.getConnection();
        }
        catch (NamingException e)
        {
            throw new RuntimeException("Getting the datasource failed!", e);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Getting an appropriate connection failed!!", e);
        }
    }

    /**
     * @param itsColums the itsColums to set
     */
    public void setColums(String[] itsColums) {
        this.itsColumns = itsColums;
    }

    /**
     * @param itsDisplay the itsDisplay to set
     */
    public void setDisplayNames(String[] itsDisplay) {
        this.itsDisplay = itsDisplay;
    }

    public String getList(HttpServletRequest request)
            throws SQLException
    {
        StringBuffer result = new StringBuffer();
        result.append("<table>");
        ResultSet rst=null;
        PreparedStatement stmt=null;

        // show list of commands
        if (request.getParameter("id") != null)
        {
            stmt=itsConnection.prepareStatement("select * from " + itsTableName + " where " + itsColumns[0] + " like ?");
            stmt.setString(1, request.getParameter("id"));
        }
        else
        if (request.getParameter("idstartswith") != null)
        {
            stmt=itsConnection.prepareStatement("select * from " + itsTableName + " where " + itsColumns[1] + " like ?");
            stmt.setString(1, request.getParameter("idstartswith") + "%" );
        }
        else
        {
            stmt=itsConnection.prepareStatement("select * from " + itsTableName);
        }

        rst=stmt.executeQuery();
        while (rst.next())
        {
            result.append("<tr>");
            boolean accessGranted = itsPlayerName.equals(rst.getString("owner")) ||
                rst.getString("owner") == null ||
                rst.getString("owner").trim().equals("");

            if (accessGranted)
            {
                result.append("<td><a HREF=\"remove_" + itsTableName.toLowerCase() +
                        ".jsp?id=" + rst.getString("id") + "\">X</a></td>");
                result.append("<td><a HREF=\"remove_ownership.jsp?id=" +
                        rst.getString("id") + "&table=" + itsTableName + "\">O</a></td>");
            }
            else
            {
                result.append("<td></td><td></td>");
            }

            for (int i=0; i < itsColumns.length; i++)
            {
                if (rst.getString(itsColumns[i]) == null)
                {
                    result.append("<td></td>");
                }
                else
                if ("0".equals(rst.getString(itsColumns[i])))
                {
                    result.append("<td><b>" + itsDisplay[i] + ":</b> No</td>");
                }
                else
                if ("1".equals(rst.getString(itsColumns[i])))
                {
                    result.append("<td><b>" + itsDisplay[i] + ":</b> Yes</td>");
                }
                else
                {
                    result.append("<td><b>" + itsDisplay[i] + ":</b> " + rst.getString(itsColumns[i]) + "</td>");
                }
            }
            result.append("</tr>");
            if (accessGranted && (rst.getString(itsColumns[0]).equals(request.getParameter("id"))))
            {
                // put some editing form here.
                result.append("<tr><td><FORM METHOD=\"POST\" ACTION=\"" + itsTableName.toLowerCase() + ".jsp\">");
                for (int i=0; i < itsColumns.length; i++)
                {
                    result.append("<b>" + itsDisplay[i] + ": </b>");
                    if (itsColumns[i].equals("callable"))
                    {
                        result.append("<SELECT NAME=\"commandcallable\" SIZE=\"2\">");
                        result.append("<option value=\"1\" " +
                                ("1".equals(rst.getString("callable")) ? "selected " : " ") + ">yes");
                        result.append("<option value=\"0\" " +
                                ("0".equals(rst.getString("callable")) ? "selected " : " ") + ">no");
                        result.append("</SELECT>");
                    }
                    else
                    {
                        result.append("<INPUT TYPE=\"text\" NAME=\"" +
                            itsColumns[i] + "\" VALUE=\"" +
                            rst.getString(itsColumns[i]) + "\" SIZE=\"40\" MAXLENGTH=\"40\">");
                    }
                }
                result.append("<INPUT TYPE=\"submit\" VALUE=\"Change Command\">");
                result.append("</FORM></td></tr>");
            }
        }
        rst.close();
        stmt.close();
        result.append("</table>");

        return result.toString();
    }

    public void addEntry(HttpServletRequest request)
    {

    }

    public void removeEntry(HttpServletRequest request)
    {
    }

    public void removeOwnershipFromEntry(HttpServletRequest request)
    {
    }
}
