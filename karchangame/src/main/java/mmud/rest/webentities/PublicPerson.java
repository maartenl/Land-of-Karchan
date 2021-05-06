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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

import mmud.database.entities.characters.User;

/**
 * @author maartenl
 */
@XmlRootElement
public class PublicPerson
{

  public String name;
  public String url;
  public String title;
  public String sleep;
  public String area;
  public String sex;
  public String description;
  public String imageurl;
  public String guild;
  public String homepageurl;
  public String dateofbirth;
  public String cityofbirth;
  public LocalDateTime lastlogin;
  public String storyline;
  public Long min;
  public Long sec;
  public String idleTime;

  public List<PublicFamily> familyvalues = new ArrayList<>();

  public PublicPerson()
  {
  }

  /**
   * Used by the wholist.
   *
   * @param person   a player
   * @param idleTime how long this player has been idle
   */
  public PublicPerson(User person, String idleTime)
  {
    String publicName = person.getName();
    if (person.getFrogging() > 0)
    {
      publicName = "a frog called " + publicName;
    }
    if (person.getJackassing() > 0)
    {
      publicName = "a jackass called " + publicName;
    }
    this.name = publicName;
    this.title = person.getTitle();
    this.sleep = (person.getSleep() != null && person.getSleep()) ? "sleeping" : "";
    this.area = person.getRoom().getArea().getShortdescription();
    if (person.getLastlogin() != null)
    {
      Duration between = Duration.between(person.getLastlogin(), LocalDateTime.now());
      this.min = between.getSeconds() / 60;
      this.sec = between.getSeconds() % 60;
    }
    this.idleTime = idleTime;
  }
}
