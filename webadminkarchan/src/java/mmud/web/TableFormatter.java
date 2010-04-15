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

import java.text.DateFormat;
import java.util.Date;

/**
 *
 * @author maartenl
 */
public class TableFormatter implements Formatter {

    private StringBuffer theContent = new StringBuffer();
    private boolean starting = true;
    private boolean firstrow = true;

    StringBuffer header = new StringBuffer("<table>");

    public TableFormatter() {
    }

    @Override
    public String returnOptionsString(String tableName, String id) {
        StringBuffer result = new StringBuffer();
        if (tableName == null || id == null)
        {
            result.append("");
        }
        else
        {
            result.append("<a HREF=\"" + tableName.replace("mm_", "").toLowerCase() +
                    ".jsp?id=" + id + "\"><img src=\"/images/icons/ok-icon.png\" alt=\"Edit Object\" style=\"width:1.8em;\"/></a> ");
            result.append("<a HREF=\"remove_" + tableName.replace("mm_", "").toLowerCase() +
                    ".jsp?id=" + id + "\"><img src=\"/images/icons/cross-icon.png\" alt=\"Delete Object\" style=\"width:1.8em;\"/></a> ");
            result.append("<a HREF=\"remove_ownership.jsp?id=" +
                    id + "&table=" + tableName.replace("mm_", "") + "\"><img src=\"/images/icons/minus-icon.png\" alt=\"Remove Ownership\" style=\"width:1.8em;\" /></a>");
        }
        return result.toString();
    }

    public void addRow() {
        if (!starting)
        {
            theContent.append("</tr>");
            firstrow = false;
        }
        theContent.append("<tr>");
        starting = false;
    }

    public void addRowString(String display, String value) {
        if (firstrow)
        {
           header.append("<td><b>" + display + "</b></td>");
        }
        theContent.append("<td>" + value + "</td>");
    }

    public void addRowItem(String item) {
        if (firstrow)
        {
           header.append("<td></td>");
        }
        theContent.append("<td>" + item + "</td>");
    }

    public String toString() {
        if (starting)
        {
            return header.toString() + theContent.toString() + "</table>";
        }
        return "<tr>" + header.toString() + "</tr>" + theContent.toString() + "</tr></table>";
    }

    public void addRowBoolean(String display, boolean value) {
        if (firstrow)
        {
           header.append("<td><b>" + display + "</b></td>");
        }
        theContent.append("<td>" +
                (value ? "Yes" : "No") + "</td>");
    }

    public void addRowDate(String display, Date value) {
        if (firstrow)
        {
           header.append("<td><b>" + display + "</b></td>");
        }
        DateFormat formatter = DateFormat.getInstance();
        theContent.append("<td>" + formatter.format(value) + "</td>");
    }


}
