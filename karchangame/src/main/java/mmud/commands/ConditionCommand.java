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

import java.util.logging.Logger;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;

/**
 * Gets or sets the condition/state of your character. For example:
 * <ul><li>"condition"</li>
 * <li>"condition You are glowing."</li>
 * <li>"condition empty"</li></ul>
 *
 * @author maartenl
 */
public class ConditionCommand extends NormalCommand
{

    private static final Logger LOGGER = Logger.getLogger(ConditionCommand.class.getName());

    public ConditionCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command, 4);
        if (myParsed.length == 1)
        {
            if (aUser.getState() == null)
            {
                aUser.writeMessage("You do not have a current condition.<br/>\r\n");
                return aUser.getRoom();
            }
            aUser.writeMessage("Your current condition is \"" + aUser.getState() + "\".<br/>\r\n");
            return aUser.getRoom();
        }
        if (myParsed.length == 2 && myParsed[1].equals("empty"))
        {
            aUser.setState(null);
            aUser.writeMessage("Removed your current condition.<br/>\r\n");
            return aUser.getRoom();
        }

        aUser.setState(command.substring("condition ".length()));
        aUser.writeMessage("Condition set.<BR>\r\n");
        return aUser.getRoom();
    }
}
