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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;

/**
 *
 * @author maartenl
 */
public class Menu
{

  private final static Logger LOGGER = Logger.getLogger(Menu.class.getName());

  /**
   * Contains a mapping between the url (for example "blogs/index.html") and the menu
   * (for example "Blogs" with template name "blogs/index").
   */
  private static final Map<String, Menu> MENUS = new HashMap<>();

  private String name;
  private final String template;
  private final String url;
  private final List<Menu> subMenu;
  private Menu parent;

  /**
   * 
   * @see #Menu(java.lang.String, java.lang.String, java.util.List) 
   * @param name the name of the menu
   * @param url the url the menu refers to (any .html extension will be removed)
   */
  Menu(String name, String url)
  {
    this(name, url, Collections.emptyList());
  }

  /**
   * Create a new menu.
   * @param name the name of the menu
   * @param url the url the menu refers to (any .html extension will be removed)
   * @param subMenu the list of submenus, if any.
   */
  Menu(String name, String url, List<Menu> subMenu)
  {
    this.name = name;
    this.template = url.replace(".html", "");
    this.url = url;
    this.subMenu = subMenu;
    subMenu.forEach(menu -> menu.setParent(this));
    MENUS.put(url, this);
  }

  public String getName()
  {
    return name;
  }

  /**
   * Primary use here is to set the name in the case the name is not known until
   * the datamodel has been evaluated.
   *
   * @param name the new name
   */
  protected void setName(String name)
  {
    this.name = name;
  }

  /**
   * Returns the url matching this menu, the url has no .html extension.
   *
   * @return for example "/blogs/index".
   */
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
   *
   * @param url the url to check.
   * @return a Menu or no menu.
   */
  Optional<Menu> findVisibleMenu(String url)
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

  protected void setParent(Menu parentMenu)
  {
    LOGGER.log(Level.FINEST, "Menu with url {0} gets parent {1}.", new Object[]
    {
      url, parentMenu
    });
    parent = parentMenu;
  }

  public Menu getParent()
  {
    return parent;
  }

  /**
   * By default does nothing, as simple pages do not require a specific
   * datamodel. Override this to implement a datamodel.
   *
   * @param entityManager the entitymanager to get things from the database.
   * @param root the map to add data to, is a tree. See freemarker on how this
   * works.
   * @param parameters the list of parameters provided by the client in the
   * request.
   */
  public void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
  {
    // nothing here on purpose.
  }

  /**
   * Looks in all menus that have been created.
   *
   * @param url the url to find, for example "/blogs/index.html".
   * @return an optional indicating a found menu or nothing.
   */
  public static Optional<Menu> findMenu(String url)
  {
    Optional<Menu> result = Optional.ofNullable(MENUS.get(url));
    if (!result.isPresent())
    {
      LOGGER.log(Level.FINEST, "Menu with url {0} not found.", url);
      LOGGER.log(Level.FINEST, "Available menus are {0}.", MENUS.keySet());
    } else
    {
      LOGGER.log(Level.FINEST, "Menu with url {0} found.", url);
    }
    return result;
  }

  @Override
  public String toString()
  {
    return "Menu{" + "name=" + name + '}';
  }

}
