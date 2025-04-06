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

import java.time.LocalDateTime;

import mmud.JsonUtils;
import mmud.database.entities.game.BoardMessage;

public class AdminBoardMessage
{
  public Long id;
  public Long boardid;
  public String name;
  public LocalDateTime posttime;
  public String message;
  public Boolean removed;
  public Boolean offensive;
  public Boolean pinned;

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
    this.offensive = item.getOffensive();
    this.pinned = item.getPinned();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminBoardMessage fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminBoardMessage.class);
  }
}
