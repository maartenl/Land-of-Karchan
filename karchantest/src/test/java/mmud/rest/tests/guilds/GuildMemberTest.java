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
package mmud.rest.tests.guilds;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import mmud.rest.tests.GameRestTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class GuildMemberTest extends GameRestTest
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

  /**
   * Show all the details of your current guild.
   */
  @Test
  public void testGuilddetailsCommand()
  {
    final String karn = "Karn";
    final String password = "secret";
    String jsession = enterGame(password, karn);
    clear(jsession, karn);

    String command = "guilddetails";
    Response gameResponse = given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", karn).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200).
            and().body("title", equalTo("Benefactors of Karchan")).
            and().body("image", equalTo("http://www.karchan.org/images/jpeg/Karchan1.jpg")).
            and().extract().response();
    String body = gameResponse.body().jsonPath().get("body").toString();
    assertThat(body, containsString("Benefactors of Karchan"));
    assertThat(body, containsString("You are a member of the <I>Benefactors of Karchan</I> (deputy)."));
    assertThat(body, containsString("The current guildmaster is <I>Karn</I>."));
    assertThat(body, containsString("The guild has 3 members."));
    assertThat(body, containsString("Karn"));
    assertThat(body, containsString("Cedri"));
    assertThat(body, containsString("Crissy"));
    assertThat(body, containsString("Hopefuls"));
    assertThat(body, containsString("Guildranks"));
    print(gameResponse);

    quit(jsession, karn);
  }

  /**
   * Send a message to all the active people in your guild.
   */
  @Test
  public void testGuildmessageCommand()
  {
    final String karn = "Karn";
    final String password = "secret";
    String jsession = enterGame(password, karn);
    clear(jsession, karn);

    String command = "guild Good morning, everyone.";
    Response gameResponse = given().log().ifValidationFails().
            cookie("JSESSIONID", jsession).
            queryParam("log", "true").
            pathParam("player", karn).
            queryParam("offset", "").
            contentType("application/json").
            header("Accept", "application/json").
            body(command).
            when().
            post("/game/{player}/play").
            then().statusCode(200).
            and().extract().response();
    String body = gameResponse.body().jsonPath().get("log.log").toString();
    assertThat(body, endsWith("<span style=\"color: Red;\">[guild]<b>Karn</b>: Good morning, everyone.</span>\n<br />\n"));

    quit(jsession, karn);
  }

}
