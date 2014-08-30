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

/**
 * Go down below: "down". This is the same command as the command "go down",
 * except the other way to execute it.
 * @author maartenl
 */
public class DownCommand extends NormalCommand
{

    public DownCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        Room myRoom = aUser.getRoom();
        if (myRoom.getDown() != null)
        {
            myRoom.sendMessageExcl(aUser, "%SNAME leave%VERB2 down.<BR>\r\n");
            aUser.setRoom(myRoom.getDown());
            aUser.getRoom().sendMessageExcl(aUser, "%SNAME appear%VERB2.<BR>\r\n");
        } else
        {
            aUser.writeMessage("You cannot go down.<BR>\r\n");
        }
        return aUser.getRoom();
    }
}
