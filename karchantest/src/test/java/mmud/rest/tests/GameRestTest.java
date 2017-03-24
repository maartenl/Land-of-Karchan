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
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.testng.annotations.BeforeClass;

/**
 *
 * @author maartenl
 */
public class GameRestTest
{

  public GameRestTest()
  {
  }

  protected String enterGame(final String password, final String player)
  {
    // enterGame
    Response response = given().log().ifValidationFails().
            param("password", password).
            pathParam("player", player).
            contentType("application/json").
            header("Accept", "application/json").
            when().
            put("/{player}/logon").
            then().statusCode(204). // no content
            and().extract().response();
    String jsession = response.cookie("JSESSIONID");
    MatcherAssert.assertThat(jsession, not(nullValue()));
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
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    RestAssured.basePath = "/karchangame/resources/game";
  }
}
