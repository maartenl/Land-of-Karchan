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
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_mailtable", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Mail.findAll", query = "SELECT m FROM Mail m"),
    @NamedQuery(name = "Mail.findById", query = "SELECT m FROM Mail m WHERE m.id = :id"),
    @NamedQuery(name = "Mail.findBySubject", query = "SELECT m FROM Mail m WHERE m.subject = :subject"),
    @NamedQuery(name = "Mail.findByWhensent", query = "SELECT m FROM Mail m WHERE m.whensent = :whensent"),
    @NamedQuery(name = "Mail.findByHaveread", query = "SELECT m FROM Mail m WHERE m.haveread = :haveread"),
    @NamedQuery(name = "Mail.findByNewmail", query = "SELECT m FROM Mail m WHERE m.newmail = :newmail"),
    @NamedQuery(name = "Mail.listmail", query = "SELECT m FROM Mail m WHERE m.deleted = 0 and m.toname = :name"),
    @NamedQuery(name = "Mail.nonewmail", query = "UPDATE Mail m SET m.newmail = 0 WHERE m.toname = :name"),
    @NamedQuery(name = "Mail.hasnewmail", query = "SELECT count(*) FROM Mail m WHERE newmail = 1 and deleted = 0 and toname = :name")
    })
public class Mail implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date whensent;
    @Column(name = "haveread")
    private Boolean haveread;
    @Column(name = "newmail")
    private Boolean newmail;
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
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @ManyToOne
    private ItemDefinition itemId;
    @JoinColumn(name = "toname", referencedColumnName = "name")
    @ManyToOne(optional = false)
    private Person toname;
    @JoinColumn(name = "name", referencedColumnName = "name")
    @ManyToOne(optional = false)
    private Person name;

    public Mail()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public Date getWhensent()
    {
        return whensent;
    }

    public void setWhensent(Date whensent)
    {
        this.whensent = whensent;
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

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public Boolean getDeleted()
    {
        return deleted;
    }

    public void setDeleted(Boolean deleted)
    {
        this.deleted = deleted;
    }

    public ItemDefinition getItemId()
    {
        return itemId;
    }

    public void setItemId(ItemDefinition itemId)
    {
        this.itemId = itemId;
    }

    public Person getToname()
    {
        return toname;
    }

    public void setToname(Person toname)
    {
        this.toname = toname;
    }

    public Person getName()
    {
        return name;
    }

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
        // TODO: Warning - this method won't work in the case the id fields are not set
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
