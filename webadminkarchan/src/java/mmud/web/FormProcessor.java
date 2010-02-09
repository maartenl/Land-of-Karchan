/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mmud.web;

import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Gebruiker
 */
public interface FormProcessor {

    void addEntry(HttpServletRequest request) throws SQLException;

    String getList(HttpServletRequest request) throws SQLException;

    void removeEntry(HttpServletRequest request) throws SQLException;

    void removeOwnershipFromEntry(HttpServletRequest request) throws SQLException;

    /**
     * @param itsColums the itsColums to set
     */
    void setColums(String[] itsColums);

    /**
     * @param itsDisplay the itsDisplay to set
     */
    void setDisplayNames(String[] itsDisplay);

}
