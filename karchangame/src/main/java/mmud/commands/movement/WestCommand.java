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
package mmud.commands.movement;

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Go to the room in the west : "west".
 * @author maartenl
 *
 * @see GoCommand
 */
public class WestCommand extends NormalCommand
{

    public WestCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        Room myRoom = aUser.getRoom();
        if (myRoom.getWest() != null)
        {
            CommunicationService.getCommunicationService(myRoom).sendMessageExcl(aUser, "%SNAME leave%VERB2 west.<BR>\r\n");
            aUser.setRoom(myRoom.getWest());
            CommunicationService.getCommunicationService(aUser.getRoom()).sendMessageExcl(aUser, "%SNAME appear%VERB2.<BR>\r\n");
        } else
        {
            CommunicationService.getCommunicationService(aUser).writeMessage("You cannot go west.<BR>\r\n");
        }
        return aUser.getRoom();
    }
}
