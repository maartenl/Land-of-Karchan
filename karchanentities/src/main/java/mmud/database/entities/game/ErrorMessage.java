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
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_errormessages")
@NamedQueries(
{
    @NamedQuery(name = "ErrorMessage.findAll", query = "SELECT e FROM ErrorMessage e"),
    @NamedQuery(name = "ErrorMessage.findByMsg", query = "SELECT e FROM ErrorMessage e WHERE e.msg = :msg")
})
public class ErrorMessage implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "msg")
    private String msg;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;

    public ErrorMessage()
    {
    }

    public ErrorMessage(String msg)
    {
        this.msg = msg;
    }

    public ErrorMessage(String msg, String description)
    {
        this.msg = msg;
        this.description = description;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (msg != null ? msg.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ErrorMessage))
        {
            return false;
        }
        ErrorMessage other = (ErrorMessage) object;
        if ((this.msg == null && other.msg != null) || (this.msg != null && !this.msg.equals(other.msg)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.ErrorMessage[ msg=" + msg + " ]";
    }

}
