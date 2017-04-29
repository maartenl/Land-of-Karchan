/*
 * Copyright (C) 2017 maartenl
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
package mmud.rest.tests;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import org.testng.annotations.BeforeClass;

/**
 *
 * @author maartenl
 */
public abstract class GameRestTest extends RestTest
{

  public GameRestTest()
  {
  }

  protected String enterGame(final String password, final String player)
  {
    String jsession = login(player, password);
    // enter game
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            pathParam("player", player).
            contentType("application/json").
            header("Accept", "application/json").
            when().
            post("/{player}/enter").
            then().statusCode(204); // no content
    return jsession;
  }

  protected void quit(String jsession, String player)
  {
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            pathParam("player", player).
            contentType("application/json").
            header("Accept", "application/json").
            when().
            get("/{player}/quit").
            then().statusCode(200);
    logoff(jsession, player);
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    RestAssured.basePath = "/karchangame/resources/game";
  }
}
