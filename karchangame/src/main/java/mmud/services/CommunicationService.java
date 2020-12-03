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
package mmud.services;

import com.google.common.annotations.VisibleForTesting;
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;

import java.util.function.Consumer;

/**
 *
 * @author maartenl
 */
public interface CommunicationService
{

  /**
   * Returns the communication service, based on a person.
   * @param person the person to base the communication on.
   * @return the service
   */
  static PersonCommunicationService getCommunicationService(Person person)
  {
    return new PersonCommunicationService(person, null);
  }

  /**
   * Returns the communication service, based on a person.
   * @param person the person to base the communication on.
   * @return the service
   */
  @VisibleForTesting
  static PersonCommunicationService getCommunicationService(Person person, Consumer<String> consumer)
  {
    return new PersonCommunicationService(person, consumer);
  }

  /**
   * Returns the communication service, based on a room.
   * @param room the room to base the communication on.
   * @return the service
   */
  static RoomCommunicationService getCommunicationService(Room room)
  {
    return new RoomCommunicationService(room);
  }

  /**
   * Returns the communication service, based on a guild.
   * @param guild the guild to base the communication on.
   * @return the service
   */
  static GuildCommunicationService getCommunicationService(Guild guild)
  {
    return new GuildCommunicationService(guild);
  }

}
