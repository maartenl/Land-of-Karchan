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
import mmud.exceptions.MudException;

/**
 * An abstract class for the commands that may only be executed by a member of a
 * guild.
 * @author maartenl
 */
public abstract class GuildCommand extends NormalCommand
{

    /**
     * Checks if the user is part of a guild, before running the command.
     * @param command
     * @param aUser
     * @return a DisplayInterface for communicating with the user.
     * @throws MudException
     */
    @Override
    DisplayInterface start(String command, User aUser) throws MudException
    {
        if (aUser.getGuild() == null)
        {
            return null;
        }
        return super.start(command, aUser);
    }

    public GuildCommand(String aRegExpr)
    {
        super(aRegExpr);
    }
}
