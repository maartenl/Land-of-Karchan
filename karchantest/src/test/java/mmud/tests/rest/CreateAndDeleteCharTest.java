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

import io.restassured.response.Response;
import mmud.tests.RestTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

/**
 * This test will try to create a new character, and delete it again, using the
 * "private" rest service.
 *
 * @author maartenl
 */
public class CreateAndDeleteCharTest extends RestTest
{

  public static Response createCharacter(final String player)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    pathParam("player", player).
                    contentType("application/json").header("Accept", "application/json").
                    body("{\"name\":\"" + player + "\",\"title\":\"W\",\"sex\":\"male\",\"password\":\"secret\",\"password2\":\"secret\",\"realname\":\"w\",\"email\":\"\",\"race\":\"human\",\"age\":\"young\",\"height\":\"tall\",\"width\":\"very thin\",\"complexion\":\"swarthy\",\"eyes\":\"black-eyed\",\"face\":\"long-faced\",\"hair\":\"black-haired\",\"beard\":\"none\",\"arm\":\"none\",\"leg\":\"none\"}").
                    when().
                    post("/game/{player}").
                    then().statusCode(200).
                    and().extract().response();
    // {"name":"Tralala","title":"W","sex":"male","password":"secret","password2":"secret","realname":"w","email":"","race":"human","age":"young","height":"tall","width":"very thin","complexion":"swarthy","eyes":"black-eyed","face":"long-faced","hair":"black-haired","beard":"none","arm":"none","leg":"none"}
    return gameResponse;
  }

  public static Response deleteCharacter(String jsession, final String player)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JREMEMBERMEID", jsession).
                    pathParam("player", player).
                    contentType("application/json").header("Accept", "application/json").
                    when().
                    delete("/private/{player}").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }

  /**
   * Create a character called Tralala, and deletes it at once again.
   */
  @Test
  public void testCreateAndDestroy()
  {
    final String tralala = "Tralala";
    final String password = "secret";

    createCharacter(tralala);
    String jsession = login(tralala, password);
    Response gameResponse
            = CreateAndDeleteCharTest.deleteCharacter(jsession, tralala);
  }

  /**
   * Attempts to delete Karn, this isn't a test. It's here to try it on a
   * more complex character.
   */
//  @Test
  public void testDestroyKarn()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = CreateAndDeleteCharTest.deleteCharacter(jsession, karn);
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
