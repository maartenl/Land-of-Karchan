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

import mmud.JsonUtils;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Guild;

import java.time.LocalDateTime;

public class AdminGuildmember
{
  public static final String GET_QUERY = """
      select json_object("name", mm_usertable.name, "guildrank", mm_guildranks.title)
      from mm_usertable
               left join mm_guildranks
                         on (mm_usertable.guildlevel = mm_guildranks.guildlevel and mm_usertable.guild = mm_guildranks.guildname)
      where mm_usertable.guild = ?1
      order by mm_usertable.name
      """;

  public String name;
  public String guildrank;

  public AdminGuildmember()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminGuildmember(User item)
  {
    this.name = item.getName();
    this.guildrank = item.getGuildrank().getTitle();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminGuildmember fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminGuildmember.class);
  }
}
