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
import mmud.services.PersonCommunicationService;

/**
 * Starts you wielding an item. Syntax: wield &lt;item&gt; with
 * &lt;lefthand|righthand|both|hands|bothhands|riding|leading&gt;
 *
 * @author maartenl
 * @see UnwieldCommand
 */
public class WieldCommand extends NormalCommand implements ItemCommand
{

  public WieldCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
    parsed.removeFirst(); // remove "wield"
    // determine the appropriate body position entered by the
    // user
    String pos = parsed.getLast();
    Wielding position = Wielding.parse(pos);
    final PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    if (position == null)
    {
      communicationService.writeMessage("Cannot wield something there.<br/>\r\n");
      return aUser.getRoom();
    }
    Item item = aUser.wields(position);
    if (item != null)
    {
      switch (position)
      {
        case RIDING:
          CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "You are already riding something.<br/>\r\n");
          break;
        case LEADING:
          CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "You are already leading something.<br/>\r\n");
          break;
        default:
          CommunicationService.getCommunicationService(aUser).writeMessage("You are already wielding something there.<br/>");
      }
      return aUser.getRoom();
    }
    // find the item on ourselves
    parsed.removeLast();
    parsed.removeLast();
    List<Item> itemsFound = aUser.findItems(parsed);
    if (itemsFound.isEmpty())
    {
      communicationService.writeMessage("You don't have that.<br/>\n");
      return aUser.getRoom();
    }
    item = itemsFound.getFirst();
    if (!item.isWieldable(position))
    {
      switch (position)
      {
        case RIDING:
          CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "You cannot ride that.<br/>\r\n");
          break;
        case LEADING:
          CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "You cannot lead that.<br/>\r\n");
          break;
        default:
          CommunicationService.getCommunicationService(aUser).writeMessage("You cannot wield that there.<BR>\n");
      }
      return aUser.getRoom();
    }
    if (!aUser.unused(item))
    {
      communicationService.writeMessage("The item is already being used.<BR>\r\n");
      return aUser.getRoom();
    }
    aUser.wield(item, position);
    switch (position)
    {
      case RIDING:
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME ride%VERB2 "
          + item.getDescription()
          + ".<br/>\r\n");
        break;
      case LEADING:
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME lead%VERB2 "
          + item.getDescription()
          + ".<br/>\r\n");
        break;
      default:
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME wield%VERB2 "
          + item.getDescription() + " " + position.toString()
          + ".<br/>\r\n");
    }

    return aUser.getRoom();
  }
}
