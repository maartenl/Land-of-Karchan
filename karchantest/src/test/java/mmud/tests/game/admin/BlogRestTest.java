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
package mmud.tests.game.admin;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import java.util.List;
import mmud.tests.RestTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the admin rest services of Karchan.
 *
 * @author maartenl
 */
public class BlogRestTest extends RestTest
{

  private static final int NOT_FOUND = 404;

  private static final int OK = 200;

  private static final int NO_CONTENT = 204;

  public BlogRestTest()
  {
  }

  public Response getBlogs(String jsession, final String player)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    when().
                    get("/administration/blogs").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }

  public Response getBlog(String jsession, final String player, Long id)
  {
    return getBlog(jsession, player, id, OK);
  }

  public Response getBlog(String jsession, final String player, Long id, int statusCode)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    pathParam("id", id).
                    when().
                    get("/administration/blogs/{id}").
                    then().statusCode(statusCode).
                    and().extract().response();
    return gameResponse;
  }

  public Response addBlog(String jsession, final String player, String body)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    body(body).
                    when().
                    post("/administration/blogs").
                    then().statusCode(OK).
                    and().extract().response();
    return gameResponse;
  }
  
  public Response updateBlog(String jsession, final String player, Long id, String body)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    pathParam("id", id).
                    body(body).
                    when().
                    put("/administration/blogs/{id}").
                    then().statusCode(NO_CONTENT).
                    and().extract().response();
    return gameResponse;
  }

  public Response deleteBlog(String jsession, final String player, Long id)
  {
    return deleteBlog(jsession, player, id, NO_CONTENT);
  }  

  public Response deleteBlog(String jsession, final String player, Long id, int statusCode)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    pathParam("id", id).
                    when().
                    delete("/administration/blogs/{id}").
                    then().statusCode(statusCode).
                    and().extract().response();
    return gameResponse;
  }

  /**
   * Retrieves all blogs of Karn.
   */
  @Test
  public void testGetBlogs()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = getBlogs(jsession, karn);
    print(gameResponse);
    List<String> titles = gameResponse.path("title");
    List<String> urlTitles = gameResponse.path("urlTitle");
    assertThat(titles.size()).isGreaterThan(40);
    assertThat(urlTitles.size()).isEqualTo(titles.size());
    assertThat(titles).containsAnyOf("Upgrade postponed indefinitely.");
    assertThat(urlTitles).containsAnyOf("upgrade-postponed-indefinitely-");
    logoff(jsession, karn);
  }

  /**
   * Retrieves a specific blog of Karn.
   */
  @Test
  public void testGetBlog()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = getBlog(jsession, karn, 53236L);
    print(gameResponse);
    String title = gameResponse.path("title");
    Integer id = gameResponse.path("id");
    String name = gameResponse.path("name");
    String urlTitle = gameResponse.path("urlTitle");
    String creation = gameResponse.path("creation");
    String modification = gameResponse.path("modification");
    assertThat(title).isEqualTo("Upgrade postponed indefinitely.");
    assertThat(id).isEqualTo(53236);
    assertThat(name).isEqualTo("Karn");
    assertThat(urlTitle).isEqualTo("upgrade-postponed-indefinitely-");
    assertThat(creation).isEqualTo("2018-12-22T23:50:41");
    assertThat(modification).isEqualTo("2018-12-22T23:50:41");
    logoff(jsession, karn);
  }

  /**
   * Retrieves a specific blog of Karn that doesn't exist.
   */
  @Test
  public void testGetUnknownBlog()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = getBlog(jsession, karn, -3L, NOT_FOUND);
    print(gameResponse);
    String errormessage = gameResponse.path("errormessage");
    String user = gameResponse.path("user");
    assertThat(errormessage).isEqualTo("Blog -3 not found.");
    assertThat(user).isEqualTo("Karn");
    logoff(jsession, karn);
  }

  /**
   * Adds, updates and consequently removes a blog.
   */
  @Test
  public void testAddUpdateAndDeleteBlog()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = addBlog(jsession, karn, "{\n"
                    + "        \"contents\": \"<p>My attempt.</p><p>Karn</p>\",\n"
                    + "        \"name\": \"Karn\",\n"
                    + "        \"title\": \"Attempt\",\n"
                    + "        \"urlTitle\": \"attempt\"\n"
                    + "    }");
    print(gameResponse);
    String title = gameResponse.path("title");
    Integer tempId = gameResponse.path("id");
    Long id = Long.valueOf(tempId);
    String name = gameResponse.path("name");
    String urlTitle = gameResponse.path("urlTitle");
    assertThat(title).isEqualTo("Attempt");
    assertThat(name).isEqualTo("Karn");
    assertThat(urlTitle).isEqualTo("attempt");
    
    updateBlog(jsession, name, id, "{\n"
            + "        \"id\": " + id + ",\n"
            + "        \"contents\": \"<p>My second attempt.</p><p>Karn</p>\",\n"
            + "        \"name\": \"Karn\",\n"
            + "        \"title\": \"Second attempt\",\n"
            + "        \"urlTitle\": \"second-attempt\"\n"
            + "    }");

    gameResponse
            = getBlog(jsession, karn, id);
    print(gameResponse);
    title = gameResponse.path("title");
    name = gameResponse.path("name");
    urlTitle = gameResponse.path("urlTitle");
    assertThat(title).isEqualTo("Second attempt");
    assertThat(name).isEqualTo("Karn");
    assertThat(urlTitle).isEqualTo("second-attempt");

    deleteBlog(jsession, name, id);
    
    getBlog(jsession, karn, id, NOT_FOUND);
    
    logoff(jsession, karn);
  }

  /**
   * Removes a blog that does not exist.
   */
  @Test
  public void testDeleteUnknownBlog()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse = deleteBlog(jsession, karn, -3L, NOT_FOUND);
    print(gameResponse);
    String errormessage = gameResponse.path("errormessage");
    String user = gameResponse.path("user");
    assertThat(errormessage).isEqualTo("Blog -3 not found.");
    assertThat(user).isEqualTo("Karn");

    logoff(jsession, karn);
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
    init();
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
