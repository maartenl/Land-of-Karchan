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
import java.time.Month;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import mmud.database.entities.characters.User;
import mmud.database.entities.game.DisplayInterface;
import mmud.services.CommunicationService;

import javax.annotation.Nonnull;

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

  private static final LoadingCache<String, LocalDateTime> ribbits;

  static
  {
    // just put a default here.
    // as long as it is sufficiently old, it'll be fine.
    CacheLoader<String, LocalDateTime> loader = new CacheLoader<>()
    {
      @Nonnull
      @Override
      public LocalDateTime load(@Nonnull String key)
      {
        // just put a default here.
        // as long as it is sufficiently old, it'll be fine.
        return LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0, 0);
      }
    };
    ribbits = CacheBuilder.newBuilder()
      .expireAfterAccess(20, TimeUnit.SECONDS)
      .build(loader);
  }


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
    if (tenSecondsAgo.isBefore(ribbits.getUnchecked(aUser.getName())))
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("You cannot say 'Ribbit' that fast! You will " +
        "get tongue tied!<br/>\r\n");
      return aUser.getRoom();
    }
    CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser,
      "A frog called " + aUser.getName() + " says \"Rrribbit!\""
      + ".<br/>\n");
    aUser.lessFrogging();
    if (aUser.getFrogging() == 0)
    {
      CommunicationService.getCommunicationService(aUser.getRoom()).sendMessage(aUser,
        "A frog called " + aUser.getName() + " magically changes back to a " + aUser.getRace() + ".<br/>\n");
    } else
    {
      CommunicationService.getCommunicationService(aUser).writeMessage("You feel the need to say 'Ribbit' just " + aUser.getFrogging() + " times.<br/>\r\n");
      ribbits.put(aUser.getName(), LocalDateTime.now());
    }
    return aUser.getRoom();
  }

}
