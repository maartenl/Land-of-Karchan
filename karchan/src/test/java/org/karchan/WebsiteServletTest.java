/*
 * Copyright (C) 2018 maartenl
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
package org.karchan;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class WebsiteServletTest
{

  public WebsiteServletTest()
  {
  }

  @Test
  public void rootVisibleMenuFindTest()
  {
    WebsiteServlet servlet = new WebsiteServlet();
    Optional<Menu> findMenu = servlet.rootMenu.findVisibleMenu("/introduction.html");

    assertThat(findMenu).isPresent();
    assertThat(findMenu.get().getUrl()).isEqualTo("/introduction.html");
    assertThat(findMenu.get().getName()).isEqualTo("Introduction");
  }
  
  @Test
  public void rootVisibleMenuFindMainTest()
  {
    WebsiteServlet servlet = new WebsiteServlet();
    Optional<Menu> findMenu = servlet.rootMenu.findVisibleMenu("/index.html");

    assertThat(findMenu).isPresent();
    assertThat(findMenu.get().getUrl()).isEqualTo("/index.html");
    assertThat(findMenu.get().getName()).isEqualTo("Welcome");
  }

  @Test
  public void rootVisibleMenuNotFindTest()
  {
    WebsiteServlet servlet = new WebsiteServlet();
    Optional<Menu> findMenu = servlet.rootMenu.findVisibleMenu("/instroduction.html");

    assertThat(findMenu).isNotPresent();
  }

  @Test
  public void rootMenuFindTest()
  {
    WebsiteServlet servlet = new WebsiteServlet();
    Optional<Menu> findMenu = Menu.findMenu("/notFound.html");

    assertThat(findMenu).isPresent();
    assertThat(findMenu.get().getUrl()).isEqualTo("/notFound.html");
    assertThat(findMenu.get().getName()).isEqualTo("Not found");
  }

  @Test
  public void rootMenuNotFindTest()
  {
    WebsiteServlet servlet = new WebsiteServlet();
    Optional<Menu> findMenu = Menu.findMenu("/instroduction.html");

    assertThat(findMenu).isNotPresent();
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
