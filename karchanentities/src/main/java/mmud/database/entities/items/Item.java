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
 *g
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.database.entities.items;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import mmud.database.Attributes;
import mmud.database.InputSanitizer;
import mmud.database.OutputFormatter;
import mmud.database.entities.Ownage;
import mmud.database.entities.characters.Person;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Attribute;
import mmud.database.entities.game.AttributeWrangler;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import mmud.exceptions.ItemException;
import mmud.exceptions.MudException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An item. To be more precise an instance of an item definition. An item can
 * either reside in a room, on a person or in another item.
 *
 * @author maartenl
 */
// TODO: create subclass named NormalItem.
// TODO: create subclass named ContainerItem.
// TODO: create subclass named ShopKeeperItem.
// TODO: fix named queries.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "discriminator",
    discriminatorType = DiscriminatorType.INTEGER)
@Table(name = "mm_itemtable")
@NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i")
@NamedQuery(name = "Item.findById", query = "SELECT i FROM Item i WHERE i.id = :id")
@NamedQuery(name = "Item.drop", query = "UPDATE Item i SET i.belongsto = null, i.room = :room WHERE i = :item and i" +
    ".belongsto = :person and i.room is null and i.container is null and i.itemDefinition.dropable <> 0")
@NamedQuery(name = "Item.get", query = "UPDATE Item i SET i.room = null, i.belongsto = :person WHERE i = :item and i" +
    ".belongsto is null and i.room = :room and i.container is null and i.itemDefinition.getable <> 0")
@NamedQuery(name = "Item.give", query = "UPDATE Item i SET i.belongsto = :toperson WHERE i = :item and i.belongsto = " +
    ":fromperson and i.room is null and i.container is null and i.itemDefinition.getable <> 0")
@NamedQuery(name = "Item.put", query = "UPDATE Item i SET i.container = :container, i.belongsto = null, i.room = null" +
    " WHERE i = :item and i.belongsto = :person and i.room is null and i.container is null and i.itemDefinition" +
    ".getable <> 0")
@NamedQuery(name = "Item.retrieve", query = "UPDATE Item i SET i.container = null, i.belongsto = :person, i.room = " +
    "null WHERE i = :item and i.belongsto is null and i.room is null and i.container = :container and i" +
    ".itemDefinition.getable <> 0")
abstract public class Item implements Serializable, DisplayInterface, AttributeWrangler, ItemWrangler, Ownage
{

  private static final Logger LOGGER = Logger.getLogger(Item.class.getName());

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id")
  private Integer id;

  @Basic(optional = false)
  @NotNull
  @Column(name = "creation")
  private LocalDateTime creation;

  /**
   * Indicates what kind of item it is. Current values are used:
   * <ul><li>0, a normal item</li>
   * <li>1, a shopkeeper list</li>
   * <li>2, a writable item like a board</li></ul>
   */
  @Basic(optional = false)
  @NotNull
  @Column(name = "discriminator")
  private Integer discriminator;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "container", orphanRemoval = true)
  private Set<Item> items = new HashSet<>();

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

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "item", orphanRemoval = true)
  private List<Itemattribute> itemattributeCollection = new ArrayList<>();

  /**
   * Constructor. Creates a completely empty item. Usually required by ORM.
   */
  public Item()
  {
  }

  /**
   * Constructor.
   *
   * @param id the item definition used as a template of this (new) item.
   */
  public Item(ItemDefinition id)
  {
    this.itemDefinition = id;
    this.creation = LocalDateTime.now();
    this.discriminator = id.getDiscriminator();
  }

  public Integer getDiscriminator()
  {
    return discriminator;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public LocalDateTime getCreation()
  {
    return creation;
  }

  public void setCreation(LocalDateTime creation)
  {
    this.creation = creation;
  }

  /**
   * Indicates the items that are contained (if possible) inside this item. Will
   * in most cases return an empty list.
   *
   * @return set of items.
   */
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
   * Retrieves the item that contains this item (if possible). Might be null.
   *
   * @return the container
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

  public void setContainer(@Nonnull Item container)
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

  /**
   * Will return the room in which this item can be found. Might be null.
   *
   * @return the room which contains this item.
   * @see #getBelongsTo()
   * @see #getContainer()
   */
  public Room getRoom()
  {
    return room;
  }

  /**
   * Will return the person that owns this item. Might be null.
   *
   * @return the person who owns this item.
   * @see #getRoom()
   * @see #getContainer()
   */
  public Person getBelongsTo()
  {
    return belongsto;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.items.Item[ id=" + id + " ]";
  }

  /**
   * Description of the item, for example "a white, cloth pants".
   *
   * @return a description of the item.
   */
  public String getDescription()
  {
    return InputSanitizer.alphanumericalandpuntuation(getItemDefinition().getShortDescription());
  }

  /**
   * @param parsed
   * @return true if the item is properly described.
   * @see ItemDefinition#isDescribedBy(java.util.List)
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
    return getItemDefinition().getShortDescription();
  }

  @Override
  public String getMainTitle() throws MudException
  {
    return OutputFormatter.startWithCapital(getTitle());
  }

  @Override
  public String getImage() throws MudException
  {
    return getItemDefinition().getImage();
  }

  public String getLongDescription()
  {
    Attribute description = getAttribute(Attributes.DESCRIPTION);
    if (description == null || description.getValue() == null || description.getValue().trim().equals(""))
    {
      return getItemDefinition().getDescription();
    }
    return description.getValue();
  }

  @Override
  public String getBody() throws MudException
  {
    StringBuilder builder = new StringBuilder(getLongDescription());
    if (getItemDefinition().getId() > 0)
    {
      builder.append("With your expert eye your judge this item to be worth ").
          append(OutputFormatter.getDescriptionOfMoney(getCopper())).
          append(".<br/>\r\n");
      if (isDrinkable())
      {
        builder.append("You can try drinking it.<br/>\r\n");
      }
      if (isEatable())
      {
        builder.append("You can try eating it.<br/>\r\n");
      }
      Set<Wielding> wieldable = getItemDefinition().getWieldable();
      Set<Wearing> wearable = getItemDefinition().getWearable();
      for (Wielding wield : wieldable)
      {
        builder.append("It can be wielded ").append(wield.toString().replace("%SHISHER", "your")).append(".<br/>\r\n");
      }
      for (Wearing wear : wearable)
      {
        builder.append("It can be worn ").append(wear.toString().replace("%SHISHER", "your")).append(".<br/>\r\n");
      }
    }
    if (isContainer())
    {
      if (hasLid())
      {
        if (isOpen())
        {
          builder.append("It is open.<br/>\r\n");
        } else
        {
          builder.append("It is closed.<br/>\r\n");
        }
      }
      if (hasLock())
      {
        if (isLocked())
        {
          builder.append("It is locked.<br/>\r\n");
        } else
        {
          builder.append("It is unlocked.<br/>\r\n");
        }
      }
    }
    return builder.toString();
  }

  public Integer getCopper()
  {
    return getItemDefinition().getCopper();
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
      LOGGER.finer("getItemattribute name=" + name + " collection is null");
      return null;
    }
    for (Itemattribute attr : itemattributeCollection)
    {
      LOGGER.finer("getItemattribute name=" + name + " attr=" + attr);
      if (attr.getName().equals(name))
      {
        return attr;
      }
    }
    LOGGER.log(Level.FINER, "getItemattribute name={0} not found", name);
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
    LOGGER.log(Level.FINER, "setAttribute name={0} value={1}", new Object[]
        {
            name, value
        });
    Itemattribute attr = getItemattribute(name);
    if (attr == null)
    {
      attr = new Itemattribute(name, this);
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
      LOGGER.log(Level.FINER, "verifyAttribute (name={0}, value={1}) not found on item {2}.", new Object[]
          {
              name, value, getId()
          });
      return false;
    }
    if (attr.getValue() == value)
    {
      LOGGER.finer("verifyAttribute (name=" + name + ", value=" + value + ") same object on item " + getId() + "!");
      return true;
    }
    if (attr.getValue().equals(value))
    {
      LOGGER.finer("verifyAttribute (name=" + name + ", value=" + value + ") matches on item " + getId() + "!");
      return true;
    }
    LOGGER.finer("verifyAttribute (name=" + name + ", value=" + value + ") with (name=" + attr.getName() + ", value=" + attr.getValue() + ") no match on item " + getId() + ".");
    return false;
  }

  /**
   * Indicates if the container is open or closed. Only makes sense if this item
   * is in fact a container.
   *
   * @return true if there is an attribute named "isopen".
   */
  public boolean isOpen()
  {
    return verifyAttribute("isopen", "true");
  }

  /**
   * Returns true if the container has a lid, if the container can be opened.
   *
   * @return boolean true if the container has a lid.
   */
  public boolean hasLid()
  {
    return isOpenable();
  }

  /**
   * Returns true if the container has a lid, if the container can be opened.
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
   * @see ItemDefinition#getKey()
   */
  public boolean isLocked()
  {
    return verifyAttribute(Attributes.LOCKED, "true");
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
   * Returns true if the item can be eaten. This is better than just using a
   * compare on the getEatable.
   *
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

  /**
   * Provides the description of what happens when you try to eat it. Can be
   * null or empty, for example if the item cannot be eaten. Eating an item will
   * always destroy that item.
   *
   * @return
   */
  public String getEatable()
  {
    return getItemDefinition().getEatable();
  }

  @Override
  public List<Item> findItems(List<String> parsed)
  {
    List<Item> result = new ArrayList<>();
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
    // TODO: move this to a container
    // return items.remove(item);
    // note: as the collection is an orphan, the delete
    // on the set will take place automatically.
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose
    // Tools | Templates.
  }

  /**
   * Returns true if the item can be drunk. This is better than just using a
   * compare on the getDrinkable.
   *
   * @return true if drinkable, false otherwise
   * @see #getDrinkable
   */
  public boolean isDrinkable()
  {
    if (getAttribute("drinkable") != null)
    {
      return verifyAttribute("drinkable", "true");
    }
    return getItemDefinition().getDrinkable() != null && !getItemDefinition().getDrinkable().trim().isEmpty();
  }

  /**
   * Provides the description of what happens when you try to drink it. Can be
   * null or empty, for example if the item cannot be drunk. Drinking an item
   * will always destroy that item.
   *
   * @return
   */
  public String getDrinkable()
  {
    return getItemDefinition().getDrinkable();

  }

  public boolean isWearable(Wearing position)
  {
    return getItemDefinition().isWearable(position);
  }

  public boolean isWieldable(Wielding position)
  {
    return getItemDefinition().isWieldable(position);
  }

  /**
   * Whether or not you are able to drop this item.
   *
   * @return true, in case you can, false otherwise.
   */
  public boolean isDroppable()
  {
    if (isBound())
    {
      return false;
    }
    if (getItemDefinition().getId() < 0)
    {
      return false;
    }
    if (getAttribute("notdropable") != null)
    {
      return !verifyAttribute("notdropable", "true");
    }
    return getItemDefinition().getDropable();
  }

  /**
   * Whether or not you are able to drop this item or give this item, or in some
   * other way dispose of this item, besides selling it to a vendor or just
   * destroying it.
   *
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
   * Whether or not you are able to retrieve this item from the floor of the
   * room.
   *
   * @return true, in case you can, false otherwise.
   */
  public boolean isGetable()
  {
    if (isBound())
    {
      return false;
    }
    if (getItemDefinition().getId() < 0)
    {
      return false;
    }
    if (getAttribute("notgetable") != null)
    {
      return !verifyAttribute("notgetable", "true");
    }
    return getItemDefinition().getGetable();
  }

  public void drop(Person person, Room room)
  {
    if (isBound())
    {
      throw new ItemException("You are not allowed to drop this item.");
    }
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

  /**
   * Specialty method for the creation of new items in a room. Only used by
   * {@link Room#createItem(mmud.database.entities.items.ItemDefinition) }
   *
   * @param room the room to drop the new item into.
   */
  public void drop(Room room)
  {
    if (isBound())
    {
      throw new ItemException("You are not allowed to drop this item.");
    }
    // TODO: equals directly on room doesn't seem to work right. Different objects
    // same ids.
    belongsto = null;
    this.room = room;
  }

  public void get(Person person, Room room)
  {
    if (isBound())
    {
      throw new ItemException("You are not allowed to drop this item.");
    }
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

  public void give(Person toperson)
  {
    if (isBound())
    {
      throw new ItemException("You are not allowed to give this item.");
    }
    if (room != null)
    {
      throw new ItemException("Cannot give an item that is in a room.");
    }
    if (container != null)
    {
      throw new ItemException("Cannot give an item that is stored in a container.");
    }
    belongsto = toperson;
  }

  public boolean isReadable()
  {
    boolean foundAttribute =
        getAttribute("readable") != null && getAttribute("readable").getValue() != null && !getAttribute("readable").getValue().trim().isEmpty();
    boolean foundReadable =
        (getItemDefinition().getReaddescription() != null && !getItemDefinition().getReaddescription().trim().isEmpty());
    return foundAttribute || foundReadable;
  }

  public DisplayInterface getRead()
  {
    LOGGER.info("Item getRead");
    if (!isReadable())
    {
      throw new ItemException("Item cannot be read.");
    }
    return new DisplayInterface()
    {
      @Override
      public String getMainTitle() throws MudException
      {
        return getDescription();
      }

      @Override
      public String getImage() throws MudException
      {
        return getItemDefinition().getImage();
      }

      @Override
      public String getBody() throws MudException
      {
        String read
            = getAttribute("readable") == null ? null : getAttribute("readable").getValue();
        if (read == null || read.trim().isEmpty())
        {
          read = getItemDefinition().getReaddescription();
        }
        if (read == null || read.trim().isEmpty())
        {
          return null;
        }
        return read;
      }
    };
  }

  /**
   * Returns whether or not the container has a lock. Meaning whether or not the
   * container can be unlocked/locked.
   *
   * @return boolean true if the container can be locked/unlocked.
   */
  public boolean hasLock()
  {
    return getItemDefinition().hasLock();
  }

  /**
   * Verifies that the key provided can open this container.
   *
   * @param key the key to hopefully open this container.
   * @return true, if it is possible to open this container with the key
   * provided.
   */
  public boolean isKey(Item key)
  {
    if (key == null)
    {
      return false;
    }
    return key.getItemDefinition().equals(getItemDefinition().getKey());
  }

  public void lock()
  {
    setAttribute(Attributes.LOCKED, "true");
  }

  public void unlock()
  {
    setAttribute(Attributes.LOCKED, "false");
  }

  public boolean isVisible()
  {
    return getItemDefinition().getId() >= 0
        && (getItemDefinition().getVisible() == null || getItemDefinition().getVisible());
  }

  /**
   * Returns the category of the item.
   *
   * @return the category of the item.
   */
  public abstract ItemCategory getCategory();

  /**
   * Items are sellable by default, unless attribute "notsellable" is set.
   *
   * @return can the item be sold.
   * @see Attributes#NOTSELLABLE
   */
  public boolean isSellable()
  {
    if (isBound())
    {
      return false;
    }
    if (containsItems())
    {
      return false;
    }
    if (getCopper() == 0)
    {
      return false;
    }
    if (getItemDefinition().getId() < 0)
    {
      return false;
    }
    if (getAttribute(Attributes.NOTSELLABLE) != null)
    {
      return !verifyAttribute(Attributes.NOTSELLABLE, "true");
    }
    return true;
    // TODO: getItemDefinition().isSellable();
  }

  /**
   * Items are buyable by default, unless attribute "notbuyable" is set.
   *
   * @return
   * @see Attributes#NOTBUYABLE
   */
  public boolean isBuyable()
  {
    if (isBound())
    {
      return false;
    }
    if (getItemDefinition().getId() < 0)
    {
      return false;
    }
    if (getCopper() == 0)
    {
      return false;
    }
    if (getAttribute(Attributes.NOTBUYABLE) != null)
    {
      return !verifyAttribute(Attributes.NOTBUYABLE, "true");
    }
    return true;
    // TODO: getItemDefinition().isBuyable();
  }

  /**
   * Tells you if this item contains other items, for example if it is a bag.
   *
   * @return
   */
  public boolean containsItems()
  {
    return items != null && !items.isEmpty();
  }

  /**
   * Adds an {@link Item} to the bag (container).
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
    if (!this.isContainer())
    {
      throw new MudException("You cannot drop a new item in another item, if the other item is not a container.");
    }
    if (!items.add(item))
    {
      return null;
    }
    item.setContainer(this);
    return item;
  }

  public void assignTo(Room room)
  {
    if (getBelongsTo() != null || getRoom() != null || getContainer() != null)
    {
      throw new MudException("Item already assigned.");
    }
    this.room = room;
  }

  public void assignTo(Room room, Person belongsto, Item container)
  {
    if (getBelongsTo() != null || getRoom() != null || getContainer() != null)
    {
      throw new MudException("Item already assigned.");
    }
    int counter = 0;
    if (room != null)
    {
      counter++;
    }
    if (container != null)
    {
      counter++;
    }
    if (belongsto != null)
    {
      counter++;
    }
    if (counter == 0)
    {
      throw new MudException("Item not assigned to anything.");
    }
    if (counter > 1)
    {
      throw new MudException("Item  assigned to more than one mud object.");
    }
    this.room = room;
    this.belongsto = belongsto;
    this.container = container;
  }
}
