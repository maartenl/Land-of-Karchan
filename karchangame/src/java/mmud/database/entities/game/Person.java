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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import mmud.Constants;
import mmud.Utils;
import mmud.database.enums.God;
import mmud.database.enums.Health;
import mmud.database.enums.Sex;
import mmud.exceptions.MudException;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A character in the game. Might be both a bot, a shopkeeper, a user, or an administrator.
 * Note: it contains a filter annotation which is Hibernate specific.
 * @author maartenl
 */
@Entity
@Table(name = "mm_usertable", catalog = "mmud", schema = "")
@NamedQueries(






{
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.findByName", query = "SELECT p FROM Person p WHERE p.name = :name"),
    @NamedQuery(name = "Person.fortunes", query = "SELECT p.name, p.copper FROM Person p WHERE p.god = 0 ORDER by p.copper DESC, p.name ASC"),
    @NamedQuery(name = "Person.who", query = "SELECT p FROM Person p WHERE p.god <=1 and p.active=1 "),
    @NamedQuery(name = "Person.status", query = "select p from Person p, Admin a WHERE a.name = p.name AND a.validuntil > CURRENT_DATE"),
    @NamedQuery(name = "Person.authorise", query = "select p from Person p WHERE p.name = :name and p.password = sha1(:password)")
})
@Filters(





{
    @Filter(name = "activePersons")
})
public class Person implements Serializable
{

    private static final Logger itsLog = LoggerFactory.getLogger(Person.class);
    private static final long serialVersionUID = 1L;
    private static final String NAME_REGEXP = "[a-zA-Z]{3,}";
    private static final String PASSWORD_REGEXP = ".{5,}";
    public static final String EMPTY_LOG = "";
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 3, max = 20)
    @Column(name = "name")
    @Pattern(regexp = NAME_REGEXP, message = "Invalid name")
    private String name;
    @Size(min = 5, max = 200)
    @Column(name = "address")
    private String address;
    @Size(max = 40)
    // TODO get this fixed properly with a sha1 hash code upon insert.
    @Column(name = "password")
    @Pattern(regexp = PASSWORD_REGEXP, message = "Invalid password")
    private String password;
    @Size(max = 254)
    @Column(name = "title")
    private String title;
    @Size(max = 80)
    @Column(name = "realname")
    private String realname;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 40)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "race")
    private String race;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "sex")
    private String sex;
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
    @Size(max = 40)
    @Column(name = "lok")
    private String lok;
    @Column(name = "whimpy")
    private Integer whimpy;
    @Column(name = "experience")
    private Integer experience;
    @Size(max = 20)
    // TODO recursion into the same table
    @Column(name = "fightingwho")
    private String fightingwho;
    @Column(name = "sleep")
    private Boolean sleep;
    @Column(name = "punishment")
    private Integer punishment;
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
    @Column(name = "lastlogin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastlogin;
    @Column(name = "birth")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birth;
    @Column(name = "god")
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
    @Size(max = 40)
    @Column(name = "cgiServerSoftware")
    private String cgiServerSoftware;
    @Size(max = 40)
    @Column(name = "cgiServerName")
    private String cgiServerName;
    @Size(max = 40)
    @Column(name = "cgiGatewayInterface")
    private String cgiGatewayInterface;
    @Size(max = 40)
    @Column(name = "cgiServerProtocol")
    private String cgiServerProtocol;
    @Size(max = 40)
    @Column(name = "cgiServerPort")
    private String cgiServerPort;
    @Size(max = 40)
    @Column(name = "cgiRequestMethod")
    private String cgiRequestMethod;
    @Size(max = 40)
    @Column(name = "cgiPathInfo")
    private String cgiPathInfo;
    @Size(max = 40)
    @Column(name = "cgiPathTranslated")
    private String cgiPathTranslated;
    @Size(max = 40)
    @Column(name = "cgiScriptName")
    private String cgiScriptName;
    @Size(max = 40)
    @Column(name = "cgiRemoteHost")
    private String cgiRemoteHost;
    @Size(max = 40)
    @Column(name = "cgiRemoteAddr")
    private String cgiRemoteAddr;
    @Size(max = 40)
    @Column(name = "cgiAuthType")
    private String cgiAuthType;
    @Size(max = 40)
    @Column(name = "cgiRemoteUser")
    private String cgiRemoteUser;
    @Size(max = 40)
    @Column(name = "cgiRemoteIdent")
    private String cgiRemoteIdent;
    @Size(max = 40)
    @Column(name = "cgiContentType")
    private String cgiContentType;
    @Size(max = 40)
    @Column(name = "cgiAccept")
    private String cgiAccept;
    @Size(max = 40)
    @Column(name = "cgiUserAgent")
    private String cgiUserAgent;
    @Column(name = "jumpmana")
    private Integer jumpmana;
    @Column(name = "jumpmove")
    private Integer jumpmove;
    @Column(name = "jumpvital")
    private Integer jumpvital;
    @Basic(optional = false)
    @NotNull
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @Lob
    @Size(max = 65535)
    @Column(name = "currentstate")
    private String state;
    @JoinColumn(name = "guild", referencedColumnName = "name")
    @ManyToOne(fetch = FetchType.LAZY)
    private Guild guild;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne(fetch = FetchType.LAZY)
    private Admin owner;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private Collection<Charattribute> charattributeCollection;
    @Transient
    private File theLogfile = null;
    @Transient
    private StringBuffer theLog = null;

    public Person()
    {

        this.copper = 0; // no money
        this.whimpy = 0; // no coward, either
        this.experience = 0; // no experience points to speak of
        this.fightingwho = null; // not fighting anybody
        this.sleep = false;
        this.punishment = 0;
        this.fightable = 0;
        this.vitals = MAX_VITALS;
        this.fysically = 100;
        this.mentally = 100;
        this.drinkstats = 0;
        this.eatstats = 0;
        this.active = 0;
        this.lastlogin = null;
        this.birth = new Date();
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
        this.creation = new Date();
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
     * Sets the name of the person. Should contain only alphabetical characters, but
     * has to have at least size of 3.
     * @param name the (new) name.
     * @throws MudException if the name is not allowed.
     */
    public void setName(String name) throws MudException
    {
        if (name != null)
        {
            Utils.checkRegexp(NAME_REGEXP, name);
        }
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPassword()
    {
        return password;
    }

    /**
     * Sets the password of the person. Can contain any character, but
     * has to have at least size of 5. You cannot set a password
     * this way, you can only set it for the first time, i.e. when creating
     * a new character.
     * @param password the new password.
     * @throws MudException if the password is not allowed.
     */
    public void setPassword(String password) throws MudException
    {
        if (this.password != null)
        {
            return;
        }

        if (password != null)
        {
            Utils.checkRegexp(PASSWORD_REGEXP, password);
        }

        this.password = password;
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
     * @param title
     *            String containing the title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getRealname()
    {
        return realname;
    }

    public void setRealname(String realname)
    {
        this.realname = realname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getRace()
    {
        return race;
    }

    public void setRace(String race)
    {
        this.race = race;
    }

    public Sex getSex()
    {
        return Sex.createFromString(sex);
    }

    public void setSex(Sex sex)
    {
        this.sex = sex.toString();
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getHeight()
    {
        return height;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public String getWidth()
    {
        return width;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    public String getComplexion()
    {
        return complexion;
    }

    public void setComplexion(String complexion)
    {
        this.complexion = complexion;
    }

    public String getEyes()
    {
        return eyes;
    }

    public void setEyes(String eyes)
    {
        this.eyes = eyes;
    }

    public String getFace()
    {
        return face;
    }

    public void setFace(String face)
    {
        this.face = face;
    }

    public String getHair()
    {
        return hair;
    }

    public void setHair(String hair)
    {
        this.hair = hair;
    }

    public String getBeard()
    {
        return beard;
    }

    public void setBeard(String beard)
    {
        this.beard = beard;
    }

    public String getArm()
    {
        return arm;
    }

    public void setArm(String arm)
    {
        this.arm = arm;
    }

    public String getLeg()
    {
        return leg;
    }

    public void setLeg(String leg)
    {
        this.leg = leg;
    }

    public Integer getCopper()
    {
        return copper;
    }

    public void setCopper(Integer copper)
    {
        this.copper = copper;
    }

    public Room getRoom()
    {
        return room;
    }

    public void setRoom(Room room)
    {
        this.room = room;
    }

    /**
     * retrieve sessionpassword
     *
     * @return String containing the session password
     */
    public String getLok()
    {
        return lok;
    }

    public void setLok(String lok)
    {
        this.lok = lok;
    }

    /**
     * get the setting for when to flee the fight.
     *
     * @return integer containing the setting
     */
    public Health getWhimpy()
    {
        return Health.get(whimpy);
    }

    /**
     * sets the whimpy of the character.
     *
     * @param whimpy
     *            Health enum containing the whimpy
     * @see #getWhimpy()
     */
    public void setWhimpy(Health whimpy)
    {
        if (whimpy == null)
        {
            this.whimpy = null;
            return;
        }
        this.whimpy = whimpy.getOrdinalValue();
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
     * @return integer between 0 and 1000. The closer to 1000 is the closer to
     *         the next level.
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

    public Boolean getSleep()
    {
        if (sleep == null)
        {
            return false;
        }
        return sleep;
    }

    public void setSleep(Boolean sleep)
    {
        this.sleep = sleep;
    }

    public Integer getPunishment()
    {
        return punishment;
    }

    public void setPunishment(Integer punishment)
    {
        this.punishment = punishment;
    }

    public Integer getFightable()
    {
        return fightable;
    }

    public void setFightable(Integer fightable)
    {
        this.fightable = fightable;
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
    public boolean getActive()
    {
        if (active == null)
        {
            return false;
        }
        return active.equals(1);
    }

    /**
     * Make a person actively playing the game (or not).
     * @param active boolean, true if he's playing, false otherwise.
     */
    public void setActive(Boolean active)
    {
        if (active == null)
        {
            this.active = null;
        }
        this.active = active ? 1 : 0;
    }

    public boolean isNewUser()
    {
        return lastlogin == null;
    }

    /**
     * Returns the last time the user was logged in.
     * Can return null, which means the user has never once logged on
     * and is new. (or not a user)
     * @return the date of last logged on.
     */
    public Date getLastlogin()
    {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin)
    {
        this.lastlogin = lastlogin;
    }

    public Date getBirth()
    {
        return birth;
    }

    public void setBirth(Date birth)
    {
        this.birth = birth;
    }

    public God getGod()
    {
        return God.get(god);
    }

    public void setGod(God god)
    {
        this.god = god.getValue();
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

    public String getCgiServerSoftware()
    {
        return cgiServerSoftware;
    }

    public void setCgiServerSoftware(String cgiServerSoftware)
    {
        this.cgiServerSoftware = cgiServerSoftware;
    }

    public String getCgiServerName()
    {
        return cgiServerName;
    }

    public void setCgiServerName(String cgiServerName)
    {
        this.cgiServerName = cgiServerName;
    }

    public String getCgiGatewayInterface()
    {
        return cgiGatewayInterface;
    }

    public void setCgiGatewayInterface(String cgiGatewayInterface)
    {
        this.cgiGatewayInterface = cgiGatewayInterface;
    }

    public String getCgiServerProtocol()
    {
        return cgiServerProtocol;
    }

    public void setCgiServerProtocol(String cgiServerProtocol)
    {
        this.cgiServerProtocol = cgiServerProtocol;
    }

    public String getCgiServerPort()
    {
        return cgiServerPort;
    }

    public void setCgiServerPort(String cgiServerPort)
    {
        this.cgiServerPort = cgiServerPort;
    }

    public String getCgiRequestMethod()
    {
        return cgiRequestMethod;
    }

    public void setCgiRequestMethod(String cgiRequestMethod)
    {
        this.cgiRequestMethod = cgiRequestMethod;
    }

    public String getCgiPathInfo()
    {
        return cgiPathInfo;
    }

    public void setCgiPathInfo(String cgiPathInfo)
    {
        this.cgiPathInfo = cgiPathInfo;
    }

    public String getCgiPathTranslated()
    {
        return cgiPathTranslated;
    }

    public void setCgiPathTranslated(String cgiPathTranslated)
    {
        this.cgiPathTranslated = cgiPathTranslated;
    }

    public String getCgiScriptName()
    {
        return cgiScriptName;
    }

    public void setCgiScriptName(String cgiScriptName)
    {
        this.cgiScriptName = cgiScriptName;
    }

    public String getCgiRemoteHost()
    {
        return cgiRemoteHost;
    }

    public void setCgiRemoteHost(String cgiRemoteHost)
    {
        this.cgiRemoteHost = cgiRemoteHost;
    }

    public String getCgiRemoteAddr()
    {
        return cgiRemoteAddr;
    }

    public void setCgiRemoteAddr(String cgiRemoteAddr)
    {
        this.cgiRemoteAddr = cgiRemoteAddr;
    }

    public String getCgiAuthType()
    {
        return cgiAuthType;
    }

    public void setCgiAuthType(String cgiAuthType)
    {
        this.cgiAuthType = cgiAuthType;
    }

    public String getCgiRemoteUser()
    {
        return cgiRemoteUser;
    }

    public void setCgiRemoteUser(String cgiRemoteUser)
    {
        this.cgiRemoteUser = cgiRemoteUser;
    }

    public String getCgiRemoteIdent()
    {
        return cgiRemoteIdent;
    }

    public void setCgiRemoteIdent(String cgiRemoteIdent)
    {
        this.cgiRemoteIdent = cgiRemoteIdent;
    }

    public String getCgiContentType()
    {
        return cgiContentType;
    }

    public void setCgiContentType(String cgiContentType)
    {
        this.cgiContentType = cgiContentType;
    }

    public String getCgiAccept()
    {
        return cgiAccept;
    }

    public void setCgiAccept(String cgiAccept)
    {
        this.cgiAccept = cgiAccept;
    }

    public String getCgiUserAgent()
    {
        return cgiUserAgent;
    }

    public void setCgiUserAgent(String cgiUserAgent)
    {
        this.cgiUserAgent = cgiUserAgent;
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

    public Date get()
    {
        return creation;
    }

    public void setCreation(Date creation)
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

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public Guild getGuild()
    {
        return guild;
    }

    public void setGuild(Guild guild)
    {
        this.guild = guild;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
    }

    public Collection<Charattribute> getCharattributeCollection()
    {
        return charattributeCollection;
    }

    public void setCharattributeCollection(Collection<Charattribute> charattributeCollection)
    {
        this.charattributeCollection = charattributeCollection;
    }

    private void addDescriptionPiece(StringBuilder builder, String part)
    {
        if (part != null && !"none".equalsIgnoreCase(part))
        {
            builder.append(", ").append(part);
        }
    }

    /**
     * Returns the description of the character. All characteristics, if
     * possible, are taken into account. Does not provide any info on the name
     * or title. It contains a strictly visual cue.
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
     * verify sessionpassword
     *
     * @param aSessionPassword the sessionpassword to be verified.
     * @return boolean, true if the sessionpassword provided is an exact match
     * with the original sessionpassword.
     */
    public boolean verifySessionPassword(String aSessionPassword)
    {
        itsLog.debug("entering verifySessionPassword");
        if (!isUser())
        {
            // is not a common user, therefore does not have a session password.
            return false;
        }
        return lok == null ? false : lok.equals(aSessionPassword);
    }

    /**
     * Indicates if this is a common user. This means it indicates that it
     * is basically someone behind a keyboard.
     *
     * @return true if it is a common user (or a god/administrator),
     * false otherwise.
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
     * generate a session password to be used by player during game session. Use
     * {@link #getLok() } to return a String containing 25 random digits,
     * capitals and smallcaps.
     */
    public void generateSessionPassword()
    {
        char[] myCharArray =
        {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'
        };
        StringBuilder myString = new StringBuilder(26);
        Random myRandom = new Random();
        for (int i = 1; i < 26; i++)
        {

            myString.append(myCharArray[myRandom.nextInt(myCharArray.length)]);
        }
        lok = myString.toString();
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
     * activate a character
     */
    public void activate(String address) throws MudException
    {
        if (!isUser())
        {
            throw new MudException("user not a user");
        }
        this.setAddress(address);
        this.setLastlogin(new Date());
        this.setActive(true);
        createLog();
    }

    /**
     * Clears the log.
     */
    public void clearLog() throws MudException
    {
        createLog();
    }

    /**
     * writes a message to the log file of the character that contains all
     * communication and messages. The sentence will start with a capital.
     *
     * <p><b>Important!</b> : Use this method only for Environmental communication,
     * as it does not check the Ignore Flag. Use the writeMessage(Person
     * aSource, String aMessage) for specific communication between users.</p>
     * TODO : move the logging to a protected hashtable in a singleton bean
     * containing StringBuffers.
     *
     * @param aMessage
     *            the message to be written to the logfile.
     * @see #writeMessage(Person aSource, Person aTarget, String aMessage)
     * @see #writeMessage(Person aSource, String aMessage)
     */
    public void writeMessage(String aMessage) throws MudException
    {

        if (aMessage == null)
        {
            return;
        }
        try
        {
            aMessage = Utils.security(aMessage);
        } catch (PolicyException | ScanException ex)
        {
            throw new MudException(ex);
        }

        int i = 0;
        int _container = 0;
        int foundit = -1;
        while ((i < aMessage.length()) && (foundit == -1))
        {
            if (_container == 0)
            {
                if (aMessage.charAt(i) == '<')
                {
                    _container = 1;
                } else if (aMessage.charAt(i) != ' ')
                {
                    foundit = i;
                }
            } else if (_container == 1)
            {
                if (aMessage.charAt(i) == '>')
                {
                    _container = 0;
                }
            }
            i++;
        }
        if (foundit != -1)
        {
            aMessage = aMessage.substring(0, foundit)
                    + Character.toUpperCase(aMessage.charAt(foundit))
                    + aMessage.substring(foundit + 1);
        }
        try (FileWriter myFileWriter = new FileWriter(getLogfile(), true))
        {
            myFileWriter.write(aMessage, 0, aMessage.length());

        } catch (IOException e)
        {
            throw new MudException("error writing message", e);
        }
    }

    /**
     * writes a message to the log file of the character that contains all
     * communication and messages. The message will be <I>interpreted</I> by
     * replacing the following values by the following other values:
     * <TABLE>
     * <TR>
     * <TD><B>REPLACE</B></TD>
     * <TD><B>WITH (if target)</B></TD>
     * <TD><B>WITH (if not target)</B></TD>
     * </TR>
     * <TR>
     * <TD>%TNAME</TD>
     * <TD>you</TD>
     * <TD>name</TD>
     * </TR>
     * <TR>
     * <TD>%TNAMESELF</TD>
     * <TD>yourself</TD>
     * <TD>name</TD>
     * </TR>
     * <TR>
     * <TD>%THISHER</TD>
     * <TD>your</TD>
     * <TD>his/her</TD>
     * </TR>
     * <TR>
     * <TD>%THIMHER</TD>
     * <TD>you</TD>
     * <TD>him/her</TD>
     * </TR>
     * <TR>
     * <TD>%THESHE</TD>
     * <TD>you</TD>
     * <TD>he/she</TD>
     * </TR>
     * <TR>
     * <TD>%TISARE</TD>
     * <TD>are</TD>
     * <TD>is</TD>
     * </TR>
     * <TR>
     * <TD>%THASHAVE</TD>
     * <TD>have</TD>
     * <TD>has</TD>
     * </TR>
     * <TR>
     * <TD>%TYOUPOSS</TD>
     * <TD>your</TD>
     * <TD>name + s</TD>
     * </TR>
     * <TR>
     * <TD></TD>
     * <TD></TD>
     * <TD></TD>
     * </TR>
     * </TABLE>
     *
     * @param aMessage
     *            the message to be written to the logfile.
     * @param aSource
     *            the source of the message, the thing originating the message.
     * @param aTarget
     *            the target of the message, could be null if there is not
     *            target for this specific message.
     * @see #writeMessage(String aMessage)
     * @see #writeMessage(Person aSource, String aMessage)
     */
    public void writeMessage(Person aSource, Person aTarget, String aMessage) throws MudException
    {
        // TODO : ignoring people
//		if (aSource.isIgnored(this))
//		{
//			return;
//		}
        String message = aMessage;
        if (aTarget == this)
        {
            message = message.replaceAll("%TNAMESELF", "yourself");
            message = message.replaceAll("%TNAME", "you");
            message = message.replaceAll("%THISHER", "your");
            message = message.replaceAll("%THIMHER", "you");
            message = message.replaceAll("%THESHE", "you");
            message = message.replaceAll("%TISARE", "are");
            message = message.replaceAll("%THASHAVE", "have");
            message = message.replaceAll("%TYOUPOSS", "your");
        } else
        {
            message = message.replaceAll("%TNAMESELF", aTarget.getName());
            message = message.replaceAll("%TNAME", aTarget.getName());
            message = message.replaceAll("%THISHER", (aTarget.getSex().posession()));
            message = message.replaceAll("%THIMHER", (aTarget.getSex().indirect()));
            message = message.replaceAll("%THESHE", (aTarget.getSex().direct()));
            message = message.replaceAll("%TISARE", "is");
            message = message.replaceAll("%THASHAVE", "has");
            message = message.replaceAll("%TYOUPOSS", aTarget.getName() + "s");
        }
        writeMessage(aSource, message);
    }

    /**
     * writes a message to the log file of the character that contains all
     * communication and messages. The message will be <I>interpreted</I> by
     * replacing the following values by the following other values:
     * <TABLE>
     * <TR>
     * <TD><B>REPLACE</B></TD>
     * <TD><B>WITH (if source)</B></TD>
     * <TD><B>WITH (if not source)</B></TD>
     * </TR>
     * <TR>
     * <TD>%SNAME</TD>
     * <TD>you</TD>
     * <TD>name</TD>
     * </TR>
     * <TR>
     * <TD>%SNAMESELF</TD>
     * <TD>yourself</TD>
     * <TD>name</TD>
     * </TR>
     * <TR>
     * <TD>%SHISHER</TD>
     * <TD>your</TD>
     * <TD>his/her</TD>
     * </TR>
     * <TR>
     * <TD>%SHIMHER</TD>
     * <TD>you</TD>
     * <TD>him/her</TD>
     * </TR>
     * <TR>
     * <TD>%SHESHE</TD>
     * <TD>you</TD>
     * <TD>he/she</TD>
     * </TR>
     * <TR>
     * <TD>%SISARE</TD>
     * <TD>are</TD>
     * <TD>is</TD>
     * </TR>
     * <TR>
     * <TD>%SHASHAVE</TD>
     * <TD>have</TD>
     * <TD>has</TD>
     * </TR>
     * <TR>
     * <TD>%SYOUPOSS</TD>
     * <TD>your</TD>
     * <TD>name + s</TD>
     * </TR>
     * <TR>
     * <TD>%VERB1</TD>
     * <TD></TD>
     * <TD>es</TD>
     * </TR>
     * <TR>
     * <TD>%VERB2</TD>
     * <TD></TD>
     * <TD>s</TD>
     * </TR>
     * <TR>
     * <TD></TD>
     * <TD></TD>
     * <TD></TD>
     * </TR>
     * </TABLE>
     *
     * @param aMessage
     *            the message to be written to the logfile.
     * @param aSource
     *            the source of the message, the thing originating the message.
     * @see #writeMessage(Person aSource, Person aTarget, String aMessage)
     * @see #writeMessage(String aMessage)
     */
    public void writeMessage(Person aSource, String aMessage) throws MudException
    {
        // TODO : ignoring people
//		if (aSource.isIgnored(this))
//		{
//			return;
//		}
        String message = aMessage;
        if (aSource == this)
        {
            message = message.replaceAll("%SNAMESELF", "yourself");
            message = message.replaceAll("%SNAME", "you");
            message = message.replaceAll("%SHISHER", "your");
            message = message.replaceAll("%SHIMHER", "you");
            message = message.replaceAll("%SHESHE", "you");
            message = message.replaceAll("%SISARE", "are");
            message = message.replaceAll("%SHASHAVE", "have");
            message = message.replaceAll("%SYOUPOSS", "your");
            message = message.replaceAll("%VERB1", "");
            message = message.replaceAll("%VERB2", "");
        } else
        {
            message = message.replaceAll("%SNAMESELF", aSource.getName());
            message = message.replaceAll("%SNAME", aSource.getName());
            message = message.replaceAll("%SHISHER", (aSource.getSex().posession()));
            message = message.replaceAll("%SHIMHER", (aSource.getSex().indirect()));
            message = message.replaceAll("%SHESHE", (aSource.getSex().direct()));
            message = message.replaceAll("%SISARE", "is");
            message = message.replaceAll("%SHASHAVE", "has");
            message = message.replaceAll("%SYOUPOSS", aSource.getName() + "s");
            message = message.replaceAll("%VERB1", "es");
            message = message.replaceAll("say%VERB2", "says");
            message = message.replaceAll("y%VERB2", "ies");
            message = message.replaceAll("%VERB2", "s");
        }
        writeMessage(message);
    }

    /**
     * creates a new log file and deletes the old one.
     *
     * @throws MudException
     *             which indicates probably that the log file could not be
     *             created. Possibly due to either permissions or the directory
     *             does not exist.
     */
    private void createLog() throws MudException
    {
        if (getLogfile().exists())
        {
            getLogfile().delete();
        }
        try
        {
            getLogfile().createNewFile();
        } catch (IOException e)
        {
            throw new MudException("Error creating logfile for " + getName()
                    + " in " + Constants.mudfilepath, e);
        }
    }

    private File getLogfile()
    {
        if (theLogfile == null)
        {
            theLogfile = new File(Constants.mudfilepath, getName() + ".log");
        }
        return theLogfile;
    }

    /**
     * deactivate a character (usually because someone typed quit.)
     */
    public void deactivate()
    {
        setActive(false);
        setLok(null);
        setLastlogin(new Date());
    }

    private void readLog() throws MudException
    {
        File file = getLogfile();

        try (BufferedReader reader = new BufferedReader(
                        new FileReader(file)))
        {
            theLog = new StringBuffer(1000);

            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1)
            {
                theLog.append(buf, 0, numRead);
            }
        } catch (FileNotFoundException ex)
        {
            try
            {
                throw new MudException("Logfile of " + name + " (" + file.getCanonicalPath() + ") not found", ex);
            } catch (IOException ex1)
            {
                throw new MudException("Logfile of " + name, ex);
                // surrender peacefully with your hands up!
            }
        } catch (IOException ex)
        {
            try
            {
                throw new MudException("Error reading logfile of " + name + " (" + file.getCanonicalPath() + ")", ex);
            } catch (IOException ex1)
            {
                throw new MudException("Error reading logfile of " + name, ex);
                // surrender peacefully with your hands up!
            }
        }
    }

    /**
     * Returns the log starting from the offset, or an empty string if
     * the offset is past the length of the log.Can also contain the empty string, if the log happens to be empty.
     * @param offset the offset from whence to read the log. Offset starts with 0
     * and is inclusive.
     * @return a String, part of the Log.
     * @throws MudException in case of accidents with reading the log, or a negative
     * offset.
     */
    public String getLog(Integer offset) throws MudException
    {
        if (offset == null)
        {
            offset = 0;
        }
        if (theLog == null)
        {
            readLog();
        }
        if (offset >= theLog.length())
        {
            return EMPTY_LOG;
        }
        if (offset < 0)
        {
            throw new MudException("Attempting to get log with negative offset.");
        }
        return theLog.substring(offset);
    }

    /**
     * Runs a specific command. If this person appears to be asleep, the only
     * possible commands are "quit" and "awaken". If the command found returns a
     * false, i.e. nothing is executed, the method will call the standard
     * BogusCommand.
     *
     * @param command
     *            the command to be run
     * @return DisplayInterface object containing the result of the command executed.
     */
    public DisplayInterface runCommand(String command)
    {
        return null;
//		Command boguscommand = new BogusCommand(".+");
//		Command command = boguscommand;
//		if (isaSleep())
//		{
//			if (aCommand.trim().equalsIgnoreCase("awaken"))
//			{
//				command = new AwakenCommand("awaken");
//			} else if (aCommand.trim().equalsIgnoreCase("quit"))
//			{
//				command = new QuitCommand("quit");
//			} else
//			{
//				command = new AlreadyAsleepCommand(".+");
//			}
//			command.setCommand(aCommand);
//			command.start(this);
//			return command.getResult();
//		}
//
//		Collection myCol = Constants.getCommand(aCommand);
//		Iterator myI = myCol.iterator();
//		while (myI.hasNext())
//		{
//			command = (Command) myI.next();
//			command = command.createCommand();
//			command.setCommand(aCommand);
//			if (command.start(this))
//			{
//				return command.getResult();
//			}
//		}
//		throw new MudException("We shouldn't reach this point."
//				+ " At least the bogus command should execute successfully."
//				+ " This is probably a big bug!.");
    }
}
