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
import mmud.Constants;
import mmud.commands.*;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.PersonCommunicationService;

/**
 * Open a container: "open chest".
 *
 * @see CloseCommand
 * @see LockCommand
 * @see UnlockCommand
 * @author maartenl
 */
public class OpenCommand extends NormalCommand
{

    public OpenCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
      PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
        // first is find the item
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "open"
        // find the item on ourselves
        List<Item> itemsFound = aUser.findItems(parsed);
        if (itemsFound.isEmpty())
        {
            // find the item in the room
            itemsFound = aUser.getRoom().findItems(parsed);
        }
        if (itemsFound.isEmpty())
        {
            communicationService.writeMessage("No items found that match that description.<br/>\n");
            return aUser.getRoom();
        }
        for (Item item : itemsFound)
        {
            if (item.isContainer())
            {
                if (!item.isOpenable())
                {
                    communicationService.writeMessage(item.getDescription()
                            + " cannot be opened.<BR>\r\n");
                    return aUser.getRoom();
                }
                if (item.isOpen())
                {
                    communicationService.writeMessage(item.getDescription()
                            + " is already open.<BR>\r\n");
                    return aUser.getRoom();
                }
                if (item.isLocked())
                {
                    communicationService.writeMessage(item.getDescription()
                            + " is locked.<BR>\r\n");
                    return aUser.getRoom();
                }
                item.open();
                CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME open%VERB2 "
					+ item.getDescription() + ".<br/>\r\n");
                return aUser.getRoom();
            }
        }
        communicationService.writeMessage("You cannot open that.<br/>\n");
        return aUser.getRoom();
    }
}
