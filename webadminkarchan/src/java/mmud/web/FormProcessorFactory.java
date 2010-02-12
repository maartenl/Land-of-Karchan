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

/**
 *
 * @author Gebruiker
 */
public class FormProcessorFactory {

    /**
     * Defeat instantiation.
     */
    private FormProcessorFactory()
    {    
    }

    public static FormProcessor create(String aTableName, String aPlayerName,
            String[] display, String[] columns)
            throws SQLException
    {
        return create(aTableName, aPlayerName, display, columns, null);
    }

    public static FormProcessor create(String aTableName, String aPlayerName,
            String[] display, String[] columns , Formatter formatter)
            throws SQLException
    {
        boolean isOwnerFound = false;
        for (int i = 0; i < columns.length && !isOwnerFound; i++)
        {
            isOwnerFound = "owner".equals(columns[i]);
        }
        FormProcessor result = null;
        if (formatter == null)
        {
            formatter = new StandardFormatter();
        }
        if (isOwnerFound)
        {
            result = new StandardFormProcessor(aTableName, aPlayerName, formatter);
        }
        else
        {
            result = new StandardFormNoOwnerProcessor(aTableName, aPlayerName, formatter);
        }
        result.setColumns(columns);
        result.setDisplayNames(display);
        return result;
    }
}
