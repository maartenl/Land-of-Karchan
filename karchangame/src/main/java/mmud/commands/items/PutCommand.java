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

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.exceptions.ParseException;
import mmud.services.CommunicationService;
import mmud.services.ItemBean;
import mmud.services.PersonCommunicationService;

/**
 * Put an item into a container: "put ring in sack". Requirements for it to be
 * successful:
 * <ul>
 * <li>the item to put must be in your inventory
 * <li>the container must be in your inventory or in the room
 * <li>the container must be a container
 * </ul>
 * The possible syntax can range from: "put ring in sack" to
 * "put 8 old gold shiny ring in new leather beaten sack".
 *
 * @see RetrieveCommand
 */
public class PutCommand extends NormalCommand
{

    public PutCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        // first is find the item
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "put"
        int amount = 1;
        try
        {
            amount = Integer.parseInt(parsed.get(0));
            parsed.remove(0);
        } catch (NumberFormatException e)
        {// do nothing here, we assume we need to put only one item.
        }
      final PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
        if (amount <= 0)
        {
            communicationService.writeMessage("That is an illegal amount.<br/>\n");
            return aUser.getRoom();
        }
        int pos = 0;
        for (String str : parsed)
        {
            if (str.equalsIgnoreCase("in"))
            {
                break;
            }
            pos++;
        }
        if (pos == parsed.size())
        {
            // no in found.
            throw new ParseException();
        }

        List<String> itemDescription = parsed.subList(0, pos);
        List<String> containerDescription = parsed.subList(pos + 1, parsed.size());
        // find the container on ourselves
        List<Item> containerFound = aUser.findItems(containerDescription);
        if (containerFound.isEmpty())
        {
            // find the item in the room
            containerFound = aUser.getRoom().findItems(containerDescription);
        }
        if (containerFound.isEmpty())
        {
            communicationService.writeMessage("No containers found that match that description.<br/>\n");
            return aUser.getRoom();
        }
        Item container = containerFound.get(0);
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
        List<Item> itemsFound = aUser.findItems(itemDescription);
        if (itemsFound.isEmpty())
        {
            communicationService.writeMessage("You don't have that.<br/>\n");
            return aUser.getRoom();
        }
        if (itemsFound.size() < amount)
        {
            communicationService.writeMessage("You do not have that many items in your inventory.<br/>\r\n");
            return aUser.getRoom();
        }
        boolean put = false;
        ItemBean itemBean = getItemBean();
        for (Item item : itemsFound)
        {
            if (aUser.unused(item) && !item.isBound() && !item.isContainer())
            {
                // item is not used.
                if (!itemBean.put(item, container, aUser))
                {
                    continue;
                }
                CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME put%VERB2 " + item.getDescription() + " in " + container.getDescription() + ".<br/>\r\n");
                put = true;
                amount--;
                if (amount == 0)
                {
                    return aUser.getRoom();
                }
            }
        }
        if (!put)
        {
            communicationService.writeMessage("You did not put anything.<br/>");
        } else
        {
            communicationService.writeMessage("You put some of your items.<br/>\r\n");
        }

        return aUser.getRoom();
    }
}
