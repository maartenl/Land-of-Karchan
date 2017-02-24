/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.karchan.tests;

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

  @Test
  public void verifyStatus()
  {
    given().contentType("application/json").header("Accept", "application/json").
            when().
            get("http://localhost:8080/karchangame/resources/public/status").
            then().body("name", hasItems("Aurican", "Blackfyre", "Eilan", "Ephinie", "Karn", "Midevia", "Mya", "Victoria"));

    String result
            = given().contentType("application/json").header("Accept", "application/json").
                    when().
                    get("http://localhost:8080/karchangame/resources/public/status").
                    prettyPrint();

    MatcherAssert.assertThat(result, equalTo("Hello"));
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
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
