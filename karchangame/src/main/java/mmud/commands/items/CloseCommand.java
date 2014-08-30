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

/**
 * Close a container: "close chest".
 *
 * @see OpenCommand
 * @see LockCommand
 * @see UnlockCommand
 */
public class CloseCommand extends NormalCommand
{

    public CloseCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
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
            aUser.writeMessage("No items found that match that description.<br/>\n");
            return aUser.getRoom();
        }
        for (Item item : itemsFound)
        {
            if (item.isContainer())
            {
                if (!item.isOpenable())
                {
                    aUser.writeMessage(item.getDescription()
                            + " cannot be closed.<BR>\r\n");
                    return aUser.getRoom();
                }
                if (!item.isOpen())
                {
                    aUser.writeMessage(item.getDescription()
                            + " is already closed.<BR>\r\n");
                    return aUser.getRoom();
                }
                if (item.isLocked())
                {
                    aUser.writeMessage(item.getDescription()
                            + " is locked.<BR>\r\n");
                    return aUser.getRoom();
                }
                item.close();
                aUser.getRoom().sendMessage(aUser, "%SNAME close%VERB2 "
					+ item.getDescription() + ".<br/>\r\n");
                return aUser.getRoom();
            }
        }
        aUser.writeMessage("You cannot close that.<br/>\n");
        return aUser.getRoom();
    }
}
