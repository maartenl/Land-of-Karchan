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
import io.restassured.response.Response;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the private rest services of Karchan.
 *
 * @author maartenl
 */
public class WikipageRestTest extends RestTest
{

  public WikipageRestTest()
  {
  }

  /**
   * Retrieves a wikipage.
   */
  @Test
  public void testGetWikipage()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = Helper.getWikipage(jsession, karn, "Roleplay");
    print(gameResponse);
    assertThat((String) gameResponse.path("title")).isEqualTo("Roleplay");
    assertThat((String) gameResponse.path("content")).contains("This Page is a WIP.");
    assertThat((String) gameResponse.path("name")).isEqualTo("Victoria");
    assertThat((String) gameResponse.path("version")).isEqualTo("5.9");
    assertThat((String) gameResponse.path("createDate")).isEqualTo("2018-10-17T01:04:52");
    assertThat((String) gameResponse.path("modifiedDate")).isEqualTo("2018-10-17T01:04:52");
    logoff(jsession, karn); 
  }
  /**
   * Verifies the guild of Karn.
   */
  @Test
  public void testCreateModifyAndDeleteWikipage()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    String createJson = "{\"content\":\"This is a test Page.\",\"title\":\"Testingpage\"}";
    Helper.createWikipage(createJson, jsession, karn);

    Response wikipage
            = Helper.getWikipage(jsession, karn, "Testingpage");
    print(wikipage);
    assertThat((String) wikipage.path("title")).isEqualTo("Testingpage");
    assertThat((String) wikipage.path("content")).isEqualTo("This is a test Page.");
    assertThat((String) wikipage.path("name")).isEqualTo("Karn");
    assertThat((String) wikipage.path("version")).isEqualTo("1.0");
    assertThat((String) wikipage.path("createDate")).isNotNull();
    assertThat((String) wikipage.path("modifiedDate")).isNotNull();

    String updateJson = "{\"content\":\"This is a test Page.\",\"title\":\"Testingpage\"}";
    Helper.updateWikipage(updateJson, jsession, karn);

    Response updatedWikipage
            = Helper.getWikipage(jsession, karn, "Testingpage");
    print(updatedWikipage);
    assertThat((String) updatedWikipage.path("title")).isEqualTo("Testingpage");
    assertThat((String) updatedWikipage.path("content")).isEqualTo("This is a test Page.");
    assertThat((String) updatedWikipage.path("name")).isEqualTo("Karn");
    assertThat((String) updatedWikipage.path("version")).isEqualTo("1.1");
    assertThat((String) updatedWikipage.path("createDate")).isNotNull();
    assertThat((String) updatedWikipage.path("modifiedDate")).isNotNull();

    logoff(jsession, karn);
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
