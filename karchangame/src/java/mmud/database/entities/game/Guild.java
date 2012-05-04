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
import mmud.database.entities.characters.Person;

/**
 *
 * @author maartenl
 */
@Entity
@Table(name = "mm_guilds", catalog = "mmud", schema = "")
@NamedQueries(


{
    @NamedQuery(name = "Guild.findAll", query = "SELECT g FROM Guild g ORDER BY g.title"),
    @NamedQuery(name = "Guild.findByName", query = "SELECT g FROM Guild g WHERE g.name = :name"),
    @NamedQuery(name = "Guild.findByTitle", query = "SELECT g FROM Guild g WHERE g.title = :title"),
    @NamedQuery(name = "Guild.findByDaysguilddeath", query = "SELECT g FROM Guild g WHERE g.daysguilddeath = :daysguilddeath"),
    @NamedQuery(name = "Guild.findByMaxguilddeath", query = "SELECT g FROM Guild g WHERE g.maxguilddeath = :maxguilddeath"),
    @NamedQuery(name = "Guild.findByMinguildmembers", query = "SELECT g FROM Guild g WHERE g.minguildmembers = :minguildmembers"),
    @NamedQuery(name = "Guild.findByMinguildlevel", query = "SELECT g FROM Guild g WHERE g.minguildlevel = :minguildlevel"),
    @NamedQuery(name = "Guild.findByGuildurl", query = "SELECT g FROM Guild g WHERE g.guildurl = :guildurl"),
    @NamedQuery(name = "Guild.findByActive", query = "SELECT g FROM Guild g WHERE g.active = :active"),
    @NamedQuery(name = "Guild.findByCreation", query = "SELECT g FROM Guild g WHERE g.creation = :creation")
})
public class Guild implements Serializable
{

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "name")
    private String name;
    @Size(max = 100)
    @Column(name = "title")
    private String title;
    @Column(name = "daysguilddeath")
    private Integer daysguilddeath;
    @Column(name = "maxguilddeath")
    private Integer maxguilddeath;
    @Column(name = "minguildmembers")
    private Integer minguildmembers;
    @Column(name = "minguildlevel")
    private Integer minguildlevel;
    @Lob
    @Size(max = 65535)
    @Column(name = "guilddescription")
    private String guilddescription;
    @Size(max = 255)
    @Column(name = "guildurl")
    private String guildurl;
    @Basic(optional = false)
    @NotNull
    @Column(name = "active")
    private Boolean active;
//    @Basic(optional = false)
//    @Column(name =  "creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creation;
    @Lob
    @Size(max = 65535)
    @Column(name = "logonmessage")
    private String logonmessage;
    @OneToMany(mappedBy = "guild")
    private Collection<Person> members;
    @JoinColumn(name = "owner", referencedColumnName = "name")
    @ManyToOne
    private Admin owner;
    @JoinColumn(name = "bossname", nullable = false, referencedColumnName = "name")
    @ManyToOne(optional = false)
    private Person bossname;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "guild")
    private Collection<Guildrank> guildrankCollection;

    public Guild()
    {
    }

    public Guild(String name)
    {
        this.name = name;
    }

    public Guild(String name, Boolean active, Date creation)
    {
        this.name = name;
        this.active = active;
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

    public Integer getDaysguilddeath()
    {
        return daysguilddeath;
    }

    public void setDaysguilddeath(Integer daysguilddeath)
    {
        this.daysguilddeath = daysguilddeath;
    }

    public Integer getMaxguilddeath()
    {
        return maxguilddeath;
    }

    public void setMaxguilddeath(Integer maxguilddeath)
    {
        this.maxguilddeath = maxguilddeath;
    }

    public Integer getMinguildmembers()
    {
        return minguildmembers;
    }

    public void setMinguildmembers(Integer minguildmembers)
    {
        this.minguildmembers = minguildmembers;
    }

    public Integer getMinguildlevel()
    {
        return minguildlevel;
    }

    public void setMinguildlevel(Integer minguildlevel)
    {
        this.minguildlevel = minguildlevel;
    }

    public String getDescription()
    {
        return guilddescription;
    }

    public void setDescription(String guilddescription)
    {
        this.guilddescription = guilddescription;
    }

    public String getHomepage()
    {
        return guildurl;
    }

    public void setHomepage(String guildurl)
    {
        this.guildurl = guildurl;
    }

    public Boolean getActive()
    {
        return active;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }

    public Date getCreation()
    {
        return creation;
    }

    public void setCreation(Date creation)
    {
        this.creation = creation;
    }

    public String getLogonmessage()
    {
        return logonmessage;
    }

    public void setLogonmessage(String logonmessage)
    {
        this.logonmessage = logonmessage;
    }

    public Collection<Person> getMembers()
    {
        return members;
    }

    public void setMembers(Collection<Person> personCollection)
    {
        this.members = personCollection;
    }

    public Admin getOwner()
    {
        return owner;
    }

    public void setOwner(Admin owner)
    {
        this.owner = owner;
    }

    public Person getBossname()
    {
        return bossname;
    }

    public void setBossname(Person bossname)
    {
        this.bossname = bossname;
    }

    public Collection<Guildrank> getGuildrankCollection()
    {
        return guildrankCollection;
    }

    public void setGuildrankCollection(Collection<Guildrank> guildrankCollection)
    {
        this.guildrankCollection = guildrankCollection;
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
        if (!(object instanceof Guild))
        {
            return false;
        }
        Guild other = (Guild) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.game.Guild[ name=" + name + " ]";
    }

    /**
     * Returns a description in case the guild is in danger of being purged from
     * the database. This is necessary to make sure there is not a plethora of
     * guilds with few members.
     * <P>
     * The following preconditions are in effect:
     * <UL>
     * <LI>the guild is active
     * <LI>there are too few guildmembers (minguildmembers > amountofmembers)
     * </UL>
     * TODO : remove html tags here, let it return a  daysguilddeath or something
     * @return String indicating if there are too few members, usually should be
     *         the empty string if all is well.
     */
    public String getAlarmDescription()
    {
        if ((!getActive()) || (getMinguildmembers() <= getMembers().size()))
        {
            return "";
        }
        return "The minimum amount of members of this guild is "
                + getMinguildmembers()
                + ".<br/>The guild has too few members, and will be removed in "
                + getDaysguilddeath() + " days.";
    }
}
