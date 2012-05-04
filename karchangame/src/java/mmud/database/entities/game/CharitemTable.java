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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import mmud.database.entities.characters.Person;

/**
 * A coupling entity between an item and who has it and what they are doing with it.
 * TODO : needs a rename to Charitem or something.
 * @author maartenl
 */
@Entity
@Table(name = "mm_charitemtable", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "CharitemTable.findAll", query = "SELECT c FROM CharitemTable c"),
    @NamedQuery(name = "CharitemTable.findById", query = "SELECT c FROM CharitemTable c WHERE c.id = :id"),
    @NamedQuery(name = "CharitemTable.findByWearing", query = "SELECT c FROM CharitemTable c WHERE c.wearing = :wearing")
})
public class CharitemTable implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Column(name = "wearing")
    private Integer wearing;
    @JoinColumn(name = "belongsto", referencedColumnName = "name")
    @ManyToOne(optional = false)
    private Person belongsto;
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Item item;

    public CharitemTable()
    {
    }

    public CharitemTable(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getWearing()
    {
        return wearing;
    }

    public void setWearing(Integer wearing)
    {
        this.wearing = wearing;
    }

    public Person getBelongsto()
    {
        return belongsto;
    }

    public void setBelongsto(Person belongsto)
    {
        this.belongsto = belongsto;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CharitemTable))
        {
            return false;
        }
        CharitemTable other = (CharitemTable) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.CharitemTable[ id=" + id + " ]";
    }

}
