package mmud.database.entities;

import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeUtilitiesTest
{
  @Test
  public void testGetDatetime()
  {
    String datetime = DateTimeUtilities.getDatetime(LocalDateTime.of(2024, Month.FEBRUARY, 12, 13, 14, 15));
    assertThat(datetime).isEqualTo("2024-02-12 13:14:15");
  }

  @Test
  public void testGetFullDatetime()
  {
    String datetime = DateTimeUtilities.getFullDatetime(LocalDateTime.of(2024, Month.FEBRUARY, 12, 13, 14, 15));
    assertThat(datetime).isEqualTo("Monday, 12 February 2024 13:14:15");
  }
}