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
package mmud.tests.rest;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import static mmud.tests.RestTest.BASEPATH;

/**
 *
 * @author maartenl
 */
public class Helper
{

  public static Response getGuild(String jsession, final String player)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JREMEMBERMEID", jsession).
                    pathParam("player", player).
                    contentType("application/json").header("Accept", "application/json").
                    when().
                    get(BASEPATH + "/private/{player}/guild").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }

  public static Response getGuildranks(String jsession, String player)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JREMEMBERMEID", jsession).
                    pathParam("player", player).
                    contentType("application/json").header("Accept", "application/json").
                    when().
                    get(BASEPATH + "/private/{player}/guild/ranks").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }

  static Response getWikipage(String jsession, String player, String title)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JREMEMBERMEID", jsession).
                    pathParam("title", title).
                    contentType("application/json").header("Accept", "application/json").
                    when().
                    get(BASEPATH + "/wikipages/{title}").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }

  static Response createWikipage(String json, String jsession, String karn)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JREMEMBERMEID", jsession).
                    pathParam("title", "Testingpage").
                    contentType("application/json").header("Accept", "application/json").
                    body(json).
                    when().
                    post(BASEPATH + "/wikipages/{title}").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }

  static Response updateWikipage(String json, String jsession, String karn)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JREMEMBERMEID", jsession).
                    pathParam("title", "Testingpage").
                    contentType("application/json").header("Accept", "application/json").
                    body(json).
                    when().
                    put(BASEPATH + "/wikipages/{title}").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }
}
