/*
 * Copyright (C) 2019 maartenl
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
package mmud.tests.rest;

import com.google.common.io.ByteStreams;
import mmud.tests.RestTest;
import io.restassured.response.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests the image rest service of Karchan.
 *
 * @author maartenl
 */
public class ImageRestTest extends RestTest
{

  public ImageRestTest()
  {
  }

  /**
   * Retrieves a list of images.
   */
  @Test
  public void testGetimages()
  {
    final String victoria = "Victoria";
    final String password = "secret";

    String jsession = login(victoria, password);
    Response response
            = Helper.getImages(jsession, victoria);
    print(response);
    assertThat(((List<String>) response.path("url")).get(0)).isEqualTo("/Blackmyre/phantom.jpg");
    assertThat(((List<String>) response.path("mimeType")).get(0)).contains("image/jpeg");
    assertThat(((List<String>) response.path("owner")).get(0)).isEqualTo("Victoria");
    assertThat(((List<Integer>) response.path("id")).get(0)).isEqualTo(1);
    assertThat(((List<String>) response.path("createDate")).get(0)).isEqualTo("2019-01-23T00:27:39");
    logoff(jsession, victoria);
  }

  /**
   * Creates an image for Karn.
   */
  @Test
  public void testCreateImage() throws IOException
  {
    final String karn = "Karn";
    final String password = "secret";

    String jsession = login(karn, password);
    String encodedImage = "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"; //Base64.getEncoder().encodeToString(ByteStreams.toByteArray(resource));
    String createJson = "{\"createDate\":\"2019-01-23T00:27:39\","
            + "\"id\":1,"
            + "\"mimeType\":\"image/gif\","
            + "\"owner\":\"Karn\","
            + "\"url\":\"/dragon.gif\","
            + "\"content\":\""
            + encodedImage
            + "\"}";
    Helper.createImage(createJson, jsession, karn);

    Response response
            = Helper.getImages(jsession, karn);
    assertThat(((List<String>) response.path("url")).get(0)).isEqualTo("/dragon.gif");
    assertThat(((List<String>) response.path("mimeType")).get(0)).contains("image/gif");
    assertThat(((List<String>) response.path("owner")).get(0)).isEqualTo("Karn");
    assertThat(((List<Integer>) response.path("id")).get(0)).isNotNull();
    assertThat(((List<String>) response.path("createDate")).get(0)).isNotNull();

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
