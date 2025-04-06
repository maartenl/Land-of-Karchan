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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.entities.characters.Person;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author maartenl
 */
@Entity
@Table(name = "mm_charattributes")
@NamedQuery(name = "Charattribute.findAll", query = "SELECT c FROM Charattribute c")
@NamedQuery(name = "Charattribute.deleteByName", query = "DELETE FROM Charattribute c WHERE c.name = :name")
@NamedQuery(name = "Charattribute.findByName", query = "SELECT c FROM Charattribute c WHERE c.name = :name order by c.person.name")
@NamedQuery(name = "Charattribute.findByNameAndPerson", query = "SELECT c FROM Charattribute c WHERE c.name = :name and c.person.name = :person")
@NamedQuery(name = "Charattribute.findByPerson", query = "SELECT c FROM Charattribute c WHERE c.person.name = :person")
@NamedQuery(name = "Charattribute.findByValueType", query = "SELECT c FROM Charattribute c WHERE c.valueType = :valueType")
public class Charattribute implements Serializable, Attribute
{

    @Serial
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
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Charattribute that = (Charattribute) o;
        return Objects.equals(getAttributeId(), that.getAttributeId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getPerson(), that.getPerson());
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Charattribute[ attributeId=" + attributeId + " ]";
    }

}
