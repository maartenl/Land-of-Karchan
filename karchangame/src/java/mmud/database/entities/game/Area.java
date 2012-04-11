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
@Table(name = "mm_area", catalog = "mmud", schema = "")
@NamedQueries(
{
    @NamedQuery(name = "Area.findAll", query = "SELECT a FROM Area a"),
    @NamedQuery(name = "Area.findByArea", query = "SELECT a FROM Area a WHERE a.area = :area"),
    @NamedQuery(name = "Area.findByShortdesc", query = "SELECT a FROM Area a WHERE a.shortdesc = :shortdesc"),
    @NamedQuery(name = "Area.findByCreation", query = "SELECT a FROM Area a WHERE a.creation = :creation")
})
public class Area implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 49)
    @Column(name = "area")
    private String area;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "shortdesc")
    private String shortdescription;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "area")
    private Collection<Room> roomCollection;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;

    public Area()
    {
    }

    public Area(String area)
    {
        this.area = area;
    }

    public Area(String area, String description, String shortdesc, Date creation)
    {
        this.area = area;
        this.description = description;
        this.shortdescription = shortdesc;
        this.creation = creation;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getShortdescription()
    {
        return shortdescription;
    }

    public void setShortdescription(String shortdescription)
    {
        this.shortdescription = shortdescription;
    }

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public Collection<Room> getRoomCollection()
    {
        return roomCollection;
    }

    public void setRoomCollection(Collection<Room> roomCollection)
    {
        this.roomCollection = roomCollection;
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
        hash += (area != null ? area.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Area))
        {
            return false;
        }
        Area other = (Area) object;
        if ((this.area == null && other.area != null) || (this.area != null && !this.area.equals(other.area)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Area[ area=" + area + " ]";
    }

}
