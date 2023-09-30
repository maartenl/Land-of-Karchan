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

/**
 * Leaves a chat to a group of people in the same chatline.
 * "leavechat deputies".
 *
 * @author maartenl
 * @see CreateChatCommand
 * @see DeleteChatCommand
 * @see JoinChatCommand
 * @see LeaveChatCommand
 * @see EditChatCommand
 */
public class LeaveChatCommand extends NormalCommand
{

  public LeaveChatCommand(String aRegExpr)
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
    boolean success = aUser.leaveChatLine(chatlinename);
    if (success)
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("You left chatline.<BR>\r\n");
    } else
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("Chatline not found.<BR>\r\n");
    }
    return aUser.getRoom();
  }

}
