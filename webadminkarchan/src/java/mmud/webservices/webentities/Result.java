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
package mmud.webservices.webentities;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A result message, used by Sencha. Has XmlRootElement property, for proper formatting
 * into JSON.
 * @author maartenl
 */
@XmlRootElement
public class Result {

    private String errorMessage;

    /**
     * Get the value of errorMessage
     *
     * @return the value of errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the value of errorMessage
     *
     * @param errorMessage new value of errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the result.
     * @return an Object.
     */
    public LogonMessage getData() {
        return data;
    }

    /**
     * Set the result.
     * @param data the object which indicates the result.
     */
    public void setData(LogonMessage data) {
        this.data = data;
    }

    /**
     * Has the result been successfully determined?
     * @return false, if problems, true if success.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set the success of the result retrieval.
     * @param success true, if successful, false otherwise.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Constructor
     * @param success
     * @param data
     */
    public Result(LogonMessage data) {
        this.success = true;
        this.data = data;
    }

    /**
     * Constructor
     * @param errorMessage
     */
    public Result(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    /**
     * Default constructor. Constructs
     * a successfull, albeit empty result object.
     */
    public Result() {
    }

    private boolean success = true;
    private LogonMessage data = null;
}
