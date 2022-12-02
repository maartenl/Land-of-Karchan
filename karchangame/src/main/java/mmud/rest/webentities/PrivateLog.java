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

import mmud.JsonUtils;

/**
 * @author maartenl
 */
public class PrivateLog
{
  public long offset;
  public long size;
  public String log;
  public long totalsize;

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static PrivateLog fromJson(String json)
  {
    return JsonUtils.fromJson(json, PrivateLog.class);
  }
}
