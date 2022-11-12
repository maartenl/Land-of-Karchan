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
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.Ownage;
import mmud.database.entities.characters.User;
import mmud.exceptions.BoardException;
import mmud.exceptions.MudException;
import org.eclipse.persistence.annotations.Customizer;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_boards")
@NamedQueries(
  {
    @NamedQuery(name = "Board.findAll", query = "SELECT b FROM Board b"),
    @NamedQuery(name = "Board.findById", query = "SELECT b FROM Board b WHERE b.id = :id"),
    @NamedQuery(name = "Board.findByName", query = "SELECT b FROM Board b WHERE b.name = :name"),
    @NamedQuery(name = "Board.findByCreation", query = "SELECT b FROM Board b WHERE b.creation = :creation"),
    @NamedQuery(name = "Board.countAll", query = "SELECT count(b) FROM Board b"),
  })
@Customizer(MessagesFilterForBoard.class)
public class Board implements Serializable, DisplayInterface, Ownage
{

  private static final long serialVersionUID = 1L;

  private static final long ONE_WEEK = 1000l * 60l * 60l * 24l * 7l;

  private static final Logger LOGGER = Logger.getLogger(Board.class.getName());

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "id")
  private Long id;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 80)
  @Column(name = "name")
  private String name;

  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "description")
  private String description;

  @Basic(optional = false)
  @NotNull
  @Column(name = "creation")
  private LocalDateTime creation;

  @JoinColumn(name = "owner", referencedColumnName = "name")
  @ManyToOne(optional = false)
  private Admin owner;

  @JoinColumn(name = "room", nullable = false, referencedColumnName = "id")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @NotNull
  private Room room;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "board", fetch = FetchType.LAZY)
  @OrderBy
  private Set<BoardMessage> messages;

  public Board()
  {
  }

  public Board(Long id)
  {
    this.id = id;
  }

  public Board(Long id, String name, String description, LocalDateTime creation)
  {
    this.id = id;
    this.name = name;
    this.description = description;
    this.creation = creation;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public LocalDateTime getCreation()
  {
    return creation;
  }

  public void setCreation(LocalDateTime creation)
  {
    this.creation = creation;
  }

  @Override
  public Admin getOwner()
  {
    return owner;
  }

  @Override
  public void setOwner(Admin owner)
  {
    this.owner = owner;
  }

  /**
   * The room in which this message board resides.
   *
   * @return Room, cannot be null.
   */
  public Room getRoom()
  {
    return room;
  }

  /**
   * Sets the room in which this message board resides.
   *
   * @param room The room. Cannot be null.
   */
  public void setRoom(Room room)
  {
    this.room = room;
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
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Board))
    {
      return false;
    }
    Board other = (Board) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Board[ id=" + id + " ]";
  }

  public boolean addMessage(User aUser, String message)
  {
    BoardMessage boardMessage = new BoardMessage();
    boardMessage.setBoard(this);
    boardMessage.setMessage(message);
    boardMessage.setPerson(aUser);
    boardMessage.setPosttime(LocalDateTime.now());
    boardMessage.setRemoved(false);
    return messages.add(boardMessage);
  }

  @Override
  public String getMainTitle() throws MudException
  {
    return getName();
  }

  @Override
  public String getImage() throws MudException
  {
    // TODO : add an image to the board, we can suffice for now with a generic
    // image, but it would be nice to have an image per board.
    // TODO : add an image field to the board table.
    return null;
  }

  @Override
  public String getBody() throws MudException
  {
    StringBuilder builder = new StringBuilder(getDescription());
    builder.append("<hr/>");
    final Set<BoardMessage> sortedMessages = new TreeSet<>(new Comparator<BoardMessage>()
    {

      @Override
      public int compare(BoardMessage arg0, BoardMessage arg1)
      {
        return arg0.getPosttime().compareTo(arg1.getPosttime());
      }
    });
    sortedMessages.addAll(messages);
    for (BoardMessage message : sortedMessages)
    {
      builder.append(message.getPosttime());
      builder.append("<p>");
      builder.append(message.getMessage());
      builder.append("</p><i>");
      if (message.getPerson() == null)
      {
        builder.append("&lt;unknown&gt;");
        // TODO : this is weird, but the error is gone. Don't know what happened here.
        // can this be removed?
        throw new BoardException("message " + message.getId() + " gave an error.");
      } else
      {
        builder.append(message.getPerson().getName());
      }
      builder.append("</i>");
      builder.append("<hr/>");
    }
    return builder.toString();
  }

}
