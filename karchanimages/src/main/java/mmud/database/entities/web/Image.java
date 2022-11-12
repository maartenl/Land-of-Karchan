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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "images")
@NamedQueries(
        {
          @NamedQuery(name = "Image.find", query = "SELECT i FROM Image i WHERE i.owner = :owner and i.url = :url")
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
  @Column(name = "length")
  private int length;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "owner")
  private String owner;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Column(name = "content")
  private byte[] content;

  @Basic
  @Column(name = "accessDate")
  private LocalDateTime accessDate;

  @Basic
  @Column(name = "counter")
  private Long counter;

  public Image()
  {
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

  public int getLength()
  {
    return length;
  }

  public void setLength(int length)
  {
    this.length = length;
  }

  public String getOwner()
  {
    return owner;
  }

  public void setOwner(String owner)
  {
    this.owner = owner;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public byte[] getContent()
  {
    return content;
  }

  public void setContent(byte[] content)
  {
    this.content = content;
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
    return "mmud.database.entities.web.Image[ url=" + url + " owner=" + owner + " ]";
  }

  /**
   * Increased the access counter, to show that the picture has been accessed.
   */
  public void addHit()
  {
    if (counter == null)
    {
      counter = 1L;
      return;
    }
    counter++;
  }

  /**
   * Enter the last accessed time to indicate when this image was last viewed.
   */
  public void lastAccessed()
  {
    this.accessDate = LocalDateTime.now();
  }

}
