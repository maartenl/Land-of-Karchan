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

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.exceptions.ParseException;
import mmud.services.CommunicationService;
import mmud.services.ItemService;
import mmud.services.PersonCommunicationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static mmud.database.OutputFormatter.startWithCapital;

/**
 * /**
 * Empty a container in your inventory: "empy sack". Requirements
 * for it to be successfull:
 * <ul>
 * <li>the container must be in your inventory
 * <li>the container must be a container
 * </ul>
 * The possible syntax can range from: "empty sack" to
 * "empty new leather beaten sack".
 *
 * @see RetrieveCommand
 */
public class EmptyCommand extends NormalCommand implements ItemCommand
{

  public EmptyCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    // first is find the item
    List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
    parsed.removeFirst(); // remove "empty"
    final PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    // find the container on ourselves
    List<Item> containerFound = aUser.findItems(parsed);
    if (containerFound.isEmpty())
    {
      communicationService.writeMessage("No containers found in your inventory that match that description.<br/>\n");
      return aUser.getRoom();
    }
    Item container = containerFound.getFirst();
    if (!container.isContainer())
    {
      communicationService.writeMessage(container.getDescription() + " is not a container.<br/>\n");
      return aUser.getRoom();
    }
    if (!container.isOpen())
    {
      communicationService.writeMessage(container.getDescription() + " is closed.<br/>\n");
      return aUser.getRoom();
    }
    // find the item on ourselves
    Set<Item> itemsFound = container.getItems();
    if (itemsFound.isEmpty())
    {
      communicationService.writeMessage("It is already empty.<br/>\n");
      return aUser.getRoom();
    }
    boolean retrieve = false;
    ItemService itemService = getItemService();
    for (Item item : itemsFound)
    {
      if (item.isGetable())
      {
        // let's get 'em!
        if (!itemService.retrieve(item, container, aUser))
        {
          continue;
        }
        if (!retrieve)
        {
          CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME empty%VERB2 " + container.getDescription() + ".<br/>\r\n");
        }
        retrieve = true;
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME retrieve%VERB2 " + item.getDescription() + ".<br/>\r\n");
      }
    }
    if (!retrieve)
    {
      communicationService.writeMessage("You did not retrieve anything.<br/>");
    } else
    {
      CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, startWithCapital(container.getDescription()) + " is now empty.<br/>\r\n");
    }
    return aUser.getRoom();
  }
}
