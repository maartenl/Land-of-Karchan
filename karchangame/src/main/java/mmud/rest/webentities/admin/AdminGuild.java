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
import mmud.database.entities.game.Guild;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.LocalDateTime;

public class AdminGuild
{
  public static final String GET_QUERY = "select json_object(\"name\", name, \"title\", title, \"bossname\", bossname, \"owner\", owner, \"creation\", creation) from mm_guilds order by name";

  public String name;
  public String title;
  public String guilddescription;
  public String guildurl;
  public String bossname;
  public String logonmessage;
  public String colour;
  public String imageurl;
  public LocalDateTime creation;
  public String owner;

  public AdminGuild()
  {
    // empty constructor, for creating a web entity from scratch.
  }

  public AdminGuild(Guild item)
  {
    this.name = item.getName();
    this.title = item.getTitle();
    this.guilddescription = item.getDescription();
    this.guildurl = item.getHomepage();
    this.bossname = item.getBoss() == null ? null : item.getBoss().getName();
    this.logonmessage = item.getLogonmessage();
    this.colour = item.getColour();
    this.imageurl = item.getImage();
    this.creation = item.getCreation();
    this.owner = item.getOwner() == null ? null : item.getOwner().getName();
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminGuild fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    return jsonb.fromJson(json, AdminGuild.class);
  }
}
