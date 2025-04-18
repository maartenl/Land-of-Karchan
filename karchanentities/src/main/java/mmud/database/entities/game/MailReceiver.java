/*
 *  Copyright (C) 2012 maartenl
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
package mmud.database.entities.game;

import java.io.Serializable;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import mmud.database.entities.characters.Person;

/**
 * It's basically a couple table between the mail sent, and the addressee.
 * Which means there are potentially many records here for the same mail, if the mail
 * is sent in bulk.
 *
 * @author maartenl
 * @see Mail
 */
@Entity
@Table(name = "mm_mailtable")
@NamedQuery(name = "MailReceiver.deleteByToname", query = "DELETE FROM MailReceiver m WHERE m.toname = :person")
@NamedQuery(name = "MailReceiver.deleteByName", query = "DELETE FROM MailReceiver m WHERE m.mail.name = :person")
public class MailReceiver implements Serializable
{

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Long id;

  @Column(name = "haveread")
  private Boolean haveread;

  @Column(name = "newmail")
  private Boolean newmail;

  @Basic(optional = false)
  @NotNull
  @Column(name = "deleted")
  private Boolean deleted;

  @JoinColumn(name = "toname", referencedColumnName = "name")
  @ManyToOne(optional = false)
  private Person toname;

  @JoinColumn(name = "contentsid", referencedColumnName = "id")
  @ManyToOne(optional = false)
  private Mail mail;

  public MailReceiver()
  {
    // ORMs usually require an empty constructor.
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public Boolean getHaveread()
  {
    return haveread;
  }

  public void setHaveread(Boolean haveread)
  {
    this.haveread = haveread;
  }

  public Boolean getNewmail()
  {
    return newmail;
  }

  public void setNewmail(Boolean newmail)
  {
    this.newmail = newmail;
  }

  /**
   * Indicates wether the received of the mail has deleted the mail.
   * This has no effect on other receivers or the send said mail.
   */
  public Boolean getDeleted()
  {
    if (deleted == null)
    {
      return false;
    }
    return deleted;
  }

  /**
   * @see #getDeleted()
   */
  public void setDeleted(Boolean deleted)
  {
    this.deleted = deleted;
  }

  /**
   * Returns the receiver of the mail.
   *
   * @return the person receiving the mail
   */
  public Person getToname()
  {
    return toname;
  }

  /**
   * Sets the receiver of the mail.
   *
   * @param toname
   */
  public void setToname(Person toname)
  {
    this.toname = toname;
  }

  /**
   * Gets the actual mail record this person received.
   *
   * @return the mail itself
   */
  public Mail getMail()
  {
    return mail;
  }

  /**
   * @see #getMail()
   */
  public void setMail(Mail mail)
  {
    this.mail = mail;
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
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof MailReceiver))
    {
      return false;
    }
    MailReceiver other = (MailReceiver) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.MailReceiver[ id=" + id + " ]";
  }
}
