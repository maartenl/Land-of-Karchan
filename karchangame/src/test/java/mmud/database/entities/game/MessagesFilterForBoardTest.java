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
   * Test of customize method, of class MessagesFilterForBoard.
   */
  @Test
  public void testGetLastSundayOnASaturday()
  {
    Calendar calendar = new GregorianCalendar(2019, Calendar.FEBRUARY, 9);
    Date actual = MessagesFilterForBoard.getLastSunday(calendar);
    Date expected = new GregorianCalendar(2019, Calendar.FEBRUARY, 3, 0, 0).getTime();
    assertThat(actual).isEqualTo(expected);
  }
  
  @Test
  public void testGetLastSundayOnAMonday()
  {
    Calendar calendar = new GregorianCalendar(2019, Calendar.FEBRUARY, 11);
    Date actual = MessagesFilterForBoard.getLastSunday(calendar);
    Date expected = new GregorianCalendar(2019, Calendar.FEBRUARY, 10, 0, 0).getTime();
    assertThat(actual).isEqualTo(expected);
  }

}
