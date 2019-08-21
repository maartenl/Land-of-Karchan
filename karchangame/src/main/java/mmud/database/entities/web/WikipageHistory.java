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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "wikipages_his")
@NamedQueries(
        {
          @NamedQuery(name = "WikipageHistory.findAll", query = "SELECT w FROM WikipageHistory w"),
          @NamedQuery(name = "WikipageHistory.findById", query = "SELECT w FROM WikipageHistory w WHERE w.id = :id"),
          @NamedQuery(name = "WikipageHistory.findByTitle", query = "SELECT w FROM WikipageHistory w WHERE w.title = :title"),
          @NamedQuery(name = "WikipageHistory.findByName", query = "SELECT w FROM WikipageHistory w WHERE w.name = :name"),
          @NamedQuery(name = "WikipageHistory.findByCreateDate", query = "SELECT w FROM WikipageHistory w WHERE w.createDate = :createDate"),
          @NamedQuery(name = "WikipageHistory.findByModifiedDate", query = "SELECT w FROM WikipageHistory w WHERE w.modifiedDate = :modifiedDate"),
          @NamedQuery(name = "WikipageHistory.findByVersion", query = "SELECT w FROM WikipageHistory w WHERE w.version = :version"),
          @NamedQuery(name = "WikipageHistory.findByParentTitle", query = "SELECT w FROM WikipageHistory w WHERE w.parentTitle = :parentTitle")
        })
public class WikipageHistory implements Serializable
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
  @Column(name = "title")
  private String title;

  @Basic(optional = false)
  @NotNull()
  @Size(min = 1, max = 100)
  @Column(name = "name")
  private String name;

  @Basic(optional = false)
  @NotNull
  @Column(name = "createDate")
  private LocalDateTime createDate;

  @Basic(optional = false)
  @NotNull
  @Column(name = "modifiedDate")
  private LocalDateTime modifiedDate;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 10)
  @Column(name = "version")
  private String version;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "content")
  private String content;

  @Lob
  @Size(max = 65535)
  @Column(name = "summary")
  private String summary;

  @Size(max = 100)
  @Column(name = "parentTitle")
  private String parentTitle;

  @Basic(optional = false)
  @NotNull()
  @Column(name = "administration")
  private boolean administration;

  @Size(max = 200)
  @Column(name = "comment")
  private String comment;

  public WikipageHistory()
  {
  }

  public WikipageHistory(Wikipage wikipage)
  {
    this.title = wikipage.getTitle();
    this.name = wikipage.getName();
    this.createDate = wikipage.getCreateDate();
    this.modifiedDate = wikipage.getModifiedDate();
    this.version = wikipage.getVersion();
    this.content = wikipage.getContent();
    this.administration = wikipage.getAdministration();
    this.comment = wikipage.getComment();
    this.parentTitle = wikipage.getParentTitle() == null ? null : wikipage.getParentTitle().getTitle();
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public LocalDateTime getCreateDate()
  {
    return createDate;
  }

  public void setCreateDate(LocalDateTime createDate)
  {
    this.createDate = createDate;
  }

  public LocalDateTime getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(LocalDateTime modifiedDate)
  {
    this.modifiedDate = modifiedDate;
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

  public boolean getAdministration()
  {
    return administration;
  }

  public void setAdministration(boolean administration)
  {
    this.administration = administration;
  }

  public String getComment()
  {
    return comment;
  }

  public void setComment(String comment)
  {
    this.comment = comment;
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
    if (!(object instanceof WikipageHistory))
    {
      return false;
  }
    WikipageHistory other = (WikipageHistory) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
  {
      return false;
  }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.WikipageHistory[ id=" + id + " ]";
  }

}
