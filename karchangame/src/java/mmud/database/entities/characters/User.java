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

import java.util.Date;
import java.util.Random;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import mmud.Utils;
import mmud.exceptions.MudException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A user in the game. Might be an administrator.
 * @author maartenl
 */
@Entity
@DiscriminatorValue("0")
@NamedQueries(


{
    @NamedQuery(name = "User.fortunes", query = "SELECT p.name, p.copper FROM Person p WHERE p.god = 0 ORDER by p.copper DESC, p.name ASC"),
    @NamedQuery(name = "User.who", query = "SELECT p FROM Person p WHERE p.god <=1 and p.active=1 "),
    @NamedQuery(name = "User.status", query = "select p from Person p, Admin a WHERE a.name = p.name AND a.validuntil > CURRENT_DATE"),
    @NamedQuery(name = "User.authorise", query = "select p from Person p WHERE p.name = :name and p.password = sha1(:password)")
})
public class User extends Person
{

    private static final String PASSWORD_REGEXP = ".{5,}";
    private static final Logger itsLog = LoggerFactory.getLogger(User.class);
    @Size(min = 5, max = 200)
    @Column(name = "address")
    private String address;
    @Size(max = 40)
    // TODO get this fixed properly with a sha1 hash code upon insert.
    @Column(name = "password")
    @Pattern(regexp = PASSWORD_REGEXP, message = "Invalid password")
    private String password;
    @Size(max = 80)
    @Column(name = "realname")
    private String realname;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 40)
    @Column(name = "email")
    private String email;
    @Size(max = 40)
    @Column(name = "lok")
    private String lok;
    @Column(name = "punishment")
    private Integer punishment;
    @Column(name = "lastlogin")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastlogin;
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
    @Column(name = "lastcommand")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastcommand;

    public User()
    {
        super();
        this.punishment = 0;
        this.lastlogin = null;
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

    public Integer getPunishment()
    {
        return punishment;
    }

    public void setPunishment(Integer punishment)
    {
        this.punishment = punishment;
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


    /**
     * Returns the last time that a command was issued. Used for determining
     * idle time.
     * @return the date of the last time a command was entered.
     */
    public Date getLastcommand()
    {
        return lastcommand;
    }

    /**
     * Sets the last time that a command was issued to *now*.
     */
    public void setNow()
    {
        this.lastcommand = new Date();
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

    /**
     * deactivate a character (usually because someone typed quit.)
     */
    public void deactivate()
    {
        setActive(false);
        setLok(null);
        setLastlogin(new Date());
    }

    public boolean isNewUser()
    {
        return lastlogin == null;
    }
}
