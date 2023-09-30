package mmud.database.entities.game;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import mmud.database.entities.characters.User;


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

  @JoinColumn(name = "owner", referencedColumnName = "name")
  @ManyToOne
  private User owner;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "chatline", orphanRemoval = true)
  private Set<Chatlineusers> users = new HashSet<>();

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

  public @Nullable User getOwner()
  {
    return owner;
  }

  public void setOwner(@Nullable User owner)
  {
    this.owner = owner;
  }

  public Set<Chatlineusers> getUsers()
  {
    return Collections.unmodifiableSet(users);
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Chatline chatline = (Chatline) o;
    return Objects.equals(chatname, chatline.chatname);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(chatname);
  }

  /**
   * Updates the time with when the last chat message was sent.
   */
  public void updateTime()
  {
    this.lastchattime = LocalDateTime.now();
  }
}
