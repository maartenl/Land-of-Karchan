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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import mmud.database.entities.characters.Person;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_charattributes")
@NamedQueries(
        {
          @NamedQuery(name = "Charattribute.findAll", query = "SELECT c FROM Charattribute c"),
          @NamedQuery(name = "Charattribute.findByName", query = "SELECT c FROM Charattribute c WHERE c.name = :name"),
          @NamedQuery(name = "Charattribute.findByValueType", query = "SELECT c FROM Charattribute c WHERE c.valueType = :valueType")
        })
public class Charattribute implements Serializable, Attribute
{

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
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

  @JoinColumn(name = "charname", referencedColumnName = "name", nullable = false)
  @ManyToOne(optional = false)
  private Person person;

  public Charattribute()
  {
  }

  public Charattribute(String name, Person person)
  {
    this.name = name;
    this.person = person;
  }

  @Override
  public Long getAttributeId()
  {
    return attributeId;
  }

  @Override
  public String getName()
  {
    return name;
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

  public Person getPerson()
  {
    return person;
  }

  public void setPerson(Person person)
  {
    this.person = person;
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
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Charattribute))
    {
      return false;
    }
    Charattribute other = (Charattribute) object;
    if ((this.attributeId == null && other.attributeId != null) || (this.attributeId != null && !this.attributeId.equals(other.attributeId)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Charattribute[ attributeId=" + attributeId + " ]";
  }

}
