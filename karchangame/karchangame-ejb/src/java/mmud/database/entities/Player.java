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
import java.util.Random;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author maartenl
 */
@Entity
@DiscriminatorValue("0")
@NamedQueries(
{
    @NamedQuery(name = "Player.findByAddress", query = "SELECT p FROM Player p WHERE p.address = :address"),
    @NamedQuery(name = "Player.findByEmail", query = "SELECT p FROM Player p WHERE p.email = :email")
})
public class Player extends Person implements Serializable
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
    @Size(max = 80)
    @Column(name = "realname")
    private String realname;
    @Column(name = "punishment")
    private Integer punishment;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 40)
    @Column(name = "email")
    private String email;
    @Column(name = "lastlogin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastlogin;
    @JoinColumn(name = "guild", referencedColumnName = "name")
    @ManyToOne
    private Guild guild;
    @Size(max = 40)
    @Column(name = "lok")
    private String lok;

    public Player()
    {
    }

    public Player(String name)
    {
        this.name = name;
    }

    public Player(String name, String race, String sex, Date creation)
    {
        this.name = name;
        this.setRace(race);
        this.setSex(sex);
        this.setCreation(creation);
    }

    /**
     * verify sessionpassword
     *
     * @param aSessionPassword
     *            the sessionpassword to be verified.
     * @return boolean, true if the sessionpassword provided is an exact match
     *         with the original sessionpassword.
     */
    public boolean verifySessionPassword(String aSessionPassword)
    {
        return (lok == null ? false : lok.equals(aSessionPassword));
    }

    /**
     * retrieve sessionpassword
     *
     * @return String containing the session password
     */
    public String getSessionPassword()
    {
        return lok;
    }

    /**
     * set sessionpassword
     *
     * @param aSessionPassword
     *            sets the session password.
     */
    public void setSessionPassword(String aSessionPassword)
    {
        this.lok = aSessionPassword;
    }

    public Date getLastlogin()
    {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin)
    {
        this.lastlogin = lastlogin;
    }

    /**
     * Returns the address
     *
     * @return String containing the address
     */
    public String getAddress()
    {
        return address;
    }

    /**
     * Sets the address
     *
     * @param anAddress
     *            String containing the address
     */
    public void setAddress(String address)
    {
        this.address = address;
    }

    /**
     * Returns the password
     *
     * @return String containing password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * generate a session password to be used by player during game session Use
     * getSessionPassword to return a String containing 25 random digits,
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

    /**
     * Sets the password. The password can only be set, if it has not been
     * previously set, i.e. when the password provided in the constructor is the
     * null value.
     *
     * @param aPassword
     *            String containing password
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Verifies the password
     *
     * @return boolean, returns true if password provided is an exact match.
     * @param aPassword
     *            the password that needs to be verified.
     */
    public boolean verifyPassword(String aPassword)
    {
        return (password != null && password.equals(aPassword));
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

    public Integer getPunishment()
    {
        return punishment;
    }

    public void setPunishment(Integer punishment)
    {
        this.punishment = punishment;
    }

    public Guild getGuild()
    {
        return guild;
    }

    public void setGuild(Guild guild)
    {
        this.guild = guild;
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
        if (!(object instanceof Player))
        {
            return false;
        }
        Player other = (Player) object;
        if ((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mmud.database.entities.Player[ name=" + name + " ]";
    }
}
