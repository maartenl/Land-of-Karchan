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

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class ClearTest extends GameRestTest
{

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    init();
  }

  @Test
  public void testSayCommand()
  {
    final String hotblack = "Hotblack";
    final String password = "secret";
    String jsessionHotblack = enterGame(password, hotblack);
    final String command = "clear";

    Response gameResponse = given().log().ifValidationFails().
            cookie("JSESSIONID", jsessionHotblack).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200).
            and().body("title", equalTo("The Cave")).
            and().body("image", equalTo("/images/gif/cave.gif")).
            and().extract().response();
    String log = gameResponse.body().jsonPath().get("log.log").toString();
    assertThat(log, equalTo("You cleared your mind.<br />\n"));

    quit(jsessionHotblack, hotblack);
  }

}