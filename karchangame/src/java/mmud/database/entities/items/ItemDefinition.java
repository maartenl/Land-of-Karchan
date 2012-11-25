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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import mmud.Constants;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Mail;

/**
 * The definition of an item. The analogy with Java would be the difference
 * between a class and an object.
 * @author maartenl
 */
@Entity
@Table(name = "mm_items", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "ItemDefinition.findAll", query = "SELECT i FROM ItemDefinition i"),
    @NamedQuery(name = "ItemDefinition.findById", query = "SELECT i FROM ItemDefinition i WHERE i.id = :id"),
    @NamedQuery(name = "ItemDefinition.maxid", query = "SELECT max(id) FROM ItemDefinition i")
})
public class ItemDefinition implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    // TODO : fix this, make it automatically get an id.
    @Column(name = "id")
    private Integer id;
    @Size(max = 100)
    @Column(name = "name")
    private String name;
    @Size(max = 30)
    @Column(name = "adject1")
    private String adject1;
    @Size(max = 30)
    @Column(name = "adject2")
    private String adject2;
    @Size(max = 30)
    @Column(name = "adject3")
    private String adject3;
    @Column(name = "manaincrease")
    private Integer manaincrease;
    @Column(name = "hitincrease")
    private Integer hitincrease;
    @Column(name = "vitalincrease")
    private Integer vitalincrease;
    @Column(name = "movementincrease")
    private Integer movementincrease;
    @Lob
    @Size(max = 65535)
    @Column(name = "eatable")
    private String eatable;
    @Lob
    @Size(max = 65535)
    @Column(name = "drinkable")
    private String drinkable;
    @Column(name = "room")
    private Integer room;
    @Column(name = "lightable")
    private Integer lightable;
    @Column(name = "getable")
    private Integer getable;
    @Column(name = "dropable")
    private Integer dropable;
    @Column(name = "visible")
    private Integer visible;
    @Column(name = "wieldable")
    private Integer wieldable;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @Lob
    @Size(max = 65535)
    @Column(name = "readdescr")
    private String readdescription;
    @Column(name = "wearable")
    private Integer wearable;
    @Column(name = "copper")
    private Integer copper;
    @Basic(optional = false)
    @NotNull
    @Column(name = "weight")
    private int weight;
    @Column(name = "pasdefense")
    private Integer pasdefense;
    @Column(name = "damageresistance")
    private Integer damageresistance;
    @Basic(optional = false)
    @NotNull
    @Column(name = "container")
    private int container;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @Column(name = "capacity")
    private Integer capacity;
    @Basic(optional = false)
    @NotNull
    @Column(name = "isopenable")
    private int isopenable;
    @Column(name = "keyid")
    private Integer keyid;
    @Column(name = "containtype")
    private Integer containtype;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @OneToMany(mappedBy = "itemDefinition")
    private Collection<Item> itemCollection;
    @OneToMany(mappedBy = "itemDefinition")
    private Collection<Mail> mailCollection;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;
    @Column(name = "image")
    private String image;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    @Column(name = "title")
    private String title;

    public ItemDefinition()
    {
    }

    public ItemDefinition(Integer id)
    {
        this.id = id;
    }

    public ItemDefinition(Integer id, String description, int weight, int container, Date creation, int isopenable)
    {
        this.id = id;
        this.description = description;
        this.weight = weight;
        this.container = container;
        this.creation = creation;
        this.isopenable = isopenable;
    }

    /**
     * Return the id.
     *
     * @return integer containing the identification number of the item
     *         definition.
     */
    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * Return the noun.
     *
     * @return String containing the noun of the item definition.
     */
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Return the first adjective.
     *
     * @return String containing the first adjective of the item definition.
     */
    public String getAdject1()
    {
        return adject1;
    }

    public void setAdject1(String adject1)
    {
        this.adject1 = adject1;
    }

    /**
     * Return the second adjective.
     *
     * @return String containing the second adjective of the item definition.
     */
    public String getAdject2()
    {
        return adject2;
    }

    public void setAdject2(String adject2)
    {
        this.adject2 = adject2;
    }

    /**
     * Return the third adjective.
     *
     * @return String containing the third adjective of the item definition.
     */
    public String getAdject3()
    {
        return adject3;
    }

    public void setAdject3(String adject3)
    {
        this.adject3 = adject3;
    }

    public Integer getManaincrease()
    {
        return manaincrease;
    }

    public void setManaincrease(Integer manaincrease)
    {
        this.manaincrease = manaincrease;
    }

    public Integer getHitincrease()
    {
        return hitincrease;
    }

    public void setHitincrease(Integer hitincrease)
    {
        this.hitincrease = hitincrease;
    }

    public Integer getVitalincrease()
    {
        return vitalincrease;
    }

    public void setVitalincrease(Integer vitalincrease)
    {
        this.vitalincrease = vitalincrease;
    }

    public Integer getMovementincrease()
    {
        return movementincrease;
    }

    public void setMovementincrease(Integer movementincrease)
    {
        this.movementincrease = movementincrease;
    }

    public String getEatable()
    {
        return eatable;
    }

    public void setEatable(String eatable)
    {
        this.eatable = eatable;
    }

    public String getDrinkable()
    {
        return drinkable;
    }

    public void setDrinkable(String drinkable)
    {
        this.drinkable = drinkable;
    }

    public Integer getRoom()
    {
        return room;
    }

    public void setRoom(Integer room)
    {
        this.room = room;
    }

    public Integer getLightable()
    {
        return lightable;
    }

    public void setLightable(Integer lightable)
    {
        this.lightable = lightable;
    }

    /**
     * You can or cannot retrieve this item.
     * @return true if you can retrieve the item, for example from the floor.
     */
    public Boolean getGetable()
    {
        if (getId() < 0)
        {
            return false;
        }
        return Integer.valueOf(1).equals(getable);
    }

    public void setGetable(Boolean getable)
    {
        if (getId() < 0)
        {
            this.getable = 0;
            return;
        }
        if (getable != null)
        {
            this.getable = getable ? 1 : 0;
            return;
        }
        this.getable = null;
    }

    /**
     * You can or cannot drop this item
     * @return  true if you can drop the item, for example on the floor.
     */
    public Boolean getDropable()
    {
        if (getId() < 0)
        {
            return false;
        }
        return Integer.valueOf(1).equals(dropable);
    }

    public void setDropable(Boolean dropable)
    {
        if (getId() < 0)
        {
            this.dropable = 0;
            return;
        }
        if (dropable != null)
        {
            this.dropable = dropable ? 1 : 0;
            return;
        }
        this.dropable = null;
    }

    /**
     * Indicates if the item is visible to people.
     * @return true if the item is visible.
     */
    public Boolean getVisible()
    {
        return Integer.valueOf(1).equals(visible);
    }

    public void setVisible(Boolean visible)
    {

        if (visible != null)
        {
            this.visible = visible ? 1 : 0;
            return;
        }
        this.visible = null;
    }

    public Integer getWieldable()
    {
        return wieldable;
    }

    public void setWieldable(Integer wieldable)
    {
        this.wieldable = wieldable;
    }

    /**
     * A full and long description of an item. Usually quite a lot of text.
     * @return
     */
    public String getLongDescription()
    {
        return description;
    }

    public void setLongDescription(String description)
    {
        this.description = description;
    }

    public String getReaddescription()
    {
        return readdescription;
    }

    public void setReaddescription(String readdescr)
    {
        this.readdescription = readdescr;
    }

    public Integer getWearable()
    {
        return wearable;
    }

    public void setWearable(Integer wearable)
    {
        this.wearable = wearable;
    }

    /**
     * Return the amount of money, in copper coins, it costs.
     *
     * @return integer containing number of copper coins.
     */
    public Integer getCopper()
    {
        return copper;
    }

    public void setCopper(Integer copper)
    {
        this.copper = copper;
    }

    public int getWeight()
    {
        return weight;
    }

    public void setWeight(int weight)
    {
        this.weight = weight;
    }

    public Integer getPasdefense()
    {
        return pasdefense;
    }

    public void setPasdefense(Integer pasdefense)
    {
        this.pasdefense = pasdefense;
    }

    public Integer getDamageresistance()
    {
        return damageresistance;
    }

    public void setDamageresistance(Integer damageresistance)
    {
        this.damageresistance = damageresistance;
    }

    public int getContainer()
    {
        return container;
    }

    public void setContainer(int container)
    {
        this.container = container;
    }

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public Integer getCapacity()
    {
        return capacity;
    }

    public void setCapacity(Integer capacity)
    {
        this.capacity = capacity;
    }

    public int getIsopenable()
    {
        return isopenable;
    }

    public void setIsopenable(int isopenable)
    {
        this.isopenable = isopenable;
    }

    public Integer getKeyid()
    {
        return keyid;
    }

    public void setKeyid(Integer keyid)
    {
        this.keyid = keyid;
    }

    public Integer getContaintype()
    {
        return containtype;
    }

    public void setContaintype(Integer containtype)
    {
        this.containtype = containtype;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public Collection<Item> getItemCollection()
    {
        return itemCollection;
    }

    public void setItemCollection(Collection<Item> itemCollection)
    {
        this.itemCollection = itemCollection;
    }

    public Collection<Mail> getMailCollection()
    {
        return mailCollection;
    }

    public void setMailCollection(Collection<Mail> mailCollection)
    {
        this.mailCollection = mailCollection;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
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
        if (!(object instanceof ItemDefinition))
        {
            return false;
        }
        ItemDefinition other = (ItemDefinition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.ItemDefinition[ id=" + id + " ]";
    }

    /**
     * @see Constants#getDescriptionOfItem(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     * @return
     */
    public String getDescription()
    {
        return Constants.getDescriptionOfItem(getAdject1(), getAdject2(), getAdject3(), getName(), true);
    }

    /**
     * Determines if this item is described by the list of strings.
     * The list of strings can be in the following format:
     * <ul><li>hammer</li><li>strong hammer</li><li>strong iron hammer</li><li>
     * strong heavy iron hammer</li></ul>
     * @param parsed the list of strings.
     * @return if this item corresponds to the description.
     */
    boolean isDescribedBy(List<String> parsed)
    {
        return Constants.compareItemDescription(parsed, getAdject1(), getAdject2(), getAdject3(), getName());
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }


}
