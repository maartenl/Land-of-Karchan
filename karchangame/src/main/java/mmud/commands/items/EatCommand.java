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

/**
 * Eat an item from inventory. "eat apple pie". Should improve your eat stats.
 * @author maartenl
 */
public class EatCommand extends NormalCommand
{

    public EatCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "eat"
        // find the item on ourselves
        List<Item> itemsFound = aUser.findItems(parsed);
        if (itemsFound.isEmpty())
        {
            aUser.writeMessage("You don't have that.<br/>\n");
            return aUser.getRoom();
        }
        final Item item = itemsFound.get(0);
        if (!item.isEatable())
        {
            aUser.writeMessage("You cannot eat that.<BR>\r\n");
            return aUser.getRoom();
        }
        if (!aUser.unused(item))
        {
            aUser.writeMessage("You are using that.<BR>\r\n");
            return aUser.getRoom();
        }
        aUser.getRoom().sendMessage(aUser, "%SNAME eat%VERB2 "
                + item.getDescription() + ".<br/>\r\n");
        //TODO increase eat stats
        aUser.destroyItem(item);
        return new DisplayInterface()
        {

            @Override
            public String getMainTitle() throws MudException
            {
               return item.getDescription();
            }

            @Override
            public String getImage() throws MudException
            {
                return item.getImage();
            }

            @Override
            public String getBody() throws MudException
            {
                return item.getEatable();
            }
        };
    }
}