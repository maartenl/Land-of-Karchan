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
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Embeddable
public class BoardmessagePK implements Serializable
{
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "boardid")
    private String boardid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Column(name = "posttime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date posttime;

    public BoardmessagePK()
    {
    }

    public BoardmessagePK(String boardid, String name, Date posttime)
    {
        this.boardid = boardid;
        this.name = name;
        this.posttime = posttime;
    }

    public String getBoardid()
    {
        return boardid;
    }

    public void setBoardid(String boardid)
    {
        this.boardid = boardid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getPosttime()
    {
        return posttime;
    }

    public void setPosttime(Date posttime)
    {
        this.posttime = posttime;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (boardid != null ? boardid.hashCode() : 0);
        hash += (name != null ? name.hashCode() : 0);
        hash += (posttime != null ? posttime.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BoardmessagePK))
        {
            return false;
        }
        BoardmessagePK other = (BoardmessagePK) object;
        if ((this.boardid == null && other.boardid != null) || (this.boardid != null && !this.boardid.equals(other.boardid)))
        {
            return false;
        }
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
        {
            return false;
        }
        if ((this.posttime == null && other.posttime != null) || (this.posttime != null && !this.posttime.equals(other.posttime)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.BoardmessagePK[ boardid=" + boardid + ", name=" + name + ", posttime=" + posttime + " ]";
    }

}
