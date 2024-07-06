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
package mmud.database.entities;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtilities
{

  private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter AWESOME_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss");

  public static LocalDateTime getLastSunday(LocalDateTime now)
  {
    return now.with(DayOfWeek.SUNDAY).minusDays(7).with(LocalTime.of(0, 1));
  }

  /**
   * Formats date and time.
   * @param posttime the date and time to format
   * @return Something like "2024-02-12 13:14:15".
   */
  public static String getDatetime(LocalDateTime posttime)
  {
    if (posttime == null)
    {
      return null;
    }
    return posttime.format(CUSTOM_FORMATTER);
  }

  /**
   * Formats date and time.
   * @param posttime the date and time to format
   * @return Something like "Monday, 12 February 2024 13:14:15".
   */
  public static String getFullDatetime(LocalDateTime posttime) {
    if (posttime == null)
    {
      return null;
    }
    return posttime.format(AWESOME_FORMATTER);
  }
}
