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

import mmud.Utils;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.MudException;
import mmud.services.CommunicationService;
import mmud.services.PersonCommunicationService;
import mmud.services.RoomCommunicationService;

/**
 * Provides an emotional response. Acceptable format is:
 * <TT>[emotion] &lt;[adverb]&gt; &lt;to [person]&gt;</TT><P>
 * For example: <UL><LI>agree<LI>smile to Karn
 * <LI>smile evilly<LI>smile evilly to Karn</UL>
 *
 * @author maartenl
 */
public class EmotionCommand extends TargetCommand
{

  public EmotionCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @Override
  protected DisplayInterface actionTo(User aUser, Person aTarget, String verb, String command) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    PersonCommunicationService communicationServiceTarget = CommunicationService.getCommunicationService(aTarget);
    RoomCommunicationService communicationServiceRoom = CommunicationService.getCommunicationService(aUser.getRoom());
    String[] plural =
    {
      verb.toLowerCase(), Utils.returnEmotion(verb.toLowerCase())
    };
    if (plural == null)
    {
      return null;
    }
    if (command != null)
    {
      // agree evilly to Karn
      if (Utils.existsAdverb(command))
      {
        communicationService.writeMessage("You " + plural[0] + " " + command.toLowerCase() + " to " + aTarget.getName() + ".<BR>\r\n");
        communicationServiceTarget.writeMessage(aUser.getName() + " " + plural[1] + " " + command.toLowerCase() + " to you.<BR>\r\n");
        communicationServiceRoom.sendMessageExcl(aUser, aTarget, aUser.getName() + " " + plural[1] + " " + command.toLowerCase() + " to " + aTarget.getName() + ".<BR>\r\n");
      } else
      {
        communicationService.writeMessage("Unknown adverb found.<BR>\r\n");
      }
      return aUser.getRoom();
    }
    // agree to Karn
    communicationService.writeMessage("You " + plural[0] + " to " + aTarget.getName() + ".<BR>\r\n");
    communicationServiceTarget.writeMessage(aUser.getName() + " " + plural[1] + " to you.<BR>\r\n");
    communicationServiceRoom.sendMessageExcl(aUser, aTarget, aUser.getName() + " " + plural[1] + " to " + aTarget.getName() + ".<BR>\r\n");
    return aUser.getRoom();
  }

  @Override
  protected DisplayInterface action(User aUser, String verb, String command) throws MudException
  {
    PersonCommunicationService communicationService = CommunicationService.getCommunicationService(aUser);
    RoomCommunicationService communicationServiceRoom = CommunicationService.getCommunicationService(aUser.getRoom());
    String[] plural =
    {
      verb.toLowerCase(), Utils.returnEmotion(verb.toLowerCase())
    };
    if (plural == null)
    {
      return null;
    }
    if (command != null)
    {
      // agree evilly
      if (Utils.existsAdverb(command))
      {
        communicationService.writeMessage("You " + plural[0] + " " + command.toLowerCase() + ".<BR>\r\n");
        communicationServiceRoom.sendMessageExcl(aUser, aUser.getName() + " " + plural[1] + " " + command.toLowerCase() + ".<BR>\r\n");
      } else
      {
        communicationService.writeMessage("Unknown adverb found.<BR>\r\n");
      }
      return aUser.getRoom();
    }
    // agree
    communicationService.writeMessage("You " + plural[0] + ".<BR>\r\n");
    communicationServiceRoom.sendMessageExcl(aUser, aUser.getName() + " " + plural[1] + ".<BR>\r\n");
    return aUser.getRoom();
  }
}
