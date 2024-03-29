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
package mmud.rest.webentities;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author maartenl
 */
@XmlRootElement
public class PrivateDisplay
{

  public String image;
  public String title;
  public String body;
  public PrivateLog log;

  public Boolean west;
  public Boolean east;
  public Boolean north;
  public Boolean south;
  public Boolean up;
  public Boolean down;

  public List<PrivatePerson> persons;
  public List<PrivateItem> items;
}
