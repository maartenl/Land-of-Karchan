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
    @NamedQuery(name = "Command.findAll", query = "SELECT c FROM Command c"),
    @NamedQuery(name = "Command.findById", query = "SELECT c FROM Command c WHERE c.commandPK.id = :id"),
    @NamedQuery(name = "Command.match", query = "SELECT c FROM Command c WHERE c.callable = 1"), //  and c.commandPK.command regexp :regular_expression"),
    @NamedQuery(name = "Command.list", query = "SELECT c FROM Command c WHERE c.callable = 1")
})
public class Command implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CommandPK commandPK;
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
    private Method methodName;

    public Command()
    {
    }

    public Command(CommandPK commandPK)
    {
        this.commandPK = commandPK;
    }

    public Command(CommandPK commandPK, Date creation)
    {
        this.commandPK = commandPK;
        this.creation = creation;
    }

    public Command(int id, String command)
    {
        this.commandPK = new CommandPK(id, command);
    }

    public CommandPK getCommandPK()
    {
        return commandPK;
    }

    public void setCommandPK(CommandPK commandPK)
    {
        this.commandPK = commandPK;
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

    public Method getMethodName()
    {
        return methodName;
    }

    public void setMethodName(Method methodName)
    {
        this.methodName = methodName;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (commandPK != null ? commandPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Command))
        {
            return false;
        }
        Command other = (Command) object;
        if ((this.commandPK == null && other.commandPK != null) || (this.commandPK != null && !this.commandPK.equals(other.commandPK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Command[ commandPK=" + commandPK + " ]";
    }

}
