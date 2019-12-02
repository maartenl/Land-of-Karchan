/*
 * Copyright (C) 2014 maartenl
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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import mmud.database.entities.Ownage;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_commands")
@NamedQueries(
        {
          @NamedQuery(name = "UserCommand.findAll", query = "SELECT m FROM UserCommand m order by m.command"),
          @NamedQuery(name = "UserCommand.countAll", query = "SELECT count(m) FROM UserCommand m"),
          @NamedQuery(name = "UserCommand.findAllByOwner", query = "SELECT m FROM UserCommand m where m.owner.name = :owner order by m.command"),
          @NamedQuery(name = "UserCommand.countAllByOwner", query = "SELECT count(m) FROM UserCommand m where m.owner.name = :owner"),

          @NamedQuery(name = "UserCommand.findById", query = "SELECT m FROM UserCommand m WHERE m.id = :id"),
          @NamedQuery(name = "UserCommand.findActive", query = "SELECT m FROM UserCommand m WHERE m.callable = 1"),
          @NamedQuery(name = "UserCommand.findByCommand", query = "SELECT m FROM UserCommand m WHERE m.command = :command"),
          @NamedQuery(name = "UserCommand.findByRoom", query = "SELECT m FROM UserCommand m WHERE m.room = :room"),
          @NamedQuery(name = "UserCommand.findByCreation", query = "SELECT m FROM UserCommand m WHERE m.creation = :creation")
        })
public class UserCommand implements Serializable, Ownage
{

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  private Integer id;
  @Basic(optional = true)
  @Column(name = "callable")
  private Boolean callable;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  private String command;
  @JoinColumn(name = "room", nullable = true, referencedColumnName = "id")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  private Room room;
  @Basic(optional = false)
  @NotNull
  private LocalDateTime creation;
  @JoinColumn(name = "owner", referencedColumnName = "name")
  @ManyToOne
  private Admin owner;
  @JoinColumn(name = "method_name", referencedColumnName = "name")
  @ManyToOne(optional = false)
  private Method methodName;

  public UserCommand()
  {
  }

  public UserCommand(Integer id)
  {
    this.id = id;
  }

  public UserCommand(Integer id, String command, LocalDateTime creation)
  {
    this.id = id;
    this.command = command;
    this.creation = creation;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public Boolean getCallable()
  {
    return callable != null && callable;
  }

  public void setCallable(Boolean callable)
  {
    if (callable == null || callable == false)
    {
      this.callable = false;
      return;
    }
    this.callable = true;
  }

  public String getCommand()
  {
    return command;
  }

  public void setCommand(String command)
  {
    this.command = command;
  }

  public Room getRoom()
  {
    return room;
  }

  public void setRoom(Room room)
  {
    this.room = room;
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

  public Method getMethodName()
  {
    return methodName;
  }

  public void setMethodName(Method methodName)
  {
    this.methodName = methodName;
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
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof UserCommand))
    {
      return false;
    }
    UserCommand other = (UserCommand) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.MmCommands[ id=" + id + " ]";
  }

}
