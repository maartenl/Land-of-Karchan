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
package mmud.database.entities.game;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class MessagesFilterForBoardTest
{
  
  public MessagesFilterForBoardTest()
  {
  }

  /**
   * Returns the last Sunday, of date 2019-02-09, at 15:05:02, which was a Saturday.
   * Expected result: Sunday, 2019-02-03.
   */
  @Test
  public void testGetLastSundayOnASaturday()
  {
    LocalDateTime calendar = LocalDateTime.of(2019, Month.FEBRUARY, 9, 15, 5, 2);
    LocalDateTime actual = MessagesFilterForBoard.getLastSunday(calendar);
    LocalDateTime expected = LocalDateTime.of(2019, Month.FEBRUARY, 3, 0, 1, 0);
    assertThat(actual).isEqualTo(expected);
  }
  
  /**
   * Returns the last Sunday, of date 2019-02-09, at 15:05:02, which was a Monday.
   * Expected result: Sunday, 2019-02-10.
   */
  @Test
  public void testGetLastSundayOnAMonday()
  {
    LocalDateTime calendar = LocalDateTime.of(2019, Month.FEBRUARY, 11, 15, 5, 2);
    LocalDateTime actual = MessagesFilterForBoard.getLastSunday(calendar);
    LocalDateTime expected = LocalDateTime.of(2019, Month.FEBRUARY, 10, 0, 1, 0);
    assertThat(actual).isEqualTo(expected);
  }

}
