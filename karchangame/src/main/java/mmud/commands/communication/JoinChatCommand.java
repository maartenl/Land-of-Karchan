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

import java.util.Optional;

import mmud.commands.NormalCommand;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.Chatline;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Joins a chat to a group of people in the same chatline.
 * "joinchat deputies".
 *
 * @author maartenl
 * @see CreateChatCommand
 * @see DeleteChatCommand
 * @see JoinChatCommand
 * @see LeaveChatCommand
 * @see EditChatCommand
 */
public class JoinChatCommand extends NormalCommand
{

  public JoinChatCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    String[] strings = parseCommand(command);
    if (strings.length != 2)
    {
      // "chat deputies", chat what?
      return null;
    }
    String chatlinename = strings[1];
    Optional<Chatline> chatline = getChatService().getChatline(chatlinename);
    if (chatline.isEmpty())
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("Chatline not found.<BR>\r\n");
      return aUser.getRoom();
    }
    if (!aUser.joinChatline(chatline.get()))
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("Chatline not allowed.<BR>\r\n");
      return aUser.getRoom();
    }
    CommunicationService.getCommunicationService(aUser).writeMessage("You joined a chatline.<BR>\r\n");
    return aUser.getRoom();
  }

}
