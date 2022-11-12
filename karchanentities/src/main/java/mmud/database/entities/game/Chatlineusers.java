package mmud.database.entities.game;

import java.util.Objects;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import mmud.database.entities.characters.User;

@Entity
@Table(name = "mm_chatlineusers", schema = "mmud", catalog = "")
public class Chatlineusers
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private long id;

  @JoinColumn(name = "chatid", referencedColumnName = "id")
  @ManyToOne(cascade = CascadeType.ALL, optional = false)
  private Chatline chatline;

  @JoinColumn(name = "name", referencedColumnName = "name")
  @ManyToOne(optional = false)
  private User user;

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public Chatline getChatline()
  {
    return chatline;
  }

  public User getUser()
  {
    return user;
  }

  public void setChatline(Chatline chatline)
  {
    this.chatline = chatline;
  }

  public void setUser(User user)
  {
    this.user = user;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Chatlineusers that = (Chatlineusers) o;
    return id == that.id;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(id);
  }
}
