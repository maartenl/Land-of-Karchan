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
import org.karchan.wiki.WikiRenderer;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "wikipages")
@NamedQueries(
{
  @NamedQuery(name = "Wikipage.findAll", query = "SELECT w FROM Wikipage w"),
  @NamedQuery(name = "Wikipage.findById", query = "SELECT w FROM Wikipage w WHERE w.id = :id"),
  @NamedQuery(name = "Wikipage.findByName", query = "SELECT w FROM Wikipage w WHERE w.name = :name"),
  @NamedQuery(name = "Wikipage.findByCreateDate", query = "SELECT w FROM Wikipage w WHERE w.createDate = :createDate"),
  @NamedQuery(name = "Wikipage.findByModifiedDate", query = "SELECT w FROM Wikipage w WHERE w.modifiedDate = :modifiedDate"),
  @NamedQuery(name = "Wikipage.findByTitle", query = "SELECT w FROM Wikipage w WHERE w.title = :title order by w.version desc"),
  @NamedQuery(name = "Wikipage.findByVersion", query = "SELECT w FROM Wikipage w WHERE w.version = :version"),
  @NamedQuery(name = "Wikipage.findByParentTitle", query = "SELECT w FROM Wikipage w WHERE w.parentTitle = :parentTitle")
})
public class Wikipage implements Serializable
{

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Long id;

  @Size(max = 100)
  @Column(name = "name")
  private String name;

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

  @Size(max = 200)
  @Column(name = "title")
  private String title;

  @Size(max = 10)
  @Column(name = "version")
  private String version;

  @Lob
  @Size(max = 65535)
  @Column(name = "content")
  private String content;

  @Lob
  @Size(max = 65535)
  @Column(name = "summary")
  private String summary;

  @Size(max = 100)
  @Column(name = "parentTitle")
  private String parentTitle;

  public Wikipage()
  {
  }

  public Wikipage(Long id)
  {
    this.id = id;
  }

  public Wikipage(Long id, Date createDate, Date modifiedDate)
  {
    this.id = id;
    this.createDate = createDate;
    this.modifiedDate = modifiedDate;
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

  public String getVersion()
  {
    return version;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public String getHtmlContent()
  {
    return new WikiRenderer().render(content);
  }
  
  public String getSummary()
  {
    return summary;
  }

  public void setSummary(String summary)
  {
    this.summary = summary;
  }

  public String getParentTitle()
  {
    return parentTitle;
  }

  public void setParentTitle(String parentTitle)
  {
    this.parentTitle = parentTitle;
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
    if (!(object instanceof Wikipage))
    {
      return false;
    }
    Wikipage other = (Wikipage) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.Wikipage[ id=" + id + " ]";
  }
  
}
