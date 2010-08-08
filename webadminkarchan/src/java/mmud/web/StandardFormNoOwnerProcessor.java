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

import java.sql.*;
import java.text.DateFormat;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author maartenl
 */
public class StandardFormNoOwnerProcessor extends BaseFormProcessor {

    public StandardFormNoOwnerProcessor(String aTablename, String aPlayerName, Formatter formatter)
    throws SQLException
    {
       super(aTablename, aPlayerName, formatter);
    }

    public String getList(HttpServletRequest request, String query)
            throws SQLException
    {
        ResultSet rst=null;
        PreparedStatement stmt=null;
        try
        {
            // show list of commands
            if (query != null)
            {
                stmt=itsConnection.prepareStatement(query);
                if (query.contains("?"))
                {
                    if (request.getParameter("id") == null)
                    {
                        throw new RuntimeException("Id was null!");
                    }
                    stmt.setString(1, request.getParameter("id"));
                }
            }
            else
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
                itsFormatter.addRow();
                boolean accessGranted = true;
                String firstParam = null;
                if (CREATION.equals(itsColumns[0]))
                {
                    firstParam = rst.getTimestamp(itsColumns[0]).toString();
                }
                else
                {
                    firstParam = rst.getString(itsColumns[0]);
                }
                if (!firstParam.equals(request.getParameter("id")))
                {
                   // put the list here
                   if (accessGranted)
                    {
                       itsFormatter.addRowItem(itsFormatter.returnOptionsString(itsTableName, rst.getString(itsColumns[0])));
                    }
                    else
                    {
                       itsFormatter.addRowItem("");
                    }

                    for (int i=0; i < itsColumns.length; i++)
                    {
                        if (rst.getString(itsColumns[i]) == null)
                        {
                            itsFormatter.addRowItem("");
                        }
                        if ("0".equals(rst.getString(itsColumns[i])))
                        {
                            itsFormatter.addRowBoolean(itsDisplay[i], false);
                        }
                        else
                        if ("1".equals(rst.getString(itsColumns[i])))
                        {
                            itsFormatter.addRowBoolean(itsDisplay[i], true);
                        }
                        else
                        if (itsColumns[i].equals(CREATION))
                        {
                            itsFormatter.addRowDate(itsDisplay[i], rst.getDate(itsColumns[i]));
                        }
                        else
                        if (itsColumns[i].equals("src"))
                        {
                            itsFormatter.addRowString(itsDisplay[i],
                                    rst.getString(itsColumns[i]).replace("&", "&amp;").replace(">","&gt;").replace("<", "&lt;"));
                        }
                        else
                        {
                            itsFormatter.addRowString(itsDisplay[i], rst.getString(itsColumns[i]));
                        }
                    }
               }
               else
                {
                    StringBuffer result = new StringBuffer();
                    // put some editing form here.
                    result.append("<tr><td><table><tr><FORM METHOD=\"POST\" ACTION=\"" +
                            itsTableName.replace("mm_", "").toLowerCase() +
                            ".jsp\">");
                    result.append("<td><b>" + itsDisplay[0] + ": </b></td><td>" + rst.getString(itsColumns[0]) + "</td></tr>");
                    for (int i=1; i < itsColumns.length; i++)
                    {
                        result.append("<td><b>" + itsDisplay[i] + ": </b></td><td>");
                        if (itsColumns[i].equals("creation"))
                        {
                            Date creation = rst.getDate(itsColumns[i]);
                            DateFormat formatter = DateFormat.getInstance();
                            result.append("<td><b>" + itsDisplay[i] + ":</b> " + formatter.format(creation) + "</td>");
                        }
                        else
                        if (itsColumns[i].equals("callable"))
                        {
                            result.append("<SELECT NAME=\"callable\" SIZE=\"2\">");
                            result.append("<option value=\"1\" " +
                                    ("1".equals(rst.getString("callable")) ? "selected " : " ") + ">yes");
                            result.append("<option value=\"0\" " +
                                    ("0".equals(rst.getString("callable")) ? "selected " : " ") + ">no");
                            result.append("</SELECT><br/>");
                        }
                        else
                        if (itsColumns[i].equals("src"))
                        {
                            String disp = rst.getString(itsColumns[i]);
                            disp = (disp == null ? "" : disp);
                            result.append("<TEXTAREA NAME=\"" + itsColumns[i] + "\" ROWS=\"14\" COLS=\"85\"><" + disp +
                                    "</TEXTAREA><br/>");
                        }
                        else
                        {
                            String disp = rst.getString(itsColumns[i]);
                            disp = (disp == null ? "" : disp);
                            result.append("<INPUT TYPE=\"text\" NAME=\"" +
                                itsColumns[i] + "\" VALUE=\"" +
                                disp + "\" SIZE=\"40\" MAXLENGTH=\"40\"><br/>");
                        }
                        result.append("</td></tr>");
                    }
                    result.append("</table><INPUT TYPE=\"submit\" VALUE=\"Change " + itsTableName.replace("mm_", "") + "\">");
                    result.append("</FORM></td></tr>");
                }
            }
        }
        finally
        {
            if (rst != null) {rst.close();}
            if (stmt != null) {stmt.close();}
        }

        return itsFormatter.toString();
    }

    @Override
    public void removeOwnershipFromEntry(HttpServletRequest request)
    throws SQLException
    {
        // do nothing,
    }

    @Override
    public void changeEntry(HttpServletRequest request)
    throws SQLException
    {
        StringBuffer query = new StringBuffer();
        PreparedStatement stmt=null;
        try
        {
            // add one
            query.append("update " + itsTableName + " set ");
            for (int i=1; i < itsColumns.length; i++)
            {
                if (CREATION.equals(itsColumns[i]))
                {
                    // creation timestamp! skip it!
                    continue;
                }
                query.append(itsColumns[i] + " = ?");
                if (i != itsColumns.length - 1)
                {
                    query.append(", ");
                }
            }
            query.append(" where " + itsColumns[0] + " = ?");
            stmt=itsConnection.prepareStatement(query.toString());
            for (int i=1; i < itsColumns.length; i++)
            {
                if (CREATION.equals(itsColumns[i]))
                {
                    // creation timestamp! skip it!
                    continue;
                }
                if (itsColumns[i].equals(OWNER))
                {
                    stmt.setString(i, itsPlayerName);
                }
                else
                {
                    stmt.setString(i, request.getParameter(itsColumns[i]));
                }
            }
            stmt.setString(itsColumns.length + 1, request.getParameter(itsColumns[0]));

            stmt.executeUpdate(query.toString());
        }
        finally
        {
            if (stmt != null) {stmt.close();}
        }
    }

    @Override
    public void removeEntry(HttpServletRequest request)
    throws SQLException
    {
        StringBuffer query = new StringBuffer();
        PreparedStatement stmt=null;
        try
        {
            // add one
            query.append("delete from " + itsTableName +
                    " where " + itsColumns[0] + " = ?");
            stmt=itsConnection.prepareStatement(query.toString());
            stmt.setString(1, request.getParameter("id"));
            stmt.executeUpdate(query.toString());
        }
        finally
        {
            if (stmt != null) {stmt.close();}
        }
    }

    @Override
    public void addEntry(HttpServletRequest request)
            throws SQLException
    {
        StringBuffer query = new StringBuffer();
        PreparedStatement stmt=null;
        try
        {
            // add one
            query.append("insert into " + itsTableName + " values(");
            String appendstuff = "";
            for (int i=0; i < itsColumns.length; i++)
            {
                query.append(itsColumns[i]);
                appendstuff+="?";
                if (i != itsColumns.length - 1)
                {
                    query.append(",");
                    appendstuff+=",";
                }
            }
            query.append(") (" + appendstuff + ")");

            stmt=itsConnection.prepareStatement(query.toString());
            for (int i=0; i < itsColumns.length; i++)
            {
                if (itsColumns[i].equals(OWNER))
                {
                    stmt.setString(i + 1, itsPlayerName);
                }
                else
                {
                    stmt.setString(i + 1, request.getParameter(itsColumns[i]));
                }
            }

            stmt.executeUpdate(query.toString());
        }
        finally
        {
            if (stmt != null) {stmt.close();}
        }
    }

}
