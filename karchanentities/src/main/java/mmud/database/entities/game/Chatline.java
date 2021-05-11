package mmud.database.entities.game;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * A chatline, a chatroom that can be defined by players.
 */
@Entity
@Table(name = "mm_chatlines")
@NamedQuery(name = "Chatline.all", query = "SELECT b FROM Chatline b")
@NamedQuery(name = "Chatline.findByName", query = "SELECT b FROM Chatline b WHERE b.chatname = :name")
public class Chatline
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private long id;

  @Basic
  @Column(name = "chatname")
  private String chatname;

  @Basic
  @Column(name = "attributename")
  private String attributename;

  @Basic
  @Column(name = "colour")
  private String colour;

  @Basic
  @Column(name = "lastchattime")
  private LocalDateTime lastchattime;

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getChatname()
  {
    return chatname;
  }

  public void setChatname(String chatname)
  {
    this.chatname = chatname;
  }

  public String getAttributename()
  {
    return attributename;
  }

  public void setAttributename(String attributename)
  {
    this.attributename = attributename;
  }

  public String getColour()
  {
    return colour;
  }

  public void setColour(String colour)
  {
    this.colour = colour;
  }

  public LocalDateTime getLastchattime()
  {
    return lastchattime;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Chatline chatline = (Chatline) o;
    return id == chatline.id && Objects.equals(chatname, chatline.chatname) && Objects.equals(attributename, chatline.attributename) && Objects.equals(colour, chatline.colour);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(id, chatname, attributename, colour);
  }

  /**
   * Updates the time with when the last chat message was sent.
   */
  public void updateTime()
  {
    this.lastchattime = LocalDateTime.now();
  }
}
