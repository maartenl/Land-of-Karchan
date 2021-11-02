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
package org.karchan.webentities;


import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import mmud.database.entities.game.Guild;
import org.apache.commons.lang3.StringUtils;

/**
 * Information on a certain guild that is available to the big unwashed public.
 *
 * @author maartenl
 */
public class PublicGuild
{
  private final static Logger LOGGER = Logger.getLogger(PublicGuild.class.getName());

  private final String guildurl;
  private final String title;
  private final String bossname;
  private final String guilddescription;
  private final LocalDateTime creation;
  private final String image;

  public PublicGuild(Guild guild)
  {
    guildurl = StringUtils.trimToNull(guild.getHomepage());
    title = guild.getTitle();
    if (guild.getBoss() == null)
    {
      LOGGER.log(Level.INFO, "guilds: no boss found for guild {0}", guild.getName());
      bossname = "[Unknown]";
    } else
    {
      bossname = guild.getBoss().getName();
    }
    guilddescription = guild.getDescription();
    creation = guild.getCreation();
    image = guild.getImage();
  }

  public String getGuildurl()
  {
    return guildurl;
  }

  public String getTitle()
  {
    return title;
  }

  public String getBossname()
  {
    return bossname;
  }

  public String getGuilddescription()
  {
    return guilddescription;
  }

  public LocalDateTime getCreation()
  {
    return creation;
  }

  public String getImage()
  {
    return image;
  }
}
