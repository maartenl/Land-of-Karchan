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
package mmud.rest.tests.emotes;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import mmud.rest.tests.GameRestTest;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class BowTest extends GameRestTest
{

  @Test
  public void testBowCommand()
  {
    final String hotblack = "Hotblack";
    final String password = "secret";
    String jsession = enterGame(password, hotblack);
    final String command = "bow";

    // bow
    Response gameResponse = given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/{player}/play").
            then().statusCode(200).
            and().body("title", equalTo("The Cave")).
            and().body("image", equalTo("/images/gif/cave.gif")).
            and().body("log.log", endsWith("You have no new Mudmail...</p>\nYou bow.<br />\n")).
            and().extract().response();
    System.out.println(gameResponse.prettyPrint());

    quit(jsession, hotblack);
  }

}
