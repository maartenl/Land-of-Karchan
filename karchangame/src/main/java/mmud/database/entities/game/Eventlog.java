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

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_eventlog")
@NamedQueries(
{
    @NamedQuery(name = "Eventlog.findAll", query = "SELECT e FROM Eventlog e"),
    @NamedQuery(name = "Eventlog.findById", query = "SELECT e FROM Eventlog e WHERE e.id = :id"),
    @NamedQuery(name = "Eventlog.findByCreation", query = "SELECT e FROM Eventlog e WHERE e.creation = :creation"),
    @NamedQuery(name = "Eventlog.findByName", query = "SELECT e FROM Eventlog e WHERE e.name = :name"),
    @NamedQuery(name = "Eventlog.findByMessage", query = "SELECT e FROM Eventlog e WHERE e.message = :message")
})
public class Eventlog implements Serializable
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
    @Column(name = "creation")
    private LocalDateTime creation;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "message")
    private String message;

    @Lob
    @Size(max = 65535)
    @Column(name = "addendum")
    private String addendum;

    public Eventlog()
    {
    }

    public Eventlog(Long id)
    {
        this.id = id;
    }

    public Eventlog(Long id, LocalDateTime creation, String name, String message)
    {
        this.id = id;
        this.creation = creation;
        this.name = name;
        this.message = message;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public LocalDateTime getCreation()
    {
        return creation;
    }

    public void setCreation(LocalDateTime creation)
    {
        this.creation = creation;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getAddendum()
    {
        return addendum;
    }

    public void setAddendum(String addendum)
    {
        this.addendum = addendum;
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
        if (!(object instanceof Eventlog))
        {
            return false;
        }
        Eventlog other = (Eventlog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Eventlog[ id=" + id + " ]";
    }

}
