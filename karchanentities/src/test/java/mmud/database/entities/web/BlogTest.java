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
package mmud.database.entities.web;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author maartenl
 */
public class BlogTest
{

  public BlogTest()
  {
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

  /**
   * Test of getContent method, of class Blog. Default test.
   */
  @Test
  public void testGetContent()
  {
    Blog blog = new Blog();
    blog.setContents("<p>Sorry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>");
    String expected = "<p>Sorry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>";
    String actual = blog.getContent();
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Test of getContent method, of class Blog. Test with image reference to be removed.
   */
  @Test
  public void testGetContentWithImage()
  {
    Blog blog = new Blog();
    blog.setContents("<p><img alt=\"S\" src=\"/images/gif/letters/s.gif\" style=\"float: left;\">orry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>");
    String expected = "<p>Sorry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>";
    String actual = blog.getContent();
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Test of getHtmlContent method, of class Blog. Test with image reference to be
   * added.
   */
  @Test
  public void testGetHtmlContentWithoutImage()
  {
    Blog blog = new Blog();
    blog.setContents("<p>Sorry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>");
    String expected = "<p><img alt=\"S\" src=\"/images/gif/letters/s.gif\" style=\"float: left;\">orry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>";
    String actual = blog.getHtmlContent();
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Test of getHtmlContent method, of class Blog. Test with image reference to
   * be removed and subsequently added.
   */
  @Test
  public void testGetHtmlContentWithImage()
  {
    Blog blog = new Blog();
    blog.setContents("<p><img alt=\"S\" src=\"/images/gif/letters/s.gif\" style=\"float: left;\">orry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>");
    String expected = "<p><img alt=\"S\" src=\"/images/gif/letters/s.gif\" style=\"float: left;\">orry guys. The upgrade has been postponed until further notice, perhaps indefinitely, while I look into other options.</p>";
    String actual = blog.getHtmlContent();
    assertThat(actual).isEqualTo(expected);
  }
}
