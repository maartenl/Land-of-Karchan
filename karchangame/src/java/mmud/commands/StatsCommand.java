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
 * Show the statistics of a player.
 * @author maartenl
 */
public class StatsCommand extends NormalCommand
{

    public StatsCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String invent = aUser.getStatistics();
        invent = invent.replaceAll("%SHESHE", "You");
        invent = invent.replaceAll("%SISARE", "are");
        final String body = invent;
        return new DisplayInterface()
        {
            @Override
            public String getTitle()
            {
                return "Stats";
            }

            @Override
            public String getImage()
            {
                return null;
            }

            @Override
            public String getBody()
            {
                return body;
            }
        };
    }
}
