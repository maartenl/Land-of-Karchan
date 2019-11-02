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
 * You start stripping.
 * @see UnwearCommand
 * @author maartenl
 */
public class UndressCommand extends NormalCommand
{

    public UndressCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        for (Wearing position : Wearing.values())
        {
            aUser.wear(null, position);
        }
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessageExcl(aUser, "%SNAME undresses completely.<br/>\r\n");
        CommunicationService.getCommunicationService(aUser).writeMessage("You undress completely.<br/>\r\n");
        return aUser.getRoom();
    }
}