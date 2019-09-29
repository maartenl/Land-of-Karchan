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
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import mmud.database.RegularExpressions;
import mmud.database.entities.characters.User;

/**
 * Players may upload images to the server, and consequently use them.
 * @author maartenl
 */
@Entity
@Table(name = "images")
@NamedQueries(
{
  @NamedQuery(name = "Image.findAll", query = "SELECT i FROM Image i"),
  @NamedQuery(name = "Image.findByOwner", query = "SELECT i FROM Image i WHERE i.owner = :player order by i.url"),
  @NamedQuery(name = "Image.findByOwnerAndUrl", query = "SELECT i FROM Image i WHERE i.owner = :player and i.url = :url"),
})
public class Image implements Serializable
{

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Long id;
  
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 200)
  @Column(name = "url")
  @Pattern(regexp = RegularExpressions.URL_REGEXP, message = RegularExpressions.URL_MESSAGE)
  private String url;

  @Basic(optional = false)
  @NotNull
  @Column(name = "createDate")
  private LocalDateTime createDate;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "mimeType")
  private String mimeType;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Column(name = "content")
  private byte[] content;

  @Basic(optional = false)
  @NotNull
  @Column(name = "length")
  private int length;

  @JoinColumn(name = "owner", nullable = false, referencedColumnName = "name")
  @ManyToOne(optional = false)
  private User owner;
  
  public Image()
  {
  }

  public Image(Long id)
  {
    this.id = id;
  }

  public Image(Long id, String url, LocalDateTime createDate, String mimeType, byte[] content, int length)
  {
    this.id = id;
    this.url = url;
    this.createDate = createDate;
    this.mimeType = mimeType;
    this.content = content;
    this.length = length;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public LocalDateTime getCreateDate()
  {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate)
  {
    this.createDate = createDate;
  }

  public String getMimeType()
  {
    return mimeType;
  }

  public void setMimeType(String mimeType)
  {
    this.mimeType = mimeType;
  }

  public byte[] getContent()
  {
    return content;
  }

  public void setContent(byte[] content)
  {
    this.content = content;
    this.length = content.length;
  }

  public User getOwner()
  {
    return owner;
  }

  public void setOwner(User owner)
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
    if (!(object instanceof Image))
    {
      return false;
    }
    Image other = (Image) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.Image[ id=" + id + " ]";
  }
  
}
