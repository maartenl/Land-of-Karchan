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
package mmud.database.entities.items;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.game.Attribute;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_itemattributes")
@NamedQuery(name = "Itemattribute.findAll", query = "SELECT i FROM Itemattribute i")
@NamedQuery(name = "Itemattribute.findByName", query = "SELECT i FROM Itemattribute i WHERE i.name = :name order by i" +
    ".item.id")
@NamedQuery(name = "Itemattribute.findByNameAndItemid", query = "SELECT i FROM Itemattribute i WHERE i.name = :name " +
    "and i.item.id = :itemid")
@NamedQuery(name = "Itemattribute.findByItemid", query = "SELECT i FROM Itemattribute i WHERE i.item.id = :itemid")
@NamedQuery(name = "Itemattribute.findByValueType", query = "SELECT i FROM Itemattribute i WHERE i.valueType = " +
    ":valueType")
public class Itemattribute implements Serializable, Attribute
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
  private Item item;

  public Itemattribute()
  {
  }

  public Itemattribute(String name, Item item)
  {
    this.name = name;
    this.item = item;
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

  public String getValueType()
  {
    return valueType;
  }

  @Override
  public void setValueType(String valueType)
  {
    this.valueType = valueType;
  }

  public Item getItem()
  {
    return item;
  }

  public void setItem(Item item)
  {
    this.item = item;
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (attributeId != null ? attributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Itemattribute that = (Itemattribute) o;
    return Objects.equals(getAttributeId(), that.getAttributeId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getItem(), that.getItem());
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Itemattribute[ attributeId=" + attributeId + " value=" + value + " ]";
  }

  @Override
  public String getName()
  {
    return name;
  }
}
