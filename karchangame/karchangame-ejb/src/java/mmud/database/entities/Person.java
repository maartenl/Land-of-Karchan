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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
@Table(name = "mm_usertable")
@NamedQueries(
{
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.findByName", query = "SELECT p FROM Person p WHERE p.name = :name"),
    @NamedQuery(name = "Person.findByAddress", query = "SELECT p FROM Person p WHERE p.address = :address"),
    @NamedQuery(name = "Person.findByPassword", query = "SELECT p FROM Person p WHERE p.password = :password"),
    @NamedQuery(name = "Person.findByTitle", query = "SELECT p FROM Person p WHERE p.title = :title"),
    @NamedQuery(name = "Person.findByRealname", query = "SELECT p FROM Person p WHERE p.realname = :realname"),
    @NamedQuery(name = "Person.findByEmail", query = "SELECT p FROM Person p WHERE p.email = :email"),
    @NamedQuery(name = "Person.findByRace", query = "SELECT p FROM Person p WHERE p.race = :race"),
    @NamedQuery(name = "Person.findBySex", query = "SELECT p FROM Person p WHERE p.sex = :sex"),
    @NamedQuery(name = "Person.findByAge", query = "SELECT p FROM Person p WHERE p.age = :age"),
    @NamedQuery(name = "Person.findByLength", query = "SELECT p FROM Person p WHERE p.length = :length"),
    @NamedQuery(name = "Person.findByWidth", query = "SELECT p FROM Person p WHERE p.width = :width"),
    @NamedQuery(name = "Person.findByComplexion", query = "SELECT p FROM Person p WHERE p.complexion = :complexion"),
    @NamedQuery(name = "Person.findByEyes", query = "SELECT p FROM Person p WHERE p.eyes = :eyes"),
    @NamedQuery(name = "Person.findByFace", query = "SELECT p FROM Person p WHERE p.face = :face"),
    @NamedQuery(name = "Person.findByHair", query = "SELECT p FROM Person p WHERE p.hair = :hair"),
    @NamedQuery(name = "Person.findByBeard", query = "SELECT p FROM Person p WHERE p.beard = :beard"),
    @NamedQuery(name = "Person.findByArm", query = "SELECT p FROM Person p WHERE p.arm = :arm"),
    @NamedQuery(name = "Person.findByLeg", query = "SELECT p FROM Person p WHERE p.leg = :leg"),
    @NamedQuery(name = "Person.findByCopper", query = "SELECT p FROM Person p WHERE p.copper = :copper"),
    @NamedQuery(name = "Person.findByRoom", query = "SELECT p FROM Person p WHERE p.room = :room"),
    @NamedQuery(name = "Person.findByLok", query = "SELECT p FROM Person p WHERE p.lok = :lok"),
    @NamedQuery(name = "Person.findByWhimpy", query = "SELECT p FROM Person p WHERE p.whimpy = :whimpy"),
    @NamedQuery(name = "Person.findByExperience", query = "SELECT p FROM Person p WHERE p.experience = :experience"),
    @NamedQuery(name = "Person.findByFightingwho", query = "SELECT p FROM Person p WHERE p.fightingwho = :fightingwho"),
    @NamedQuery(name = "Person.findBySleep", query = "SELECT p FROM Person p WHERE p.sleep = :sleep"),
    @NamedQuery(name = "Person.findByPunishment", query = "SELECT p FROM Person p WHERE p.punishment = :punishment"),
    @NamedQuery(name = "Person.findByFightable", query = "SELECT p FROM Person p WHERE p.fightable = :fightable"),
    @NamedQuery(name = "Person.findByVitals", query = "SELECT p FROM Person p WHERE p.vitals = :vitals"),
    @NamedQuery(name = "Person.findByFysically", query = "SELECT p FROM Person p WHERE p.fysically = :fysically"),
    @NamedQuery(name = "Person.findByMentally", query = "SELECT p FROM Person p WHERE p.mentally = :mentally"),
    @NamedQuery(name = "Person.findByDrinkstats", query = "SELECT p FROM Person p WHERE p.drinkstats = :drinkstats"),
    @NamedQuery(name = "Person.findByEatstats", query = "SELECT p FROM Person p WHERE p.eatstats = :eatstats"),
    @NamedQuery(name = "Person.findByActive", query = "SELECT p FROM Person p WHERE p.active = :active"),
    @NamedQuery(name = "Person.findByLastlogin", query = "SELECT p FROM Person p WHERE p.lastlogin = :lastlogin"),
    @NamedQuery(name = "Person.findByBirth", query = "SELECT p FROM Person p WHERE p.birth = :birth"),
    @NamedQuery(name = "Person.findByGod", query = "SELECT p FROM Person p WHERE p.god = :god"),
    @NamedQuery(name = "Person.findByStrength", query = "SELECT p FROM Person p WHERE p.strength = :strength"),
    @NamedQuery(name = "Person.findByIntelligence", query = "SELECT p FROM Person p WHERE p.intelligence = :intelligence"),
    @NamedQuery(name = "Person.findByDexterity", query = "SELECT p FROM Person p WHERE p.dexterity = :dexterity"),
    @NamedQuery(name = "Person.findByConstitution", query = "SELECT p FROM Person p WHERE p.constitution = :constitution"),
    @NamedQuery(name = "Person.findByWisdom", query = "SELECT p FROM Person p WHERE p.wisdom = :wisdom"),
    @NamedQuery(name = "Person.findByPractises", query = "SELECT p FROM Person p WHERE p.practises = :practises"),
    @NamedQuery(name = "Person.findByTraining", query = "SELECT p FROM Person p WHERE p.training = :training"),
    @NamedQuery(name = "Person.findByBandage", query = "SELECT p FROM Person p WHERE p.bandage = :bandage"),
    @NamedQuery(name = "Person.findByAlignment", query = "SELECT p FROM Person p WHERE p.alignment = :alignment"),
    @NamedQuery(name = "Person.findByManastats", query = "SELECT p FROM Person p WHERE p.manastats = :manastats"),
    @NamedQuery(name = "Person.findByMovementstats", query = "SELECT p FROM Person p WHERE p.movementstats = :movementstats"),
    @NamedQuery(name = "Person.findByMaxmana", query = "SELECT p FROM Person p WHERE p.maxmana = :maxmana"),
    @NamedQuery(name = "Person.findByMaxmove", query = "SELECT p FROM Person p WHERE p.maxmove = :maxmove"),
    @NamedQuery(name = "Person.findByMaxvital", query = "SELECT p FROM Person p WHERE p.maxvital = :maxvital"),
    @NamedQuery(name = "Person.findByCgiServerSoftware", query = "SELECT p FROM Person p WHERE p.cgiServerSoftware = :cgiServerSoftware"),
    @NamedQuery(name = "Person.findByCgiServerName", query = "SELECT p FROM Person p WHERE p.cgiServerName = :cgiServerName"),
    @NamedQuery(name = "Person.findByCgiGatewayInterface", query = "SELECT p FROM Person p WHERE p.cgiGatewayInterface = :cgiGatewayInterface"),
    @NamedQuery(name = "Person.findByCgiServerProtocol", query = "SELECT p FROM Person p WHERE p.cgiServerProtocol = :cgiServerProtocol"),
    @NamedQuery(name = "Person.findByCgiServerPort", query = "SELECT p FROM Person p WHERE p.cgiServerPort = :cgiServerPort"),
    @NamedQuery(name = "Person.findByCgiRequestMethod", query = "SELECT p FROM Person p WHERE p.cgiRequestMethod = :cgiRequestMethod"),
    @NamedQuery(name = "Person.findByCgiPathInfo", query = "SELECT p FROM Person p WHERE p.cgiPathInfo = :cgiPathInfo"),
    @NamedQuery(name = "Person.findByCgiPathTranslated", query = "SELECT p FROM Person p WHERE p.cgiPathTranslated = :cgiPathTranslated"),
    @NamedQuery(name = "Person.findByCgiScriptName", query = "SELECT p FROM Person p WHERE p.cgiScriptName = :cgiScriptName"),
    @NamedQuery(name = "Person.findByCgiRemoteHost", query = "SELECT p FROM Person p WHERE p.cgiRemoteHost = :cgiRemoteHost"),
    @NamedQuery(name = "Person.findByCgiRemoteAddr", query = "SELECT p FROM Person p WHERE p.cgiRemoteAddr = :cgiRemoteAddr"),
    @NamedQuery(name = "Person.findByCgiAuthType", query = "SELECT p FROM Person p WHERE p.cgiAuthType = :cgiAuthType"),
    @NamedQuery(name = "Person.findByCgiRemoteUser", query = "SELECT p FROM Person p WHERE p.cgiRemoteUser = :cgiRemoteUser"),
    @NamedQuery(name = "Person.findByCgiRemoteIdent", query = "SELECT p FROM Person p WHERE p.cgiRemoteIdent = :cgiRemoteIdent"),
    @NamedQuery(name = "Person.findByCgiContentType", query = "SELECT p FROM Person p WHERE p.cgiContentType = :cgiContentType"),
    @NamedQuery(name = "Person.findByCgiAccept", query = "SELECT p FROM Person p WHERE p.cgiAccept = :cgiAccept"),
    @NamedQuery(name = "Person.findByCgiUserAgent", query = "SELECT p FROM Person p WHERE p.cgiUserAgent = :cgiUserAgent"),
    @NamedQuery(name = "Person.findByJumpmana", query = "SELECT p FROM Person p WHERE p.jumpmana = :jumpmana"),
    @NamedQuery(name = "Person.findByJumpmove", query = "SELECT p FROM Person p WHERE p.jumpmove = :jumpmove"),
    @NamedQuery(name = "Person.findByJumpvital", query = "SELECT p FROM Person p WHERE p.jumpvital = :jumpvital"),
    @NamedQuery(name = "Person.findByCreation", query = "SELECT p FROM Person p WHERE p.creation = :creation")
})
public class Person implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Size(max = 200)
    @Column(name = "address")
    private String address;
    @Size(max = 40)
    @Column(name = "password")
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
    @Column(name = "length")
    private String length;
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
    @Column(name = "room")
    private Integer room;
    @Size(max = 40)
    @Column(name = "lok")
    private String lok;
    @Column(name = "whimpy")
    private Integer whimpy;
    @Column(name = "experience")
    private Integer experience;
    @Size(max = 20)
    @Column(name = "fightingwho")
    private String fightingwho;
    @Column(name = "sleep")
    private Integer sleep;
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
    @Column(name = "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @Lob
    @Size(max = 65535)
    @Column(name = "notes")
    private String notes;
    @Lob
    @Size(max = 65535)
    @Column(name = "state")
    private String state;
    @JoinTable(name = "mm_ignore", joinColumns =
    {
        @JoinColumn(name = "fromperson", referencedColumnName = "name")
    }, inverseJoinColumns =
    {
        @JoinColumn(name = "toperson", referencedColumnName = "name")
    })
    @ManyToMany
    private Set<Person> personSet;
    @ManyToMany(mappedBy = "personSet")
    private Set<Person> personSet1;
    @JoinColumn(name = "guild", referencedColumnName = "name")
    @ManyToOne
    private Guild guild;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "belongsto")
    private Set<Charitem> charitemSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "toname")
    private Set<Mail> mailSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "name")
    private Set<Mail> mailSet1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private Set<Charattribute> charattributeSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private Set<Answers> answersSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bossname")
    private Set<Guild> guildSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person")
    private Set<Macro> macroSet;

    public Person()
    {
    }

    public Person(String name)
    {
        this.name = name;
    }

    public Person(String name, String race, String sex, Date creation)
    {
        this.name = name;
        this.race = race;
        this.sex = sex;
        this.creation = creation;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
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

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getTitle()
    {
        return title;
    }

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

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    public String getLength()
    {
        return length;
    }

    public void setLength(String length)
    {
        this.length = length;
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

    public Integer getRoom()
    {
        return room;
    }

    public void setRoom(Integer room)
    {
        this.room = room;
    }

    public String getLok()
    {
        return lok;
    }

    public void setLok(String lok)
    {
        this.lok = lok;
    }

    public Integer getWhimpy()
    {
        return whimpy;
    }

    public void setWhimpy(Integer whimpy)
    {
        this.whimpy = whimpy;
    }

    public Integer getExperience()
    {
        return experience;
    }

    public void setExperience(Integer experience)
    {
        this.experience = experience;
    }

    public String getFightingwho()
    {
        return fightingwho;
    }

    public void setFightingwho(String fightingwho)
    {
        this.fightingwho = fightingwho;
    }

    public Integer getSleep()
    {
        return sleep;
    }

    public void setSleep(Integer sleep)
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
        return vitals;
    }

    public void setVitals(Integer vitals)
    {
        this.vitals = vitals;
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

    public Integer getActive()
    {
        return active;
    }

    public void setActive(Integer active)
    {
        this.active = active;
    }

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

    public Integer getGod()
    {
        return god;
    }

    public void setGod(Integer god)
    {
        this.god = god;
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

    public Date getCreation()
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

    public Set<Person> getPersonSet()
    {
        return personSet;
    }

    public void setPersonSet(Set<Person> personSet)
    {
        this.personSet = personSet;
    }

    public Set<Person> getPersonSet1()
    {
        return personSet1;
    }

    public void setPersonSet1(Set<Person> personSet1)
    {
        this.personSet1 = personSet1;
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

    public Set<Charitem> getCharitemSet()
    {
        return charitemSet;
    }

    public void setCharitemSet(Set<Charitem> charitemSet)
    {
        this.charitemSet = charitemSet;
    }

    public Set<Mail> getMailSet()
    {
        return mailSet;
    }

    public void setMailSet(Set<Mail> mailSet)
    {
        this.mailSet = mailSet;
    }

    public Set<Mail> getMailSet1()
    {
        return mailSet1;
    }

    public void setMailSet1(Set<Mail> mailSet1)
    {
        this.mailSet1 = mailSet1;
    }

    public Set<Charattribute> getCharattributeSet()
    {
        return charattributeSet;
    }

    public void setCharattributeSet(Set<Charattribute> charattributeSet)
    {
        this.charattributeSet = charattributeSet;
    }

    public Set<Answers> getAnswersSet()
    {
        return answersSet;
    }

    public void setAnswersSet(Set<Answers> answersSet)
    {
        this.answersSet = answersSet;
    }

    public Set<Guild> getGuildSet()
    {
        return guildSet;
    }

    public void setGuildSet(Set<Guild> guildSet)
    {
        this.guildSet = guildSet;
    }

    public Set<Macro> getMacroSet()
    {
        return macroSet;
    }

    public void setMacroSet(Set<Macro> macroSet)
    {
        this.macroSet = macroSet;
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
        return "mmud.database.entities.Person[ name=" + name + " ]";
    }

}
