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

import mmud.Utils;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Change your current title : "title Ruler of the Land".
 *
 * @author maartenl
 */
public class TitleCommand extends NormalCommand
{

    public TitleCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        if ("title".equalsIgnoreCase(command))
        {
            aUser.writeMessage("Your current title is '" + aUser.getTitle()
                    + "'.<br/>\r\n");
            return aUser.getRoom();
        }
        if ("title remove".equalsIgnoreCase(command))
        {
            aUser.setTitle(null);
            aUser.writeMessage("You have removed your current title.<br/>\r\n");
            return aUser.getRoom();
        }
        aUser.setTitle(command.substring(6));
        aUser.writeMessage("Changed your title to : '" + aUser.getTitle()
                + "'.<br/>\r\n");
        return aUser.getRoom();
    }

}
