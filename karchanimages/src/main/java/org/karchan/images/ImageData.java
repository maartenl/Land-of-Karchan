/*
 * Copyright (C) 2019 maartenl
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
package org.karchan.images;

/**
 *
 * @author maartenl
 */
class ImageData
{
  private final String playerName;
  private final String imageUrl;
  private final boolean isValid;
  
  ImageData(String url) 
  {
    if (!url.startsWith("/images/player/") || "/images/player/".equals(url))
    {
      playerName = null;
      imageUrl = null;
      isValid = false;
      return;
    }
    String playerName = url.substring("/images/player/".length());
    if (playerName.indexOf("/") == -1)
    {
      this.playerName = playerName;
      imageUrl = null;
      isValid = false;
      return;
    }
    imageUrl = playerName.substring(playerName.indexOf("/"));
    playerName = playerName.substring(0, playerName.indexOf("/"));
    this.playerName = playerName;
    isValid = true;
  }
  
  String getPlayerName()
  {
    return playerName;
  }

  String getImageUrl()
  {
    return imageUrl;
  }

  boolean isValid()
  {
    return isValid;
  }
  
  @Override
  public String toString()
  {
    return "ImageData{" + "playerName=" + playerName + ", imageUrl=" + imageUrl + '}';
  }
  
}
