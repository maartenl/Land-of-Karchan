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

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlRootElement;
import mmud.database.entities.web.Blog;

/**
 * @see {@link Blog}
 * @author maartenl
 */
@XmlRootElement
public class AdminBlog
{

  public Long id;
  public LocalDateTime creation;
  public LocalDateTime modification;
  public String title;
  public String urlTitle;
  public String contents;
  public String name;

  public AdminBlog()
  {
    // empty constructor, for creating a AdminBlog from scratch.
  }

  public AdminBlog(Blog blog)
  {
    id = blog.getId();
    creation = blog.getCreateDate();
    modification = blog.getModifiedDate();
    title = blog.getTitle();
    urlTitle = blog.getUrlTitle();
    contents = blog.getContents();
    name = blog.getOwner().getName();
  }
}
