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

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The templates used for displaying web pages with Freemarker.
 *
 * @author maartenl
 */
@Entity
@Table(name = "templates")
@NamedQueries(
        {
          @NamedQuery(name = "HtmlTemplate.findAll", query = "SELECT t FROM HtmlTemplate t"),
          @NamedQuery(name = "HtmlTemplate.findById", query = "SELECT t FROM HtmlTemplate t WHERE t.id = :id"),
          @NamedQuery(name = "HtmlTemplate.findByName", query = "SELECT t FROM HtmlTemplate t WHERE t.name = :name"),
          @NamedQuery(name = "HtmlTemplate.findByCreated", query = "SELECT t FROM HtmlTemplate t WHERE t.created = :created"),
          @NamedQuery(name = "HtmlTemplate.findByModified", query = "SELECT t FROM HtmlTemplate t WHERE t.modified = :modified"),
          @NamedQuery(name = "HtmlTemplate.findByVersion", query = "SELECT t FROM HtmlTemplate t WHERE t.version = :version")
        })
public class HtmlTemplate implements Serializable
{

  private static final long serialVersionUID = 1L;

  private static final BigDecimal VERSION_STEP = new BigDecimal("0.1");

  @Id
  @Basic(optional = false)
  @NotNull
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

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "editor")
  private String editor;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "comment")
  private String comment;

  public HtmlTemplate()
  {
    // ORM needs a default no-args constructor.
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

  public long getLastModified()
  {
    if (modified == null)
    {
      return created.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
    return modified.atZone(ZoneId.systemDefault()).toEpochSecond();
  }

  public Reader getReader()
  {
    return new StringReader(content);
  }

  /**
   * Adds 0.1 to the version number. This is to be used when a change occurs to
   * the template.
   */
  public void increaseVersion()
  {
    version = version.add(VERSION_STEP);
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
    if (!(object instanceof HtmlTemplate))
    {
      return false;
    }
    HtmlTemplate other = (HtmlTemplate) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.Template[ id=" + id + " ]";
  }

  /**
   * @return the editor
   */
  public String getEditor()
  {
    return editor;
  }

  /**
   * @param editor the editor to set
   */
  public void setEditor(String editor)
  {
    this.editor = editor;
  }

  /**
   * @return the comment
   */
  public String getComment()
  {
    return comment;
  }

  /**
   * @param comment the comment to set
   */
  public void setComment(String comment)
  {
    this.comment = comment;
  }

}
