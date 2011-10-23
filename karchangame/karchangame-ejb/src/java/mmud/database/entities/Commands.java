/*
 * Copyright (C) 2011 maartenl
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
package mmud.database.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_commands")
@NamedQueries(
{
    @NamedQuery(name = "Commands.findAll", query = "SELECT c FROM Commands c"),
    @NamedQuery(name = "Commands.findById", query = "SELECT c FROM Commands c WHERE c.commandsPK.id = :id"),
    @NamedQuery(name = "Commands.findByCallable", query = "SELECT c FROM Commands c WHERE c.callable = :callable"),
    @NamedQuery(name = "Commands.findByCommand", query = "SELECT c FROM Commands c WHERE c.commandsPK.command = :command"),
    @NamedQuery(name = "Commands.findByRoom", query = "SELECT c FROM Commands c WHERE c.room = :room"),
    @NamedQuery(name = "Commands.findByCreation", query = "SELECT c FROM Commands c WHERE c.creation = :creation")
})
public class Commands implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CommandsPK commandsPK;
    @Column(name = "callable")
    private Integer callable;
    @Column(name = "room")
    private Integer room;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;
    @JoinColumn(name = "method_name", referencedColumnName = "name")
    @ManyToOne(optional = false)
    private Methods methodName;

    public Commands()
    {
    }

    public Commands(CommandsPK commandsPK)
    {
        this.commandsPK = commandsPK;
    }

    public Commands(CommandsPK commandsPK, Date creation)
    {
        this.commandsPK = commandsPK;
        this.creation = creation;
    }

    public Commands(int id, String command)
    {
        this.commandsPK = new CommandsPK(id, command);
    }

    public CommandsPK getCommandsPK()
    {
        return commandsPK;
    }

    public void setCommandsPK(CommandsPK commandsPK)
    {
        this.commandsPK = commandsPK;
    }

    public Integer getCallable()
    {
        return callable;
    }

    public void setCallable(Integer callable)
    {
        this.callable = callable;
    }

    public Integer getRoom()
    {
        return room;
    }

    public void setRoom(Integer room)
    {
        this.room = room;
    }

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
    }

    public Methods getMethodName()
    {
        return methodName;
    }

    public void setMethodName(Methods methodName)
    {
        this.methodName = methodName;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (commandsPK != null ? commandsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Commands))
        {
            return false;
        }
        Commands other = (Commands) object;
        if ((this.commandsPK == null && other.commandsPK != null) || (this.commandsPK != null && !this.commandsPK.equals(other.commandsPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Commands[ commandsPK=" + commandsPK + " ]";
    }

}
