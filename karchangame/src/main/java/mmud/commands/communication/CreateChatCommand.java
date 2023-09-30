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
import mmud.database.entities.game.Chatline;
import mmud.database.entities.game.Chatlineusers;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.ChatlineAlreadyExistsException;
import mmud.exceptions.ChatlineOperationNotAllowedException;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a new chatline.
 * "createchat [name] [attribute|none] [colour]", for example "createchat deputies deputy green".
 *
 * @author maartenl
 * @see CreateChatCommand
 * @see DeleteChatCommand
 * @see JoinChatCommand
 * @see LeaveChatCommand
 * @see EditChatCommand
 */
public class CreateChatCommand extends NormalCommand
{
  private static final Logger LOGGER = Logger.getLogger(CreateChatCommand.class.getName());

  public CreateChatCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  public DisplayInterface run(String command, User aUser) throws MudException
  {
    String[] strings = parseCommand(command);
    if (strings.length != 4)
    {
      return null;
    }
    String chatlinename = strings[1];
    String attribute = strings[2].equalsIgnoreCase("none") ? null : strings[2];
    String colour = strings[3];
    try {
      Chatline chatline = getChatService().createChatline(chatlinename, attribute, colour, aUser);
      aUser.joinChatline(chatline);
      CommunicationService.getCommunicationService(aUser).writeMessage("Chatline created.<BR>\r\n");
    } catch (ChatlineAlreadyExistsException e) {
      LOGGER.log(Level.SEVERE, "Chatline already exists", e);
      CommunicationService.getCommunicationService(aUser).writeMessage("Chatline already exists.<BR>\r\n");
    }
    return aUser.getRoom();
  }

}
