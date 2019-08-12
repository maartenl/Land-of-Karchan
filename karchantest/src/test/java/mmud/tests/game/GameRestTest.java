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
package mmud.tests.game;

import mmud.tests.RestTest;
import static io.restassured.RestAssured.given;

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
            cookie("JREMEMBERMEID", jsession).
            pathParam("player", player).
            contentType("application/json").
            header("Accept", "application/json").
            when().
            post("/game/{player}/enter").
            then().statusCode(204); // no content
    return jsession;
  }

  protected void clear(final String jsession, final String player)
  {
    final String command = "clear";
    given().log().ifValidationFails().
            cookie("JREMEMBERMEID", jsession).
            queryParam("log", "true").
            pathParam("player", player).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200);
  }

  protected void quit(String jsession, String player)
  {
    given().log().ifValidationFails().
            cookie("JREMEMBERMEID", jsession).
            pathParam("player", player).
            contentType("application/json").
            header("Accept", "application/json").
            when().
            get("/game/{player}/quit").
            then().statusCode(200);
    logoff(jsession, player);
  }

}
