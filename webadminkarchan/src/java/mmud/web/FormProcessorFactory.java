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

    public static FormProcessor create(String aTableName, String aPlayerName)
            throws SQLException
    {
        return new StandardFormProcessor(aTableName, aPlayerName);
    }
}
