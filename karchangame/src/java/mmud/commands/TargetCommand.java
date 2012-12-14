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

import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;

/**
 * Indicates a command that needs or can have a target, like "action to Marvin"
 * but necessarily, like "bow". Target needs to be in the same room.
 * @author maartenl
 */
public abstract class TargetCommand extends NormalCommand
{

    public TargetCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    /**
     * Run the action command to a target.
     * @param aUser the one executing the command
     * @param aTarget the target of the command
     * @param verb the verb, usually the first word in the command, for example "cheer".
     * @param command the rest of the command, for
     * example "bow to Marvin evilly", will provide "evilly". But "bow to Marvin"
     * will return a null pointer.
     * @return something to display
     * @throws MudException
     */
    protected abstract DisplayInterface actionTo(User aUser, Person aTarget, String verb, String command) throws MudException;

    /**
     * Run an action without a target.
     * @param aUser the one executing the command
     * @param verb the verb, usually the first word in the command, for example "bow".
     * @param command the rest of the command, for example "bow evilly" would return "evilly". "bow" would return null.
     * @return something to display
     * @throws MudException
     */
    protected abstract DisplayInterface action(User aUser, String verb, String command) throws MudException;

    /**
     * Retrieves a target from the command line, detected in the form of "[command] to [target] stuff".
     * @param myParsed the command, parsed. Usually only needs the first three words.
     * @param aUser the user issuing the command
     * @return the Person found.
     * @throws PersonNotFoundException if the person is not found in the room.
     */
    protected Person getTarget(String[] myParsed, User aUser) throws PersonNotFoundException
    {
        if (myParsed.length > 2 && myParsed[1].equalsIgnoreCase("to"))
        {
            Person toChar = aUser.getRoom().retrievePerson(myParsed[2]);
            if (toChar == null)
            {
                // action to unknown
                throw new PersonNotFoundException("Cannot find " + myParsed[2] + ".<BR>\r\n");
            }
            return toChar;
        }
        return null;
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command, 4);
        Person toChar = getTarget(myParsed, aUser);
        if (toChar != null)
        {
            return actionTo(aUser, toChar, myParsed[0], myParsed.length <= 3 ? null : myParsed[3]);
        }
        String[] stuff = parseCommand(command, 2);
        return action(aUser, myParsed[0], (stuff.length == 2 ? stuff[1] : null));
    }
}
