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
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Chatline;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.ChatlineNotFoundException;
import mmud.exceptions.ChatlineOperationNotAllowedException;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Edits an existing chatline.
 * "editchat [name] [attribute|none] [colour]", for example "createchat deputies deputy green".
 *
 * @author maartenl
 * @see CreateChatCommand
 * @see DeleteChatCommand
 * @see JoinChatCommand
 * @see LeaveChatCommand
 * @see EditChatCommand
 */
public class EditChatCommand extends NormalCommand
{
    private static final Logger LOGGER = Logger.getLogger(EditChatCommand.class.getName());

    public EditChatCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] strings = parseCommand(command);
        if (strings.length != 4 && strings.length != 5)
        {
            return null;
        }
        String chatlinename = strings[1];
        String attribute = strings[2].equalsIgnoreCase("none") ? null : strings[2];
        String colour = strings[3];
        User newowner = strings.length < 5 ? null : getPersonService().getUser(strings[4]);
        if (newowner == null) {
            newowner = aUser;
        }
        try
        {
            getChatService().editChatline(chatlinename, attribute, colour, aUser, newowner);
            CommunicationService.getCommunicationService(aUser).writeMessage("Chatline edited.<BR>\r\n");
        } catch (ChatlineOperationNotAllowedException e)
        {
            LOGGER.log(Level.SEVERE, "Edit of chatline not allowed", e);
            CommunicationService.getCommunicationService(aUser).writeMessage("Edit of chatline not allowed. You are neither the owner nor an administrator.<BR>\r\n");
        } catch (ChatlineNotFoundException e)
        {
            LOGGER.log(Level.SEVERE, "Chatline not found", e);
            CommunicationService.getCommunicationService(aUser).writeMessage("Chatline not found.<BR>\r\n");
        }
        return aUser.getRoom();
    }

}
