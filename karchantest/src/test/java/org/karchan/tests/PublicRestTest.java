/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.karchan.tests;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.*;
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
            = given().contentType("application/json").header("Accept", "application/json").
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
    given().contentType("application/json").header("Accept", "application/json").
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
    given().contentType("application/json").header("Accept", "application/json").
            when().
            get("/who").
            then().statusCode(200).and().contentType("application/json").and().body("$", hasSize(0));
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
