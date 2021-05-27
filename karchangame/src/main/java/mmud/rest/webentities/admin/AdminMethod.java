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
import mmud.database.entities.game.Method;

/**
 * @author maartenl
 */
public class AdminMethod
{
  public static String GET_QUERY = "select json_object(\"name\", name, \"owner\", owner, \"creation\", creation) "
    + "from mm_methods r "
    + "order by r.name";

  public String name;
  public String src;
  public LocalDateTime creation;
  public String owner;

  public AdminMethod()
  {
    // empty constructor, for creating a AdminMethod from scratch.
  }

  public AdminMethod(Method method)
  {
    creation = method.getCreation();
    name = method.getName();
    src = method.getSrc();
    owner = method.getOwner() != null ? method.getOwner().getName() : null;
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static AdminMethod fromJson(String json)
  {
    return JsonUtils.fromJson(json, AdminMethod.class);

  }
}
