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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;

/**
 *
 * @author maartenl
 */
public class Menu
{
  private static final Map<String, Menu> menus = new HashMap<>();
  
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
    menus.put(url, this);
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

  /**
   * Returns the visible menu, based on the URL. Can return Null, in case the 
   * menu option is not visible in the nagigation bar.
   * @param url the url to check.
   * @return a Menu or no menu.
   */
  public Optional<Menu> findVisibleMenu(String url)
  {
    if (getUrl().equals(url))
    {
      return Optional.of(this);
    }
    for (Menu submenu : getSubMenu())
    {
      Optional<Menu> findMenu = submenu.findVisibleMenu(url);
      if (findMenu.isPresent())
      {
        return findMenu;
      }
    }
    return Optional.empty();
  }

  private void setParent(Menu parentMenu)
  {
    parent = parentMenu;
  }

  public Menu getParent()
  {
    return parent;
  }

  /**
   * By default does nothing, as simple pages do not require a specific
   * datamodel. Override this to implement a datamodel.
   * @param entityManager the entitymanager to get things from the database.
   * @param root the map to add data to, is a tree. See freemarker on how this 
   * works.
   */
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root)
  {
    // nothing here on purpose.
  }
  
  public static Optional<Menu> findMenu(String url)
  {
    return Optional.ofNullable(menus.get(url));
  }

}
