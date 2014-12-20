/*
 *  Copyright (C) 2012 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.rest.webentities.admin;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Event;

/**
 * @see Event
 * @author maartenl
 */
@XmlRootElement
public class AdminEvent
{

    public Integer eventid;
    public String person;
    public Integer month;
    public Integer dayofmonth;
    public Integer hour;
    public Integer minute;
    public Integer dayofweek;
    public Boolean callable;
    public Integer room;
    public Date creation;
    public Admin owner;
    public String method;
}
