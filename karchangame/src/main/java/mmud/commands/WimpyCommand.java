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
import mmud.database.enums.Health;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.PersonCommunicationService;

/**
 * Sets the wimpy or displays the wimpy of the user: "wimpy".
 * @author maartenl
 */
public class WimpyCommand extends NormalCommand
{

    public WimpyCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
      PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
        command = command.trim();
        if (command.equalsIgnoreCase("wimpy help"))
        {
            communicationService.writeMessage("Syntax: <B>wimpy &lt;string&gt;</B><UL><LI>feeling well"
                    + "<LI>feeling fine<LI>feeling quite nice<LI>slightly hurt"
                    + "<LI>hurt<LI>quite hurt<LI>extremely hurt<LI>terribly hurt"
                    + "<LI>feeling bad<LI>feeling very bad<LI>at death's door</UL>\r\n");
            return aUser.getRoom();

        }
        if (command.equalsIgnoreCase("wimpy"))
        {
            communicationService.writeMessage("Current wimpy setting: <B>"
                    + (aUser.getWimpy() == null ? "not set" : aUser.getWimpy().getDescription())
                    + "</B>.<BR>\r\n");
            return aUser.getRoom();

        }
        String myString = parseCommand(command, 2)[1];
        Health health = Health.get(myString);
        if (health == null)
        {
            communicationService.writeMessage("Cannot find that wimpy level.<BR>\r\n");
            return aUser.getRoom();
        }
        communicationService.writeMessage("Wimpy level set.<BR>\r\n");
        aUser.setWimpy(health);
        return aUser.getRoom();
    }
}
