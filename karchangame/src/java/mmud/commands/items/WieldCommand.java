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
import mmud.database.enums.Wielding;
import mmud.exceptions.MudException;

/**
 * Starts you wielding an item. Syntax: wield &lt;item&gt; with
 * &lt;lefthand|righthand|both|hands|bothhandsd&gt;
 * @see UnwieldCommand
 * @author maartenl
 */
public class WieldCommand extends NormalCommand
{

    public WieldCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "wield"
        // determine the appropriate body position entered by the
        // user
        String pos = parsed.get(parsed.size() - 1);
        Wielding position = Wielding.parse(pos);
        if (position == null)
        {
            aUser.writeMessage("Cannot wield something there.<br/>\r\n");
            return aUser.getRoom();
        }
        Item item = aUser.wields(position);
        if (item != null)
        {
            aUser.writeMessage("You are already wielding something there.<br/>\r\n");
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
        if (!item.isWieldable(position))
        {
            aUser.writeMessage("You cannot wield that there.<BR>\r\n");
            return aUser.getRoom();
        }
        if (aUser.isWielding(item))
        {
            aUser.writeMessage("It is already being wielded.<BR>\r\n");
            return aUser.getRoom();
        }
        aUser.wield(item, position);
        aUser.getRoom().sendMessage(aUser, "%SNAME wield%VERB2 "
                + item.getDescription() + " " + position.toString()
                + ".<br/>\r\n");
        return aUser.getRoom();
    }
}