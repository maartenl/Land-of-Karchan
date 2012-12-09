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
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.database.enums.Wearing;
import mmud.exceptions.MudException;

/**
 * Starts you wear an item on you. Syntax: wear &lt;item&gt; on &lt;body
 * position&gt;
 * @see UnwearCommand
 * @author maartenl
 */
public class WearCommand extends NormalCommand
{

    public WearCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "wear"
        // determine the appropriate body position entered by the
        // user
        String pos = parsed.get(parsed.size() - 1);
        Wearing position = Wearing.parse(pos);
        if (position == null)
        {
            aUser.writeMessage("Cannot wear something there.<br/>\r\n");
            return aUser.getRoom();
        }
        Item item = aUser.wears(position);
        if (item != null)
        {
            aUser.writeMessage("You are already wearing something there.<br/>\r\n");
            return aUser.getRoom();
        }
        // find the item on ourselves
        parsed.remove(parsed.size()-1);
        parsed.remove(parsed.size()-1);
        List<Item> itemsFound = aUser.findItems(parsed);
        if (itemsFound.isEmpty())
        {
            aUser.writeMessage("You don't have that.<br/>\n");
            return aUser.getRoom();
        }
        item = itemsFound.get(0);
        if (!item.isWearable(position))
        {
            aUser.writeMessage("You cannot wear that there.<BR>\r\n");
            return aUser.getRoom();
        }
        if (aUser.isWearing(item))
        {
            aUser.writeMessage("It is already being worn.<BR>\r\n");
            return aUser.getRoom();
        }
        aUser.wear(item, position);
        aUser.getRoom().sendMessage(aUser, "%SNAME wear%VERB2 "
                + item.getDescription() + " " + position.toString()
                + ".<br/>\r\n");
        return aUser.getRoom();
    }
}