/*
 * Copyright (C) 2019 maartenl
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

import java.time.LocalDateTime;

import mmud.JsonUtils;
import mmud.database.entities.game.Room;

/**
 * @author maartenl
 */
public class AdminRoom
{

  public static String GET_QUERY = "select json_object(\"id\", id, \"area\", area,  \"title\", title, \"owner\", owner, \"creation\", creation) "
    + "from mm_rooms r "
    + "order by r.id";

  public static String GET_SEARCH_QUERY = "select json_object(\"id\", id, \"area\", area,  \"title\", title, \"owner\", owner, \"creation\", creation) "
    + "from mm_rooms r "
    + "where r.contents like ? "
    + "order by r.id";

  public Long id;
  public String contents;
  public String area;
  public Long down;
  public Long up;
  public Long east;
  public Long west;
  public Long south;
  public Long north;
  public String picture;
  public String title;
  public LocalDateTime creation;
  public String owner;

  public AdminRoom()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminRoom(Room room)
  {
    id = room.getId();
    creation = room.getCreation();
    title = room.getTitle();
    contents = room.getContents();
    picture = room.getPicture();
    owner = room.getOwner() != null ? room.getOwner().getName() : null;
    if (room.getUp() != null)
    {
      up = room.getUp().getId();
    }
    if (room.getDown() != null)
    {
      down = room.getDown().getId();
    }
    if (room.getWest() != null)
    {
      west = room.getWest().getId();
    }
    if (room.getEast() != null)
    {
      east = room.getEast().getId();
    }
    if (room.getNorth() != null)
    {
      north = room.getNorth().getId();
    }
    if (room.getSouth() != null)
    {
      south = room.getSouth().getId();
    }
    if (room.getArea() != null)
    {
      area = room.getArea().getArea();
    }
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminRoom fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminRoom.class);
  }
}
