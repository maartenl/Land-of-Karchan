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
 * Look in an item: "look in chest". There are two possibilities.
 * <ul>
 * <li>look in something in your inventory.
 * <li>look in something in the room that you occupy
 * </ul>It has a lot in common with the InventoryCommand and the LookAtCommand.
 * Some requirements, the item must be :
 * <ul>
 * <li>it must be unlocked and </li>
 * <li>it must be open or</li>
 * <li>it must be lidless</li>
 * <li>
 * @author maartenl
 * @see InventoryCommand
 */
public class LookInCommand extends NormalCommand
{

    public LookInCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        // first is find the item
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "look"
        parsed.remove(0); // remove "in"
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
                if (!item.isOpen() && item.hasLid())
                {
                    aUser.getRoom().sendMessage(
                            aUser,
                            "%SNAME attempt%VERB2 to look in "
                            + item.getDescription()
                            + ", but unfortunately it seems closed.<BR>\r\n");
                    return aUser.getRoom();
                }
                // found container! Using it!
                aUser.getRoom().sendMessage(aUser, "%SNAME look%VERB2 in "
                        + item.getDescription() + ".<br/>\r\n");
                final Item finalitem = item;
                return new DisplayInterface()
                {
                    @Override
                    public String getMainTitle() throws MudException
                    {
                        return finalitem.getDescription();
                    }

                    @Override
                    public String getImage() throws MudException
                    {
                        return finalitem.getImage();
                    }

                    @Override
                    public String getBody() throws MudException
                    {
                        StringBuilder builder = new StringBuilder("You look in ");
                        builder.append(finalitem.getDescription());
                        builder.append(". It contains ");
                        Constants.addInventory(finalitem.getItems(), builder);
                        return builder.toString();
                    }
                };
            }
        }
        // TODO implement this!!!
        aUser.writeMessage("Not implemented yet.<br/>\n");
        return aUser.getRoom();
    }
}
