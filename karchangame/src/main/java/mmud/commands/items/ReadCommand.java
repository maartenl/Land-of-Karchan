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
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.PersonCommunicationService;

/**
 * Read stuff: "read sign". There are two different possibilities:
 * <ul>
 * <li>read something in your inventory.
 * <li>read something in the room that you occupy
 * </ul>
 * @author maartenl
 */
public class ReadCommand extends NormalCommand
{

    public ReadCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        DisplayInterface result = null;
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "read"
        // find the item on ourselves
        List<Item> itemsFound = aUser.findItems(parsed);
        if (itemsFound.isEmpty())
        {
            // find the item in the room
            itemsFound = aUser.getRoom().findItems(parsed);
        }
      final PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
        if (itemsFound.isEmpty())
        {
            communicationService.writeMessage("You don't know what to read.<br/>\n");
            return aUser.getRoom();
        }
        for (Item item : itemsFound)
        {
            if (item.isReadable())
            {
                CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME read%VERB2 "
                        + item.getDescription() + ".<br/>\r\n");
                return item.getRead();
            }
        }
        communicationService.writeMessage("You cannot read that.<br/>\n");
        return aUser.getRoom();
    }
}