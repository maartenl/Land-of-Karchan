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

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A simple container for a question and answer.
 * @author maartenl
 */
@Entity
@Table(name = "faq")
@NamedQueries(
{
  @NamedQuery(name = "Faq.findAll", query = "SELECT f FROM Faq f WHERE f.deleted = 0 order by f.id"),
  @NamedQuery(name = "Faq.findById", query = "SELECT f FROM Faq f WHERE f.id = :id"),
  @NamedQuery(name = "Faq.findByQuestion", query = "SELECT f FROM Faq f WHERE f.question = :question"),
  @NamedQuery(name = "Faq.findByCreated", query = "SELECT f FROM Faq f WHERE f.created = :created"),
  @NamedQuery(name = "Faq.findByModified", query = "SELECT f FROM Faq f WHERE f.modified = :modified"),
  @NamedQuery(name = "Faq.findByDeleted", query = "SELECT f FROM Faq f WHERE f.deleted = :deleted")
})
public class Faq implements Serializable
{

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  private Long id;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 254)
  private String question;
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
  private String answer;
  private Boolean deleted;

  public Faq()
  {
  }

  public Faq(Long id)
  {
    this.id = id;
  }

  public Faq(Long id, String question, Date created, Date modified, String answer)
  {
    this.id = id;
    this.question = question;
    this.created = created;
    this.modified = modified;
    this.answer = answer;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getQuestion()
  {
    return question;
  }

  public void setQuestion(String question)
  {
    this.question = question;
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

  public String getAnswer()
  {
    return answer;
  }

  public void setAnswer(String answer)
  {
    this.answer = answer;
  }

  public Boolean getDeleted()
  {
    return deleted;
  }

  public void setDeleted(Boolean deleted)
  {
    this.deleted = deleted;
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
    if (!(object instanceof Faq))
    {
      return false;
    }
    Faq other = (Faq) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.web.Faq[ id=" + id + " ]";
  }

}
