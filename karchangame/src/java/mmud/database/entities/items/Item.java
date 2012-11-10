/*
 * Copyright (C) 2012 maartenl
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Room;
import mmud.database.entities.characters.Person;

/**
 * An item. To be more precise an instance of an item definition.
 * An item can either reside in a room, on a person or in another item.
 * @author maartenl
 */
@Entity
@Table(name = "mm_itemtable")
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
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @OneToMany(mappedBy = "container")
    private Collection<Item> items;
    @JoinColumn(name = "containerid", referencedColumnName = "id")
    @ManyToOne
    private Item container;
    @ManyToOne
    @JoinColumn(name = "room", referencedColumnName = "id")
    private Room room;
    @ManyToOne
    @JoinColumn(name = "belongsto", referencedColumnName = "name")
    private Person belongsto;
    @ManyToOne
    @JoinColumn(name = "itemid", referencedColumnName = "id")
    private ItemDefinition itemDefinition;
    @ManyToOne
    @JoinColumn(name = "owner", referencedColumnName = "name")
    private Admin owner;

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

    /**
     * Indicates the items that are contained (if possible) inside this item.
     * Will in most cases return an empty list.
     *
     * @return
     */
    public Collection<Item> getItems()
    {
        return items;
    }

    public void setItems(Collection<Item> items)
    {
        this.items = items;
    }

    /**
     * Retrieves the item that contains this item (if possible). Might be null.
     *
     * @return
     * @see #getBelongsTo()
     * @see #getRoom()
     */
    public Item getContainer()
    {
        return container;
    }

    public void setContainer(Item container)
    {
        this.container = container;
    }

    public ItemDefinition getItemDefinition()
    {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition)
    {
        this.itemDefinition = itemDefinition;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
    }

    /**
     * Will return the room in which this item can be found. Might be null.
     * @see #getBelongsto()
     * @see #getContainer()
     */
    public Room getRoom()
    {
        return room;
    }

    /**
     * Will return the person that owns this item. Might be null.
     * @see #getRoom()
     * @see #getContainer()
     */
    public Person getBelongsTo()
    {
        return belongsto;
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
        return "mmud.database.entities.items.Item[ id=" + id + " ]";
    }

    public String getDescription()
    {
        return getItemDefinition().getDescription();
    }
}
