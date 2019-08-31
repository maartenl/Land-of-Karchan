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
package org.karchan.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class KarchanAuthenticationStoreNGTest
{

  public KarchanAuthenticationStoreNGTest()
  {
  }

  /**
   * Test of encryptThisString method, of class KarchanAuthenticationStore.
   */
  @Test
  public void testEncryptThisString()
  {
    assertThat(KarchanAuthenticationStore.encryptThisString("secret")).isEqualTo("bd2b1aaf7ef4f09be9f52ce2d8d599674d81aa9d6a4421696dc4d93dd0619d682ce56b4d64a9ef097761ced99e0f67265b5f76085e5b0ee7ca4696b2ad6fe2b2");
  }

}
