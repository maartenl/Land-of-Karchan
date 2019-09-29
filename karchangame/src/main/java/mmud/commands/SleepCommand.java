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
import mmud.services.CommunicationService;

/**
 * Makes your character fall asleep: "sleep".
 * @author maartenl
 */
public class SleepCommand extends NormalCommand
{

    public SleepCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        if (aUser.getSleep())
        {
            CommunicationService.getCommunicationService(aUser).writeMessage("You already are asleep.<br/>\n");
        } else
        {
            aUser.setSleep(true);
            CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "%SNAME go%VERB1 to sleep.<br/>\n");
        }
        return aUser.getRoom();
    }

}
