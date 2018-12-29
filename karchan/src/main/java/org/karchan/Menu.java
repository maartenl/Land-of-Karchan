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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author maartenl
 */
public class Menu
{

  private final String name;
  private final String template;
  private final String url;
  private final List<Menu> subMenu;
  private Menu parent;

  public Menu(String name, String url)
  {
    this(name, url, Collections.emptyList());
  }

  public Menu(String name, String url, List<Menu> subMenu)
  {
    this.name = name;
    this.template = url.replace(".html", "");
    this.url = url;
    this.subMenu = subMenu;
    subMenu.forEach(menu -> menu.setParent(this));
  }

  public String getName()
  {
    return name;
  }

  public String getUrl()
  {
    return url;
  }

  public List<Menu> getSubMenu()
  {
    return subMenu;
  }

  public String getTemplate()
  {
    return template;
  }

  public Menu findMenu(String url)
  {
    if (getUrl().equals(url))
    {
      return this;
    }
    for (Menu submenu : getSubMenu())
    {
      Menu findMenu = submenu.findMenu(url);
      if (findMenu != null)
      {
        return findMenu;
      }
    }
    return null;
  }

  private void setParent(Menu parentMenu)
  {
    parent = parentMenu;
  }

  public Menu getParent()
  {
    return parent;
  }
  
}
