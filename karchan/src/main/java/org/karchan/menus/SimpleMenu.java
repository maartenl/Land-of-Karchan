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
package org.karchan.menus;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;

/**
 * A simple menu that does not require a datamodel of any kind.
 * @author maartenl
 */
public final class SimpleMenu extends Menu
{
  
  /**
   * @see Menu#Menu(java.lang.String, java.lang.String) 
   */
  public SimpleMenu(String name, String url)
  {
    super(name, url);
  }

  public SimpleMenu(String name, String url, List<Menu> subMenu)
  {
    super(name, url, subMenu);
  }

  @Override
  final void setDatamodel(EntityManager entityManager, Map<String, Object> root, Map<String, String[]> parameters)
  {
    super.setDatamodel(entityManager, root, parameters); //To change body of generated methods, choose Tools | Templates.
  }
  
  
  
}
