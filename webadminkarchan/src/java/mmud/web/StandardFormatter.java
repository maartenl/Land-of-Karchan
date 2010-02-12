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

/**
 *
 * @author maartenl
 */
public class StandardFormatter implements Formatter {

    private StringBuffer theContent = new StringBuffer("<table>");
    private boolean firstRow = true;

    public StandardFormatter() {
    }

    @Override
    public String returnOptionsString(String tableName, String id) {
        StringBuffer result = new StringBuffer();
        if (tableName == null || id == null)
        {
            result.append("<td></td>");
        }
        else
        {
            result.append("<a HREF=\"" + tableName.replace("mm_", "").toLowerCase() +
                    ".jsp?id=" + id + "\">E</a></td> ");
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
            theContent.append("</tr>");
        }
        theContent.append("<tr>");
        firstRow = false;
    }

    public void addRowString(String display, String value) {
        theContent.append("<td><b>" + display + ": </b>" +
                value + "</td>");
    }

    public void addRowItem(String item) {
        theContent.append("<td>" + item + "</td>");
    }

    public String getTable() {
        if (firstRow)
        {
            return theContent.toString() + "</table>";
        }
        return theContent.toString() + "</tr></table>";
    }

    public void addRowBoolean(String display, boolean value) {
        theContent.append("<td><b>" + display + ": </b>" +
                (value ? "Yes" : "No") + "</td>");
    }

    public void addRowDate(String display, Date value) {
        DateFormat formatter = DateFormat.getInstance();
        theCOntent.append("<td><b>" + display + ":</b> " + formatter.format(value) + "</td>");
    }


}
