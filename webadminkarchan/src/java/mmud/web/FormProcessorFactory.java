/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
            String[] display, String[] columns )
            throws SQLException
    {
        boolean isOwnerFound = false;
        for (int i = 0; i < columns.length && !isOwnerFound; i++)
        {
            isOwnerFound = "owner".equals(columns[i]);
        }
        FormProcessor result = null;
        if (isOwnerFound)
        {
            result = new StandardFormProcessor(aTableName, aPlayerName);
        }
        else
        {
            result = new StandardFormNoOwnerProcessor(aTableName, aPlayerName);            
        }
        result.setColumns(columns);
        result.setDisplayNames(display);
        return result;
    }
}
