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
import mmud.services.CommunicationService;

/**
 * Bow : "bow".
 * @author maartenl
 */
public class BowCommand extends TargetCommand
{

    public BowCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String command) throws MudException
    {
        if (command != null)
        {
            if (Utils.existsAdverb(command))
            {
                // bow to Marvin evilly
                CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, aTarget,
                        "%SNAME bow%VERB2 to %TNAME "
                        + command.toLowerCase()
                        + ".<BR>\r\n");
                return aUser.getRoom();
            }
            // bow to Marvin unknownadverb
            CommunicationService.getCommunicationService(aUser).writeMessage("Unknown adverb found.<BR>\r\n");
            return aUser.getRoom();
        }
        // bow to Marvin
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, aTarget, "%SNAME bow%VERB2 to %TNAME.<BR>\r\n");
        return aUser.getRoom();
    }

    @Override
    protected DisplayInterface action(User aUser, String verb, String command) throws MudException
    {
        if (command != null)
        {
            if (!Utils.existsAdverb(command))
            {
                // bow unknownadverb
                CommunicationService.getCommunicationService(aUser).writeMessage("Unknown adverb found.<BR>\r\n");
                return aUser.getRoom();
            }
            // bow evilly
            CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME bow%VERB2 "
                    + command.toLowerCase() + ".<BR>\r\n");
            return aUser.getRoom();
        }
        // bow
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME bow%VERB2.<BR>\r\n");
        return aUser.getRoom();
    }
}
