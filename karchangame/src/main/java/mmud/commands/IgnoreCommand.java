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
import mmud.exceptions.PersonNotFoundException;
import mmud.services.CommunicationService;

/**
 * Ignore everything someone says. Syntax: <TT>ignore &lt;name&gt;</TT>
 * @author maartenl
 */
class IgnoreCommand extends NormalCommand
{

    public IgnoreCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] myParsed = parseCommand(command, 3);
        User toChar = aUser.getRoom().retrieveUser(myParsed[2]);
        if (toChar == null)
        {
            // action to unknown
            throw new PersonNotFoundException("Cannot find " + myParsed[2] + ".<br/>\r\n");
        }
        aUser.addAnnoyingUser(toChar);
        CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, toChar,
                "%SNAME start%VERB2 to fully ignore %TNAME.<br/>\r\n");
        return aUser.getRoom();
    }
}
