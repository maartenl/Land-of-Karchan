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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "mm_usertable")
@DiscriminatorColumn(
        name = "god",
        discriminatorType = DiscriminatorType.INTEGER)
@NamedQueries(
{
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
    @NamedQuery(name = "Person.findByName", query = "SELECT p FROM Person p WHERE p.name = :name")
})
public abstract class Person implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;


    @Size(max = 254)
    @Column(name = "title")
    private String title;


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
    private String personal_description;


    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;

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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
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

    public Date getBirth()
    {
        return birth;
    }

    public void setBirth(Date birth)
    {
        this.birth = birth;
    }

    /**
     * what this character is, 0=default user, 1=god user (not used currently), 2=bot, 3=mob, 4=shopkeeper
     * @return
     */
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
        return personal_description;
    }

    public void setState(String state)
    {
        this.personal_description = state;
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
