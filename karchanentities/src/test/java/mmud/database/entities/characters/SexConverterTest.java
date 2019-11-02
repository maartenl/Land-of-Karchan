/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mmud.database.entities.characters;

import mmud.database.enums.Sex;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class SexConverterTest
{
  
  public SexConverterTest()
  {
  }

  /**
   * Test of convertToDatabaseColumn method, of class SexConverter.
   */
  @Test
  public void testConvertToDatabaseColumn()
  {
    SexConverter converter = new SexConverter();
    assertThat(converter.convertToDatabaseColumn(Sex.FEMALE)).isEqualTo("female");
    assertThat(converter.convertToDatabaseColumn(Sex.MALE)).isEqualTo("male");
    assertThat(converter.convertToDatabaseColumn(Sex.OTHER)).isEqualTo("other");
  }

  /**
   * Test of convertToEntityAttribute method, of class SexConverter.
   */
  @Test
  public void testConvertToEntityAttribute()
  {
    SexConverter converter = new SexConverter();
    assertThat(converter.convertToEntityAttribute("female")).isEqualTo(Sex.FEMALE);
    assertThat(converter.convertToEntityAttribute("male")).isEqualTo(Sex.MALE);
    assertThat(converter.convertToEntityAttribute("other")).isEqualTo(Sex.OTHER);
  }
  
}
