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
import java.util.Collection;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.Ownage;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_methods")
@NamedQueries(
        {
          @NamedQuery(name = "Method.findAll", query = "SELECT m FROM Method m order by m.name"),
          @NamedQuery(name = "Method.countAll", query = "SELECT count(m) FROM Method m"),
          @NamedQuery(name = "Method.findByName", query = "SELECT m FROM Method m WHERE m.name = :name"),
          @NamedQuery(name = "Method.findByCreation", query = "SELECT m FROM Method m WHERE m.creation = :creation"),
          @NamedQuery(name = "Method.findRange", query = "SELECT m from Method m WHERE m.name like :alphabet")
        })
public class Method implements Serializable, Ownage
{

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "methodName")
  private Collection<UserCommand> mmCommandsCollection;
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 52)
  @Column(name = "name")
  private String name;
  @Basic(optional = false)
  @NotNull
  @Lob
  @Size(min = 1, max = 65535)
  @Column(name = "src")
  private String src;
  @Basic(optional = false)
  @NotNull
  @Column(name = "creation")
  private LocalDateTime creation;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "method")
  private Collection<Event> eventCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "methodName")
  private Collection<UserCommand> commandCollection;
  @JoinColumn(name = "owner", referencedColumnName = "name")
  @ManyToOne
  private Admin owner;

  public Method()
  {
  }

  public Method(String name)
  {
    this.name = name;
  }

  public Method(String name, String src, LocalDateTime creation)
  {
    this.name = name;
    this.src = src;
    this.creation = creation;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Get the javascript source to execute.
   *
   * @return
   */
  public String getSrc()
  {
    return src;
  }

  public void setSrc(String src)
  {
    this.src = src;
  }

  public LocalDateTime getCreation()
  {
    return creation;
  }

  public void setCreation(LocalDateTime creation)
  {
    this.creation = creation;
  }

  public Collection<Event> getEventCollection()
  {
    return eventCollection;
  }

  public void setEventCollection(Collection<Event> eventCollection)
  {
    this.eventCollection = eventCollection;
  }

  public Collection<UserCommand> getCommandCollection()
  {
    return commandCollection;
  }

  public void setCommandCollection(Collection<UserCommand> commandCollection)
  {
    this.commandCollection = commandCollection;
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
    if (!(object instanceof Method))
    {
      return false;
    }
    Method other = (Method) object;
    if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Method[ name=" + name + " ]";
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
