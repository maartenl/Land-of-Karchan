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
 * Chat to a group of people in the same chatline.
 * "chat deputies Help!".
 *
 * @author maartenl
 */
public class ChatCommand extends NormalCommand
{

  public ChatCommand(String aRegExpr)
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
    if (strings.length <= 2)
    {
      // "chat deputies", chat what?
      return null;
    }
    String chatlinename = strings[1];
    String aMessage = command.substring(command.indexOf(strings[2]));

    Optional<Chatline> chatLineOptional = aUser.getChatLine(chatlinename);
    if (chatLineOptional.isEmpty())
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("Unknown chatline.<BR>\r\n");
      return aUser.getRoom();
    }
    Chatline chatline = chatLineOptional.get();
    chatline.updateTime();
    String chatmessage = "<span class=\"chat-" + chatline.getColour() + "\">[" + chatline.getChatname() + "]<b>" + aUser.getName() + "</b>: " + aMessage + "</span><br/>\r\n";
    getPersonService().getActivePlayers().stream()
      .filter(x -> x.hasChatLine(chatlinename))
      .forEach(aTarget ->
      {
        CommunicationService.getCommunicationService(aUser).writeMessage(aTarget, chatmessage);
      });
    return aUser.getRoom();
  }

}
