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
import mmud.rest.services.ItemBean;

/**
 * Retrieve item from floor: "get bucket".
 * This command will make a <I>best effort</I> regarding dropping of the
 * requested items. This means that, if you request 5 items, and there are 5
 * or more items in your inventory, this method will attempt to aquire 5
 * items. It is possible that not all items are available, in which case you
 * could conceivably only receive 3 items for instance.
 *
 * @see DropCommand
 * @author maartenl
 */
public class GetCommand extends NormalCommand
{

    public GetCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "get"
        int amount = 1;
        try
        {
            amount = Integer.parseInt(parsed.get(0));
            parsed.remove(0);
        } catch (NumberFormatException e)
        {// do nothing here, we assume we need to drop only one item.
        }
        if (amount <= 0)
        {
            aUser.writeMessage("That is an illegal amount.<br/>\n");
            return aUser.getRoom();
        }

        // find the item on the floor
        List<Item> itemsFound = aUser.getRoom().findItems(parsed);
        if (itemsFound.isEmpty())
        {
            aUser.writeMessage("There are none.<br/>\n");
            return aUser.getRoom();
        }
        if (itemsFound.size() < amount)
        {
            aUser.writeMessage("You do not see that many items.<br/>\r\n");
            return aUser.getRoom();
        }
        boolean getted = false;
        ItemBean itemBean = getItemBean();
        for (Item item : itemsFound)
        {
            if (item.isGetable())
            {
                if (!itemBean.get(item, aUser))
                {
                    continue;
                }
                aUser.getRoom().sendMessage(aUser, "%SNAME get%VERB2 " + item.getDescription() + ".<br/>\r\n");
                getted = true;
                amount--;
                if (amount == 0)
                {
                    return aUser.getRoom();
                }
            }
        }
        if (!getted)
        {
            aUser.writeMessage("You did not retrieve anything.<br/>");
        } else
        {
            aUser.writeMessage("You retrieved some of the items.<br/>\r\n");
        }
        return aUser.getRoom();
    }
}
