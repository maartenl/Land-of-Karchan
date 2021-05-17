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
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;

/**
 * Creates a new chatline.
 * "createchat [name] [attribute|none] [colour]", for example "createchat deputies deputy green".
 *
 * @author maartenl
 */
public class CreateChatCommand extends NormalCommand
{

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

    if (getPersonBean().getChatline(chatlinename).isPresent())
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("Chatline already exists.<BR>\r\n");
      return aUser.getRoom();
    }

    Chatline chatline = new Chatline();
    chatline.setChatname(chatlinename);
    chatline.setAttributename(attribute);
    chatline.setColour(colour);
    aUser.createChatline(chatline);
    CommunicationService.getCommunicationService(aUser).writeMessage("Chatline created.<BR>\r\n");
    return aUser.getRoom();
  }

}
