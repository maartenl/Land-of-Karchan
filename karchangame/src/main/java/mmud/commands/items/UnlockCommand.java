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
import mmud.services.PersonCommunicationService;

/**
 * Unlocks a container with a key: "unlock chest with key". Requirements for it
 * to be successfull:
 * <ul>
 * <li>the container must be in your inventory or in the room
 * <li>the container must be a container
 * <li>the container must be closed
 * <li>the container must be lockable.
 * <li>the container must be locked.
 * <li>the key item must be in your inventory
 * </ul>
 * The possible syntax can range from: "unlock chest with key" to
 * "unlock iron studded wooden chest with old long ornate key".
 *
 * @see LockCommand
 */
public class UnlockCommand extends NormalCommand
{

    public UnlockCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        // first is find the item
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "lock"
        int pos = 0;
        for (String str : parsed)
        {
            if (str.equalsIgnoreCase("with"))
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

        List<String> itemDescription = parsed.subList(pos + 1, parsed.size());
        List<String> containerDescription = parsed.subList(0, pos);
        // find the container on ourselves
        List<Item> containerFound = aUser.findItems(containerDescription);
        if (containerFound.isEmpty())
        {
            // find the item in the room
            containerFound = aUser.getRoom().findItems(containerDescription);
        }
      final PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
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
        if (container.isOpen())
        {
            communicationService.writeMessage(container.getDescription() + " is open.<br/>\n");
            return aUser.getRoom();
        }
        if (!container.hasLock())
        {
            communicationService.writeMessage(container.getDescription()
                    + " does not have a lock.<br/>\r\n");
            return aUser.getRoom();
        }
        if (!container.isLocked())
        {
            communicationService.writeMessage(container.getDescription()
                    + " is already unlocked.<br/>\r\n");
            return aUser.getRoom();
        }
        // find the item on ourselves
        List<Item> itemsFound = aUser.findItems(itemDescription);
        if (itemsFound.isEmpty())
        {
            communicationService.writeMessage("You don't have that.<br/>\n");
            return aUser.getRoom();
        }
        for (Item key : itemsFound)
        {
            if (aUser.unused(key) && !key.isContainer() && container.isKey(key))
            {
                // item is not used.
                container.unlock();
                CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME unlock%VERB2 "
                        + container.getDescription() + " with "
                        + key.getDescription() + ".<br/>\r\n");
                return aUser.getRoom();
            }
        }
        communicationService.writeMessage("It doesn't fit in the lock.<br/>");
        return aUser.getRoom();
    }
}
