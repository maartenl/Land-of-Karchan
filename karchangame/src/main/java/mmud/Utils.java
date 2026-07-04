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
package mmud;

import jakarta.ws.rs.core.Response;
import mmud.database.entities.characters.Person;
import mmud.exceptions.MudWebException;

public class Utils
{

  private Utils()
  {
  }

  private static boolean offline = false;

  /**
   * Indicates the game is offline for maintenance.
   *
   * @return usually false, indicating the game is live.
   */
  public static boolean isOffline()
  {
    return Utils.offline;
  }

  public static void setOffline(boolean offline)
  {
    Utils.offline = offline;
  }

  public static void throwIfOffline(Person person) {
    if (Utils.isOffline() && !person.isGod())
    {
      throw new MudWebException(person.getName(), "Game is offline", Response.Status.BAD_REQUEST);
    }
  }

  /**
   * split up the command into different words.
   *
   * @param aCommand String containing the command
   * @return String array where each String contains a word from the command.
   */
  public static String[] parseCommand(String aCommand)
  {
    return aCommand.split("( )+", 50);
  }
}
