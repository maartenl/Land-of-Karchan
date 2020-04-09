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


import mmud.database.entities.game.Event;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDateTime;

/**
 * @author maartenl
 * @see Event
 */
public class AdminEvent
{
  public static final String GET_QUERY = "select json_object(\"eventid\", eventid, \"name\", name, \"methodname\", method_name, \"room\", room, \"owner\", owner, \"creation\", creation) from mm_events order by eventid";

  public Integer eventid;
  public String name;
  public Integer month;
  public Integer dayofmonth;
  public Integer hour;
  public Integer minute;
  public Integer dayofweek;
  public Boolean callable;
  public Long room;
  public LocalDateTime creation;
  public String owner;
  public String methodname;

  public AdminEvent()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminEvent(Event item)
  {
    this.eventid = item.getEventid();
    this.name = item.getPerson() == null ? null : item.getPerson().getName();
    this.month = item.getMonth();
    this.dayofmonth = item.getDayofmonth();
    this.hour = item.getHour();
    this.minute = item.getMinute();
    this.dayofweek = item.getDayofweek();
    this.callable = item.getCallable();
    this.room = item.getRoom() == null ? null : item.getRoom().getId();
    this.methodname = item.getMethod() == null ? null : item.getMethod().getName();
    this.creation = item.getCreation();
    this.owner = item.getOwner() == null ? null : item.getOwner().getName();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminEvent fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminEvent.class);
  }
}
