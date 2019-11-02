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
import mmud.services.CommunicationService;

/**
 * Stop you wearing an item on you. Syntax: remove from &lt;body
 * position&gt;
 * @see WearCommand
 * @author maartenl
 */
public class UnwearCommand extends NormalCommand
{

    public UnwearCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        List<String> parsed = new ArrayList<>(Arrays.asList(parseCommand(command)));
        parsed.remove(0); // remove "remove"
        parsed.remove(0); // remove "from"
        // determine the appropriate body position entered by the
        // user
        String pos = parsed.get(0);
        Wearing position = Wearing.parse(pos);
        if (position == null)
        {
            CommunicationService.getCommunicationService(aUser).writeMessage("Cannot wear something there.<br/>\r\n");
            return aUser.getRoom();
        }
        Item item = aUser.wears(position);
        if (item == null)
        {
            CommunicationService.getCommunicationService(aUser).writeMessage("You are not wearing anything there.<br/>\r\n");
            return aUser.getRoom();
        }
        aUser.wear(null, position);
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME remove%VERB2 "
                + item.getDescription() + " from " + position.toString()
                + ".<br/>\r\n");
        return aUser.getRoom();
    }
}