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
@Table(name = "mm_boardmessages", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "BoardMessage.findAll", query = "SELECT b FROM BoardMessage b"),
    @NamedQuery(name = "BoardMessage.findByBoardid", query = "SELECT b FROM BoardMessage b WHERE b.boardMessagePK.boardid = :boardid"),
    @NamedQuery(name = "BoardMessage.findByName", query = "SELECT b FROM BoardMessage b WHERE b.boardMessagePK.name = :name"),
    @NamedQuery(name = "BoardMessage.findByPosttime", query = "SELECT b FROM BoardMessage b WHERE b.boardMessagePK.posttime = :posttime"),
    @NamedQuery(name = "BoardMessage.findByRemoved", query = "SELECT b FROM BoardMessage b WHERE b.removed = :removed")
})
public class BoardMessage implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected BoardMessagePK boardMessagePK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "message")
    private String message;
    @Column(name = "removed")
    private Integer removed;

    public BoardMessage()
    {
    }

    public BoardMessage(BoardMessagePK boardMessagePK)
    {
        this.boardMessagePK = boardMessagePK;
    }

    public BoardMessage(BoardMessagePK boardMessagePK, String message)
    {
        this.boardMessagePK = boardMessagePK;
        this.message = message;
    }

    public BoardMessage(String boardid, String name, Date posttime)
    {
        this.boardMessagePK = new BoardMessagePK(boardid, name, posttime);
    }

    public BoardMessagePK getBoardMessagePK()
    {
        return boardMessagePK;
    }

    public void setBoardMessagePK(BoardMessagePK boardMessagePK)
    {
        this.boardMessagePK = boardMessagePK;
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
        hash += (boardMessagePK != null ? boardMessagePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BoardMessage))
        {
            return false;
        }
        BoardMessage other = (BoardMessage) object;
        if ((this.boardMessagePK == null && other.boardMessagePK != null) || (this.boardMessagePK != null && !this.boardMessagePK.equals(other.boardMessagePK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.BoardMessage[ boardMessagePK=" + boardMessagePK + " ]";
    }

}
