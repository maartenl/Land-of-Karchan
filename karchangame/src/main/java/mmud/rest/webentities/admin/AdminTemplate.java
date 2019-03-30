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
package mmud.rest.webentities.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlRootElement;
import mmud.database.entities.web.Template;

/**
 * @see {@link Template}
 * @author maartenl
 */
@XmlRootElement
public class AdminTemplate
{

  public Long id;
  public String name;
  public LocalDateTime created;
  public LocalDateTime modified;
  public String content;
  public BigDecimal version;
  public String editor;
  public String comment;

  public AdminTemplate()
  {
    // empty constructor, for creating a AdminTemplate from scratch.
  }

  public AdminTemplate(Template template)
  {
    id = template.getId();
    name = template.getName();
    created = template.getCreated();
    modified = template.getModified();
    content = template.getContent();
    version = template.getVersion();
    editor = template.getEditor();
    comment = template.getComment();
  }
}
