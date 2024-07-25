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
package mmud.commands;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Set your AFK, or remove your AFK. For example "afk Getting some coffeee.".
 *
 * @author maartenl
 */
public class AfkCommand extends NormalCommand
{

  public AfkCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    if (command.equalsIgnoreCase("afk"))
    {
      aUser.setAfk(null);
      CommunicationService.getCommunicationService(aUser).writeMessage("Afk removed.<br/>\n");
    } else
    {
      aUser.setAfk(command.substring("afk ".length()));
      CommunicationService.getCommunicationService(aUser).writeMessage("Afk set.<br/>\n");
    }
    return aUser.getRoom();
  }

}
