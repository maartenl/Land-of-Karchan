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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.characters.Person;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemDefinition;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_admin")
@NamedQuery(name = "Admin.findAll", query = "SELECT a FROM Admin a")
@NamedQuery(name = "Admin.findByName", query = "SELECT a FROM Admin a WHERE a.name = :name")
@NamedQuery(name = "Admin.findValidByName", query = "SELECT a FROM Admin a WHERE a.name = :name AND a.validuntil " +
  "> current_date")
@NamedQuery(name = "Admin.findValid", query = "SELECT a FROM Admin a WHERE a.validuntil > current_date")
@NamedQuery(name = "Admin.findByIp", query = "SELECT a FROM Admin a WHERE a.ip = :ip")
@NamedQuery(name = "Admin.findByCreated", query = "SELECT a FROM Admin a WHERE a.created = :created")
@NamedQuery(name = "Admin.findByValiduntil", query = "SELECT a FROM Admin a WHERE a.validuntil = :validuntil")
@NamedQuery(name = "Admin.findByEmail", query = "SELECT a FROM Admin a WHERE a.email = :email")
public class Admin implements Serializable
{

  @OneToMany(mappedBy = "owner")
  private Collection<UserCommand> mmCommandsCollection;

  public static final String DEFAULT_OWNER = "Karn";

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 39)
  @Column(name = "name")
  private String name;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 38)
  @Column(name = "ip")
  private String ip;
  @Basic(optional = false)
  @NotNull
  @Column(name = "created")
  private LocalDateTime created;
  @Basic(optional = false)
  @NotNull
  @Column(name = "validuntil")
  private LocalDate validuntil;
  // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9]
  // (?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains
  // email address consider using this annotation to enforce field validation
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 80)
  @Column(name = "email")
  private String email;
  @OneToMany(mappedBy = "owner")
  private Collection<Room> roomCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<Item> itemCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<Person> personCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<Event> eventCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<ItemDefinition> itemDefinitionCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<Area> areaCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<UserCommand> commandCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<Method> methodCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
  private Collection<Board> boardCollection;
  @OneToMany(mappedBy = "owner")
  private Collection<Guild> guildCollection;

  public Admin()
  {
  }

  public Admin(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getIp()
  {
    return ip;
  }

  public void setIp(String ip)
  {
    this.ip = ip;
  }

  public LocalDateTime getCreated()
  {
    return created;
  }

  public void setCreated(LocalDateTime created)
  {
    this.created = created;
  }

  public LocalDate getValiduntil()
  {
    return validuntil;
  }

  public void setValiduntil(LocalDate validuntil)
  {
    this.validuntil = validuntil;
  }

  /**
   * Returns true if this account is still valid. Which means, that the
   * expiration date has not yet passed.
   *
   * @return
   */
  public boolean isValid()
  {
    return validuntil.isAfter(LocalDate.now());
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public Collection<Room> getRoomCollection()
  {
    return roomCollection;
  }

  public void setRoomCollection(Collection<Room> roomCollection)
  {
    this.roomCollection = roomCollection;
  }

  public Collection<Item> getItemCollection()
  {
    return itemCollection;
  }

  public void setItemCollection(Collection<Item> itemCollection)
  {
    this.itemCollection = itemCollection;
  }

  public Collection<Person> getPersonCollection()
  {
    return personCollection;
  }

  public void setPersonCollection(Collection<Person> personCollection)
  {
    this.personCollection = personCollection;
  }

  public Collection<Event> getEventCollection()
  {
    return eventCollection;
  }

  public void setEventCollection(Collection<Event> eventCollection)
  {
    this.eventCollection = eventCollection;
  }

  public Collection<ItemDefinition> getItemDefinitionCollection()
  {
    return itemDefinitionCollection;
  }

  public void setItemDefinitionCollection(Collection<ItemDefinition> itemDefinitionCollection)
  {
    this.itemDefinitionCollection = itemDefinitionCollection;
  }

  public Collection<Area> getAreaCollection()
  {
    return areaCollection;
  }

  public void setAreaCollection(Collection<Area> areaCollection)
  {
    this.areaCollection = areaCollection;
  }

  public Collection<UserCommand> getCommandCollection()
  {
    return commandCollection;
  }

  public void setCommandCollection(Collection<UserCommand> commandCollection)
  {
    this.commandCollection = commandCollection;
  }

  public Collection<Method> getMethodCollection()
  {
    return methodCollection;
  }

  public void setMethodCollection(Collection<Method> methodCollection)
  {
    this.methodCollection = methodCollection;
  }

  public Collection<Board> getBoardCollection()
  {
    return boardCollection;
  }

  public void setBoardCollection(Collection<Board> boardCollection)
  {
    this.boardCollection = boardCollection;
  }

  public Collection<Guild> getGuildCollection()
  {
    return guildCollection;
  }

  public void setGuildCollection(Collection<Guild> guildCollection)
  {
    this.guildCollection = guildCollection;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (name != null ? name.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Admin))
    {
      return false;
    }
    Admin other = (Admin) object;
    return (this.name != null || other.name == null) && (this.name == null || this.name.equals(other.name));
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Admin[ name=" + name + " ]";
  }

  public Collection<UserCommand> getMmCommandsCollection()
  {
    return mmCommandsCollection;
  }

  public void setMmCommandsCollection(Collection<UserCommand> mmCommandsCollection)
  {
    this.mmCommandsCollection = mmCommandsCollection;
  }

}
