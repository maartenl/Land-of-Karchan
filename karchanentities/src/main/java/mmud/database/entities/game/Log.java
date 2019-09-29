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

import java.util.Objects;
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
 * Event logging. For instance when somebody logs on or logs off or
 * an error occurs.
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_log")
@NamedQueries(
        {
            @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
            @NamedQuery(name = "Log.findByName", query = "SELECT l FROM Log l WHERE l.name = :name"),
            @NamedQuery(name = "Log.findByMessage", query = "SELECT l FROM Log l WHERE l.message = :message")
        })
public class Log implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    private LocalDateTime creation;
    
    @Basic(optional = true)
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
    
    @Basic(optional = false)
    @Column(name = "deputy")
    private boolean deputy;

    public Log()
    {
        this.creation = LocalDateTime.now();
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
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Log other = (Log) obj;
        if (!Objects.equals(this.id, other.id))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Log{" + "id=" + id + '}';
    }

    /**
     * Indicates that this log message was transmitted by the actions of a
     * deputy. The {@link #getName() } can show you which deputy it was.
     *
     * @return true if it is a deputy log message.
     */
    public boolean getDeputy()
    {
        return deputy;
    }

    /**
     * @see #getDeputy()
     * @param deputy
     */
    public void setDeputy(boolean deputy)
    {
        this.deputy = deputy;
    }
}
