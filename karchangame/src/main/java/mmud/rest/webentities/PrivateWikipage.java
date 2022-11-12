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
package mmud.rest.webentities;

import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlRootElement;
import mmud.JsonUtils;
import mmud.database.entities.web.Wikipage;

/**
 * @author maartenl
 */
@XmlRootElement
public class PrivateWikipage
{

  public String title;
  public String name;
  public String summary;
  public String content;
  public LocalDateTime createDate;
  public LocalDateTime modifiedDate;
  public String parentTitle;
  public String version;
  public boolean administration;
  public String comment;
  public Integer ordering;

  public PrivateWikipage()
  {
  }

  public PrivateWikipage(Wikipage wikipage)
  {
    this.title = wikipage.getTitle();
    this.name = wikipage.getName();
    this.summary = wikipage.getSummary();
    this.content = wikipage.getContent();
    this.createDate = wikipage.getCreateDate();
    this.modifiedDate = wikipage.getModifiedDate();
    this.parentTitle = wikipage.getParent() == null ? null : wikipage.getParent().getTitle();
    this.version = wikipage.getVersion();
    this.administration = wikipage.getAdministration();
    this.comment = wikipage.getComment();
    this.ordering = wikipage.getOrdering();
  }

  public String toJson()
  {
    return JsonUtils.toJson(this);
  }

  public static PrivateWikipage fromJson(String json)
  {
    return JsonUtils.fromJson(json, PrivateWikipage.class);
  }

}
