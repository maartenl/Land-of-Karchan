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
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A room. Bear in mind that this room has potential exits to the north, south,
 * east, west, up and down, which are also rooms. The structure forms a kind of
 * graph. In order for Hibernate/JPA not to load the entire structure eagerly due to ManyToOne
 * annotations,
 * it is required that the hibernate.cfg.xml file has hibernate.ejb.use_class_enhancer
 * set to true. Also means that Hibernate as JPA provider should be used
 * in full-blown JEE environment.
 * @see http://justonjava.blogspot.com/2010_09_01_archive.html
 * @see http://docs.jboss.org/hibernate/stable/entitymanager/reference/en/html/configuration.html
 * @author maartenl
 */
@Entity
@Table(name = "mm_rooms", catalog = "mmud", schema = "")
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

    /**
     * The first room that new characters appear in.
     */
    public static final Integer STARTERS_ROOM = 1;

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
    private Collection<Room> roomCollection;

    @JoinColumn(name = "down", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room down;

    @OneToMany(mappedBy = "up")
    private Collection<Room> roomCollection1;

    @JoinColumn(name = "up", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room up;

    @OneToMany(mappedBy = "west")
    private Collection<Room> roomCollection2;

    @JoinColumn(name = "west", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room west;

    @OneToMany(mappedBy = "east")
    private Collection<Room> roomCollection3;

    @JoinColumn(name = "east", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room east;

    @OneToMany(mappedBy = "south")
    private Collection<Room> roomCollection4;

    @JoinColumn(name = "south", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room south;

    @OneToMany(mappedBy = "north")
    private Collection<Room> roomCollection5;

    @JoinColumn(name = "north", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Room north;

    @JoinColumn(name = "area", referencedColumnName = "area")
    @ManyToOne(optional = false)
    private Area area;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
    private Collection<Roomattribute> roomattributeCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
    private Collection<RoomitemTable> roomitemTableCollection;

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

    public Collection<Room> getRoomCollection()
    {
        return roomCollection;
    }

    public void setRoomCollection(Collection<Room> roomCollection)
    {
        this.roomCollection = roomCollection;
    }

    public Room getDown()
    {
        return down;
    }

    public void setDown(Room down)
    {
        this.down = down;
    }

    public Collection<Room> getRoomCollection1()
    {
        return roomCollection1;
    }

    public void setRoomCollection1(Collection<Room> roomCollection1)
    {
        this.roomCollection1 = roomCollection1;
    }

    public Room getUp()
    {
        return up;
    }

    public void setUp(Room up)
    {
        this.up = up;
    }

    public Collection<Room> getRoomCollection2()
    {
        return roomCollection2;
    }

    public void setRoomCollection2(Collection<Room> roomCollection2)
    {
        this.roomCollection2 = roomCollection2;
    }

    public Room getWest()
    {
        return west;
    }

    public void setWest(Room west)
    {
        this.west = west;
    }

    public Collection<Room> getRoomCollection3()
    {
        return roomCollection3;
    }

    public void setRoomCollection3(Collection<Room> roomCollection3)
    {
        this.roomCollection3 = roomCollection3;
    }

    public Room getEast()
    {
        return east;
    }

    public void setEast(Room east)
    {
        this.east = east;
    }

    public Collection<Room> getRoomCollection4()
    {
        return roomCollection4;
    }

    public void setRoomCollection4(Collection<Room> roomCollection4)
    {
        this.roomCollection4 = roomCollection4;
    }

    public Room getSouth()
    {
        return south;
    }

    public void setSouth(Room south)
    {
        this.south = south;
    }

    public Collection<Room> getRoomCollection5()
    {
        return roomCollection5;
    }

    public void setRoomCollection5(Collection<Room> roomCollection5)
    {
        this.roomCollection5 = roomCollection5;
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

    public Collection<Roomattribute> getRoomattributeCollection()
    {
        return roomattributeCollection;
    }

    public void setRoomattributeCollection(Collection<Roomattribute> roomattributeCollection)
    {
        this.roomattributeCollection = roomattributeCollection;
    }

    public Collection<RoomitemTable> getRoomitemTableCollection()
    {
        return roomitemTableCollection;
    }

    public void setRoomitemTableCollection(Collection<RoomitemTable> roomitemTableCollection)
    {
        this.roomitemTableCollection = roomitemTableCollection;
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
        return "mmud.database.entities.game.Room[ id=" + id + " ]";
    }

}
