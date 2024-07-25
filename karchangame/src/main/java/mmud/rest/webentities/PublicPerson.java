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

import mmud.JsonUtils;
import mmud.database.entities.characters.User;
import mmud.rest.webentities.admin.AdminManpage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author maartenl
 */
public class PublicPerson
{

  public String name;
  public String url;
  public String title;
  public String familyname;
  public String sleep;
  public String afk;
  public String area;
  public String sex;
  /**
   * Description of the player, readonly.
   */
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
    this.familyname = person.getFamilyname();
    this.sleep = person.getSleep() ? "sleeping" : "";
    this.afk = person.getAfk();
    this.area = person.getRoom().getArea().getShortdescription();
    if (person.getLastlogin() != null)
    {
      Duration between = Duration.between(person.getLastlogin(), LocalDateTime.now());
      this.min = between.getSeconds() / 60;
      this.sec = between.getSeconds() % 60;
    }
    this.idleTime = idleTime;
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static PublicPerson fromJson(String json)
  {
    return JsonUtils.fromJson(json, PublicPerson.class);
  }

}
