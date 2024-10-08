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
package mmud.database.entities.characters;

import com.google.common.annotations.VisibleForTesting;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import mmud.database.Attributes;
import mmud.database.RegularExpressions;
import mmud.database.entities.Ownage;
import mmud.database.entities.game.Admin;
import mmud.database.entities.game.Attribute;
import mmud.database.entities.game.AttributeWrangler;
import mmud.database.entities.game.Charattribute;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Room;
import mmud.database.entities.items.Item;
import mmud.database.entities.items.ItemWrangler;
import mmud.database.enums.Alignment;
import mmud.database.enums.Appetite;
import mmud.database.enums.God;
import mmud.database.enums.Health;
import mmud.database.enums.Movement;
import mmud.database.enums.Sex;
import mmud.database.enums.Sobriety;
import mmud.database.enums.Wearing;
import mmud.database.enums.Wielding;
import mmud.exceptions.ItemException;
import mmud.exceptions.MoneyException;
import mmud.exceptions.MudException;

import javax.annotation.Nullable;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A character in the game. Might be both a bot, a shopkeeper, a user, or an
 * administrator.
 *
 * @author maartenl
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "god",
    discriminatorType = DiscriminatorType.INTEGER)
@Table(name = "mm_usertable")
@NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p")
@NamedQuery(name = "Person.findByName", query = "SELECT p FROM Person p WHERE lower(p.name) = lower(:name)")
@NamedQuery(name = "Person.countAll", query = "SELECT count(p) FROM Person p")
public abstract class Person implements Serializable, AttributeWrangler, DisplayInterface, ItemWrangler, Ownage
{

  private static final Logger LOGGER = Logger.getLogger(Person.class.getName());

  @Serial
  private static final long serialVersionUID = 1L;

  public static final String EMPTY_LOG = "";

  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 3, max = 20, message = RegularExpressions.NAME_MESSAGE)
  @Column(name = "name")
  @Pattern(regexp = RegularExpressions.NAME_REGEXP, message = RegularExpressions.NAME_MESSAGE)
  private String name;

  @Size(max = 254)
  @Column(name = "title")
  private String title;

  @Size(max = 120)
  @Column(name = "familyname")
  private String familyname;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 50)
  @Column(name = "race")
  private String race;

  @Basic(optional = false)
  @NotNull
  @Column(name = "sex")
  @Convert(converter = SexConverter.class)
  private Sex sex = Sex.MALE;

  @Size(max = 20)
  @Column(name = "age")
  private String age;

  @Size(max = 20)
  @Column(name = "height")
  private String height;

  @Size(max = 40)
  @Column(name = "width")
  private String width;

  @Size(max = 40)
  @Column(name = "complexion")
  private String complexion;

  @Size(max = 40)
  @Column(name = "eyes")
  private String eyes;

  @Size(max = 40)
  @Column(name = "face")
  private String face;

  @Size(max = 40)
  @Column(name = "hair")
  private String hair;

  @Size(max = 40)
  @Column(name = "beard")
  private String beard;

  @Size(max = 40)
  @Column(name = "arm")
  private String arm;

  @Size(max = 40)
  @Column(name = "leg")
  private String leg;

  @Column(name = "copper")
  private Integer copper;

  @JoinColumn(name = "room", nullable = false, referencedColumnName = "id")
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @NotNull
  private Room room;

  @Column(name = "whimpy")
  private Integer wimpy;

  @Column(name = "experience")
  private Integer experience;

  @Size(max = 20)
  // TODO recursion into the same table
  @Column(name = "fightingwho")
  private String fightingwho;

  @Column(name = "sleep")
  private Boolean sleep;

  @Column(name = "afk")
  private String afk;

  @Column(name = "fightable")
  private Integer fightable;

  @Column(name = "vitals")
  private Integer vitals;

  @Column(name = "fysically")
  private Integer fysically;

  @Column(name = "mentally")
  private Integer mentally;

  @Column(name = "drinkstats")
  private Integer drinkstats;

  @Column(name = "eatstats")
  private Integer eatstats;

  @Column(name = "active")
  private Integer active;

  @Column(name = "birth")
  private LocalDateTime birth;

  @Column(name = "god", insertable = false, updatable = false)
  private Integer god;

  @Column(name = "strength")
  private Integer strength;

  @Column(name = "intelligence")
  private Integer intelligence;

  @Column(name = "dexterity")
  private Integer dexterity;

  @Column(name = "constitution")
  private Integer constitution;

  @Column(name = "wisdom")
  private Integer wisdom;

  @Column(name = "practises")
  private Integer practises;

  @Column(name = "training")
  private Integer training;

  @Column(name = "bandage")
  private Integer bandage;

  @Column(name = "alignment")
  private Integer alignment;

  @Column(name = "manastats")
  private Integer manastats;

  @Column(name = "movementstats")
  private Integer movementstats;

  @Column(name = "maxmana")
  private Integer maxmana;

  @Column(name = "maxmove")
  private Integer maxmove;

  @Column(name = "maxvital")
  private Integer maxvital;

  @Column(name = "jumpmana")
  private Integer jumpmana;

  @Column(name = "jumpmove")
  private Integer jumpmove;

  @Column(name = "jumpvital")
  private Integer jumpvital;

  @Basic(optional = false)
  @NotNull
  @Column(name = "creation_date")
  private LocalDateTime creation;

  @Lob
  @Size(max = 65535)
  @Column(name = "notes")
  private String notes;

  @Lob
  @Size(max = 65535)
  @Column(name = "currentstate")
  private String state;

  @JoinColumn(name = "owner", referencedColumnName = "name")
  @ManyToOne(fetch = FetchType.LAZY)
  private Admin owner;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "belongsto", orphanRemoval = true)
  private Set<Item> items = new HashSet<>();

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
  private Set<Charattribute> attributes = new HashSet<>();

  @JoinColumn(name = "wieldleft", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wieldleft;

  @JoinColumn(name = "wieldright", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wieldright;

  @JoinColumn(name = "wieldboth", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wieldboth;

  @JoinColumn(name = "wearhead", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearhead;

  @JoinColumn(name = "wearneck", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearneck;

  @JoinColumn(name = "weartorso", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item weartorso;

  @JoinColumn(name = "weararms", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item weararms;

  @JoinColumn(name = "wearleftwrist", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearleftwrist;

  @JoinColumn(name = "wearrightwrist", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearrightwrist;

  @JoinColumn(name = "wearleftfinger", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearleftfinger;

  @JoinColumn(name = "wearrightfinger", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearrightfinger;

  @JoinColumn(name = "wearfeet", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearfeet;

  @JoinColumn(name = "wearhands", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearhands;

  @JoinColumn(name = "wearfloatingnearby", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearfloatingnearby;

  @JoinColumn(name = "wearwaist", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearwaist;

  @JoinColumn(name = "wearlegs", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearlegs;

  @JoinColumn(name = "weareyes", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item weareyes;

  @JoinColumn(name = "wearears", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearears;

  @JoinColumn(name = "wearaboutbody", referencedColumnName = "id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Item wearaboutbody;

  @Column(name = "websockets")
  private boolean websocketSupport;

  @Column(name = "posx")
  private BigDecimal posx;
  @Column(name = "posy")
  private BigDecimal posy;
  @Column(name = "posz")
  private BigDecimal posz;

  @Column(name = "rotx")
  private BigDecimal rotx;
  @Column(name = "roty")
  private BigDecimal roty;
  @Column(name = "rotz")
  private BigDecimal rotz;

  @Column(name = "scalex")
  private BigDecimal scalex;
  @Column(name = "scaley")
  private BigDecimal scaley;
  @Column(name = "scalez")
  private BigDecimal scalez;

  public Person()
  {

    this.copper = 0; // no money
    this.wimpy = 0; // no coward, either
    this.experience = 0; // no experience points to speak of
    this.fightingwho = null; // not fighting anybody
    this.sleep = false;
    this.fightable = 0;
    this.vitals = MAX_VITALS;
    this.fysically = 100;
    this.mentally = 100;
    this.drinkstats = 0;
    this.eatstats = 0;
    this.active = 0;
    this.birth = LocalDateTime.now();
    this.god = God.DEFAULT_USER.getValue();
    this.strength = 0;
    this.intelligence = 0;
    this.dexterity = 0;
    this.constitution = 0;
    this.wisdom = 0;
    this.practises = 0;
    this.training = 0;
    this.bandage = 0;
    this.alignment = 0;
    this.manastats = 100;
    this.movementstats = 0;
    this.maxmana = 11999;
    this.maxmove = 11999;
    this.maxvital = MAX_VITALS;
    this.jumpmana = 1;
    this.jumpmove = 1;
    this.jumpvital = 1;
    this.creation = LocalDateTime.now();
    this.notes = null;
    this.state = null;
  }

  public static final int MAX_VITALS = 11999;

  /**
   * returns the name of the character.
   *
   * @return String containing the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Sets the name of the person. Should contain only alphabetical characters,
   * but has to have at least size of 3.
   *
   * @param name the (new) name.
   * @throws MudException if the name is not allowed.
   */
  public void setName(String name) throws MudException
  {
    this.name = name;
  }

  /**
   * Also known as a "surname" or last name.
   *
   * @return the family name, can be NULL.
   */
  public @Nullable String getFamilyname()
  {
    return familyname;
  }

  /**
   * Sets the family name, see {@link #getFamilyname()}.
   *
   * @param familyname the new familyname or NULL.
   */
  public void setFamilyname(@Nullable String familyname)
  {
    this.familyname = familyname;
  }

  /**
   * returns the title of the character.
   *
   * @return String containing the title
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * sets the title of the character.
   *
   * @param title String containing the title
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * returns the race of the character.
   *
   * @return String containing the race
   */
  public String getRace()
  {
    return race;
  }

  public void setRace(String race)
  {
    this.race = race;
  }

  /**
   * returns what gender the character is.
   *
   * @return Sex containing either male or female.
   */
  public Sex getSex()
  {
    return sex;
  }

  public void setSex(Sex sex)
  {
    this.sex = sex;
  }

  /**
   * returns the age of the character.
   *
   * @return String containing the age
   */
  public String getAge()
  {
    return age;
  }

  public void setAge(String age)
  {
    this.age = age;
  }

  /**
   * returns the length of the character.
   *
   * @return String containing the length
   */
  public String getHeight()
  {
    return height;
  }

  public void setHeight(String height)
  {
    this.height = height;
  }

  /**
   * returns the width of the character.
   *
   * @return String containing the width
   */
  public String getWidth()
  {
    return width;
  }

  public void setWidth(String width)
  {
    this.width = width;
  }

  /**
   * returns the complexion of the character.
   *
   * @return String containing the complexion
   */
  public String getComplexion()
  {
    return complexion;
  }

  public void setComplexion(String complexion)
  {
    this.complexion = complexion;
  }

  /**
   * returns the eyes of the character.
   *
   * @return String containing the eyes
   */
  public String getEyes()
  {
    return eyes;
  }

  public void setEyes(String eyes)
  {
    this.eyes = eyes;
  }

  /**
   * returns the face of the character.
   *
   * @return String containing the face
   */
  public String getFace()
  {
    return face;
  }

  public void setFace(String face)
  {
    this.face = face;
  }

  /**
   * returns the hair of the character.
   *
   * @return String containing the hair
   */
  public String getHair()
  {
    return hair;
  }

  public void setHair(String hair)
  {
    this.hair = hair;
  }

  /**
   * returns the beard of the character.
   *
   * @return String containing the beard
   */
  public String getBeard()
  {
    return beard;
  }

  public void setBeard(String beard)
  {
    this.beard = beard;
  }

  /**
   * returns the arms of the character.
   *
   * @return String containing the arms
   */
  public String getArm()
  {
    return arm;
  }

  public void setArm(String arm)
  {
    this.arm = arm;
  }

  /**
   * returns the legs of the character.
   *
   * @return String containing the legs
   */
  public String getLeg()
  {
    return leg;
  }

  public void setLeg(String leg)
  {
    this.leg = leg;
  }

  /**
   * Returns the amount of money a character has (in coppers). Basically 10
   * coppers is 1 silver, and 10 silvers is 1 gold.
   *
   * @return Integer with the amount of coppers.
   */
  public Integer getCopper()
  {
    return copper;
  }

  /**
   * @return Integer with the amount of coppers
   * @see #getCopper()
   */
  public Integer getMoney()
  {
    return getCopper();
  }

  /**
   * Do not use this method ... ever!
   * Used for testing and for inside this class itself.
   *
   * @param copper amount of copper of a person
   */
  @VisibleForTesting
  public void setCopper(Integer copper)
  {
    this.copper = copper;
  }

  /**
   * returns in which Room the character is.
   *
   * @return Room the room that the character is currently occupying
   */
  public Room getRoom()
  {
    return room;
  }

  /**
   * sets the room of the character.
   *
   * @param room the new current room of the character
   */
  public void setRoom(Room room)
  {
    this.room = room;
  }

  /**
   * get the setting for when to flee the fight.
   *
   * @return integer containing the setting
   */
  public Health getWimpy()
  {
    return Health.get(wimpy);
  }

  /**
   * sets the wimpy of the character.
   *
   * @param wimpy Health enum containing the wimpy
   * @see #getWimpy()
   */
  public void setWimpy(Health wimpy)
  {
    if (wimpy == null)
    {
      this.wimpy = null;
      return;
    }
    this.wimpy = wimpy.getOrdinalValue();
  }

  /**
   * Returns the level of the character.
   *
   * @return integer.
   */
  public int getLevel()
  {
    if (experience == null)
    {
      experience = 0;
    }
    return experience / 1000;
  }

  /**
   * Returns the experience of the character.
   *
   * @return integer between 0 and 1000. The closer to 1000 is the closer to the
   * next level.
   */
  public int getExperience()
  {
    if (experience == null)
    {
      experience = 0;
    }
    return experience % 1000;
  }

  public String getFightingwho()
  {
    return fightingwho;
  }

  public void setFightingwho(String fightingwho)
  {
    this.fightingwho = fightingwho;
  }

  /**
   * returns whether or not the character is asleep.
   *
   * @return boolean, true if character is asleep.
   */
  public boolean getSleep()
  {
    if (sleep == null)
    {
      return false;
    }
    return sleep;
  }

  public boolean isAfk()
  {
    return afk != null;
  }

  @Nullable
  public String getAfk()
  {
    return afk;
  }

  public void setAfk(String afk)
  {
    this.afk = afk;
  }

  /**
   * sets the sleep status of the character.
   *
   * @param sleep boolean containing the sleep status
   */
  public void setSleep(Boolean sleep)
  {
    this.sleep = sleep;
  }

  /**
   * Indicates if this person can be fought with by other persons/users/players.
   *
   * @return true if can be fought with, false otherwise.
   */
  public Boolean getFightable()
  {
    return fightable != null && fightable != 0;
  }

  /**
   * @param fightable Indicates if this person can be fought with by other
   *                  persons/users/players.
   * @see #getFightable()
   */
  public void setFightable(Boolean fightable)
  {
    if (fightable == null)
    {
      this.fightable = 0;
      return;
    }
    this.fightable = (fightable ? 1 : 0);
  }

  public Integer getVitals()
  {
    if (vitals == null)
    {
      vitals = 11999;
    }
    return vitals;
  }

  public void increaseHealth(Integer vitals)
  {
    if (vitals < 0)
    {
      return;
    }
    this.vitals += vitals;
    this.vitals = this.vitals % 12000;
  }

  public void decreaseHealth(Integer vitals)
  {
    if (vitals >= 12000)
    {
      return;
    }
    this.vitals -= vitals;
    if (this.vitals < 0)
    {
      this.vitals = 0;
    }
  }

  public Health getHealth()
  {
    return Health.get(getVitals());
  }

  public boolean isDead()
  {
    return getVitals() == 0;
  }

  public Integer getFysically()
  {
    return fysically;
  }

  public void setFysically(Integer fysically)
  {
    this.fysically = fysically;
  }

  public Integer getMentally()
  {
    return mentally;
  }

  public void setMentally(Integer mentally)
  {
    this.mentally = mentally;
  }

  public Integer getDrinkstats()
  {
    return drinkstats;
  }

  public void setDrinkstats(Integer drinkstats)
  {
    this.drinkstats = drinkstats;
  }

  public Integer getEatstats()
  {
    return eatstats;
  }

  public void setEatstats(Integer eatstats)
  {
    this.eatstats = eatstats;
  }

  /**
   * Can tell you if a person is playing the game or not.
   *
   * @return boolean, true if the person is playing, false otherwise.
   */
  public boolean isActive()
  {
    if (active == null)
    {
      return false;
    }
    return active.equals(1);
  }

  /**
   * Make a person actively playing the game (or not).
   *
   * @param active boolean, true if he's playing, false otherwise.
   */
  public void setActive(Boolean active)
  {
    if (active == null)
    {
      this.active = null;
      return;
    }
    this.active = active ? 1 : 0;
  }

  public LocalDateTime getBirth()
  {
    return birth;
  }

  public void setBirth(LocalDateTime birth)
  {
    this.birth = birth;
  }

  public God getGod()
  {
    return God.get(god);
  }

  public Integer getStrength()
  {
    return strength;
  }

  public void setStrength(Integer strength)
  {
    this.strength = strength;
  }

  public Integer getIntelligence()
  {
    return intelligence;
  }

  public void setIntelligence(Integer intelligence)
  {
    this.intelligence = intelligence;
  }

  public Integer getDexterity()
  {
    return dexterity;
  }

  public void setDexterity(Integer dexterity)
  {
    this.dexterity = dexterity;
  }

  public Integer getConstitution()
  {
    return constitution;
  }

  public void setConstitution(Integer constitution)
  {
    this.constitution = constitution;
  }

  public Integer getWisdom()
  {
    return wisdom;
  }

  public void setWisdom(Integer wisdom)
  {
    this.wisdom = wisdom;
  }

  public Integer getPractises()
  {
    return practises;
  }

  public void setPractises(Integer practises)
  {
    this.practises = practises;
  }

  public Integer getTraining()
  {
    return training;
  }

  public void setTraining(Integer training)
  {
    this.training = training;
  }

  public Integer getBandage()
  {
    return bandage;
  }

  public void setBandage(Integer bandage)
  {
    this.bandage = bandage;
  }

  public Integer getAlignment()
  {
    return alignment;
  }

  public void setAlignment(Integer alignment)
  {
    this.alignment = alignment;
  }

  public Integer getManastats()
  {
    return manastats;
  }

  public void setManastats(Integer manastats)
  {
    this.manastats = manastats;
  }

  public Integer getMovementstats()
  {
    return movementstats;
  }

  public void setMovementstats(Integer movementstats)
  {
    this.movementstats = movementstats;
  }

  public Integer getMaxmana()
  {
    return maxmana;
  }

  public void setMaxmana(Integer maxmana)
  {
    this.maxmana = maxmana;
  }

  public Integer getMaxmove()
  {
    return maxmove;
  }

  public void setMaxmove(Integer maxmove)
  {
    this.maxmove = maxmove;
  }

  public Integer getMaxvital()
  {
    return maxvital;
  }

  public void setMaxvital(Integer maxvital)
  {
    this.maxvital = maxvital;
  }

  public Integer getJumpmana()
  {
    return jumpmana;
  }

  public void setJumpmana(Integer jumpmana)
  {
    this.jumpmana = jumpmana;
  }

  public Integer getJumpmove()
  {
    return jumpmove;
  }

  public void setJumpmove(Integer jumpmove)
  {
    this.jumpmove = jumpmove;
  }

  public Integer getJumpvital()
  {
    return jumpvital;
  }

  public void setJumpvital(Integer jumpvital)
  {
    this.jumpvital = jumpvital;
  }

  public LocalDateTime getCreation()
  {
    return creation;
  }

  public void setCreation(LocalDateTime creation)
  {
    this.creation = creation;
  }

  public String getNotes()
  {
    return notes;
  }

  public void setNotes(String notes)
  {
    this.notes = notes;
  }

  /**
   * returns the state of the character, for example "He seems to be on fire.".
   * Primarily used for roleplaying.
   *
   * @return String containing the description of the current condition of the
   * character.
   */
  public String getState()
  {
    return state;
  }

  /**
   * sets the state/condition of the character.
   *
   * @param state String containing the description of the current condition of
   *              the character.
   */
  public void setState(String state)
  {
    this.state = state;
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

  private void addDescriptionPiece(StringBuilder builder, String part)
  {
    if (part != null && !"none".equalsIgnoreCase(part))
    {
      builder.append(", ").append(part);
    }
  }

  /**
   * Returns the description of the character. All characteristics, if possible,
   * are taken into account. Does not provide any info on the name or title. It
   * contains a strictly visual cue.
   *
   * @return String containing the description
   */
  public String getDescription()
  {
    StringBuilder builder = new StringBuilder();
    if (getAge() != null)
    {
      builder.append(getAge());
    }
    addDescriptionPiece(builder, getHeight());
    addDescriptionPiece(builder, getWidth());
    addDescriptionPiece(builder, getComplexion());
    addDescriptionPiece(builder, getEyes());
    addDescriptionPiece(builder, getFace());
    addDescriptionPiece(builder, getHair());
    addDescriptionPiece(builder, getBeard());
    addDescriptionPiece(builder, getArm());
    addDescriptionPiece(builder, getLeg());
    addDescriptionPiece(builder, getFace());
    builder.append(" ").append(getSex()).append(" ").append(getRace());
    return builder.toString().trim();
  }

  /**
   * Indicates if this is a common user. This means it indicates that it is
   * basically someone behind a keyboard.
   *
   * @return true if it is a common user (or a god/administrator), false
   * otherwise.
   */
  public boolean isUser()
  {
    if (getGod() == null)
    {
      return true;
    }
    return (getGod() == God.DEFAULT_USER || getGod() == God.GOD);
  }

  /**
   * Indicates if this is a shopkeeper, a specialized bot.
   * @return true if it is a shopkeeper, false
   * otherwise.
   */
  public boolean isShopkeeper()
  {
    if (getGod() == null)
    {
      return true;
    }
    return (getGod() == God.SHOPKEEPER);
  }

  @Override
  public int hashCode()
  {
    int hash = 0;
    hash += (name != null ? name.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Person))
    {
      return false;
    }
    Person other = (Person) object;
    if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Person[ name=" + name + " ]";
  }

  /**
   * Display statistics .
   *
   * @return String containing all the statistics in html format.
   */
  public String getStatistics() throws MudException
  {
    // TODO : get wearables
    // String stuff = ItemsDb.getWearablesFromChar(this);
    // stuff.replaceAll("%SHISHER", "your");
    StringBuilder stuff = new StringBuilder();
    String whimpy = (getWimpy() == null ? "You are not whimpy at all.<BR>"
        : "You will flee when you are " + getWimpy().getDescription() + ".<BR>");
    String state = getState() == null ? "" : "Your condition is \"" + getState() + "\"<br/>";
    stuff.append("A ").append(getLongDescription()).append(".<BR>You seem to be ").append(Health.getHealth(getVitals()).getDescription()).append(".<BR>You are ").append(Movement.getMovement(getMovementstats()).getDescription()).append(".<BR>").append(Sobriety.getSobriety(getDrinkstats()).getDescription()).append("<br />").append(Appetite.getAppetite(getEatstats()).getDescription()).append("<br />" + "You are ").append(Alignment.getAlignment(alignment).getDescription()).append(".<BR>" + "You are level ").append(getLevel()).append(" and ").append(1000 - getExperience()).append(" experience points away from levelling.<BR>").append(whimpy);
    stuff.append(state);
    // Skill
    return stuff.toString();
  }

  private StringBuilder addLongDescription(StringBuilder builder)
  {
    builder.append(getAge().equals("none") ? "" : getAge() + ", ");
    builder.append(getHeight().equals("none") ? "" : getHeight() + ", ");
    builder.append(getWidth().equals("none") ? "" : getWidth() + ", ");
    builder.append(getComplexion().equals("none") ? "" : getComplexion() + ", ");
    builder.append(getEyes().equals("none") ? "" : getEyes() + ", ");
    builder.append(getFace().equals("none") ? "" : getFace() + ", ");
    builder.append(getHair().equals("none") ? "" : getHair() + ", ");
    builder.append(getBeard().equals("none") ? "" : getBeard() + ", ");
    builder.append(getArm().equals("none") ? "" : getArm() + ", ");
    builder.append(getLeg().equals("none") ? "" : getLeg() + ", ");
    builder.append(getSex()).append(" ").append(getRace()).append(" who calls ").append(getSex().indirect());
    builder.append("self ").append(getName());
    if (getTitle() != null && !getTitle().trim().equals(""))
    {
      builder.append(" (").append(getTitle()).append(")");
    }
    builder.append(".");
    return builder;
  }

  /**
   * returns the description of the character. All characteristics, if possible,
   * are taken into account.
   *
   * @return String containing the description
   */
  public String getLongDescription()
  {

    return addLongDescription(new StringBuilder()).toString();
  }

  @Override
  public boolean removeAttribute(String name)
  {
    Charattribute attr = getCharattribute(name);
    if (attr == null)
    {
      return false;
    }
    return attributes.remove(attr);
  }

  private Charattribute getCharattribute(String name)
  {
    if (attributes == null)
    {
      LOGGER.finer(() -> "getCharattribute name=" + name + " collection is null");
      return null;
    }
    for (Charattribute attr : attributes)
    {
      LOGGER.finer(() -> "getCharattribute name=" + name + " attr=" + attr.getName() + ":" + attr.getValue());
      if (attr.getName().equals(name))
      {
        return attr;
      }
    }
    LOGGER.log(Level.FINER, "getCharattribute name={0} not found", name);
    return null;
  }

  @Override
  public Attribute getAttribute(String name)
  {
    return getCharattribute(name);
  }

  @Override
  public void setAttribute(String name, String value)
  {
    LOGGER.log(Level.FINER, "setAttribute name={0} value={1}", new Object[]
        {
            name, value
        });
    Charattribute attr = getCharattribute(name);
    if (attr == null)
    {
      LOGGER.log(Level.FINER, "setAttribute new charattribute.");
      attr = new Charattribute(name, this);
    } else
    {
      LOGGER.log(Level.FINER, "setAttribute charattribute changed.");
    }
    attr.setValue(value);
    attr.setValueType(Attributes.VALUETYPE_STRING);
    attributes.add(attr);
  }

  @Override
  public boolean verifyAttribute(String name, String value)
  {
    Charattribute attr = getCharattribute(name);
    if (attr == null)
    {
      LOGGER.log(Level.FINER, "verifyAttribute (name={0}, value={1}) not found on user {2}.", new Object[]
          {
              name, value, getName()
          });
      return false;
    }
    if (attr.getValue().equals(value))
    {
      LOGGER.log(Level.FINER, "verifyAttribute (name={0}, value={1}) matches on user {2}!", new Object[]
          {
              name, value, getName()
          });
      return true;
    }
    LOGGER.log(Level.FINER, "verifyAttribute (name={0}, value={1}) with (name={2}, value={3}) no match on user {4}.",
        new Object[]
            {
                name, value, attr.getName(), attr.getValue(), getName()
            });
    return false;
  }

  @Override
  public Set<Item> getItems()
  {
    return items;
  }

  @Override
  public String getMainTitle() throws MudException
  {
    return getName();
  }

  @Override
  public String getImage() throws MudException
  {
    return null;
  }

  private String getWearables()
  {
    StringBuilder builder = new StringBuilder();
    if (getWieldleft() != null)
    {
      builder.append("%SHESHE %SISARE wielding ").append(getWieldleft().getDescription())
          .append(" in %SHISHER left hand.<br/>\r\n");
    }
    if (getWieldright() != null)
    {
      builder.append("%SHESHE %SISARE wielding ").append(getWieldright().getDescription())
          .append(" in %SHISHER right hand.<br/>\r\n");
    }
    if (getWieldboth() != null)
    {
      builder.append("%SHESHE %SISARE wielding ").append(getWieldboth().getDescription())
          .append(" in both hands.<br/>\r\n");
    }
    if (getWearhead() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearhead().getDescription())
          .append(" on %SHISHER head.<br/>\r\n");
    }
    if (getWearneck() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearneck().getDescription())
          .append(" on %SHISHER neck.<br/>\r\n");
    }
    if (getWeartorso() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWeartorso().getDescription())
          .append(" on %SHISHER torso.<br/>\r\n");
    }
    if (getWeararms() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWeararms().getDescription())
          .append(" on %SHISHER arms.<br/>\r\n");
    }
    if (getWearleftwrist() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearleftwrist().getDescription())
          .append(" on %SHISHER left wrist.<br/>\r\n");
    }
    if (getWearrightwrist() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearrightwrist().getDescription())
          .append(" on %SHISHER right wrist.<br/>\r\n");
    }
    if (getWearleftfinger() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearleftfinger().getDescription())
          .append(" on %SHISHER left finger.<br/>\r\n");
    }
    if (getWearrightfinger() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearrightfinger().getDescription())
          .append(" on %SHISHER right finger.<br/>\r\n");
    }
    if (getWearfeet() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearfeet().getDescription())
          .append(" on %SHISHER feet.<br/>\r\n");
    }
    if (getWearhands() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearhands().getDescription())
          .append(" on %SHISHER hands.<br/>\r\n");
    }
    if (getWearfloatingnearby() != null)
    {
      builder.append(getWearfloatingnearby().getDescription())
          .append(" is floating nearby.<br/>\r\n");
    }
    if (getWearwaist() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearwaist().getDescription())
          .append(" around %SHISHER waist.<br/>\r\n");
    }
    if (getWearlegs() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearlegs().getDescription())
          .append(" on %SHISHER legs.<br/>\r\n");
    }
    if (getWeareyes() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWeareyes().getDescription())
          .append(" %SHISHER.<br/>\r\n");
    }
    if (getWearears() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearears().getDescription())
          .append(" in %SHISHER ears.<br/>\r\n");
    }
    if (getWearaboutbody() != null)
    {
      builder.append("%SHESHE %SISARE wearing ").append(getWearaboutbody().getDescription())
          .append(" about %SHISHER body.<br/>\r\n");
    }

    return builder.toString();
  }

  @Override
  public String getBody() throws MudException
  {
    StringBuilder builder = new StringBuilder(
        getLongDescription() + "<br/>\r\n"
            + getWearables());
    if (getState() != null)
    {
      builder.append(getState()).append("<br/>\r\n");
    }
    String stuff2 = builder.toString();
    stuff2 = stuff2.replace("%SHESHE", getSex().Direct());
    stuff2 = stuff2.replace("%SHISHER", getSex()
        .posession());
    stuff2 = stuff2.replace("%SISARE", "is");
    return stuff2;
  }

  /**
   * Returns items in the inventory of this character, based on description
   * provided by the user.
   *
   * @param parsed the parsed description of the item as given by the user, for
   *               example {"light-green", "leather", "pants"}.
   * @return list of found items, empty if not found.
   */
  @Override
  public List<Item> findItems(List<String> parsed)
  {
    LOGGER.warning("findItems " + parsed);
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

  private Item getWieldleft()
  {
    return wieldleft;
  }

  private void setWieldleft(Item item) throws ItemException
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }
    this.wieldleft = item;
  }

  private Item getWieldright()
  {
    return wieldright;
  }

  private void setWieldright(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }
    this.wieldright = item;
  }

  private Item getWieldboth()
  {
    return wieldboth;
  }

  private void setWieldboth(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }
    this.wieldboth = item;
  }

  private Item getWearhead()
  {
    return wearhead;
  }

  private void setWearhead(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearhead = item;
  }

  private Item getWearneck()
  {
    return wearneck;
  }

  private void setWearneck(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearneck = item;
  }

  private Item getWeartorso()
  {
    return weartorso;
  }

  private void setWeartorso(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.weartorso = item;
  }

  private Item getWeararms()
  {
    return weararms;
  }

  private void setWeararms(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.weararms = item;
  }

  private Item getWearleftwrist()
  {
    return wearleftwrist;
  }

  private void setWearleftwrist(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearleftwrist = item;
  }

  private Item getWearrightwrist()
  {
    return wearrightwrist;
  }

  private void setWearrightwrist(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearrightwrist = item;
  }

  private Item getWearleftfinger()
  {
    return wearleftfinger;
  }

  private void setWearleftfinger(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearleftfinger = item;
  }

  private Item getWearrightfinger()
  {
    return wearrightfinger;
  }

  private void setWearrightfinger(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearrightfinger = item;
  }

  private Item getWearfeet()
  {
    return wearfeet;
  }

  private void setWearfeet(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearfeet = item;
  }

  private Item getWearhands()
  {
    return wearhands;
  }

  private void setWearhands(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearhands = item;
  }

  private Item getWearfloatingnearby()
  {
    return wearfloatingnearby;
  }

  private void setWearfloatingnearby(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearfloatingnearby = item;
  }

  private Item getWearwaist()
  {
    return wearwaist;
  }

  private void setWearwaist(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearwaist = item;
  }

  private Item getWearlegs()
  {
    return wearlegs;
  }

  private void setWearlegs(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearlegs = item;
  }

  private Item getWeareyes()
  {
    return weareyes;
  }

  private void setWeareyes(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.weareyes = item;
  }

  private Item getWearears()
  {
    return wearears;
  }

  private void setWearears(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearears = item;
  }

  private Item getWearaboutbody()
  {
    return wearaboutbody;
  }

  private void setWearaboutbody(Item item)
  {
    if (item != null && !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }

    this.wearaboutbody = item;
  }

  @Override
  public boolean destroyItem(Item item)
  {
    if (item == null)
    {
      throw new ItemException("That is not an item.");
    }
    if (!items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }
    if (isWearing(item))
    {
      throw new ItemException("That item is still being worn, and cannot be destroyed.");
    }
    if (item.containsItems())
    {
      throw new ItemException("That item contains items. Empty it first.");
    }
    return items.remove(item);
    // note: as the collection is an orphan, the delete
    // on the set will take place automatically.
  }

  /**
   * Returns the item being worn at that position.
   *
   * @param position the position to check.
   * @return the item worn at that position, may be null if nothing is being
   * worn there.
   */
  public Item wears(Wearing position)
  {
    if (position == null)
    {
      throw new MudException("You cannot wear an item on null");
    }
    switch (position)
    {
      case ABOUT_BODY:
        return getWearaboutbody();
      case FLOATING_NEARBY:
        return getWearfloatingnearby();
      case ON_ARMS:
        return getWeararms();
      case ON_EARS:
        return getWearears();
      case ON_EYES:
        return getWeareyes();
      case ON_FEET:
        return getWearfeet();
      case ON_HANDS:
        return getWearhands();
      case ON_HEAD:
        return getWearhead();
      case ON_LEFT_FINGER:
        return getWearleftfinger();
      case ON_LEFT_WRIST:
        return getWearleftwrist();
      case ON_LEGS:
        return getWearlegs();
      case ON_NECK:
        return getWearneck();
      case ON_RIGHT_FINGER:
        return getWearrightfinger();
      case ON_RIGHT_WRIST:
        return getWearrightwrist();
      case ON_TORSO:
        return getWeartorso();
      case ON_WAIST:
        return getWearwaist();
    }
    throw new MudException("You cannot wear an item on " + position);
  }

  /**
   * Indicates if an item is being worn,
   *
   * @param item the item to check, if null provided it will never be worn,
   *             obviously.
   * @return true if the item is being worn, false otherwise.
   */
  public boolean isWearing(Item item)
  {
    if (item == null)
    {
      return false;
    }
    return item == getWearaboutbody()
        || item == getWearfloatingnearby()
        || item == getWeararms()
        || item == getWearears()
        || item == getWeareyes()
        || item == getWearfeet()
        || item == getWearhands()
        || item == getWearhead()
        || item == getWearleftfinger()
        || item == getWearleftwrist()
        || item == getWearlegs()
        || item == getWearneck()
        || item == getWearrightfinger()
        || item == getWearrightwrist()
        || item == getWeartorso()
        || item == getWearwaist();
  }

  /**
   * Makes you wear an item at a specific position
   *
   * @param item     the item to be worn. In case this is null, it means an item
   *                 that used to be worn at this position will be removed.
   * @param position the position on which the item is to be worn
   */
  public void wear(Item item, Wearing position)
  {
    if (position == null)
    {
      throw new ItemException("You cannot wear an item on nothing.");
    }
    if (item != null && !item.isWearable(position))
    {
      throw new ItemException("You cannot wear that item there.");
    }
    switch (position)
    {
      case ABOUT_BODY:
        setWearaboutbody(item);
        break;
      case FLOATING_NEARBY:
        setWearfloatingnearby(item);
        break;
      case ON_ARMS:
        setWeararms(item);
        break;
      case ON_EARS:
        setWearears(item);
        break;
      case ON_EYES:
        setWeareyes(item);
        break;
      case ON_FEET:
        setWearfeet(item);
        break;
      case ON_HANDS:
        setWearhands(item);
        break;
      case ON_HEAD:
        setWearhead(item);
        break;
      case ON_LEFT_FINGER:
        setWearleftfinger(item);
        break;
      case ON_LEFT_WRIST:
        setWearleftwrist(item);
        break;
      case ON_LEGS:
        setWearlegs(item);
        break;
      case ON_NECK:
        setWearneck(item);
        break;
      case ON_RIGHT_FINGER:
        setWearrightfinger(item);
        break;
      case ON_RIGHT_WRIST:
        setWearrightwrist(item);
        break;
      case ON_TORSO:
        setWeartorso(item);
        break;
      case ON_WAIST:
        setWearwaist(item);
        break;
      default:
        throw new ItemException("You cannot wear " + (item == null ? "that" : item.getDescription()) + " on " + position + ".");
    }

  }

  /**
   * Returns the item being wielded at that position.
   *
   * @param position the position to check.
   * @return the item wielded at that position, may be null if nothing is being
   * wielded there.
   */
  public Item wields(Wielding position)
  {
    if (position == null)
    {
      throw new MudException("You cannot wield an item on null");
    }
    switch (position)
    {
      case WIELD_BOTH:
        return getWieldboth();
      case WIELD_RIGHT:
        return getWieldright();
      case WIELD_LEFT:
        return getWieldleft();
    }
    throw new MudException("You cannot wield an item on " + position);
  }

  /**
   * Indicates if an item is being wielded,
   *
   * @param item the item to check, if null provided it will never be wielded,
   *             obviously.
   * @return true if the item is being wielded, false otherwise.
   */
  public boolean isWielding(Item item)
  {
    if (item == null)
    {
      return false;
    }
    return item == getWieldboth()
        || item == getWieldleft()
        || item == getWieldright();
  }

  /**
   * Makes you wield an item at a specific position
   *
   * @param item     the item to be wielded. In case this is null, it means an item
   *                 that used to be wielded at this position will be removed.
   * @param position the position on which the item is to be wielded, lefthand,
   *                 righthand or with both hands.
   */
  public void wield(Item item, Wielding position)
  {
    if (position == null)
    {
      throw new ItemException("You cannot wield an item on null");
    }
    if (item != null && !item.isWieldable(position))
    {
      throw new ItemException("You cannot wield that item there.");
    }
    switch (position)
    {
      case WIELD_BOTH:
        if (getWieldleft() != null || getWieldright() != null)
        {
          throw new ItemException("You cannot wield something in both hands, when you are already wielding something " +
              "in either right or left hand.<br/>\r\n");
        }
        setWieldboth(item);
        break;
      case WIELD_LEFT:
        if (getWieldboth() != null)
        {
          throw new ItemException("You cannot wield something in your left hand, when you are already wielding " +
              "something in both hands.<br/>\r\n");
        }
        setWieldleft(item);
        break;
      case WIELD_RIGHT:
        if (getWieldboth() != null)
        {
          throw new ItemException("You cannot wield something in your right hand, when you are already wielding " +
              "something in both hands.<br/>\r\n");
        }
        setWieldright(item);
        break;
      default:
        throw new ItemException("You cannot wield " + (item == null ? "that" : item.getDescription()) + " on " + position);
    }
  }

  /**
   * Determines if the item is being used or not. If it is used, it is not
   * allowed to be dropped, get, drunk, eaten, etc.
   *
   * @param item the item to check
   * @return true if the item is available for use.
   */
  public boolean unused(Item item)
  {
    return !isWearing(item) && !isWielding(item);
  }

  public void drop(Item item)
  {
    if (item == null || !items.contains(item))
    {
      throw new ItemException("You do not have that item.");
    }
    if (!unused(item))
    {
      throw new ItemException("You are using this item.");
    }
    if (!getRoom().drop(item))
    {
      throw new ItemException("Unable to add item to the room.");
    }
    if (!items.remove(item))
    {
      throw new ItemException("Unable to remove item from inventory.");
    }
    item.drop(this, getRoom());
  }

  /**
   * Picked up an item from a room.
   *
   * @param item
   */
  public void get(Item item)
  {
    getRoom().get(item);
    if (!items.add(item))
    {
      throw new ItemException("Unable to add item to your inventory.");
    }
    item.get(this, getRoom());
  }

  /**
   * Transfers money from one person (this one) to another.
   *
   * @param newamount the amount of copper (base currency to move)
   * @param target    the target that is to receive said money
   * @throws MoneyException if the money amount is illegal, or the person simply
   *                        does not have that much money.
   */
  public void transferMoney(Integer newamount, Person target) throws MoneyException
  {
    if (target == null)
    {
      throw new MoneyException("You want to transfer money to whom?");
    }
    if (newamount == null || newamount <= 0)
    {
      throw new MoneyException("I beg your pardon?");
    }
    if (getMoney() < newamount)
    {
      throw new MoneyException("You do not have that much money.");
    }
    setCopper(getCopper() - newamount);
    target.setCopper(target.getCopper() + newamount);
  }

  public boolean isIgnoring(Person aSource)
  {
    return false;
  }

  /**
   * Indicates whether or not this person can receive items or money from other
   * players.
   *
   * @return
   */
  public abstract boolean canReceive();

  public void give(Item item, Person toperson)
  {
    if (!items.remove(item))
    {
      throw new ItemException("Unable to remove item from inventory.");
    }
    toperson.receive(item);
    item.give(toperson);
  }

  protected void receive(Item item)
  {
    items.add(item);
  }

  /**
   * Adds an {@link Item} to the inventory of this person. It is assumed that
   * this item has not yet been assigned to something (a person, a room or a
   * container (bag)).
   *
   * @param item the new item. May not be null.
   * @return the new item, null if unable to add.
   */
  @Override
  public Item addItem(Item item)
  {
    LOGGER.log(Level.FINE, "addItem {0}", item.getItemDefinition().getId());
    if (item.getBelongsTo() != null || item.getRoom() != null || item.getContainer() != null)
    {
      throw new MudException("Item already assigned.");
    }
    if (!items.add(item))
    {
      return null;
    }
    item.give(this);
    return item;
  }

  /**
   * A user is always visible, will return always true.
   *
   * @return true
   */
  public boolean getVisible()
  {
    return true;
  }

  /**
   * Empty implementation. A user is always visible.
   *
   * @param visible
   */
  public void setVisible(boolean visible)
  {
    // purposefully empty,
  }

  /**
   * The idea here is that some people are having problems with websockets, meaning we need to
   * fall back to the standard request-response model.
   *
   * @return true in case of websocket support, this is the default.
   */
  public boolean getWebsocketSupport()
  {
    return websocketSupport;
  }

  public void setWebsocketSupport(boolean websocketSupport)
  {
    this.websocketSupport = websocketSupport;
  }


  public Transform getTransform()
  {
    return new Transform(new Position(posx, posy, posz), new Rotation(rotx, roty, rotz), new Scale(scalex, scaley,
        scalez));
  }

  public void setTransform(Transform transform)
  {
    posx = transform.position().xPosition();
    posy = transform.position().yPosition();
    posz = transform.position().zPosition();

    rotx = transform.rotation().xRotation();
    roty = transform.rotation().yRotation();
    rotz = transform.rotation().zRotation();

    scalex = transform.scale().xScale();
    scaley = transform.scale().yScale();
    scalez = transform.scale().zScale();
  }
}
