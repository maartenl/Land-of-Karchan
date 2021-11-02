/*
 *  Copyright (C) 2012 maartenl
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
package org.karchan.webentities;

/**
 * @author maartenl
 */
public class Fortune
{

  private final String name;
  private final Integer gold;
  private final Integer silver;
  private final Integer copper;

  public Fortune(String name, int copper)
  {
    this.name = name;
    gold = copper / 100;
    silver = (copper % 100) / 10;
    this.copper = copper % 10;
  }

  public Integer getCopper()
  {
    return copper;
  }

  public Integer getGold()
  {
    return gold;
  }

  public Integer getSilver()
  {
    return silver;
  }

  public String getName()
  {
    return name;
  }
}
