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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.characters.Person;

/**
 * Contents of mail someone has sent. For a list of receivers of the mail, see {@link MailReceiver}.
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_mailcontents")
@NamedQueries(
  {
    @NamedQuery(name = "Mail.deleteByName", query = "DELETE FROM Mail m WHERE m.name = :person or m.toname = :person"),
    @NamedQuery(name = "Mail.listmail", query = "SELECT DISTINCT r FROM Mail m, MailReceiver r WHERE m = r.mail and r.deleted = false and r.toname = :name order by m.whensent desc"),
    @NamedQuery(name = "Mail.listsentmail", query = "SELECT DISTINCT m FROM Mail m WHERE m.deleted = false and m.name = :name order by m.whensent desc"),
    @NamedQuery(name = "Mail.nonewmail", query = "UPDATE MailReceiver r SET r.newmail = false WHERE r.toname = :name"),
    @NamedQuery(name = "Mail.hasnewmail", query = "SELECT count(r.id) FROM MailReceiver r WHERE r.newmail = true and r.deleted = false and r.toname = :name")
  })
public class Mail implements Serializable
{

  private static final long serialVersionUID = 1L;

  public static final long ID_OLD_FADED_YELLOW_PARCHMENT = 8008L;
  public static final long ID_CRUMPLED_PIECE_OF_PAPER = 8009L;
  public static final long ID_OFFICIAL_LOOKING_SEALED_LEGAL_DOCUMENT = 8010L;
  public static final long ID_IMPRESSIVE_LOOKING_FORGED_LEGAL_DOCUMENT = 8011L;
  public static final long ID_HANDWRITTEN_FOLDED_INKSPATTERED_LETTER = 8012L;
  public static final long ID_SHORT_RUSHED_SQUARESHAPED_MEMO = 8013L;
  public static final long ID_EMBOSSED_BEAUTIFULLY_WRITTEN_LEAFLET = 8014L;
  public static final long ID_EMBOSSED_BEAUTIFULLY_WRITTEN_INVITATION = 8015L;
  private static final Long ID_OFFICIAL_MARRIAGE_CERTIFICATE = 10000L;
  private static final Long ID_FORGED_MARRIAGE_CERTIFICATE = 10001L;
  private static final Long ID_OFFICIAL_BIRTH_CERTIFICATE = 10002L;
  private static final Long ID_FORGED_BIRTH_CERTIFICATE = 10003L;
  private static final Long ID_OFFICIAL_DEATH_CERTIFICATE = 10004L;
  private static final Long ID_FORGED_DEATH_CERTIFICATE = 10005L;
  private static final Long ID_DAMAGED_OLD_TREASURE_MAP = 10006L;
  private static final Long ID_SHORT_DETAILED_SIGNED_RECEIPT = 10007L;
  private static final Long ID_BADLYSPELLED_RANSOM_NOTE = 10008L;
  private static final Long ID_SHORT_MYSTERIOUS_NOTE = 10009L;
  /**
   * Contains the item ids of the different items that represent letters/mail.
   * The readdescription of said letters looks a little like the following:
   * <p>
   * "stuffletterhead letterbody letterfooter"</p> That way, the
   * letterhead, letterbody and letterfooter are automatically replaced.
   */
  public static final List<Long> ITEMS = Arrays.asList(ID_OLD_FADED_YELLOW_PARCHMENT,
    ID_CRUMPLED_PIECE_OF_PAPER,
    ID_OFFICIAL_LOOKING_SEALED_LEGAL_DOCUMENT,
    ID_IMPRESSIVE_LOOKING_FORGED_LEGAL_DOCUMENT,
    ID_HANDWRITTEN_FOLDED_INKSPATTERED_LETTER,
    ID_SHORT_RUSHED_SQUARESHAPED_MEMO,
    ID_EMBOSSED_BEAUTIFULLY_WRITTEN_LEAFLET,
    ID_EMBOSSED_BEAUTIFULLY_WRITTEN_INVITATION,
    ID_OFFICIAL_MARRIAGE_CERTIFICATE,
    ID_FORGED_MARRIAGE_CERTIFICATE,
    ID_OFFICIAL_BIRTH_CERTIFICATE,
    ID_FORGED_BIRTH_CERTIFICATE,
    ID_OFFICIAL_DEATH_CERTIFICATE,
    ID_FORGED_DEATH_CERTIFICATE,
    ID_DAMAGED_OLD_TREASURE_MAP,
    ID_SHORT_DETAILED_SIGNED_RECEIPT,
    ID_BADLYSPELLED_RANSOM_NOTE,
    ID_SHORT_MYSTERIOUS_NOTE
  );

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Long id;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 100)
  @Column(name = "subject")
  private String subject;

  @Basic(optional = false)
  @NotNull
  @Column(name = "whensent")
  private LocalDateTime whensent;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "body")
  private String body;

  @Basic(optional = false)
  @NotNull
  @Column(name = "deleted")
  private Boolean deleted;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 4096)
  @Column(name = "toname")
  private String toname;

  @JoinColumn(name = "name", referencedColumnName = "name")
  @ManyToOne(optional = false)
  private Person name;

  public Mail()
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

  /**
   * The subject or title of the mail.
   *
   * @return String containing the subject.
   */
  public String getSubject()
  {
    return subject;
  }

  /**
   * @see #getSubject()
   */
  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public LocalDateTime getWhensent()
  {
    return whensent;
  }

  public void setWhensent(LocalDateTime whensent)
  {
    this.whensent = whensent;
  }

  public String getBody()
  {
    return body;
  }

  public void setBody(String body)
  {
    this.body = body;
  }


  /**
   * Indicates wether or not the original send of the mail, has deleted the mail.
   * This has no effect on the mail received, these are still viewable by the receivers.
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
   * <p>Returns the receiver(s) of the mail. as originally typed. For example, "Jim", but also "Jim, Hank",
   * but also possible is "guild", "deputies", "everybody", "guild: The Inner Flame".</p>
   * <p>As such there is no hard relation to persons or anything of the kind. It's just a string.</p>
   *
   * @return comma separated list of all people receiving the mail
   */
  public String getToname()
  {
    return toname;
  }

  /**
   * Sets the receiver of the mail.
   *
   * @see #getToname()
   */
  public void setToname(String toname)
  {
    this.toname = toname;
  }

  /**
   * Returns the sender of the mail.
   *
   * @return the person sending the mail.
   */
  public Person getName()
  {
    return name;
  }

  /**
   * Sets the sender of the mail.
   *
   * @param name sender of the mail
   */
  public void setName(Person name)
  {
    this.name = name;
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
    if (!(object instanceof Mail))
    {
      return false;
    }
    Mail other = (Mail) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Mail[ id=" + id + " ]";
  }
}
