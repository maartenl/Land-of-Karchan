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
 * The interface used for all commands to be executed. Commands are executed by
 * characters. Bear in mind that commands are static classes devoid of state.
 *
 * @author maartenl
 */
public interface Command
{

    /**
     * Runs the command. The usual sequence of events is:
     * <UL>
     * <LI>parsing the command string
     * <LI>retrieving the appropriate information items/rooms/characters/etc)
     * <LI>doing the action
     * <LI>composing a return message for the user.
     * </UL>
     *
     * @param command the String containing the full command.
     * @param aUser
     * the user that is executing the command
     * @return DisplayInterface to display to the user, if it returns null,
     * it means the command was unsuccessfull.
     * @throws MudException
     * if something goes wrong.
     */
    public DisplayInterface run(String command, User aUser) throws MudException;

    /**
     * returns the regular expression the command structure must follow.
     *
     * @return String containing the regular expression.
     * @see java.util.regex.Pattern
     */
    public String getRegExpr();

}
