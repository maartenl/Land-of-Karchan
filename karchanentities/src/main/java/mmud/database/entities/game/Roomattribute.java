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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_roomattributes")
@NamedQueries(
        {
          @NamedQuery(name = "Roomattribute.findAll", query = "SELECT r FROM Roomattribute r"),
          @NamedQuery(name = "Roomattribute.findByName", query = "SELECT r FROM Roomattribute r WHERE r.name = :name"),
          @NamedQuery(name = "Roomattribute.findByValueType", query = "SELECT r FROM Roomattribute r WHERE r.valueType = :valueType")
        })
public class Roomattribute implements Serializable, Attribute
{

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "attrid")
  private Long attributeId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 32)
  @Column(name = "name")
  private String name;

  @Lob
  @Size(max = 65535)
  @Column(name = "value")
  private String value;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "value_type")
  private String valueType;

  @JoinColumn(name = "id", referencedColumnName = "id", nullable = false)
  @ManyToOne(optional = false)
  private Room room;

  public Roomattribute()
  {
  }

  public Roomattribute(String name, Room room)
  {
    this.name = name;
    this.room = room;
  }

  @Override
  public Long getAttributeId()
  {
    return attributeId;
  }

  @Override
  public String getValue()
  {
    return value;
  }

  @Override
  public void setValue(String value)
  {
    this.value = value;
  }

  @Override
  public String getValueType()
  {
    return valueType;
  }

  @Override
  public void setValueType(String valueType)
  {
    this.valueType = valueType;
  }

  public Room getRoom()
  {
    return room;
  }

  public void setRoom(Room room)
  {
    this.room = room;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (attributeId != null ? attributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Roomattribute))
    {
      return false;
    }
    Roomattribute other = (Roomattribute) object;
    if ((this.attributeId == null && other.attributeId != null) || (this.attributeId != null && !this.attributeId.equals(other.attributeId)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Roomattribute[ attributeId=" + attributeId + " ]";
  }

  @Override
  public String getName()
  {
    return name;
  }
}
