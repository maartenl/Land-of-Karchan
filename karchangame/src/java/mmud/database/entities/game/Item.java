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
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_itemtable", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findById", query = "SELECT i FROM Item i WHERE i.id = :id"),
    @NamedQuery(name = "Item.findByCreation", query = "SELECT i FROM Item i WHERE i.creation = :creation")
})
public class Item implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;
    @JoinColumn(name = "itemid", referencedColumnName = "id")
    @ManyToOne
    private ItemDefinition itemid;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "item")
    private CharitemTable charitemTable;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "containerid")
    private Collection<ItemitemTable> itemitemTableCollection;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "item")
    private ItemitemTable itemitemTable;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    private Collection<Itemattribute> itemattributeCollection;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "item")
    private RoomitemTable roomitemTable;

    public Item()
    {
    }

    public Item(Integer id)
    {
        this.id = id;
    }

    public Item(Integer id, Date creation)
    {
        this.id = id;
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

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
    }

    public ItemDefinition getItemid()
    {
        return itemid;
    }

    public void setItemid(ItemDefinition itemid)
    {
        this.itemid = itemid;
    }

    public CharitemTable getCharitemTable()
    {
        return charitemTable;
    }

    public void setCharitemTable(CharitemTable charitemTable)
    {
        this.charitemTable = charitemTable;
    }

    public Collection<ItemitemTable> getItemitemTableCollection()
    {
        return itemitemTableCollection;
    }

    public void setItemitemTableCollection(Collection<ItemitemTable> itemitemTableCollection)
    {
        this.itemitemTableCollection = itemitemTableCollection;
    }

    public ItemitemTable getItemitemTable()
    {
        return itemitemTable;
    }

    public void setItemitemTable(ItemitemTable itemitemTable)
    {
        this.itemitemTable = itemitemTable;
    }

    public Collection<Itemattribute> getItemattributeCollection()
    {
        return itemattributeCollection;
    }

    public void setItemattributeCollection(Collection<Itemattribute> itemattributeCollection)
    {
        this.itemattributeCollection = itemattributeCollection;
    }

    public RoomitemTable getRoomitemTable()
    {
        return roomitemTable;
    }

    public void setRoomitemTable(RoomitemTable roomitemTable)
    {
        this.roomitemTable = roomitemTable;
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
        if (!(object instanceof Item))
        {
            return false;
        }
        Item other = (Item) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Item[ id=" + id + " ]";
    }

}
