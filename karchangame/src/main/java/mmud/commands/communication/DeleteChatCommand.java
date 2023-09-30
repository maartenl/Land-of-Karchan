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
import mmud.exceptions.ChatlineNotFoundException;
import mmud.exceptions.ChatlineOperationNotAllowedException;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Removes a chat, including all people that have joined.
 * "deletechat deputies".
 *
 * @author maartenl
 * @see CreateChatCommand
 * @see DeleteChatCommand
 * @see JoinChatCommand
 * @see LeaveChatCommand
 * @see EditChatCommand
 */
public class DeleteChatCommand extends NormalCommand
{
    private static final Logger LOGGER = Logger.getLogger(DeleteChatCommand.class.getName());

    public DeleteChatCommand(String aRegExpr)
    {
        super(aRegExpr);
    }

    public CommType getCommType()
    {
        return CommType.CHAT;
    }

    @Override
    public DisplayInterface run(String command, User aUser) throws MudException
    {
        String[] strings = parseCommand(command);
        if (strings.length != 2)
        {
            return null;
        }
        String chatlinename = strings[1];
        try
        {
            getChatService().deleteChatLine(aUser, chatlinename);
            CommunicationService.getCommunicationService(aUser).writeMessage("You removed the chatline.<BR>\r\n");
        } catch (ChatlineOperationNotAllowedException e)
        {
            LOGGER.log(Level.SEVERE, "Delete of chatline not allowed", e);
            CommunicationService.getCommunicationService(aUser).writeMessage("Delete of chatline not allowed. You are neither the owner nor an administrator.<BR>\r\n");
        } catch (ChatlineNotFoundException e)
        {
            LOGGER.log(Level.SEVERE, "Chatline not found", e);
            CommunicationService.getCommunicationService(aUser).writeMessage("Chatline not found.<BR>\r\n");
        }
        return aUser.getRoom();
    }

}
