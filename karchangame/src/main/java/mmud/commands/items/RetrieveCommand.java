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
import mmud.commands.*;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.exceptions.ParseException;
import mmud.rest.services.ItemBean;

/**
/**
 * Retrieve an item from a container: "retrieve ring from sack". Requirements
 * for it to be successfull:
 * <ul>
 * <li>the item to retrieve must be in this container
 * <li>the container must be in your inventory or in the room
 * <li>the container must be a container
 * </ul>
 * The possible syntax can range from: "retrieve ring from sack" to
 * "retrieve 8 old gold shiny ring from new leather beaten sack".
 *
 * @see PutCommand
 */
public class RetrieveCommand extends NormalCommand
{

    public RetrieveCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        // first is find the item
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "retrieve"
        int amount = 1;
        try
        {
            amount = Integer.parseInt(parsed.get(0));
            parsed.remove(0);
        } catch (NumberFormatException e)
        {// do nothing here, we assume we need to retrieve only one item.
        }
        if (amount <= 0)
        {
            aUser.writeMessage("That is an illegal amount.<br/>\n");
            return aUser.getRoom();
        }
        int pos = 0;
        for (String str : parsed)
        {
            if (str.equalsIgnoreCase("from"))
            {
                break;
            }
            pos++;
        }
        if (pos == parsed.size())
        {
            // no from found.
            throw new ParseException();
        }
        List<String> itemDescription = parsed.subList(0, pos);
        List<String> containerDescription = parsed.subList(pos+1, parsed.size());
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
        if (!container.isOpen())
        {
            aUser.writeMessage(container.getDescription() + " is closed.<br/>\n");
            return aUser.getRoom();
        }
        // find the item on ourselves
        List<Item> itemsFound = container.findItems(itemDescription);
        if (itemsFound.isEmpty())
        {
            aUser.writeMessage("It doesn't contain that.<br/>\n");
            return aUser.getRoom();
        }
        if (itemsFound.size() < amount)
        {
            aUser.writeMessage("It doesn't contain enough requested items.<br/>\r\n");
            return aUser.getRoom();
        }
        boolean retrieve = false;
        ItemBean itemBean = getItemBean();
        for (Item item : itemsFound)
        {
            if (item.isGetable())
            {
                // let's get 'em!
                if (!itemBean.retrieve(item, container, aUser))
                {
                    continue;
                }
                aUser.getRoom().sendMessage(aUser, "%SNAME retrieve%VERB2 " + item.getDescription() + " from" + container.getDescription() + ".<br/>\r\n");
                retrieve = true;
                amount--;
                if (amount == 0)
                {
                    return aUser.getRoom();
                }
            }
        }
        if (!retrieve)
        {
            aUser.writeMessage("You did not retrieve anything.<br/>");
        } else
        {
            aUser.writeMessage("You retrieved some items from the container.<br/>\r\n");
        }
        return aUser.getRoom();
    }
}
