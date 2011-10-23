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
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_boardmessages")
@NamedQueries(
{
    @NamedQuery(name = "Boardmessage.findAll", query = "SELECT b FROM Boardmessage b"),
    @NamedQuery(name = "Boardmessage.findByBoardid", query = "SELECT b FROM Boardmessage b WHERE b.boardmessagePK.boardid = :boardid"),
    @NamedQuery(name = "Boardmessage.findByName", query = "SELECT b FROM Boardmessage b WHERE b.boardmessagePK.name = :name"),
    @NamedQuery(name = "Boardmessage.findByPosttime", query = "SELECT b FROM Boardmessage b WHERE b.boardmessagePK.posttime = :posttime"),
    @NamedQuery(name = "Boardmessage.findByRemoved", query = "SELECT b FROM Boardmessage b WHERE b.removed = :removed")
})
public class Boardmessage implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected BoardmessagePK boardmessagePK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "message")
    private String message;
    @Column(name = "removed")
    private Integer removed;

    public Boardmessage()
    {
    }

    public Boardmessage(BoardmessagePK boardmessagePK)
    {
        this.boardmessagePK = boardmessagePK;
    }

    public Boardmessage(BoardmessagePK boardmessagePK, String message)
    {
        this.boardmessagePK = boardmessagePK;
        this.message = message;
    }

    public Boardmessage(String boardid, String name, Date posttime)
    {
        this.boardmessagePK = new BoardmessagePK(boardid, name, posttime);
    }

    public BoardmessagePK getBoardmessagePK()
    {
        return boardmessagePK;
    }

    public void setBoardmessagePK(BoardmessagePK boardmessagePK)
    {
        this.boardmessagePK = boardmessagePK;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public Integer getRemoved()
    {
        return removed;
    }

    public void setRemoved(Integer removed)
    {
        this.removed = removed;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (boardmessagePK != null ? boardmessagePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Boardmessage))
        {
            return false;
        }
        Boardmessage other = (Boardmessage) object;
        if ((this.boardmessagePK == null && other.boardmessagePK != null) || (this.boardmessagePK != null && !this.boardmessagePK.equals(other.boardmessagePK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Boardmessage[ boardmessagePK=" + boardmessagePK + " ]";
    }

}
