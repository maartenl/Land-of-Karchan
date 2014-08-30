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

import mmud.commands.BogusCommand;
import mmud.commands.Command;
import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Go in a specific direction: "go south". Delegates to the appropriate command class.
 * @author maartenl
 */
public class GoCommand extends NormalCommand
{

    public GoCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command, 2);
        if (myParsed.length == 2)
        {
            Command newCommand = new BogusCommand(".+");
            if (myParsed[1].equalsIgnoreCase("south"))
            {
                newCommand = new SouthCommand(".+");
            }
            if (myParsed[1].equalsIgnoreCase("north"))
            {
                newCommand = new NorthCommand(".+");
            }
            if (myParsed[1].equalsIgnoreCase("west"))
            {
                newCommand = new WestCommand(".+");
            }
            if (myParsed[1].equalsIgnoreCase("east"))
            {
                newCommand = new EastCommand(".+");
            }
            if (myParsed[1].equalsIgnoreCase("up"))
            {
                newCommand = new UpCommand(".+");
            }
            if (myParsed[1].equalsIgnoreCase("down"))
            {
                newCommand = new DownCommand(".+");
            }
            return newCommand.run(command, aUser);
        }
        return null;
    }
}
