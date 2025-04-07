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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.characters.User;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_boardmessages")
@NamedQuery(name = "BoardMessage.deleteByName", query = "DELETE FROM BoardMessage b WHERE b.user = :person")
@NamedQuery(name = "BoardMessage.news", query = "SELECT b FROM BoardMessage b WHERE b.board.name = 'logonmessage' and" +
    " (b.posttime > :lastSunday or b.pinned = true) and b.removed = false order by b.pinned, b.id desc")
@NamedQuery(name = "BoardMessage.recent", query = "SELECT b FROM BoardMessage b WHERE b.board = :board order by b" +
    ".pinned desc, b.id desc")
public class BoardMessage implements Serializable
{

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Long id;
  @JoinColumn(name = "boardid", referencedColumnName = "id")
  @ManyToOne(optional = false)
  private Board board;
  @JoinColumn(name = "name", referencedColumnName = "name")
  @ManyToOne(optional = false)
  private User user;
  @Basic(optional = false)
  @NotNull
  @Column(name = "posttime")
  private LocalDateTime posttime;
  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "message")
  private String message;
  @Column(name = "removed")
  private Boolean removed;
  @Column(name = "offensive")
  private Boolean offensive;
  @Column(name = "pinned")
  private Boolean pinned;

  public BoardMessage()
  {
  }

  /**
   * Returns the contents of the message as posted. If the message has been
   * removed, it will return the message "Message has been removed due to
   * offensive content.".
   *
   * @return the contents of the message on the board.
   * @see #getRemoved()
   */
  public String getMessage()
  {
    if (getOffensive())
    {
      return "<FONT COLOR=red>[Message has been removed due to offensive content.]</FONT>";
    }
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  /**
   * Indicates that the message has been removed.
   *
   * @return true if removed, false otherwise.
   */
  public boolean getRemoved()
  {
    if (removed == null) {
      return false;
    }
    return removed;
  }

  /**
   * Indicates that the message has been removed, due to offensive content.
   *
   * @return true if removed, false otherwise.
   */
  public Boolean getOffensive()
  {
    return offensive;
  }

  /**
   * @param offensive true if the message needs be removed, due to offensive content, false otherwise.
   */
  public void setOffensive(Boolean offensive)
  {
    this.offensive = offensive;
  }

  /**
   * @param removed true if the message needs be removed, fals otherwise.
   */
  public void setRemoved(Boolean removed)
  {
    this.removed = removed;
  }

  /**
   * Indicates if a message of a board is pinned or not.
   */
  public Boolean getPinned()
  {
    return pinned;
  }

  public BoardMessage setPinned(Boolean pinned)
  {
    this.pinned = pinned;
    return this;
  }

  public Board getBoard()
  {
    return board;
  }

  public void setBoard(Board board)
  {
    this.board = board;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public User getPerson()
  {
    return user;
  }

  public void setPerson(User user)
  {
    this.user = user;
  }

  public LocalDateTime getPosttime()
  {
    return posttime;
  }

  public void setPosttime(LocalDateTime posttime)
  {
    this.posttime = posttime;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    if (id == null)
    {
      return -1;
    }
    hash += id.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof BoardMessage))
    {
      return false;
    }
    BoardMessage other = (BoardMessage) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.BoardMessage[ id=" + id + " ]";
  }
}
