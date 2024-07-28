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
package mmud.rest.webentities;

import jakarta.json.bind.annotation.JsonbCreator;
import mmud.Constants;
import mmud.JsonUtils;
import mmud.database.entities.characters.Position;
import mmud.database.entities.game.Room;

import java.util.List;

/**
 * @author maartenl
 */
public record PrivateRoom(Long roomid, String image, String title, String content, Long west, Long east, Long north,
                          Long south, Long up, Long down, List<PrivatePerson> persons, List<PrivateItem> items, Position minimum, Position maximum)
{

  @JsonbCreator
  public PrivateRoom
  {
    // empty constructor, because I need to put the annotation @JsonbCreator somewhere.
  }

  public PrivateRoom(Room room)
  {
    this(room.getId(), room.getImage(), room.getTitle(), room.getContents(),
        room.getWest() == null ? null : room.getWest().getId(),
        room.getEast() == null ? null : room.getEast().getId(),
        room.getNorth() == null ? null : room.getNorth().getId(),
        room.getSouth() == null ? null : room.getSouth().getId(),
        room.getUp() == null ? null : room.getUp().getId(),
        room.getDown() == null ? null : room.getDown().getId(),
        room.getPersons(null)
            .stream()
            .map(PrivatePerson::createSimplePrivatePerson)
            .toList(),
        Constants.addInventoryForRoom(room.getItems()),
        room.getMinimumPoint(),
        room.getMaximumPoint()
    );
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static PrivateRoom fromJson(String json)
  {
    return JsonUtils.fromJson(json, PrivateRoom.class);
  }

}
