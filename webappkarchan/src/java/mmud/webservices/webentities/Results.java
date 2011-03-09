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


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A result message containing one or more results in an appopriate array.
 * Has XmlRootElement property, for proper formatting
 * into JSON.
 * @author maartenl
 */
@XmlRootElement
public class Results {

    /**
     * Get the list
     *
     * @return the list
     */
    public List<String> getList() {
        return list;
    }

    /**
     * Set the list
     *
     * @param list 
     */

    public void setList(List<String> list) {
        this.list = list;
    }

    private String errorMessage;
    
    private List<String> list = new ArrayList<String>();

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
        this.success = false;
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
     * @param aList the list
     */
    public Results(ArrayList<String> aList) {
        this.list = aList;
        this.success = true;
    }

    /**
     * Constructor
     * @param errorMessage
     */
    public Results(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    /**
     * Default constructor. Constructs
     * a successfull, albeit empty result MmudObject.
     */
    public Results() {
    }

    private boolean success = false;

    @Override
    public String toString()
    {
        if (errorMessage != null)
        {
            return "success=" + success + ", errorMessage=" + errorMessage;
        }
        return "success=" + success + ", list=" + list;
    }
}
