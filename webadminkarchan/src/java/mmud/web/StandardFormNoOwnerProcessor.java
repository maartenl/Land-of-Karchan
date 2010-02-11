/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.web;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.text.DateFormat;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author maartenl
 */
public class StandardFormNoOwnerProcessor extends StandardFormProcessor {

    public StandardFormNoOwnerProcessor(String aTablename, String aPlayerName)
    throws SQLException
    {
       super(aTablename, aPlayerName);
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
            boolean accessGranted = true;

           if (!rst.getString(itsColumns[0]).equals(request.getParameter("id")))
           {
               // put the list here
               if (accessGranted)
                {
                    result.append("<td><a HREF=\"" + itsTableName.replace("mm_", "").toLowerCase() +
                            ".jsp?id=" + rst.getString(itsColumns[0]) + "\">E</a></td>");
                    result.append("<td><a HREF=\"remove_" + itsTableName.replace("mm_", "").toLowerCase() +
                            ".jsp?id=" + rst.getString(itsColumns[0]) + "\">X</a></td>");
                }
                else
                {
                    result.append("<td></td><td></td><td></td>");
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
                    if (itsColumns[i].equals(CREATION))
                    {
                        Date creation = rst.getDate(itsColumns[i]);
                        DateFormat formatter = DateFormat.getInstance();
                        result.append("<td><b>" + itsDisplay[i] + ":</b> " + formatter.format(creation) + "</td>");
                    }
                    if (itsColumns[i].equals("src"))
                    {
                        result.append("<td><b>" + itsDisplay[i] + ":</b> " +
                                rst.getString(itsColumns[i]).replace("&", "&amp;").replace(">","&gt;").replace("<", "&lt;") + "</td>");
                    }
                    else
                    {
                        result.append("<td><b>" + itsDisplay[i] + ":</b> " + rst.getString(itsColumns[i]) + "</td>");
                    }
                }
                result.append("</tr>");
           }
           else
            {
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
        rst.close();
        stmt.close();
        result.append("</table>");

        return result.toString();
    }


    
}
