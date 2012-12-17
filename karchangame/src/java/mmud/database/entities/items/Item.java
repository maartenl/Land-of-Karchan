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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import mmud.Attributes;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Room;
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Attribute;
import mmud.database.entities.game.DisplayInterface;
import mmud.exceptions.ItemException;
import mmud.exceptions.MudException;
import mmud.database.entities.game.AttributeWrangler;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import org.hibernate.annotations.Cascade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An item. To be more precise an instance of an item definition.
 * An item can either reside in a room, on a person or in another item.
 * @author maartenl
 */
// TODO: create subclass named ContainerItem.
// TODO: fix named queries.
@Entity
@Table(name = "mm_itemtable")
@NamedQueries(
{
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findById", query = "SELECT i FROM Item i WHERE i.id = :id"),
    @NamedQuery(name = "Item.drop", query = "UPDATE Item i SET i.belongsto = null, i.room = :room WHERE i = :item and i.belongsto = :person and i.room is null and i.container is null"),// and i.itemDefinition.dropable <> 0"),
    @NamedQuery(name = "Item.get", query = "UPDATE Item i SET i.room = null, i.belongsto = :person WHERE i = :item and i.belongsto is null and i.room = :room and i.container is null"),//  and i.itemDefinition.getable <> 0")
    @NamedQuery(name = "Item.give", query = "UPDATE Item i SET i.belongsto = :toperson WHERE i = :item and i.belongsto = :fromperson and i.room is null and i.container is null")//  and i.itemDefinition.getable <> 0")
})
public class Item implements Serializable, DisplayInterface, AttributeWrangler, ItemWrangler
{

    private static final Logger itsLog = LoggerFactory.getLogger(Item.class);
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
    // TODO orphanRemoval=true in JPA2 should work to replace this hibernate specific DELETE_ORPHAN.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "container")
    @Cascade(
    {
        org.hibernate.annotations.CascadeType.DELETE_ORPHAN
    })
    private Set<Item> items;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    @Cascade(
    {
        org.hibernate.annotations.CascadeType.DELETE_ORPHAN
    })
    private List<Itemattribute> itemattributeCollection;

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
    public Set<Item> getItems()
    {
        return items;
    }

    public void setItems(Set<Item> items)
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

    public boolean isContainer()
    {
        return getItemDefinition().isContainer();
    }

    public void setContainer(Item container)
    {
        if (!container.isContainer())
        {
            throw new ItemException("Item  is not a container.");
        }
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

    /**
     * Description of the item, for example "a white, cloth pants".
     * @return a description of the item.
     */
    public String getDescription()
    {
        return getItemDefinition().getDescription();
    }

    /**
     * @see ItemDefinition#isDescribedBy(java.util.List)
     * @param parsed
     * @return
     */
    public boolean isDescribedBy(List<String> parsed)
    {
        return getItemDefinition().isDescribedBy(parsed);
    }

    public String getTitle() throws MudException
    {
        if (getItemDefinition().getTitle() != null)
        {
            return getItemDefinition().getTitle();
        }
        return getItemDefinition().getDescription();
    }

    @Override
    public String getMainTitle() throws MudException
    {
        return getTitle();
    }

    @Override
    public String getImage() throws MudException
    {
        return getItemDefinition().getImage();
    }

    @Override
    public String getBody() throws MudException
    {
        StringBuilder builder = new StringBuilder(getItemDefinition().getLongDescription());
        if (isDrinkable())
        {
            builder.append("You can try drinking it.<br/>\r\n");
        }
        if (isEatable())
        {
            builder.append("You can try eating it.<br/>\r\n");
        }
        int wieldable = getItemDefinition().getWieldable();
        int wearable = getItemDefinition().getWearable();
        for (Wielding wield : Wielding.values())
        {
            if (Wielding.isIn(wieldable, wield))
            {
                builder.append("It can be wielded ").append(wield.toString().replace("%SHISHER", "your")).append(".<br/>\r\n");
            }
        }
        for (Wearing wear : Wearing.values())
        {
            if (Wearing.isIn(wearable, wear))
            {
                builder.append("It can be worn ").append(wear.toString().replace("%SHISHER", "your")).append(".<br/>\r\n");
            }
        }
        return builder.toString();
    }

    @Override
    public boolean removeAttribute(String name)
    {
        Itemattribute attr = getItemattribute(name);
        if (attr == null)
        {
            return false;
        }
        itemattributeCollection.remove(attr);
        return true;
    }

    private Itemattribute getItemattribute(String name)
    {
        if (itemattributeCollection == null)
        {
            itsLog.debug("getItemattribute name=" + name + " collection is null");
            return null;
        }
        for (Itemattribute attr : itemattributeCollection)
        {
            itsLog.debug("getItemattribute name=" + name + " attr=" + attr);
            if (attr.getName().equals(name))
            {
                return attr;
            }
        }
        itsLog.debug("getItemattribute name=" + name + " not found");
        return null;
    }

    @Override
    public Attribute getAttribute(String name)
    {
        return getItemattribute(name);
    }

    @Override
    public void setAttribute(String name, String value)
    {
        Itemattribute attr = getItemattribute(name);
        if (attr == null)
        {
            attr = new Itemattribute(name, getId());
            attr.setItem(this);
        }
        attr.setValue(value);
        attr.setValueType(Attributes.VALUETYPE_STRING);
        itemattributeCollection.add(attr);
    }

    @Override
    public boolean verifyAttribute(String name, String value)
    {
        Itemattribute attr = getItemattribute(name);
        if (attr == null)
        {
            itsLog.debug("verifyAttribute (name=" + name + ", value=" + value + ") not found on item " + getId() + ".");
            return false;
        }
        if (attr.getValue() == value)
        {
            itsLog.debug("verifyAttribute (name=" + name + ", value=" + value + ") same object on item " + getId() + "!");
            return true;
        }
        if (attr.getValue().equals(value))
        {
            itsLog.debug("verifyAttribute (name=" + name + ", value=" + value + ") matches on item " + getId() + "!");
            return true;
        }
        itsLog.debug("verifyAttribute (name=" + name + ", value=" + value + ") with (name=" + attr.getName() + ", value=" + attr.getValue() + ") no match on item " + getId() + ".");
        return false;
    }

    /**
     * Indicates if the container is open or closed.
     * Only makes sense if this item is in fact a container.
     * @return
     */
    public boolean isOpen()
    {
        return verifyAttribute("isopen", "true");
    }

    /**
     * Returns true if the container has a lid, if the
     * container can be opened.
     *
     * @return boolean true if the container has a lid.
     */
    public boolean hasLid()
    {
        return isOpenable();
    }

    /**
     * Returns true if the container has a lid, if the
     * container can be opened.
     *
     * @return boolean true if the container has a lid.
     */
    public boolean isOpenable()
    {
        if (getAttribute("isopenable") != null)
        {
            return verifyAttribute("isopenable", "true");
        }
        return getItemDefinition().isOpenable();
    }

    /**
     * Returns whether or not the container is locked. If it can be locked, it
     * needs a key.
     *
     * @return boolean true if the container is locked.
     * @see #getKey()
     */
    public boolean isLocked()
    {
        return verifyAttribute("islocked", "true");
    }

    public void open()
    {
        if (!isContainer())
        {
            throw new ItemException("Item is not a container, and cannot be opened.");
        }
        if (!isOpenable())
        {
            throw new ItemException(getDescription()
                    + "cannot be opened.");
        }
        if (isOpen())
        {
            throw new ItemException(getDescription()
                    + " is already open.");
        }
        if (isLocked())
        {
            throw new ItemException(getDescription()
                    + " is locked.");
        }
        setAttribute("isopen", "true");
    }

    public void close()
    {
        if (!isContainer())
        {
            throw new ItemException("Item is not a container, and cannot be closed.");
        }
        if (!isOpenable())
        {
            throw new ItemException(getDescription()
                    + "cannot be closed.");
        }
        if (!isOpen())
        {
            throw new ItemException(getDescription()
                    + " is already closed.");
        }
        if (isLocked())
        {
            throw new ItemException(getDescription()
                    + " is locked.");
        }
        setAttribute("isopen", "false");
    }

    /**
     * Returns true if the item can be eaten. This is better than just using
     * a compare on the getEatable.
     * @return true if eatable, false otherwise
     * @see #getEatable
     */
    public boolean isEatable()
    {
        if (getAttribute("eatable") != null)
        {
            return verifyAttribute("eatable", "true");
        }
        return getItemDefinition().getEatable() != null && !getItemDefinition().getEatable().trim().equals("");
    }

    public String getEatable()
    {
        return getItemDefinition().getEatable();
    }

    @Override
    public List<Item> findItems(List<String> parsed)
    {
        List<Item> result = new ArrayList<Item>();
        for (Item item : getItems())
        {
            if (item.isDescribedBy(parsed))
            {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public boolean destroyItem(Item item)
    {
        return items.remove(item);
        // note: as the collection is an orphan, the delete
        // on the set will take place automatically.
    }

    /**
     * Returns true if the item can be drunk. This is better than just using
     * a compare on the getDrinkable.
     * @return true if drinkable, false otherwise
     * @see #getDrinkable
     */
    public boolean isDrinkable()
    {
        if (getAttribute("drinkable") != null)
        {
            return verifyAttribute("drinkable", "true");
        }
        return getItemDefinition().getDrinkable() != null && !getItemDefinition().getDrinkable().trim().equals("");
    }

    public String getDrinkable()
    {
        return getItemDefinition().getDrinkable();

    }

    public boolean isWearable(Wearing position)
    {
        return Wearing.isIn(getItemDefinition().getWearable(), position);
    }

    public boolean isWieldable(Wielding position)
    {
        return Wielding.isIn(getItemDefinition().getWieldable(), position);
    }

    /**
     * Whether or not you are able to drop this item.
     * @return true, in case you can, false otherwise.
     */
    public boolean isDroppable()
    {
        if (getAttribute("notdropable") != null)
        {
            return !verifyAttribute("notdropable", "true");
        }
        return getItemDefinition().getDropable();
    }


    /**
     * Whether or not you are able to drop this item or give this item, or in
     * some other way dispose of this item, besides selling it to a vendor or
     * just destroying it.
     * @return true, in case you cannot, false otherwise.
     */
    public boolean isBound()
    {
        if (getAttribute("bound") != null)
        {
            return verifyAttribute("bound", "true");
        }
        return getItemDefinition().isBound();
    }

    /**
     * Whether or not you are able to retrieve this item from the floor of the room.
     * @return true, in case you can, false otherwise.
     */
    public boolean isGetable()
    {
        if (getAttribute("notgetable") != null)
        {
            return !verifyAttribute("notgetable", "true");
        }
        return getItemDefinition().getGetable();
    }

    public void drop(Person person, Room room)
    {
        if (getBelongsTo() == null || !getBelongsTo().equals(person))
        {
            throw new ItemException("Cannot drop the item, it's not yours.");
        }
        // TODO: equals directly on room doesn't seem to work right. Different objects
        // same ids.
        if (!person.getRoom().getId().equals(room.getId()))
        {
            throw new ItemException("You are not in the room.");
        }
        belongsto = null;
        this.room = room;
    }

    public void get(Person person, Room room)
    {
        if (getRoom() == null || !getRoom().equals(room))
        {
            throw new ItemException("Item not in the room.");
        }
        if (!person.getRoom().equals(room))
        {
            throw new ItemException("You are not in the room.");
        }
        belongsto = person;
        this.room = null;
    }
}
