/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.web;

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
    {
        return new StandardFormProcessor(aTableName, aPlayerName);
    }
}
