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
package mmud.testing;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

/**
 * As I am not at home yet with all the different time and date utilities in the
 * new JDK, this is here for my edification.
 */
public class DateTimeTest
{
  
  @Test
  public void testDurationInSeconds()
  {
    Duration between = Duration.between(LocalDateTime.of(2000, Month.MARCH, 3, 12, 5),
            LocalDateTime.of(2000, Month.MARCH, 3, 15, 5));
    assertThat(between.getSeconds()).isEqualTo(3 * 60 * 60);
  }
  
  @Test
  public void testFromLocalDateTimeToMsec() 
  {
    LocalDateTime creationdate = LocalDateTime.of(2018, Month.MARCH, 12, 13, 12, 40);
    Long millis = creationdate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    assertThat(millis).isEqualTo(1520856760000L);
  }
  
  @Test
  public void testFromMsecToLocalDateTime()
  {
    long millis = 1520856760000L;
    LocalDateTime actual = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();   
    LocalDateTime expected = LocalDateTime.of(2018, Month.MARCH, 12, 13, 12, 40);
    assertThat(actual).isEqualTo(expected);
  }
}
