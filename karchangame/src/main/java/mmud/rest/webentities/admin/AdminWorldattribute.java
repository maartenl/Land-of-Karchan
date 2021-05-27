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

import java.time.LocalDateTime;

import mmud.JsonUtils;
import mmud.database.entities.game.Worldattribute;

/**
 * See {@link Worldattribute}
 *
 * @author maartenl
 */
public class AdminWorldattribute
{
  public static final String GET_QUERY = "select json_object(\"name\", name, \"type\", type, \"owner\", owner, \"creation\", creation) "
    + "from mm_worldattributes r "
    + "order by r.name";

  public String name;
  public String contents;
  public String type;
  public String owner;
  public LocalDateTime creation;

  public AdminWorldattribute()
  {
    // empty constructor, for creating a AdminBlog from scratch.
  }

  public AdminWorldattribute(Worldattribute worldattribute)
  {
    name = worldattribute.getName();
    type = worldattribute.getType();
    contents = worldattribute.getContents();
    owner = worldattribute.getOwner() == null ? null : worldattribute.getOwner().getName();
    creation = worldattribute.getCreation();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminWorldattribute fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminWorldattribute.class);
  }
}
