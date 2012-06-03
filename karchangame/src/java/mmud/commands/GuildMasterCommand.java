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
 * An abstract class for the commands that may only be executed
 * by the owner of a guild, the <I>guildmaster</I>.
 * @author maartenl
 */
public abstract class GuildMasterCommand extends GuildCommand
{

    public GuildMasterCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    /**
     * Checks if the user is the guildmaster of his guild, before running its course.
     * @param command
     * @param aUser
     * @return
     */
    @Override
    DisplayInterface start(String command, User aUser) throws MudException
    {
        if (!aUser.getName().equals(aUser.getGuild().getBoss().getName()))
        {
            return null;
        }
        return super.start(command, aUser);
    }
}
