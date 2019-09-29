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
package mmud.database.entities.web;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import mmud.database.entities.Ownage;
import mmud.database.entities.game.Admin;

/**
 * Contains the blogs that are visible on the central page.
 * @author maartenl
 */
@Entity
@Table(name = "blogs")
@NamedQueries(
{
  @NamedQuery(name = "Blog.find", query = "SELECT b FROM Blog b WHERE b.owner = :user ORDER BY b.id DESC")
})
public class Blog implements Serializable, Ownage
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
  private LocalDateTime creation;

  @Basic(optional = false)
  @NotNull
  @Column(name = "modifiedDate")
  private LocalDateTime modification;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 250)
  @Column(name = "title")
  private String title;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 250)
  @Column(name = "urlTitle")
  private String urlTitle;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "content")
  private String contents;

  @JoinColumn(name = "name", referencedColumnName = "name")
  @ManyToOne(optional = false)
  private Admin owner;

  public Blog()
  {
  }

  public Blog(Long id)
  {
    this.id = id;
  }

  public Blog(Long id, LocalDateTime createDate, LocalDateTime modifiedDate, String title, String urlTitle, String content)
  {
    this.id = id;
    this.creation = createDate;
    this.modification = modifiedDate;
    this.title = title;
    this.urlTitle = urlTitle;
    this.contents = content;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public LocalDateTime getCreation()
  {
    return creation;
  }

  public void setCreation(LocalDateTime creation)
  {
    this.creation = creation;
  }

  public LocalDateTime getModification()
  {
    return modification;
  }

  public void setModification(LocalDateTime modification)
  {
    this.modification = modification;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getUrlTitle()
  {
    return urlTitle;
  }

  public void setUrlTitle(String urlTitle)
  {
    this.urlTitle = urlTitle;
  }

  public String getContents()
  {
    return contents;
  }

  public void setContents(String contents)
  {
    this.contents = contents;
  }

  public Admin getOwner()
  {
    return owner;
  }

  public void setOwner(Admin owner)
  {
    this.owner = owner;
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
  
}
