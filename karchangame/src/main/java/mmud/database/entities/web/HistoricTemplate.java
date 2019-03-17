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
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Basically, all the old templates are stored as backups.
 *
 * @author maartenl
 */
@Entity
@Table(name = "templates_his")
@NamedQueries(
        {
          @NamedQuery(name = "HistoricTemplate.findAll", query = "SELECT h FROM HistoricTemplate h"),
          @NamedQuery(name = "HistoricTemplate.findById", query = "SELECT h FROM HistoricTemplate h WHERE h.id = :id"),
          @NamedQuery(name = "HistoricTemplate.findByName", query = "SELECT h FROM HistoricTemplate h WHERE h.name = :name"),
          @NamedQuery(name = "HistoricTemplate.findByCreated", query = "SELECT h FROM HistoricTemplate h WHERE h.created = :created"),
          @NamedQuery(name = "HistoricTemplate.findByModified", query = "SELECT h FROM HistoricTemplate h WHERE h.modified = :modified"),
          @NamedQuery(name = "HistoricTemplate.findByVersion", query = "SELECT h FROM HistoricTemplate h WHERE h.version = :version")
        })
public class HistoricTemplate implements Serializable
{

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Long id;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 90)
  @Column(name = "name")
  private String name;

  @Basic(optional = false)
  @NotNull
  @Column(name = "created")
  private LocalDateTime created;

  @Basic(optional = false)
  @NotNull
  @Column(name = "modified")
  private LocalDateTime modified;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "content")
  private String content;

  // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
  @Min(1)
  @Basic(optional = false)
  @NotNull
  @Column(name = "version")
  private BigDecimal version;

  public HistoricTemplate()
  {
    // ORM needs a default no-args constructor.
  }

  public HistoricTemplate(Template entity)
  {
    this.name = entity.getName();
    this.content = entity.getContent();
    this.created = entity.getCreated();
    this.modified = entity.getModified();
    this.version = entity.getVersion();
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

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  public LocalDateTime getModified()
  {
    return modified;
  }

  public void setModified(LocalDateTime modified)
  {
    this.modified = modified;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public BigDecimal getVersion()
  {
    return version;
  }

  public void setVersion(BigDecimal version)
  {
    this.version = version;
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
    if (!(object instanceof HistoricTemplate))
    {
      return false;
    }
    HistoricTemplate other = (HistoricTemplate) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.HistoricTemplate[ id=" + id + " ]";
  }

}
