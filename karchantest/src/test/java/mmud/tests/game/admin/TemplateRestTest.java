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
 * Tests the admin rest services for templates of Karchan.
 *
 * @author maartenl
 */
public class TemplateRestTest extends RestTest
{

  private static final int NOT_FOUND = 404;

  private static final int OK = 200;

  private static final int NO_CONTENT = 204;

  public TemplateRestTest()
  {
  }

  public Response getTemplates(String jsession, final String player)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    when().
                    get("/administration/templates").
                    then().statusCode(200).
                    and().extract().response();
    return gameResponse;
  }

  public Response getTemplate(String jsession, final String player, Long id)
  {
    return getTemplate(jsession, player, id, OK);
  }

  public Response getTemplate(String jsession, final String player, Long id, int statusCode)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    pathParam("id", id).
                    when().
                    get("/administration/templates/{id}").
                    then().statusCode(statusCode).
                    and().extract().response();
    return gameResponse;
  }

  public Response updateTemplate(String jsession, final String player, Long id, String body)
  {
    Response gameResponse
            = given().log().ifValidationFails().
                    cookie("JSESSIONID", jsession).
                    contentType("application/json").header("Accept", "application/json").
                    pathParam("id", id).
                    body(body).
                    when().
                    put("/administration/templates/{id}").
                    then().statusCode(NO_CONTENT).
                    and().extract().response();
    return gameResponse;
  }

  /**
   * Retrieves all templates.
   */
  @Test
  public void testGetTemplates()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = getTemplates(jsession, karn);
    print(gameResponse);
    List<String> names = gameResponse.path("name");
    List<Integer> ids = gameResponse.path("id");
    assertThat(names.size()).isGreaterThan(20);
    assertThat(ids.size()).isEqualTo(names.size());
    assertThat(names).containsAnyOf("header","footer");
    logoff(jsession, karn);
  }

  /**
   * Retrieves a specific template.
   */
  @Test
  public void testGetTemplate()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = getTemplate(jsession, karn, 2L);
    print(gameResponse);
    String content = gameResponse.path("content");
    Integer id = gameResponse.path("id");
    String name = gameResponse.path("name");
    Float version = gameResponse.path("version");
    String created = gameResponse.path("created");
    String modified = gameResponse.path("modified");
    assertThat(content).isEqualTo("    </body>\n</html>");
    assertThat(id).isEqualTo(2);
    assertThat(name).isEqualTo("footer");
//    assertThat(version).isEqualTo(Float.valueOf("1"));
    logoff(jsession, karn);
  }

  /**
   * Retrieves a specific template that doesn't exist.
   */
  @Test
  public void testGetUnknownTemplate()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    Response gameResponse
            = getTemplate(jsession, karn, -3L, NOT_FOUND);
    print(gameResponse);
    String errormessage = gameResponse.path("errormessage");
    String user = gameResponse.path("user");
    assertThat(errormessage).isEqualTo("Template -3 not found.");
    assertThat(user).isEqualTo("Karn");
    logoff(jsession, karn);
  }

  /**
   * Updates a template.
   */
  @Test
  public void testUpdateTemplate()
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    
    updateTemplate(jsession, karn, 2L, "{\n"
            + "        \"id\": " + 2L + ",\n"
            + "        \"content\": \"woah    </body>\\n</html>\",\n"
            + "        \"name\": \"footer\",\n"
            + "        \"version\": 1\n"
            + "    }");

    Response gameResponse
            = getTemplate(jsession, karn, 2L);
    print(gameResponse);

    String content = gameResponse.path("content");
    Integer id = gameResponse.path("id");
    String name = gameResponse.path("name");
    Float version = gameResponse.path("version");
    String created = gameResponse.path("created");
    String modified = gameResponse.path("modified");

    assertThat(id).isEqualTo(2);
    assertThat(name).isEqualTo("footer");
    assertThat(version).isEqualTo(Float.valueOf("1.1"));
    assertThat(content).isEqualTo("woah    </body>\n</html>");

    updateTemplate(jsession, karn, 2L, "{\n"
            + "        \"id\": " + 2L + ",\n"
            + "        \"content\": \"    </body>\\n</html>\",\n"
            + "        \"name\": \"footer\",\n"
            + "        \"version\": 1\n"
            + "    }");

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
