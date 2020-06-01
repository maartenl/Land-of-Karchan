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
import mmud.database.entities.game.BoardMessage;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDateTime;

public class AdminBoardMessage
{
  public Long id;
  public Long boardid;
  public String name;
  public LocalDateTime posttime;
  public String message;
  public Boolean removed;

  public AdminBoardMessage()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminBoardMessage(BoardMessage item)
  {
    this.id = item.getId();
    this.boardid = item.getBoard().getId();
    this.name = item.getPerson().getName();
    this.posttime = item.getPosttime();
    this.message = item.getMessage();
    this.removed = item.getRemoved();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminBoardMessage fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminBoardMessage.class);
  }
}
