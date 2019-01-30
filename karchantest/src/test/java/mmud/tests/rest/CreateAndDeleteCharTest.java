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
package mmud.tests.rest;

import mmud.tests.RestTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test will try to create a new character, and delete it again, using 
 * the "private" rest service.
 * @author maartenl
 */
public class CreateAndDeleteCharTest extends RestTest
{
  
  /**
   * Verifies the guild of Karn.
   */
  @Test
  public void testCreateaAndDestroy()
  {
//    final String karn = "Karn";
//    final String password = "secret";
//
//    String jsession = login(karn, password);
//    Response gameResponse
//            = Helper.getGuild(jsession, karn);
//    print(gameResponse);
//    assertThat(gameResponse.path("title"), equalTo("Benefactors of Karchan"));
//    assertThat(gameResponse.path("name"), equalTo("deputy"));
//    assertThat(gameResponse.path("bossname"), equalTo("Karn"));
//    assertThat(gameResponse.path("guildurl"), equalTo("http://www.karchan.org"));
//    logoff(jsession, karn);
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    init();
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

}
