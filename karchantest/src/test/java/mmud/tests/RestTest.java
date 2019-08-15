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
import java.util.Base64;
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

  public static final String BASEPATH = "/karchangame/resources";
  
  public static void init()
  {
    RestAssured.basePath = "/";
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
    String header = player + ":" + password;
    header = new String(Base64.getEncoder().encode(header.getBytes()));   
    Response response = given().log().ifValidationFails().
            contentType("application/json").
            header("Accept", "application/json").
            header("Authorization", "Basic "  + header).
            when().
            get("/").
            then().statusCode(200).
            and().extract().response();
    String jsession = response.cookie("JREMEMBERMEID");
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
            cookie("JREMEMBERMEID", jsession).
            param("logout", "true").
            contentType("application/json").
            header("Accept", "application/json").            
            when().
            get("/").
            then().statusCode(200). // no content
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
