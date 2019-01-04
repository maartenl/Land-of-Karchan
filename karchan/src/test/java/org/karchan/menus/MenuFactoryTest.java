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
package org.karchan.menus;

import org.karchan.menus.Menu;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
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
public class MenuFactoryTest
{

  public MenuFactoryTest()
  {
  }

  @Test
  public void rootVisibleMenuFindTest()
  {
    Optional<Menu> findMenu = MenuFactory.getRootMenu().findVisibleMenu("/introduction.html");

    assertThat(findMenu).isPresent();
    assertThat(findMenu.get().getUrl()).isEqualTo("/introduction.html");
    assertThat(findMenu.get().getName()).isEqualTo("Introduction");
  }
  
  @Test
  public void rootVisibleMenuFindMainTest()
  {
    Optional<Menu> findMenu = MenuFactory.getRootMenu().findVisibleMenu("/index.html");

    assertThat(findMenu).isPresent();
    assertThat(findMenu.get().getUrl()).isEqualTo("/index.html");
    assertThat(findMenu.get().getName()).isEqualTo("Welcome");
  }

  @Test
  public void rootVisibleMenuNotFindTest()
  {
    Optional<Menu> findMenu = MenuFactory.getRootMenu().findVisibleMenu("/instroduction.html");

    assertThat(findMenu).isNotPresent();
  }

  @Test
  public void rootMenuFindTest()
  {
    Menu rootMenu = MenuFactory.getRootMenu();
    Optional<Menu> findMenu = Menu.findMenu("/notFound.html");

    assertThat(findMenu).isPresent();
    assertThat(findMenu.get().getUrl()).isEqualTo("/notFound.html");
    assertThat(findMenu.get().getName()).isEqualTo("Not found");
  }

  @Test
  public void rootMenuNotFindTest()
  {
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
