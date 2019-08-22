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
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "wikipages")
@NamedQueries(
{
  @NamedQuery(name = "Wikipage.findByTitle", query = "SELECT w FROM Wikipage w WHERE w.title = :title and w.administration = false"),
  @NamedQuery(name = "Wikipage.findByTitleAuthorized", query = "SELECT w FROM Wikipage w WHERE w.title = :title"),
  @NamedQuery(name = "Wikipage.checkExistenceOfWikipage", query = "SELECT w FROM Wikipage w WHERE w.title = :title")
})
public class Wikipage implements Serializable
{

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 200)
  @Column(name = "title")
  private String title;

  @Basic(optional = false)
  @NotNull
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

  @Basic(optional = false)
  @NotNull
  @Column(name = "administration")
  private boolean administration;

  @Size(max = 200)
  @Column(name = "comment")
  private String comment;
  
  @Column(name = "ordering")
  private Integer ordering;
  
  @OneToMany(mappedBy = "parentTitle")
  private List<Wikipage> wikipageList;

  @JoinColumn(name = "parentTitle", referencedColumnName = "title")
  @ManyToOne
  private Wikipage parentTitle;

  public Wikipage()
  {
  }

  public Wikipage(String title)
  {
    this.title = title;
  }

  public Wikipage(String title, String name, LocalDateTime createDate, LocalDateTime modifiedDate, String version, String content)
  {
    this.title = title;
    this.name = name;
    this.createDate = createDate;
    this.modifiedDate = modifiedDate;
    this.version = version;
    this.content = content;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * The owner of the wikipage, or at least the last one modifying it.
   * @return the name of a player.
   */
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

  public List<Wikipage> getWikipageList()
  {
    return wikipageList;
  }

  public void setWikipageList(List<Wikipage> wikipageList)
  {
    this.wikipageList = wikipageList;
  }

  public Wikipage getParentTitle()
  {
    return parentTitle;
  }

  public void setParentTitle(Wikipage parentTitle)
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

  public Integer getOrdering()
  {
    return ordering;
  }

  public void setOrdering(Integer ordering)
  {
    this.ordering = ordering;
  }
  
  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (title != null ? title.hashCode() : 0);
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
    if ((this.title == null && other.title != null) || (this.title != null && !this.title.equals(other.title)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.Wikipage[ title=" + title + " ]";
  }

  public void increaseVersion()
  {
    version = new BigDecimal(version).add(new BigDecimal("0.1")).toPlainString();
  }
  
}
