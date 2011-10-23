/*
 * Copyright (C) 2011 maartenl
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
package mmud.database.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
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
@Table(name = "mm_rooms")
@NamedQueries(
{
    @NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r"),
    @NamedQuery(name = "Room.findById", query = "SELECT r FROM Room r WHERE r.id = :id"),
    @NamedQuery(name = "Room.findByCreation", query = "SELECT r FROM Room r WHERE r.creation = :creation"),
    @NamedQuery(name = "Room.findByTitle", query = "SELECT r FROM Room r WHERE r.title = :title"),
    @NamedQuery(name = "Room.findByPicture", query = "SELECT r FROM Room r WHERE r.picture = :picture")
})
public class Room implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "contents")
    private String contents;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 120)
    @Column(name = "title")
    private String title;
    @Size(max = 120)
    @Column(name = "picture")
    private String picture;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;
    @OneToMany(mappedBy = "down")
    private Set<Room> roomSet;
    @JoinColumn(name = "down", referencedColumnName = "id")
    @ManyToOne
    private Room down;
    @OneToMany(mappedBy = "up")
    private Set<Room> roomSet1;
    @JoinColumn(name = "up", referencedColumnName = "id")
    @ManyToOne
    private Room up;
    @OneToMany(mappedBy = "west")
    private Set<Room> roomSet2;
    @JoinColumn(name = "west", referencedColumnName = "id")
    @ManyToOne
    private Room west;
    @OneToMany(mappedBy = "east")
    private Set<Room> roomSet3;
    @JoinColumn(name = "east", referencedColumnName = "id")
    @ManyToOne
    private Room east;
    @OneToMany(mappedBy = "south")
    private Set<Room> roomSet4;
    @JoinColumn(name = "south", referencedColumnName = "id")
    @ManyToOne
    private Room south;
    @OneToMany(mappedBy = "north")
    private Set<Room> roomSet5;
    @JoinColumn(name = "north", referencedColumnName = "id")
    @ManyToOne
    private Room north;
    @JoinColumn(name = "area", referencedColumnName = "area")
    @ManyToOne(optional = false)
    private Area area;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
    private Set<Roomattribute> roomattributeSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
    private Set<Roomitem> roomitemSet;

    public Room()
    {
    }

    public Room(Integer id)
    {
        this.id = id;
    }

    public Room(Integer id, String contents, Date creation, String title)
    {
        this.id = id;
        this.contents = contents;
        this.creation = creation;
        this.title = title;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
    }

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPicture()
    {
        return picture;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
    }

    public Set<Room> getRoomSet()
    {
        return roomSet;
    }

    public void setRoomSet(Set<Room> roomSet)
    {
        this.roomSet = roomSet;
    }

    public Room getDown()
    {
        return down;
    }

    public void setDown(Room down)
    {
        this.down = down;
    }

    public Set<Room> getRoomSet1()
    {
        return roomSet1;
    }

    public void setRoomSet1(Set<Room> roomSet1)
    {
        this.roomSet1 = roomSet1;
    }

    public Room getUp()
    {
        return up;
    }

    public void setUp(Room up)
    {
        this.up = up;
    }

    public Set<Room> getRoomSet2()
    {
        return roomSet2;
    }

    public void setRoomSet2(Set<Room> roomSet2)
    {
        this.roomSet2 = roomSet2;
    }

    public Room getWest()
    {
        return west;
    }

    public void setWest(Room west)
    {
        this.west = west;
    }

    public Set<Room> getRoomSet3()
    {
        return roomSet3;
    }

    public void setRoomSet3(Set<Room> roomSet3)
    {
        this.roomSet3 = roomSet3;
    }

    public Room getEast()
    {
        return east;
    }

    public void setEast(Room east)
    {
        this.east = east;
    }

    public Set<Room> getRoomSet4()
    {
        return roomSet4;
    }

    public void setRoomSet4(Set<Room> roomSet4)
    {
        this.roomSet4 = roomSet4;
    }

    public Room getSouth()
    {
        return south;
    }

    public void setSouth(Room south)
    {
        this.south = south;
    }

    public Set<Room> getRoomSet5()
    {
        return roomSet5;
    }

    public void setRoomSet5(Set<Room> roomSet5)
    {
        this.roomSet5 = roomSet5;
    }

    public Room getNorth()
    {
        return north;
    }

    public void setNorth(Room north)
    {
        this.north = north;
    }

    public Area getArea()
    {
        return area;
    }

    public void setArea(Area area)
    {
        this.area = area;
    }

    public Set<Roomattribute> getRoomattributeSet()
    {
        return roomattributeSet;
    }

    public void setRoomattributeSet(Set<Roomattribute> roomattributeSet)
    {
        this.roomattributeSet = roomattributeSet;
    }

    public Set<Roomitem> getRoomitemSet()
    {
        return roomitemSet;
    }

    public void setRoomitemSet(Set<Roomitem> roomitemSet)
    {
        this.roomitemSet = roomitemSet;
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
        if (!(object instanceof Room))
        {
            return false;
        }
        Room other = (Room) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Room[ id=" + id + " ]";
    }

}
