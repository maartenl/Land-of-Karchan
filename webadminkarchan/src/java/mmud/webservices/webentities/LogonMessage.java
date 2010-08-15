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

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * One logon message. Has XmlRootElement property, for proper formatting
 * into JSON.
 * @author maartenl
 */
@XmlRootElement
public class LogonMessage extends MmudObject {

    public LogonMessage() {
    }
    private String name;
    private String message;
    private Date datetime;

    /**
     * Simple constructor.
     * @param name
     * @param message
     * @param datetime
     */
    public LogonMessage(String name, String message, Date datetime)
    {
        this.name = name;
        this.message = message;
        this.datetime = datetime;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the datetime
     */
    public Date getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     */
    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString()
    {
        return name + message + datetime;
    }
}
