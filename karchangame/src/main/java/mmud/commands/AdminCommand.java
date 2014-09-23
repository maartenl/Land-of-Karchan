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
package mmud.commands;

import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.enums.God;
import mmud.exceptions.MudException;

/**
 * Shows the current date in the game: "date".
 *
 * @author maartenl
 */
public class AdminCommand extends NormalCommand
{

    public AdminCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        if (aUser.getGod() != God.GOD)
        {
            aUser.writeMessage("You are not an administrator.<br/>\r\n");
            return aUser.getRoom();

        }
        if (command.equalsIgnoreCase("admin reset commands"))
        {
            CommandFactory.clearUserCommandStructure();
            aUser
                    .writeMessage("User commands have been reloaded from database.<BR>\r\n");
        }

        if (command.equalsIgnoreCase("admin help"))
        {
            aUser.writeMessage("Possible commands are:<dl>"
                    + "<dt>admin help</dt><dd>this help text</dd>"
                    + "<dt>admin reset commands</dt><dd>reset the cached <I>special</I> commands."
                    + "Necessary if a command has been deleted, added or changed.</dd>"
                    + "</dl\r\n");
        }
        return aUser.getRoom();
    }

}
