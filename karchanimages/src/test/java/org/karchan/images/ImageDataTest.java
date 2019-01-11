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

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class ImageDataTest
{
  
  public ImageDataTest()
  {
  }
  
  /**
   * Normal test of class ImageData.
   */
  @Test
  public void testGoodExample()
  {
    ImageData instance = new ImageData("/images/player/Victoria/areas/solol/dewdrop_inn.png");
    assertThat(instance.getPlayerName()).isEqualTo("Victoria");
    assertThat(instance.getImageUrl()).isEqualTo("/areas/solol/dewdrop_inn.png");
    assertThat(instance.isValid()).isTrue();
  }
  
  /**
   * Normal test of class ImageData but without dirs.
   */
  @Test
  public void testShortExample()
  {
    ImageData instance = new ImageData("/images/player/Victoria/title.png");
    assertThat(instance.getPlayerName()).isEqualTo("Victoria");
    assertThat(instance.getImageUrl()).isEqualTo("/title.png");
    assertThat(instance.isValid()).isTrue();
  }
  
  /**
   * Normal test of class ImageData but without dirs.
   */
  @Test
  public void testNoFile()
  {
    ImageData instance = new ImageData("/images/player/Victoria");
    assertThat(instance.getPlayerName()).isEqualTo("Victoria");
    assertThat(instance.getImageUrl()).isNull();
    assertThat(instance.isValid()).isFalse();
  }

  /**
   * Bad url test of class ImageData but without dirs.
   */
  @Test
  public void testBadExample()
  {
    ImageData instance = new ImageData("/images/player/");
    assertThat(instance.getPlayerName()).isNull();
    assertThat(instance.getImageUrl()).isNull();
    assertThat(instance.isValid()).isFalse();
  }

  
}
