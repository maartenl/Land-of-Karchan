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
package org.karchan;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.karchan.menus.MenuFactory;
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
  
  @BeforeClass
  public static void setUpClass()
  {
  }
  
  @AfterClass
  public static void tearDownClass()
  {
  }
  
  @BeforeMethod
  public void setUp()
  {
  }
  
  @AfterMethod
  public void tearDown()
  {
  }

  /**
   * Test of doGet method, of class WebsiteServlet.
   * @throws javax.servlet.ServletException because we're testing a servlet.
   * @throws java.io.IOException because we're testing a servlet.
   */
  @Test
  public void testDoGet() throws ServletException, IOException
  {
    HttpServletRequest request = null;
    HttpServletResponse response = null;    
    WebsiteServlet servlet = new WebsiteServlet();
    servlet.setFreemarker(new Freemarker());
    servlet.setMenuFactory(new MenuFactory());
//    servlet.doGet(requ!est, response);
  }
  
}
