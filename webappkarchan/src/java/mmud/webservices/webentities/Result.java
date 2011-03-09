package mmud.webservices.webentities;

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


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
        this.success = (errorMessage == null);
        this.errorMessage = errorMessage;
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
     * @param errorMessage
     */
    public Result(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = (errorMessage == null);
    }

    /**
     * Default constructor. Constructs
     * a successfull, albeit empty result MmudObject.
     */
    public Result() {
    }

    private boolean success = false;

    @Override
    public String toString()
    {
        if (errorMessage != null)
        {
            return "success=" + success + ", errorMessage=" + errorMessage;
        }
        return "success=" + success;
    }
}
