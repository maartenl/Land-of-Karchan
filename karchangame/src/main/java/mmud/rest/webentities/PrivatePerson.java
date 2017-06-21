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

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maartenl
 */
@XmlRootElement
public class PrivatePerson
{

  public String name;
  public String imageurl;
  public String homepageurl;
  public String dateofbirth;
  public String cityofbirth;
  public String storyline;
  public String password;
  public String password2;
  public String title;
  public String realname;
  public String email;
  public String age;
  public String width;
  public String height;
  public String complexion;
  public String eyes;
  public String face;
  public String hair;
  public String beard;
  public String arm;
  public String leg;
  public String race;
  public String sex;
  public String guild;
  public PrivateRank guildrank;
}
