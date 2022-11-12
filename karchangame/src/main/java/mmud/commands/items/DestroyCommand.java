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

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.items.Item;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Destroys an item from inventory. "destroy apple pie". Will nicely cleanup your inventory
 * without having to litter the floor with miscellaneous items. Very similar to the
 * drinking and eating of items.
 * @author maartenl
 */
public class DestroyCommand extends NormalCommand
{

    public DestroyCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "destroy"
        // find the item on ourselves
        List<Item> itemsFound = aUser.findItems(parsed);
        if (itemsFound.isEmpty())
        {
            CommunicationService.getCommunicationService(aUser).writeMessage("You don't have that.<br/>\n");
            return aUser.getRoom();
        }
        final Item result = itemsFound.get(0);
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME destroy%VERB2 "
					+ result.getDescription() + ".<br/>\r\n");
        aUser.destroyItem(result);
        return aUser.getRoom();
    }
}
