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
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import mmud.database.entities.game.UserCommand;

/**
 *
 * @author maartenl
 */
public class AdminUserCommand
{

  public static String GET_QUERY = "select json_object(\"id\", id, \"command\", command,  \"methodName\", method_name, \"room\", room, \"owner\", owner, \"creation\", creation) "
          + "from mm_commands r "
          + "order by r.command";

  public Integer id;
  public Boolean callable;
  public String command;
  public String methodName;
  public Long room;
  public LocalDateTime creation;
  public String owner;

  public AdminUserCommand()
  {
    // empty constructor, for creating a AdminMethod from scratch.
  }

  public AdminUserCommand(UserCommand userCommand)
  {
    id = userCommand.getId();
    callable = userCommand.getCallable();
    command = userCommand.getCommand();
    methodName = userCommand.getMethodName().getName();
    room = userCommand.getRoom() != null ? userCommand.getRoom().getId() : null;

    creation = userCommand.getCreation();
    owner = userCommand.getOwner() != null ? userCommand.getOwner().getName() : null;
  }

  public String toJson()
  {
    return JsonbBuilder.create().toJson(this);
  }

  public static AdminUserCommand fromJson(String json)
  {
    Jsonb jsonb = JsonbBuilder.create();
    AdminUserCommand adminUserCommand = jsonb.fromJson(json, AdminUserCommand.class);
    return adminUserCommand;
  }
}
