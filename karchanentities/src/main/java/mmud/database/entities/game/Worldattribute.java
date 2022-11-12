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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.Ownage;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_worldattributes")
@NamedQueries(
  {
    @NamedQuery(name = "Worldattribute.findAll", query = "SELECT w FROM Worldattribute w"),
    @NamedQuery(name = "Worldattribute.findByName", query = "SELECT w FROM Worldattribute w WHERE w.name = :name"),
    @NamedQuery(name = "Worldattribute.findByType", query = "SELECT w FROM Worldattribute w WHERE w.type = :type"),
    @NamedQuery(name = "Worldattribute.countAll", query = "SELECT count(w) FROM Worldattribute w")
  })
public class Worldattribute implements Serializable, Ownage
{

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 180)
  @Column(name = "name")
  private String name;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 40)
  @Column(name = "type")
  private String type;

  @Lob
  @Size(max = 65535)
  @Column(name = "contents")
  private String contents;

  @JoinColumn(name = "owner", referencedColumnName = "name")
  @ManyToOne
  private Admin owner;

  @Basic(optional = false)
  @NotNull
  @Column(name = "creation")
  private LocalDateTime creation;

  public Worldattribute()
  {
  }

  public Worldattribute(String name)
  {
    this.name = name;
  }

  public Worldattribute(String name, String type)
  {
    this.name = name;
    this.type = type;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String type)
  {
    this.type = type;
  }

  public String getContents()
  {
    return contents;
  }

  public void setContents(String contents)
  {
    this.contents = contents;
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

  public LocalDateTime getCreation()
  {
    return creation;
  }

  public void setCreation(LocalDateTime creation)
  {
    this.creation = creation;
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
    if (!(object instanceof Worldattribute))
    {
      return false;
    }
    Worldattribute other = (Worldattribute) object;
    if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Worldattribute[ name=" + name + " ]";
  }

}
