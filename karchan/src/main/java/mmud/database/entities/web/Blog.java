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
package mmud.database.entities.web;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A blog from one of the administrators of the game. Will be shown on the main
 * page.
 *
 * @author maartenl
 */
@Entity
@Table(name = "blogs")
@NamedQueries(
        {
          @NamedQuery(name = "Blog.findAll", query = "SELECT b FROM Blog b order by b.createDate desc"),
          @NamedQuery(name = "Blog.count", query = "SELECT count(b) FROM Blog b"),
          @NamedQuery(name = "Blog.findById", query = "SELECT b FROM Blog b WHERE b.id = :id"),
          @NamedQuery(name = "Blog.findByCreateDate", query = "SELECT b FROM Blog b WHERE b.createDate = :createDate"),
          @NamedQuery(name = "Blog.findByModifiedDate", query = "SELECT b FROM Blog b WHERE b.modifiedDate = :modifiedDate"),
          @NamedQuery(name = "Blog.findByUrlTitle", query = "SELECT b FROM Blog b WHERE b.urlTitle = :title")
        })
public class Blog implements Serializable
{

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Long id;

  @Basic(optional = false)
  @NotNull
  @Column(name = "createDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createDate;

  @Basic(optional = false)
  @NotNull
  @Column(name = "modifiedDate")
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "name")
  private String name;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 150)
  @Column(name = "title")
  private String title;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 150)
  @Column(name = "urltitle")
  private String urlTitle;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "content")
  private String content;

  public Blog()
  {
    // empty but required because of JPA.
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Date getCreateDate()
  {
    return createDate;
  }

  public void setCreateDate(Date createDate)
  {
    this.createDate = createDate;
  }

  public Date getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Date modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getContent()
  {
    content = content.trim();
    if (content.contains("gif/letters"))
    {
      int letterPos = content.indexOf("gif/letters/");
      if (letterPos != -1)
      {
        String letter = content.substring(letterPos + 12, letterPos + 13);
        int beginpos = letterPos;
        int endpos = letterPos;
        while (beginpos != -1)
        {
          if (content.substring(beginpos, beginpos + 1).equals("<"))
          {
            break;
          }
          beginpos--;
        }
        while (endpos < content.length())
        {
          if (content.substring(endpos, endpos + 1).equals(">"))
          {
            break;
          }
          endpos++;
        }
        if (beginpos != -1 && endpos != content.length())
        {
          content = content.replace(content.substring(beginpos, endpos + 1), letter.toUpperCase());
        }
      }
    }
    return content;
  }

  public String getHtmlContent()
  {
    String htmlContent = getContent();
    boolean intag = false;
    int pos = 0;
    while (pos < htmlContent.length())
    {
      if (htmlContent.charAt(pos) == '<')
      {
        intag = true;
      }
      if (htmlContent.charAt(pos) == '>')
      {
        intag = false;
      }
      if (!intag && htmlContent.charAt(pos) >= 'A' && htmlContent.charAt(pos) <= 'Z')
      {
        char charAt = htmlContent.charAt(pos);
        return htmlContent.substring(0, pos) + "<img alt=\"" + charAt + "\" src=\"/images/gif/letters/" + (charAt + "").toLowerCase() + ".gif\" style=\"float: left;\">" + htmlContent.substring(pos + 1);
      }
      pos++;
    }
    return htmlContent;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Blog))
    {
      return false;
    }
    Blog other = (Blog) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.Blog[ id=" + id + " ]";
  }

  /**
   * @return the urlTitle
   */
  public String getUrlTitle()
  {
    return urlTitle;
  }

  /**
   * @param urlTitle the urlTitle to set
   */
  public void setUrlTitle(String urlTitle)
  {
    this.urlTitle = urlTitle;
  }

}
