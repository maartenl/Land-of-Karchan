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
package mmud.commands.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mmud.commands.*;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.database.enums.Wielding;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Stops you wielding an item. Syntax: unwield from &lt;body
 * position&gt;
 *
 * @author maartenl
 * @see WieldCommand
 */
public class UnwieldCommand extends NormalCommand
{

  public UnwieldCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
    parsed.removeFirst(); // remove "unwield"
    parsed.removeFirst(); // remove "from"
    // determine the appropriate body position entered by the
    // user
    String pos = parsed.getFirst();
    Wielding position = Wielding.parse(pos);
    if (position == null)
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("Cannot wield something there.<br/>\r\n");
      return aUser.getRoom();
    }
    Item item = aUser.wields(position);
    if (item == null)
    {
      switch (position)
      {
        case RIDING:
          CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "You are not riding anything.<br/>\r\n");
          break;
        case LEADING:
          CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "You are not leading anything.<br/>\r\n");
          break;
        default:
          CommunicationService.getCommunicationService(aUser).writeMessage("You are not wielding anything there.<br/>\r\n");
      }
      return aUser.getRoom();
    }
    aUser.wield(null, position);
    switch (position)
    {
      case RIDING:
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME stop%VERB2 riding "
          + item.getDescription()
          + ".<br/>\r\n");
        break;
      case LEADING:
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME stop%VERB2 leading "
          + item.getDescription()
          + " along.<br/>\r\n");
        break;
      default:
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME unwield%VERB2 "
          + item.getDescription() + " from " + position.toString()
          + ".<br/>\r\n");
    }

    return aUser.getRoom();
  }
}
