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
package mmud.tests.game.guilds;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import mmud.tests.game.GameRestTest;
import mmud.tests.rest.Helper;
import static org.hamcrest.Matchers.endsWith;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class GuildMasterTest extends GameRestTest
{

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    init();
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

  // @Test
  public void testCreateAndDestroyGuilds()
  {
    final String hotblack = "Hotblack";
    final String marvin = "Marvin";

    final String password = "secret";
    String jsession = enterGame(password, hotblack);

    String command = "createguild disaster Disaster Area";
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200).
            and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n"));

    command = "deleteguild";
    Response gameResponse = given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200).
            and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n")).
            and().extract().response();
    print(gameResponse);

    quit(jsession, hotblack);
  }

  /**
   * Creation of a guild, creating and assigning guildranks and destruction of a guild.
   * Along with all the other stuff to do.
   */
  @Test
  public void testGuildCommands()
  {
    final String hotblack = "Hotblack";
    final String marvin = "Marvin";

    final String password = "secret";
    String jsession = enterGame(password, hotblack);

    String command = "createguild disaster Disaster Area";
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200);
    // and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n"));

    // make rank
    command = "guildaddrank 1 Minion";
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200);
    // and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n"));

    // assign rank
    command = "guildassignrank 1 Marvin";
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200);
    // and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n"));

    // unassign rank
    command = "guildassignrank none Marvin";
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200);
    // and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n"));
    Response guild = Helper.getGuild(jsession, hotblack);
    System.out.println(guild.prettyPrint());
    Response guildranks = Helper.getGuildranks(jsession, hotblack);
    System.out.println(guildranks.prettyPrint());
    command = "guilddelrank 1";
    given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200);
    // and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n"));

    command = "deleteguild";
    Response gameResponse = given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", hotblack).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200).
            //            and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n")).
            and().extract().response();
    print(gameResponse);

    quit(jsession, hotblack);
  }

}