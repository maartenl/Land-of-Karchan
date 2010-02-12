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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.NamingException;

/**
 *
 * @author maartenl
 */
public abstract class BaseFormProcessor implements FormProcessor {
    public static final String CREATION = "creation";
    public static final String OWNER = "owner";
    protected String[] itsColumns;
    protected Connection itsConnection;
    protected String[] itsDisplay;
    protected Formatter itsFormatter;
    protected String itsPlayerName;
    protected String itsTableName;

    /**
     * Initialises the object with a connection to the database.
     */
    public BaseFormProcessor(String aTableName, String aPlayerName, Formatter formatter)
    throws SQLException
    {
        if (aTableName == null || aTableName.trim().equals(""))
        {
            throw new RuntimeException("aTableName is empty.");
        }
        if (aPlayerName == null || aPlayerName.trim().equals(""))
        {
            throw new RuntimeException("aPlayerName is empty.");
        }
        if (formatter == null)
        {
            throw new RuntimeException("formatter is not found.");
        }
        itsTableName = aTableName;
        itsPlayerName = aPlayerName;
        itsFormatter = formatter;

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
        checkAuthorization();
    }


    /**
     * <pre>insert into mm_areas values(area, desc, shordesc)
     * (?, ?, ?)</pre>
     * @param request
     * @throws SQLException
     */
    public abstract void addEntry(HttpServletRequest request) throws SQLException;

    /**
     * Sends an update statement, in the following form:
     * <p/>
     * <pre>update mm_areas set desc = ?, short = ?
     * where (owner = "" or owner = null or owner = ?) and area = ?</pre>
     * And fills it up with:
     * <ul><li>desc
     * <li>short
     * <li>itsPlayerName
     * <li>area
     * </ul>
     * @param request
     * @throws SQLException
     */
    public abstract void changeEntry(HttpServletRequest request) throws SQLException;

    public void checkAuthorization() throws SQLException {
        ResultSet rst = null;
        PreparedStatement stmt = null;
        try {
            // ===============================================================================
            // begin authorization check
            stmt = itsConnection.prepareStatement("select * from mm_admin where name =\t\'" + itsPlayerName + "\' and validuntil >= now()"); //  and mm_usertable.lok = '" + itsPlayerSessionId + "'
            rst = stmt.executeQuery();
            if (!rst.next()) {
                // error getting the info, user not found?
                throw new RuntimeException("Cannot find " + itsPlayerName + " in the database!");
            }
            // end authorization check
            // ===============================================================================
        } finally {
            if (rst != null) {
                rst.close();
            }
            stmt.close();
        }
    }

    public void closeConnection() throws SQLException {
        // itsConnection.commit();
        itsConnection.close();
    }

    public String getList(HttpServletRequest request) throws SQLException {
        return getList(request, false);
    }

    public abstract String getList(HttpServletRequest request, boolean newLines) throws SQLException;

    /**
     * <pre>delete from mm_areas where
     * where (owner = "" or owner = null or owner = ?) and area = ?</pre>
     * @param request
     * @throws SQLException
     */
    public abstract void removeEntry(HttpServletRequest request) throws SQLException;

    /**
     * Sends an update statement, removing the ownership from a table row.
     * <p/>
     * <pre>update mm_areas set ownership = null
     * where (owner = "" or owner = null or owner = ?) and area = ?</pre>
     * @param request
     * @throws SQLException
     */
    @Override
    public abstract void removeOwnershipFromEntry(HttpServletRequest request) throws SQLException;

    public void setColumns(String[] itsColumns) {
        if (itsDisplay != null && itsColumns.length != itsDisplay.length) {
            throw new RuntimeException("Design failure. Not equivalent number of names and columns.");
        }
        this.itsColumns = itsColumns;
    }

    public void setDisplayNames(String[] itsDisplay) {
        if (itsColumns != null && itsColumns.length != itsDisplay.length) {
            throw new RuntimeException("Design failure. Not equivalent number of names and columns.");
        }
        this.itsDisplay = itsDisplay;
    }

}
