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

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_items", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "ItemDefinition.findAll", query = "SELECT i FROM ItemDefinition i"),
    @NamedQuery(name = "ItemDefinition.findById", query = "SELECT i FROM ItemDefinition i WHERE i.id = :id"),
    @NamedQuery(name = "ItemDefinition.findByName", query = "SELECT i FROM ItemDefinition i WHERE i.name = :name"),
    @NamedQuery(name = "ItemDefinition.findByAdject1", query = "SELECT i FROM ItemDefinition i WHERE i.adject1 = :adject1"),
    @NamedQuery(name = "ItemDefinition.findByAdject2", query = "SELECT i FROM ItemDefinition i WHERE i.adject2 = :adject2"),
    @NamedQuery(name = "ItemDefinition.findByAdject3", query = "SELECT i FROM ItemDefinition i WHERE i.adject3 = :adject3"),
    @NamedQuery(name = "ItemDefinition.findByManaincrease", query = "SELECT i FROM ItemDefinition i WHERE i.manaincrease = :manaincrease"),
    @NamedQuery(name = "ItemDefinition.findByHitincrease", query = "SELECT i FROM ItemDefinition i WHERE i.hitincrease = :hitincrease"),
    @NamedQuery(name = "ItemDefinition.findByVitalincrease", query = "SELECT i FROM ItemDefinition i WHERE i.vitalincrease = :vitalincrease"),
    @NamedQuery(name = "ItemDefinition.findByMovementincrease", query = "SELECT i FROM ItemDefinition i WHERE i.movementincrease = :movementincrease"),
    @NamedQuery(name = "ItemDefinition.findByRoom", query = "SELECT i FROM ItemDefinition i WHERE i.room = :room"),
    @NamedQuery(name = "ItemDefinition.findByLightable", query = "SELECT i FROM ItemDefinition i WHERE i.lightable = :lightable"),
    @NamedQuery(name = "ItemDefinition.findByGetable", query = "SELECT i FROM ItemDefinition i WHERE i.getable = :getable"),
    @NamedQuery(name = "ItemDefinition.findByDropable", query = "SELECT i FROM ItemDefinition i WHERE i.dropable = :dropable"),
    @NamedQuery(name = "ItemDefinition.findByVisible", query = "SELECT i FROM ItemDefinition i WHERE i.visible = :visible"),
    @NamedQuery(name = "ItemDefinition.findByWieldable", query = "SELECT i FROM ItemDefinition i WHERE i.wieldable = :wieldable"),
    @NamedQuery(name = "ItemDefinition.findByWearable", query = "SELECT i FROM ItemDefinition i WHERE i.wearable = :wearable"),
    @NamedQuery(name = "ItemDefinition.findByCopper", query = "SELECT i FROM ItemDefinition i WHERE i.copper = :copper"),
    @NamedQuery(name = "ItemDefinition.findByWeight", query = "SELECT i FROM ItemDefinition i WHERE i.weight = :weight"),
    @NamedQuery(name = "ItemDefinition.findByPasdefense", query = "SELECT i FROM ItemDefinition i WHERE i.pasdefense = :pasdefense"),
    @NamedQuery(name = "ItemDefinition.findByDamageresistance", query = "SELECT i FROM ItemDefinition i WHERE i.damageresistance = :damageresistance"),
    @NamedQuery(name = "ItemDefinition.findByContainer", query = "SELECT i FROM ItemDefinition i WHERE i.container = :container"),
    @NamedQuery(name = "ItemDefinition.findByCreation", query = "SELECT i FROM ItemDefinition i WHERE i.creation = :creation"),
    @NamedQuery(name = "ItemDefinition.findByCapacity", query = "SELECT i FROM ItemDefinition i WHERE i.capacity = :capacity"),
    @NamedQuery(name = "ItemDefinition.findByIsopenable", query = "SELECT i FROM ItemDefinition i WHERE i.isopenable = :isopenable"),
    @NamedQuery(name = "ItemDefinition.findByKeyid", query = "SELECT i FROM ItemDefinition i WHERE i.keyid = :keyid"),
    @NamedQuery(name = "ItemDefinition.findByContaintype", query = "SELECT i FROM ItemDefinition i WHERE i.containtype = :containtype")
})
public class ItemDefinition implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
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
    private String readdescr;
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
    @OneToMany(mappedBy = "itemid")
    private Collection<Item> itemCollection;
    @OneToMany(mappedBy = "itemId")
    private Collection<Mail> mailCollection;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;

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

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAdject1()
    {
        return adject1;
    }

    public void setAdject1(String adject1)
    {
        this.adject1 = adject1;
    }

    public String getAdject2()
    {
        return adject2;
    }

    public void setAdject2(String adject2)
    {
        this.adject2 = adject2;
    }

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

    public Integer getGetable()
    {
        return getable;
    }

    public void setGetable(Integer getable)
    {
        this.getable = getable;
    }

    public Integer getDropable()
    {
        return dropable;
    }

    public void setDropable(Integer dropable)
    {
        this.dropable = dropable;
    }

    public Integer getVisible()
    {
        return visible;
    }

    public void setVisible(Integer visible)
    {
        this.visible = visible;
    }

    public Integer getWieldable()
    {
        return wieldable;
    }

    public void setWieldable(Integer wieldable)
    {
        this.wieldable = wieldable;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getReaddescr()
    {
        return readdescr;
    }

    public void setReaddescr(String readdescr)
    {
        this.readdescr = readdescr;
    }

    public Integer getWearable()
    {
        return wearable;
    }

    public void setWearable(Integer wearable)
    {
        this.wearable = wearable;
    }

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

}
