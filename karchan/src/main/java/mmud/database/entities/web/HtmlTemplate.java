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

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "templates")
@XmlRootElement
@NamedQueries(
{
  @NamedQuery(name = "HtmlTemplate.findAll", query = "SELECT t FROM HtmlTemplate t"),
  @NamedQuery(name = "HtmlTemplate.findById", query = "SELECT t FROM HtmlTemplate t WHERE t.id = :id"),
  @NamedQuery(name = "HtmlTemplate.findByName", query = "SELECT t FROM HtmlTemplate t WHERE t.name = :name"),
  @NamedQuery(name = "HtmlTemplate.findByCreateDate", query = "SELECT t FROM HtmlTemplate t WHERE t.created = :created"),
  @NamedQuery(name = "HtmlTemplate.findByModifiedDate", query = "SELECT t FROM HtmlTemplate t WHERE t.modified = :modified")
})
public class HtmlTemplate implements Serializable
{

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  private Long id;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 90)
  private String name;
  @Basic(optional = false)
  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;
  @Basic(optional = false)
  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  private Date modified;
  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  private String content;

  public HtmlTemplate()
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

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Date getCreated()
  {
    return created;
  }

  public void setCreated(Date created)
  {
    this.created = created;
  }

  public Date getModified()
  {
    return modified;
  }

  public void setModified(Date modified)
  {
    this.modified = modified;
  }

  public String getContents()
  {
    return content;
  }

  public void setContents(String contents)
  {
    this.content = contents;
  }

  public long getLastModified()
  {
    if (modified == null)
    {
      return created.getTime();
    }
    return modified.getTime();
  }
  
  public Reader getReader()
  {
    return new StringReader(content);
  }
  
  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (name != null ? name.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof HtmlTemplate))
    {
      return false;
    }
    HtmlTemplate other = (HtmlTemplate) object;
    if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.HtmlTemplate[ id=" + id + " ]";
  }
  
}
