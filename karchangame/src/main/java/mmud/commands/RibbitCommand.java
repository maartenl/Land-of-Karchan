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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.annotations.VisibleForTesting;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.services.CommunicationService;

/**
 * The Ribbit Command: "Rrribbit". Makes you go "Rrribbit". It's a punishment
 * method. It is not allowed to execute this command faster than once every 10
 * seconds.
 *
 * @author maartenl
 * @see HeehawCommand
 */
public class RibbitCommand extends NormalCommand
{
  private final Map<String, LocalDateTime> ribbits = new ConcurrentHashMap<>();

  public RibbitCommand(String aRegExpr)
  {
    super(aRegExpr);
  }

  @VisibleForTesting
  public void setRibbitTime(String name, LocalDateTime time)
  {
    ribbits.put(name, time);
  }

  @Override
  public DisplayInterface run(String command, User aUser)
  {
    if (aUser.getFrogging() == 0)
    {
      return null;
    }
    LocalDateTime tenSecondsAgo = LocalDateTime.now().plusSeconds(-10L);
    if (ribbits.get(aUser.getName()) != null && tenSecondsAgo.isBefore(ribbits.get(aUser.getName())))
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("You cannot say 'Ribbit' that fast! You will get tongue tied!<br/>\r\n");
      return aUser.getRoom();
    }
    CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "A frog called " + aUser.getName() + " says \"Rrribbit!\""
      + ".<br/>\n");
    aUser.lessFrogging();
    if (aUser.getFrogging() == 0)
    {
      CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser, "A frog called " + aUser.getName() + " magically changes back to a " + aUser.getRace() + ".<br/>\n");
      ribbits.remove(aUser.getName());
    } else
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("You feel the need to say 'Ribbit' just " + aUser.getFrogging() + " times.<br/>\r\n");
      ribbits.put(aUser.getName(), LocalDateTime.now());
    }
    return aUser.getRoom();
  }

}
