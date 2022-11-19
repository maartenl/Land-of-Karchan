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
import mmud.database.entities.game.Log;

public class AdminLog
{
  public Long id;
  public String name;
  public String message;
  public boolean deputy;
  public String addendum;
  public LocalDateTime creation;

  public AdminLog()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminLog(Log item)
  {
    this.id = item.getId();
    this.name = item.getName();
    this.message = item.getMessage();
    this.addendum = item.getAddendum();
    this.deputy = item.getDeputy();
    this.creation = item.getCreation();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminLog fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminLog.class);
  }
}
