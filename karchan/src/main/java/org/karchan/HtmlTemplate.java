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
package org.karchan;

import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author maartenl
 */
public class HtmlTemplate
{
  private final String name;
  private Date lastModified;
  private final Date created;
  private String contents;

  HtmlTemplate(String name)
  {
    this.name = name;
    created = new Date();
  }

  long getLastModified()
  {
    if (lastModified == null)
    {
      return created.getTime();
    }
    return lastModified.getTime();
  }

  Reader getReader()
  {
    return new StringReader(contents);
  }

  public void setContents(String contents)
  {
    this.contents = contents;
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 11 * hash + Objects.hashCode(this.name);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final HtmlTemplate other = (HtmlTemplate) obj;
    if (!Objects.equals(this.name, other.name))
    {
      return false;
    }
    return true;
  }

  
  
}
