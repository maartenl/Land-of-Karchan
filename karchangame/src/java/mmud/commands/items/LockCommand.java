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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import mmud.Constants;
import mmud.commands.*;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.exceptions.ParseException;
import mmud.rest.services.ItemBean;

/**
 * Locks a container with a key: "lock chest with key". Requirements for it to
 * be successful:
 * <ul>
 * <li>the container must be in your inventory or in the room
 * <li>the container must be a container
 * <li>the container must be closed
 * <li>the container must be lockable.
 * <li>the container must be unlocked.
 * <li>the key item must be in your inventory
 * </ul>
 * The possible syntax can range from: "lock chest with key" to
 * "lock iron studded wooden chest with old long ornate key".
 *
 * @see UnlockCommand
 */
public class LockCommand extends NormalCommand
{

    public LockCommand(String aRegExpr)
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
        if (containerFound.isEmpty())
        {
            aUser.writeMessage("No containers found that match that description.<br/>\n");
            return aUser.getRoom();
        }
        Item container = containerFound.get(0);
        if (!container.isContainer())
        {
            aUser.writeMessage(container.getDescription() + " is not a container.<br/>\n");
            return aUser.getRoom();
        }
        if (container.isOpen())
        {
            aUser.writeMessage(container.getDescription() + " is open.<br/>\n");
            return aUser.getRoom();
        }
        if (!container.hasLock())
        {
            aUser.writeMessage(container.getDescription()
                    + " does not have a lock.<br/>\r\n");
            return aUser.getRoom();
        }
        if (container.isLocked())
        {
            aUser.writeMessage(container.getDescription()
                    + " is already locked.<br/>\r\n");
            return aUser.getRoom();
        }
        // find the item on ourselves
        List<Item> itemsFound = aUser.findItems(itemDescription);
        if (itemsFound.isEmpty())
        {
            aUser.writeMessage("You don't have that.<br/>\n");
            return aUser.getRoom();
        }
        for (Item key : itemsFound)
        {
            if (aUser.unused(key) && !key.isContainer() && container.isKey(key))
            {
                // item is not used.
                container.lock();
                aUser.getRoom().sendMessage(aUser, "%SNAME lock%VERB2 "
                        + container.getDescription() + " with "
                        + key.getDescription() + ".<br/>\r\n");
                return aUser.getRoom();
            }
        }
        aUser.writeMessage("It doesn't fit in the lock.<br/>");
        return aUser.getRoom();
    }
}
