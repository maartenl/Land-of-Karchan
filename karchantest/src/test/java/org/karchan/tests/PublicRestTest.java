/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
  public void verifyStatus()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/status").
            then().statusCode(200).and().contentType("application/json").and().body("name", hasItems("Aurican", "Blackfyre", "Eilan", "Ephinie", "Karn", "Midevia", "Mya", "Victoria"));
  }

  /**
   * Verifies the wholist.
   */
  @Test
  public void verifyWhoList()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/who").
            then().statusCode(200).and().contentType("application/json").and().body("$", hasSize(0));
  }

  /**
   * Verifies the fortunes.
   */
  @Test
  public void verifyFortunes()
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
  public void verifyGuilds()
  {
    given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/guilds").
            then().statusCode(200).
            and().contentType("application/json").
            and().body("title", hasItems("Assembly of Judges", "Avis Sorei", "Benefactors of Karchan"));
  }

  /**
   * Verifies the character sheets. To be more specific, verifies that at least one character
   * called Karn appears in the list.
   */
  @Test
  public void verifyCharactersheets()
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
  public void verifyCharactersheetOfKarn()
  {
    // prettyPrint("/charactersheets");
    Response response = given().log().ifValidationFails().contentType("application/json").header("Accept", "application/json").
            when().
            get("/charactersheets/Karn").
            then().statusCode(200).
            and().contentType("application/json").
            //and().body("name", hasItems("Assembly of Judges", "Avis Sorei", "Benefactors of Karchan")).
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
