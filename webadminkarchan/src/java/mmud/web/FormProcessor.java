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

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Gebruiker
 */
public interface FormProcessor {


    /**
     * Closes the JDBC connection safely.
     * @throws SQLException
     */
    void closeConnection() throws SQLException;

    /**
     * Checks if the person who is logged on (LoginPrincipal) has enough
     * rights to do stuff. I.e. if the 'owner' field in the table matches
     * his identity.
     * @throws SQLException
     */
    void checkAuthorization() throws SQLException;

    /**
     * Add an entry to the table.
     * @param request
     * @throws SQLException
     */
    void addEntry(HttpServletRequest request) throws SQLException;

    /**
     * Change an entry of the table.
     * @param request
     * @throws SQLException
     */
    void changeEntry(HttpServletRequest request) throws SQLException;

    /**
     * Show a list of the table in different forms in html.
     * @param request
     * @return a string depicting the table list.
     * @throws SQLException
     */
    String getList(HttpServletRequest request) throws SQLException;

    /**
     * Shows a list of the table in different forms in the html,
     * but based on a non-standard query.
     * @param request
     * @param query the query to use to retrieve information.
     * @return a list of the table in html.
     * @throws SQLException
     */
    String getList(HttpServletRequest request, String query) throws SQLException;

    /**
     * Remove an entry from the table.
     * @param request
     * @throws SQLException
     */
    void removeEntry(HttpServletRequest request) throws SQLException;

    /**
     * Remove the ownership of an entry in the table. The only way this can be
     * done if you yourself are the owner. Bear in mind that, once you do this,
     * everyone with admin rights has access to the object.
     * @param request
     * @throws SQLException
     */
    void removeOwnershipFromEntry(HttpServletRequest request) throws SQLException;

    /**
     * Just sets the columns, so we know what table columns to get from
     * the query. These must match up with the display names.
     * @param itsColums the itsColums to set
     */
    void setColumns(String[] itsColums);

    /**
     * Just sets the names that are going to be displayed.
     * These must match up with the column names.
     * @param itsDisplay the itsDisplay to set
     */
    void setDisplayNames(String[] itsDisplay);

}
