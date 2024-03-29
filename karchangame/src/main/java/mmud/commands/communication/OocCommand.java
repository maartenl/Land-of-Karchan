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
package mmud.commands.communication;

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.PersonService;

/**
 * Shows the current date in the game: "date".
 *
 * @author maartenl
 */
public class OocCommand extends NormalCommand
{

    public OocCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        if (command.equalsIgnoreCase("ooc on"))
        {
          aUser.setOoc(true);
          CommunicationService.getCommunicationService(aUser).writeMessage("Your OOC channel is now turned on.<br/>\r\n");
        } else if (command.equalsIgnoreCase("ooc off"))
        {
          aUser.setOoc(false);
          CommunicationService.getCommunicationService(aUser).writeMessage("Your OOC channel is now turned off.<br/>\r\n");
        } else if (!aUser.getOoc())
        {
          CommunicationService.getCommunicationService(aUser).writeMessage("Sorry, you have your OOC channel turned off.<br/>\r\n");
        } else
        {
          PersonService personService = getPersonService();
          final String message = command.substring(4);
            //  "#4c76a2"
          personService.sendWall("<span class=\"chat-cyanblue\">[OOC: <b>" + aUser.getName() + "</b>] " + message + "</span><br/>\r\n", User::getOoc);
        }
        return aUser.getRoom();
    }

}
