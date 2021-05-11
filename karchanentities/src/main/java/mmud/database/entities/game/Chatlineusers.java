package mmud.database.entities.game;

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
