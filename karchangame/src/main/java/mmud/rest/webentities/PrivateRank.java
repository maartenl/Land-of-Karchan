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
package mmud.rest.webentities;

import javax.xml.bind.annotation.XmlRootElement;
import mmud.database.entities.game.Guildrank;

/**
 *
 * @author maartenl
 */
@XmlRootElement
public class PrivateRank
{

  public String title;
  public Integer guildlevel;
  public Boolean accept_access;
  public Boolean reject_access;
  public Boolean settings_access;
  public Boolean logonmessage_access;

  public PrivateRank()
  {
    // Jersey wants it!
  }

  public PrivateRank(Guildrank rank)
  {
    this();
    this.title = rank.getTitle();
    this.guildlevel = rank.getGuildrankPK().getGuildlevel();
    this.accept_access = rank.getAcceptAccess();
    this.reject_access = rank.getRejectAccess();
    this.settings_access = rank.getSettingsAccess();
    this.logonmessage_access = rank.getLogonmessageAccess();
  }

  public static PrivateRank createRank(Guildrank rank)
  {
    if (rank == null)
    {
      return null;
    }
    return new PrivateRank(rank);
  }
}
