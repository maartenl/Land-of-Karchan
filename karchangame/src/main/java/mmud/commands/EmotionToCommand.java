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
import mmud.exceptions.PersonNotFoundException;
import mmud.services.CommunicationService;

/**
 * Provides an emotional response to a person. Acceptable format is:
 * <TT>[emotion] to [person]</TT><P>
 * For example: <UL><LI>lick Karn<LI>nudge Westril</UL>
 * @author maartenl
 */
public class EmotionToCommand extends TargetCommand
{

    public EmotionToCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String command) throws MudException
    {
        String[] plural =
        {
            verb.toLowerCase(), Utils.returnEmotionTo(verb.toLowerCase())
        };
        if (plural == null)
        {
            return null;
        }
        if (command != null)
        {
            // agree evilly to Karn
            if (Utils.existsAdverb(command))
            {
                CommunicationService.getCommunicationService(aUser).writeMessage("You " + plural[0] + " " + command.toLowerCase() + " " + aTarget.getName() + ".<BR>\r\n");
                CommunicationService.getCommunicationService(aTarget).writeMessage(aUser.getName() + " " + plural[1] + " " + command.toLowerCase() + " you.<BR>\r\n");
                CommunicationService.getCommunicationService(aUser.getRoom()).sendMessageExcl(aUser, aTarget, aUser.getName() + " " + plural[1] + " " + command.toLowerCase() + " " + aTarget.getName() + ".<BR>\r\n");
            } else
            {
                CommunicationService.getCommunicationService(aUser).writeMessage("Unknown adverb found.<BR>\r\n");
            }
            return aUser.getRoom();
        }
        // cuddle Karn
        CommunicationService.getCommunicationService(aUser).writeMessage("You " + plural[0] + " " + aTarget.getName() + ".<BR>\r\n");
        CommunicationService.getCommunicationService(aTarget).writeMessage(aUser.getName() + " " + plural[1] + " you.<BR>\r\n");
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessageExcl(aUser, aTarget, aUser.getName() + " " + plural[1] + " " + aTarget.getName() + ".<BR>\r\n");
        return aUser.getRoom();
    }

    @Override
    protected DisplayInterface action(User aUser, String verb, String command) throws MudException
    {
        return null;
    }

    /**
     * Retrieves a target from the command line, detected in the form of "[command] [target] stuff". For example "cuddle Karn".
     * @param myParsed the command, parsed. Usually only needs the first two words.
     * @param aUser the user issuing the command
     * @return the Person found, null if no person in the command.
     * @throws PersonNotFoundException if the person is not found in the room.
     */
    @Override
    protected Person getTarget(String[] myParsed, User aUser) throws PersonNotFoundException
    {
        if (myParsed.length > 1)
        {
            Person toChar = aUser.getRoom().retrievePerson(myParsed[1]);
            if (toChar == null)
            {
                // action to unknown
                throw new PersonNotFoundException("Cannot find " + myParsed[1] + ".<BR>\r\n");
            }
            return toChar;
        }
        return null;
    }
}
