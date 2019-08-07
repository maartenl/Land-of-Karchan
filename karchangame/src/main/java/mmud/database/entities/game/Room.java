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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import mmud.Attributes;
import mmud.database.entities.Ownage;
import mmud.database.entities.characters.Person;
import mmud.database.entities.characters.User;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemWrangler;
import mmud.exceptions.ItemException;
import mmud.exceptions.MudException;
import org.eclipse.persistence.annotations.Customizer;

/**
 * A room. Bear in mind that this room has potential exits to the north, south,
 * east, west, up and down, which are also rooms. The structure forms a kind of
 * graph.
 *
 * @see <a href="http://www.eclipse.org/eclipselink/documentation/2.5/concepts/app_dev007.htm">About weaving</a>
 * @author maartenl
 */
@Entity
@Table(name = "mm_rooms")
@NamedQueries(
        {
          @NamedQuery(name = "Room.findAll", query = "SELECT r FROM Room r")
          ,
            @NamedQuery(name = "Room.findById", query = "SELECT r FROM Room r WHERE r.id = :id")
          ,
            @NamedQuery(name = "Room.findByCreation", query = "SELECT r FROM Room r WHERE r.creation = :creation")
          ,
            @NamedQuery(name = "Room.findByTitle", query = "SELECT r FROM Room r WHERE r.title = :title")
          ,
            @NamedQuery(name = "Room.findByPicture", query = "SELECT r FROM Room r WHERE r.picture = :picture")
        })
@Customizer(PersonsFilterForRoom.class)
public class Room implements Serializable, DisplayInterface, ItemWrangler, AttributeWrangler, Ownage
{

  private static final Logger LOGGER = Logger.getLogger(Room.class.getName());
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

  public Room(Integer id)
  {
    this.id = id;
  }

  public Room(Integer id, String contents, LocalDateTime creation, String title)
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

  /**
   * A description of the room.
   *
   * @return
   */
  public String getContents()
  {
    return contents;
  }

  /**
   * @see #getContents()
   * @param contents
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

  /**
   * character communication method to everyone in the room. The message is
   * parsed, based on who is sending the message.
   *
   * @param aPerson the person who is the source of the message.
   * @param aMessage the message
   *
   * @see Person#writeMessage(mmud.database.entities.characters.Person, java.lang.String)
   */
  public void sendMessage(Person aPerson, String aMessage) throws MudException
  {
    for (Person myChar : persons)
    {
      myChar.writeMessage(aPerson, aMessage);
    }
  }

  /**
   * character communication method to everyone in the room. The first person
   * is the source of the message. The second person is the target of the
   * message. The message is parsed based on the source and target.
   * Will also be sent to the person doing the communicatin' and
   * the target.
   *
   * @param aPerson the person doing the communicatin'.
   * @param aSecondPerson the person communicated to.
   * @param aMessage the message to be sent
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * mmud.database.entities.characters.Person, java.lang.String)
   */
  public void sendMessage(Person aPerson, Person aSecondPerson,
          String aMessage) throws MudException
  {
    for (Person myChar : persons)
    {
      myChar.writeMessage(aPerson, aSecondPerson, aMessage);
    }
  }

  /**
   * room communication method to everyone in the room. The message is not
   * parsed. Bear in mind that this method should only be used for
   * communication about environmental issues. If the communication originates
   * from a User/Person, you should use {@link #sendMessage(mmud.database.entities.characters.Person, java.lang.String) }.
   * Otherwise the Ignore functionality will be omitted.
   *
   * @param aMessage the message
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(java.lang.String)
   */
  public void sendMessage(String aMessage)
          throws MudException
  {
    for (Person myChar : persons)
    {
      myChar.writeMessage(aMessage);
    }
  }

  /**
   * character communication method to everyone in the room except to the two
   * persons mentioned in the header. The message is parsed based on the
   * source and target.
   *
   * @param aPerson the person doing the communicatin'.
   * @param aSecondPerson the person communicated to.
   * @param aMessage the message to be sent
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * mmud.database.entities.characters.Person, java.lang.String)
   */
  public void sendMessageExcl(Person aPerson, Person aSecondPerson,
          String aMessage) throws MudException
  {
    for (Person myChar : persons)
    {
      if (myChar != aPerson
              && myChar != aSecondPerson)
      {
        myChar.writeMessage(aPerson, aSecondPerson, aMessage);
      }
    }
  }

  /**
   * character communication method to everyone in the room excluded the
   * person mentioned in the parameters. The message is parsed, based on who
   * is sending the message.
   *
   * @param aPerson the person who is the source of the message.
   * @param aMessage the message
   * @throws MudException if the room is not correct
   * @see Person#writeMessage(mmud.database.entities.characters.Person,
   * java.lang.String)
   */
  public void sendMessageExcl(Person aPerson, String aMessage)
          throws MudException
  {
    for (Person myChar : persons)
    {
      if (myChar != aPerson)
      {
        myChar.writeMessage(aPerson, aMessage);
      }
    }
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

  /**
   * retrieve the character from the list of characters currently active in
   * the current room.
   *
   * @param aName name of the character to search for. Case matters not.
   * @return Character/Person in the room. Will return null pointer if
   * character not found.
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
   * retrieve the player from the list of characters currently active in
   * the current room.
   *
   * @param aName name of the player to search for.
   * @return Player in the room. Will return null pointer if
   * character not found or character is not a "real" player.
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
   * @return Board in the room. Will return null pointer if
   * board not found.
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
   * @param parsed the parsed description of the item as given by the user,
   * for example {"light-green", "leather", "pants"}.
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
    Roomattribute attr = getRoomattribute(name);
    if (attr == null)
    {
      attr = new Roomattribute(name, this);
      attr.setRoom(this);
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
   * persons in the room are returned.
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
}
