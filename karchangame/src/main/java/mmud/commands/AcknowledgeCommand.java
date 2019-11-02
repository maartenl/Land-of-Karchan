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

import static mmud.commands.NormalCommand.parseCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.exceptions.PersonNotFoundException;
import mmud.services.CommunicationService;

/**
 * Stop ignoring what someone says. Syntax: <TT>acknowledge &lt;name&gt;</TT>
 * @author maartenl
 */
class AcknowledgeCommand extends NormalCommand
{

    public AcknowledgeCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command, 2);
        User toChar = aUser.getRoom().retrieveUser(myParsed[1]);
        if (toChar == null)
        {
            // action to unknown
            throw new PersonNotFoundException("Cannot find " + myParsed[1] + ".<br/>\r\n");
        }
        aUser.removeAnnoyingUser(toChar);
        // TODO : check this out.
//        {
//            aUser.writeMessage("You are not ignoring " + toChar.getName() + ".<br/>,\r\n");
//            return aUser.getRoom();
//        }
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, toChar,
                "%SNAME acknowledge%VERB2 %TNAME.<br/>\r\n");
        return aUser.getRoom();
    }
}
