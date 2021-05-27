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
package mmud.rest.webentities;

import java.time.LocalDateTime;

import mmud.JsonUtils;
import mmud.database.entities.web.Image;

/**
 *
 * @author maartenl
 */
public class PrivateImage
{
  public static final String GET_QUERY = "select json_object(\"id\", id, \"url\", url, \"mimeType\", mimeType, \"createDate\", createDate) from images where owner = ? order by id";

  /**
   * Base 64 encoded content for the image.
   */
  public String content;
  public LocalDateTime createDate;
  public Long id;
  public String mimeType;
  public String owner;
  public String url;

  public PrivateImage()
  {
  }

  public PrivateImage(Image image)
  {
//    this.content = image.getContent();
    this.createDate = image.getCreateDate();
    this.id = image.getId();
    this.mimeType = image.getMimeType();
    this.owner = image.getOwner().getName();
    this.url = image.getUrl();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }
}
