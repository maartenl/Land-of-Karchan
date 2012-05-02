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
@Table(name = "mm_commandlog", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Commandlog.findAll", query = "SELECT c FROM Commandlog c"),
    @NamedQuery(name = "Commandlog.findById", query = "SELECT c FROM Commandlog c WHERE c.id = :id"),
    @NamedQuery(name = "Commandlog.findByStamp", query = "SELECT c FROM Commandlog c WHERE c.stamp = :stamp"),
    @NamedQuery(name = "Commandlog.findByName", query = "SELECT c FROM Commandlog c WHERE c.name = :name"),
    @NamedQuery(name = "Commandlog.findByCommand", query = "SELECT c FROM Commandlog c WHERE c.command = :command")
})
public class Commandlog implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "stamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stamp;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "command")
    private String command;

    public Commandlog()
    {
    }

    public Commandlog(Long id)
    {
        this.id = id;
    }

    public Commandlog(Long id, Date stamp, String name, String command)
    {
        this.id = id;
        this.stamp = stamp;
        this.name = name;
        this.command = command;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getStamp()
    {
        return stamp;
    }

    public void setStamp(Date stamp)
    {
        this.stamp = stamp;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
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
        if (!(object instanceof Commandlog))
        {
            return false;
        }
        Commandlog other = (Commandlog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Commandlog[ id=" + id + " ]";
    }

}
