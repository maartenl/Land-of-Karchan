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
package org.karchan.tests;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class GameRestTest
{

  public GameRestTest()
  {
  }

  private String enterGame(final String password, final String player)
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

  private void quit(String jsession, String player)
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
//    given().log().ifValidationFails().
//            cookie("JSESSIONID", jsession).
//            queryParam("log", "true").
//            pathParam("player", hotblack).
//            queryParam("offset", "").
//            contentType("application/json").
//            header("Accept", "application/json").
//            body(command).
//            when().
//            post("/{player}/play").
//            then().statusCode(200).
//            and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n"));
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
            post("/{player}/play").
            then().statusCode(200).
            and().body("log.log", endsWith("You have no new Mudmail...</p>\nGuild disaster created.<br />\n")).
            and().extract().response();
    System.out.println(gameResponse.prettyPrint());

    quit(jsession, hotblack);
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    RestAssured.basePath = "/karchangame/resources/game";
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

}
