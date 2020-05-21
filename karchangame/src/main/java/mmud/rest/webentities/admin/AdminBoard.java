/*
 *  Copyright (C) 2020 maartenl
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package mmud.rest.webentities.admin;

import mmud.database.entities.game.Board;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDateTime;

public class AdminBoard
{
  public static final String GET_QUERY = "select json_object(\"id\", id, \"name\", name, \"room\", room, \"owner\", owner, \"creation\", creation) from mm_boards order by id";

  // TODO: make this a Long
  public Long id;
  public String name;
  public String description;
  public Long room;
  public LocalDateTime creation;
  public String owner;

  public AdminBoard()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminBoard(Board item)
  {
    this.id = item.getId();
    this.description = item.getDescription();
    this.name = item.getName();
    this.room = item.getRoom() == null ? null : item.getRoom().getId();
    this.creation = item.getCreation();
    this.owner = item.getOwner() == null ? null : item.getOwner().getName();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminBoard fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminBoard.class);
  }
}
