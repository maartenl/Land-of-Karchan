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
import static io.restassured.RestAssured.*;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.hamcrest.MatcherAssert;
import static org.hamcrest.Matchers.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the public rest services of Karchan.
 *
 * @author maartenl
 */
public class PublicRestTest
{

  public PublicRestTest()
  {
  }

  private void prettyPrint(String url)
  {
    String result
            = given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
                    when().
                    get(url).
                    prettyPrint();
    System.out.println(result);
    MatcherAssert.assertThat(result, equalTo("Hello"));
  }

  /**
   * Verifies the list of the active deputies.
   */
  @Test
  public void testStatus()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/status").
            then().statusCode(200).
            and().contentType("application/json").
            and().body("name", hasItems("Aurican", "Blackfyre", "Eilan", "Ephinie", "Karn", "Midevia", "Mya", "Victoria"));
  }

  /**
   * Verifies the wholist.
   */
  @Test
  public void testWhoList()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/who").
            then().statusCode(200).
            and().contentType("application/json").
            and().body("$", hasSize(0));
  }

  /**
   * Verifies the fortunes.
   */
  @Test
  public void testFortunes()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/fortunes").
            then().statusCode(200).
            and().contentType("application/json").
            and().body("name", hasItems("Shara", "Rev"));
  }

  /**
   * Verifies the guilds.
   */
  @Test
  public void testGuilds()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/guilds").
            then().statusCode(200).
            and().contentType("application/json").
            and().body("title", hasItems("Assembly of Judges", "Avis Sorei", "Benefactors of Karchan"));
  }

  /**
   * Verifies the news. The news is empty.
   */
  @Test
  public void testNews()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/news").
            then().statusCode(200).
            and().contentType("application/json").
            and().body("$", hasSize(0));
  }

  /**
   * Verifies the character sheets. To be more specific, verifies that at least one character
   * called Karn appears in the list.
   */
  @Test
  public void testCharactersheets()
  {
    // prettyPrint("/charactersheets");
    Response response = given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/charactersheets").
            then().statusCode(200).
            and().contentType("application/json").
            extract().response();
    ArrayList path = response.path("$");
    boolean found = false;
    for (Object stuff : path)
    {
      HashMap thingy = (HashMap) stuff;
      if (thingy.get("name").equals("Karn"))
      {
        MatcherAssert.assertThat(thingy.get("url"), equalTo("/karchangame/resources/public/charactersheets/Karn"));
        MatcherAssert.assertThat(thingy.get("familyvalues"), equalTo(Collections.emptyList()));
        found = true;
      }
    }
    MatcherAssert.assertThat(found, equalTo(true));
  }

  /**
   * Verifies the character sheet of Karn.
   */
  @Test
  public void testCharactersheetOfKarn()
  {
    // prettyPrint("/charactersheets");
    Response response = given().log().ifValidationFails().
            contentType("application/json").
            header("Accept", "application/json").
            pathParam("player", "Karn").
            when().
            get("/charactersheets/{player}").
            then().statusCode(200).
            and().contentType("application/json").
            extract().response();
    HashMap map = response.path("$");
    MatcherAssert.assertThat(map.get("guild"), equalTo("Benefactors of Karchan"));
    MatcherAssert.assertThat(map.get("dateofbirth"), equalTo("The Beginning of Everything"));
    MatcherAssert.assertThat(map.get("homepageurl"), equalTo("http://www.karchan.org"));
    MatcherAssert.assertThat(map.get("cityofbirth"), equalTo("Unknown"));
    MatcherAssert.assertThat(map.get("sex"), equalTo("male"));
    MatcherAssert.assertThat(map.get("imageurl"), equalTo("http://www.karchan.org/images/jpeg/Karchan1.jpg"));
    MatcherAssert.assertThat(map.get("storyline"), equalTo("<p>This is my storyline.</p>"));
    MatcherAssert.assertThat(map.get("name"), equalTo("Karn"));
    MatcherAssert.assertThat(map.get("description"), equalTo("very old, , skinny, dark-skinned, yellow-eyed, white-haired, with a pony tail male human"));
    ArrayList familyvalues = (ArrayList) map.get("familyvalues");
    MatcherAssert.assertThat(map.get("title"), equalTo("Ruler of Karchan, Keeper of the Key to the Room of Lost Souls"));
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    RestAssured.basePath = "/karchangame/resources/public";
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
