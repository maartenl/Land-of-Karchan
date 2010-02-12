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
public class BigFormatter implements Formatter {

    public BigFormatter() {
    }

    private StringBuffer theContent = new StringBuffer("<table>");
    private boolean firstRow = true;

    @Override
    public String returnOptionsString(String tableName, String id) {
        StringBuffer result = new StringBuffer();
        if (tableName == null || id == null)
        {
            result.append("<br/>");
        }
        else
        {
            result.append("<a HREF=\"" + tableName.replace("mm_", "").toLowerCase() +
                    ".jsp?id=" + id + "\">E</a> ");
            result.append("<a HREF=\"remove_" + tableName.replace("mm_", "").toLowerCase() +
                    ".jsp?id=" + id + "\">X</a> ");
            result.append("<a HREF=\"remove_ownership.jsp?id=" +
                    id + "&table=" + tableName.replace("mm_", "") + "\">O</a>");
        }
        return result.toString();
    }

    public void addRow() {
        if (!firstRow)
        {
            theContent.append("</td></tr>");
        }
        theContent.append("<tr><td>");
        firstRow = false;
    }

    public void addRowString(String display, String value) {
        theContent.append("<b>" + display + ": </b>" +
                value + "<br/>");
    }

    public void addRowItem(String item) {
        theContent.append("" + item + "<br/>");
    }

    public String toString() {
        if (firstRow)
        {
            return theContent.toString() + "</table>";
        }
        return theContent.toString() + "</td></tr></table>";
    }

    public void addRowBoolean(String display, boolean value) {
        theContent.append("<b>" + display + ": </b>" +
                (value ? "Yes" : "No") + "<br/>");
    }

    public void addRowDate(String display, Date value) {
        DateFormat formatter = DateFormat.getInstance();
        theContent.append("<b>" + display + ":</b> " + formatter.format(value) + "<br/>");
    }


}
