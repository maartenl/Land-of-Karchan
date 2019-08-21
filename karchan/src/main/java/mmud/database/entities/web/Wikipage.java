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
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
  @NamedQuery(name = "Wikipage.findByTitle", query = "SELECT w FROM Wikipage w WHERE w.title = :title and w.administration = false"),
  @NamedQuery(name = "Wikipage.findByTitleAuthorized", query = "SELECT w FROM Wikipage w WHERE w.title = :title"),
  @NamedQuery(name = "Wikipage.findFrontpage", query = "SELECT w FROM Wikipage w WHERE w.title = 'FrontPage' and w.administration = false"),
  @NamedQuery(name = "Wikipage.findByParentTitle", query = "SELECT w FROM Wikipage w WHERE w.parent.title = :parentTitle and w.administration = false")
})
public class Wikipage implements Serializable
{

  private static final long serialVersionUID = 1L;
  
  private final static Logger LOGGER = Logger.getLogger(Wikipage.class.getName());

  
  @Id
  @Size(max = 200)
  @Column(name = "title", nullable = false)
  @NotNull
  private String title;

  @Size(max = 100)
  @Column(name = "name", nullable = false)
  @NotNull
  private String name;

  @Basic(optional = false)
  @NotNull
  @Column(name = "createDate", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createDate;

  @Basic(optional = false)
  @NotNull
  @Column(name = "modifiedDate", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date modifiedDate;

  @Size(max = 10)
  @NotNull
  @Column(name = "version", nullable = false)
  private String version;

  @Lob
  @Size(max = 65535)
  @NotNull
  @Column(name = "content", nullable = false)
  private String content;

  @Lob
  @Size(max = 65535)
  @Column(name = "summary")
  private String summary;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parentTitle")
  private Wikipage parent;

  @Basic(optional = false)
  @NotNull
  @Column(name = "administration")
  private boolean administration;

  @Size(max = 200)
  @Column(name = "comment")
  private String comment;
  
  @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
  private Set<Wikipage> children;
  
  public Wikipage()
  {
  }

  public Wikipage(Date createDate, Date modifiedDate)
  {
    this.createDate = createDate;
    this.modifiedDate = modifiedDate;
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

  public Wikipage getParent()
  {
    LOGGER.log(Level.FINEST, "{0} returns {1}.", new Object[]{this, parent});
    return parent;
  }

  public void setParent(Wikipage parent)
  {
    this.parent = parent;
  }

  public boolean removePage(Wikipage childPage)
  {
    return children.remove(childPage);
  }
  
  public boolean addPage(Wikipage childPage)
  {
    return children.add(childPage);
  }

  public Set<Wikipage> getChildren() {
    return Collections.unmodifiableSet(children);
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

}
