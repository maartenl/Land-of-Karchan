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
package mmud.tests;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 *
 * @author maartenl
 */
public class RestTest
{

  private static final boolean DEBUGGING = false;

  public static void init()
  {
    RestAssured.basePath = "/karchangame/resources";
  }

  /**
   * Logs the user into the website. (Does not do anything with the game)
   *
   * @param player the name of the player/user of the website
   * @param password the password of the user
   * @return the jsession id used after logging in.
   */
  public String login(final String player, final String password)
  {
    Response response = given().log().ifValidationFails().
            param("password", password).
            pathParam("player", player).
            contentType("application/json").
            header("Accept", "application/json").
            when().
            put("/game/{player}/logon").
            then().statusCode(204). // no content
            and().extract().response();
    String jsession = response.cookie("JSESSIONID");
    MatcherAssert.assertThat(jsession, not(nullValue()));
    return jsession;
  }

  /**
   * Logs the user out of the website. (Does not do anything with the game)
   *
   * @param jsession the jsession id cookie
   * @param player the name of the user
   */
  public void logoff(String jsession, final String player)
  {
    Response response = given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            pathParam("player", player).
            contentType("application/json").
            header("Accept", "application/json").
            when().
            put("/game/{player}/logoff").
            then().statusCode(204). // no content
            and().extract().response();
  }

  protected void print(Response gameResponse)
  {
    if (DEBUGGING)
    {
      System.out.println(gameResponse.prettyPrint());
    }
  }

}
