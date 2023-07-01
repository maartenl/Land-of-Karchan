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

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mmud.database.Attributes;
import mmud.database.entities.Ownage;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemWrangler;
import mmud.exceptions.ItemException;
import mmud.exceptions.MudException;
import org.eclipse.persistence.annotations.Customizer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A room. Bear in mind that this room has potential exits to the north, south,
 * east, west, up and down, which are also rooms. The structure forms a kind of
 * graph.
 *
 * @author maartenl
 * @see <a href="http://www.eclipse.org/eclipselink/documentation/2.5/concepts/app_dev007.htm">About
 * weaving</a>
 */
@Entity
@Table(name = "mm_rooms")
@NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r order by r.id")
@NamedQuery(name = "Room.countAll", query = "SELECT count(r) FROM Room r")
@NamedQuery(name = "Room.countAllByDescription", query = "SELECT count(r) FROM Room r where r.contents like :description")
@NamedQuery(name = "Room.findById", query = "SELECT r FROM Room r WHERE r.id = :id")
@NamedQuery(name = "Room.findMaxId", query = "SELECT max(r.id) FROM Room r")
@NamedQuery(name = "Room.findByCreation", query = "SELECT r FROM Room r WHERE r.creation = :creation")
@NamedQuery(name = "Room.findByTitle", query = "SELECT r FROM Room r WHERE r.title = :title")
@NamedQuery(name = "Room.findByPicture", query = "SELECT r FROM Room r WHERE r.picture = :picture")
@Customizer(PersonsFilterForRoom.class)
public class Room implements Serializable, DisplayInterface, ItemWrangler, AttributeWrangler, Ownage
{
    @Serial
    private static final Logger LOGGER = Logger.getLogger(Room.class.getName());
    /**
     * The first room that new characters appear in.
     */
    public static final long STARTERS_ROOM = 1L;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "contents")
    private String contents;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation")
    private LocalDateTime creation;
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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room", orphanRemoval = true)
    private Set<Roomattribute> attributes = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
    private Set<Item> items = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
    private Set<Person> persons = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
    private Set<Board> boards;

    public Room()
    {
    }

    public Room(Long id)
    {
        this.id = id;
    }

    public Room(Long id, String contents, LocalDateTime creation, String title)
    {
        this.id = id;
        this.contents = contents;
        this.creation = creation;
        this.title = title;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * A description of the room.
     *
     * @return a description
     */
    public String getContents()
    {
        return contents;
    }

    /**
     * @param contents the new description
     * @see #getContents()
     */
    public void setContents(String contents)
    {
        this.contents = contents;
    }

    @Override
    public String getBody()
    {
        return contents;
    }

    public LocalDateTime getCreation()
    {
        return creation;
    }

    public void setCreation(LocalDateTime creation)
    {
        this.creation = creation;
    }

    public String getTitle()
    {
        return title;
    }

    @Override
    public String getMainTitle()
    {
        return getTitle();
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getPicture()
    {
        return picture;
    }

    @Override
    public String getImage()
    {
        return picture;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    @Override
    public Admin getOwner()
    {
        return owner;
    }

    @Override
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

    public Set<Roomattribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(Set<Roomattribute> attributes)
    {
        this.attributes = attributes;
    }

    @Override
    public Set<Item> getItems()
    {
        return items;
    }

    public void setItems(Set<Item> items)
    {
        this.items = items;
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
        // Warning - this method won't work in the case the id fields are not set
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

    /**
     * retrieve the character from the list of characters currently active in the
     * current room.
     *
     * @param aName name of the character to search for. Case matters not.
     * @return Character/Person in the room. Will return null pointer if character
     * not found.
     * @see #retrieveUser(java.lang.String)
     */
    public Person retrievePerson(String aName)
    {
        for (Person person : persons)
        {
            if ((person.getName().equalsIgnoreCase(aName)))
            {
                return person;
            }
        }
        return null;
    }

    /**
     * retrieve the player from the list of characters currently active in the
     * current room.
     *
     * @param aName name of the player to search for.
     * @return Player in the room. Will return null pointer if character not found
     * or character is not a "real" player.
     * @see #retrievePerson(java.lang.String)
     */
    public User retrieveUser(String aName)
    {
        Person person = retrievePerson(aName);
        if (person instanceof User)
        {
            return (User) person;
        }
        return null;
    }

    /**
     * retrieve the board in the room. May be null if there is no board.
     *
     * @param aName name of the board to search for.
     * @return Board in the room. Will return null pointer if board not found.
     */
    public Board getBoard(String aName)
    {
        for (Board board : boards)
        {
            if ((board.getName().equalsIgnoreCase(aName)))
            {
                return board;
            }
        }
        return null;
    }

    /**
     * Returns items if found, otherwise returns an empty list.
     *
     * @param parsed the parsed description of the item as given by the user, for
     *               example {"light-green", "leather", "pants"}.
     * @return list of found items, empty if not found.
     */
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
    public boolean removeAttribute(String name)
    {
        Roomattribute attr = getRoomattribute(name);
        if (attr == null)
        {
            return false;
        }
        boolean result = attributes.remove(attr);
        return result;
    }

    @Override
    public Attribute getAttribute(String name)
    {
        return getRoomattribute(name);
    }

    @Override
    public void setAttribute(String name, String value)
    {
        LOGGER.log(Level.FINER, "setAttribute name={0} value={1}", new Object[]
                {
                        name, value
                });
        Roomattribute attr = getRoomattribute(name);
        if (attr == null)
        {
            LOGGER.log(Level.FINER, "setAttribute new roomattribute.");
            attr = new Roomattribute(name, this);
            attr.setRoom(this);
        } else
        {
            LOGGER.log(Level.FINER, "setAttribute roomattribute changed.");
        }
        attr.setValue(value);
        attr.setValueType(Attributes.VALUETYPE_STRING);
        attributes.add(attr);
    }

    @Override
    public boolean verifyAttribute(String name, String value)
    {
        Roomattribute attr = getRoomattribute(name);
        if (attr == null)
        {
            LOGGER.finer("verifyAttribute (name=" + name + ", value=" + value + ") not found on room " + getId() + ".");
            return false;
        }
        if (attr.getValue() == value)
        {
            LOGGER.finer("verifyAttribute (name=" + name + ", value=" + value + ") same object on room " + getId() + "!");
            return true;
        }
        if (attr.getValue().equals(value))
        {
            LOGGER.finer("verifyAttribute (name=" + name + ", value=" + value + ") matches on room " + getId() + "!");
            return true;
        }
        LOGGER.finer("verifyAttribute (name=" + name + ", value=" + value + ") with (name=" + attr.getName() + ", value=" + attr.getValue() + ") no match on room " + getId() + ".");
        return false;
    }

    private Roomattribute getRoomattribute(String name)
    {
        if (attributes == null)
        {
            LOGGER.finer("getRoomattribute name=" + name + " collection is null");
            return null;
        }
        for (Roomattribute attr : attributes)
        {
            LOGGER.finer("getRoomattribute name=" + name + " attr=" + attr);
            if (attr.getName().equals(name))
            {
                return attr;
            }
        }
        LOGGER.finer("getRoomattribute name=" + name + " not found");
        return null;
    }

    @Override
    public boolean destroyItem(Item item)
    {
        return items.remove(item);
        // note: as the collection is an orphan, the delete
        // on the set will take place automatically.
    }

    /**
     * A person, apparently, dropped this into the room.
     *
     * @param item the item he dropped.
     * @return boolean, true if it was added, false otherwise.
     */
    public boolean drop(Item item)
    {
        return items.add(item);
    }

    public void get(Item item)
    {
        if (item == null || !items.contains(item))
        {
            throw new ItemException("Item not found.");
        }
        if (!items.remove(item))
        {
            throw new ItemException("Unable to remove item from room.");
        }
    }

    /**
     * Returns all persons in the room, can be bots or shopkeepers or normal
     * players.
     *
     * @param excluding this user is excluded. Can be null, in this case all
     *                  persons in the room are returned.
     * @return a list of persons in the room.
     */
    public List<Person> getPersons(Person excluding)
    {
        List<Person> result = new ArrayList<>();
        for (Person person : persons)
        {
            if (excluding == null || !person.getName().equals(excluding.getName()))
            {
                result.add(person);
            }
        }
        return result;
    }

    /**
     * Retrieves a specific person of the room, by (case-insensitive) name.
     *
     * @param name the name of the person to look for.
     * @return Person in the room, null if not found.
     */
    public Person getPerson(String name)
    {
        for (Person person : persons)
        {
            if (person.getName().equalsIgnoreCase(name))
            {
                return person;
            }
        }
        return null;
    }

    /**
     * Adds an {@link Item} to the room.
     *
     * @param item the new item. May not be null.
     * @return the new item, null if unable to add.
     */
    @Override
    public Item addItem(Item item)
    {
        if (item.getBelongsTo() != null || item.getRoom() != null || item.getContainer() != null)
        {
            throw new MudException("Item already assigned.");
        }
        if (!items.add(item))
        {
            return null;
        }
        item.assignTo(this);
        return item;
    }

    /**
     * Should ONLY be used for testing!!!!
     *
     * @param person
     */
    @VisibleForTesting
    public void addPerson(Person person)
    {
        persons.add(person);
    }

}
