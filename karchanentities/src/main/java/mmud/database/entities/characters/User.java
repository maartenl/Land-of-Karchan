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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import mmud.database.Attributes;
import mmud.database.RegularExpressions;
import mmud.database.entities.game.Chatline;
import mmud.database.entities.game.Chatlineusers;
import mmud.database.entities.game.DisplayInterface;
import mmud.database.entities.game.Guild;
import mmud.database.entities.game.Guildrank;
import mmud.database.entities.game.Macro;
import mmud.database.enums.God;
import mmud.encryption.Hash;
import mmud.encryption.HexEncoder;
import mmud.exceptions.MudException;
import org.apache.commons.lang3.StringUtils;

/**
 * A user in the game. Might be an administrator.
 *
 * @author maartenl
 */
@Entity
@DiscriminatorValue("0")
@NamedQueries(
  {
    @NamedQuery(name = "User.findByName", query = "SELECT p FROM User p WHERE lower(p.name) = lower(:name)"),
    @NamedQuery(name = "User.findActiveByName", query = "SELECT p FROM User p WHERE lower(p.name) = lower(:name) and p.active=1"),
    @NamedQuery(name = "User.fortunes", query = "SELECT p.name, p.copper FROM Person p WHERE p.god = 0 ORDER by p.copper DESC, p.name ASC"),
    @NamedQuery(name = "User.who", query = "SELECT p FROM User p WHERE p.god <=1 and p.active=1 "),
    @NamedQuery(name = "User.status", query = "select p from Person p, Admin a WHERE a.name = p.name AND a.validuntil > CURRENT_DATE"),
    @NamedQuery(name = "User.everybodymail", query = "select p from User p where p.lastlogin > :olddate")
  })
public class User extends Person
{

  private static final String PASSWORD_REGEXP = ".{5,}";
  private static final Logger LOGGER = java.util.logging.Logger.getLogger(User.class.getName());

  @Size(min = 5, max = 200)
  @Column(name = "address")
  private String address;
  @Size(max = 128)
  @Column(name = "newpassword")
  private String newpassword;
  @Size(max = 80)
  @Column(name = "realname")
  private String realname;
  // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
  @Size(max = 40)
  @Column(name = "email")
  private String email;
  @Column(name = "punishment")
  private Integer punishment;
  @Column(name = "lastlogin")
  private LocalDateTime lastlogin;
  @Column(name = "timeout")
  private LocalDateTime timeout;
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

  @Column(name = "rrribbits")
  private Integer rrribbits;

  @Column(name = "heehaws")
  private Integer heehaws;

  @Column(name = "ooc")
  private Boolean ooc;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
  private Set<Macro> macroCollection;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
  private Set<Chatlineusers> chatlines;

  /**
   * The list of people that you are ignoring.
   */
  @ManyToMany(targetEntity = User.class, fetch = FetchType.LAZY, cascade =
    {
      CascadeType.ALL
    })
  @JoinTable(name = "mm_ignore", joinColumns =
    {
      @JoinColumn(name = "fromperson", referencedColumnName = "name")
    }, inverseJoinColumns =
    {
      @JoinColumn(name = "toperson", referencedColumnName = "name")
    })
  private Set<User> ignoringSet = new HashSet<>();
  /**
   * The list of people that is ignoring you.
   */
  @ManyToMany(mappedBy = "ignoringSet", targetEntity = User.class, fetch = FetchType.LAZY, cascade =
    {
      CascadeType.ALL
    })
  private Set<User> ignoredSet = new HashSet<>();
  @JoinColumn(name = "guild", referencedColumnName = "name")
  @ManyToOne(fetch = FetchType.LAZY, cascade =
    {
      CascadeType.PERSIST
    })
  private Guild guild;
  /**
   * The guild rank of this person. Let me remind you that the "guild" is
   * already used twice here. This means that one of the two is going to be
   * read-only by means of the insertable and updatable as false.
   */
  @JoinColumns(
    {
      @JoinColumn(name = "guildlevel", referencedColumnName = "guildlevel"),
      @JoinColumn(name = "guild", referencedColumnName = "guildname", insertable = false, updatable = false)
    })
  @ManyToOne
  private Guildrank guildrank;

  /**
   * The list of people that you are ignoring.
   *
   * @return
   */
  public Set<User> getIgnoringSet()
  {
    return Collections.unmodifiableSet(ignoringSet);
  }

  /**
   * The list of people that are ignoring you.
   *
   * @return
   */
  public Set<User> getIgnoredSet()
  {
    return Collections.unmodifiableSet(ignoredSet);
  }

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

  /**
   * Sets the ip address or hostname of the person playing the game. Usually
   * done when logging in.
   *
   * @param address the ip address or hostname.
   */
  public void setAddress(String address)
  {
    this.address = address;
  }

  /**
   * <p>
   * Returns the SHA-512 hash of the password in the form of a 128 length String
   * containing the HEX encoding of this hash.</p>
   * <p>
   * There used to be "password" method too, but it was deprecated and has since
   * been removed.</p>
   *
   * @return 128 character hex-encoded SHA-512 hash of the password.
   */
  public String getNewpassword()
  {
    return newpassword;
  }

  /**
   * Verifies if the password is the correct one.
   * @param newpassword the password to verify
   * @return true if it's the same, false if it is empty or does not match.
   */
  public boolean verifyPassword(String newpassword) {
    if (StringUtils.isBlank(newpassword)) {
      return false;
    }
    return getNewpassword().equals(new HexEncoder(128).encrypt(newpassword, Hash.SHA_512));
  }

  /**
   * Sets the password of the person. Can contain any character, but has to have
   * at least size of 5. It is not a good idea to enter a 128 length password
   * here, as this method will assume it is already encrypted.
   *
   * @param newpassword the new password.
   * @throws MudException if the password is not allowed.
   */
  public void setNewpassword(String newpassword) throws MudException
  {
    if (newpassword == null)
    {
      // cannot remove a password from a user!
      return;
    }
    if (newpassword.length() == 128)
    {
      this.newpassword = newpassword;
      return;
    }
    RegularExpressions.checkRegexp(PASSWORD_REGEXP, newpassword, "A password should be at least 5 characters long.");
    this.newpassword = new HexEncoder(128).encrypt(newpassword, Hash.SHA_512);
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

  /**
   * Returns the last time the user was logged in. Can return null, which means
   * the user has never once logged on and is new. (or not a user)
   *
   * @return the date of last logged on.
   */
  public LocalDateTime getLastlogin()
  {
    return lastlogin;
  }

  public void setLastlogin(LocalDateTime lastlogin)
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
   * activate a character
   *
   * @see #deactivate()
   * @see #isActive()
   */
  public void activate() throws MudException
  {
    if (!isUser())
    {
      throw new MudException("user not a user");
    }
    this.setLastlogin(LocalDateTime.now());
    this.setActive(true);
  }

  /**
   * deactivate a character (usually because someone typed quit.)
   *
   * @see #activate
   * @see #isActive()
   */
  public void deactivate()
  {
    setActive(false);
    setLastlogin(LocalDateTime.now());
  }

  public boolean isNewUser()
  {
    return lastlogin == null;
  }

  public DisplayInterface writeMacros()
  {
    return new DisplayInterface()
    {
      @Override
      public String getMainTitle() throws MudException
      {
        return "Macros";
      }

      @Override
      public String getImage() throws MudException
      {
        // TODO : create a macro icon.
        return null;
      }

      @Override
      public String getBody() throws MudException
      {
        StringBuilder sb = new StringBuilder("<table><tr><td><b>macro</b></td><td><b>contents</b></td></tr>");
        for (Macro m : macroCollection)
        {
          sb.append("<tr><td>").append(m.getMacroname()).append("</td><td>").append(m.getContents()).append("</td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
      }

    };
  }

  public void createChatline(Chatline chatline)
  {
    if (hasChatLine(chatline.getChatname()))
    {
      return;
    }
    Chatlineusers user = new Chatlineusers();
    user.setUser(this);
    user.setChatline(chatline);
    chatlines.add(user);
  }

  public boolean joinChatline(Chatline chatline)
  {
    if (hasChatLine(chatline.getChatname()))
    {
      return false;
    }
    if (chatline.getAttributename() == null ||
      chatline.getAttributename().trim().equals("") ||
      getAttribute(chatline.getAttributename()) != null)
    {
      Chatlineusers user = new Chatlineusers();
      user.setUser(this);
      user.setChatline(chatline);
      chatlines.add(user);
      return true;
    }
    return false;
  }

  public boolean leaveChatLine(String chatlinename)
  {
    if (!hasChatLine(chatlinename))
    {
      return false;
    }
    chatlines.stream()
      .filter(x -> x.getChatline().getChatname().equalsIgnoreCase(chatlinename))
      .findFirst()
      .ifPresent(xx -> chatlines.remove(xx));
    return true;
  }

  public boolean hasChatLine(String name)
  {
    Optional<Chatlineusers> first = chatlines.stream()
      .filter(x -> x.getChatline().getChatname().equalsIgnoreCase(name))
      .findFirst();
    if (first.isPresent())
    {
      String attributename = first.get().getChatline().getAttributename();
      return attributename == null ||
        attributename.trim().equals("") ||
        (getAttribute(attributename) != null &&
          "true".equalsIgnoreCase(getAttribute(attributename).getValue()));
    }
    return false;
  }

  public Optional<Chatline> getChatLine(String name)
  {
    Optional<Chatline> first = chatlines.stream()
      .map(Chatlineusers::getChatline)
      .filter(x -> x.getChatname().equalsIgnoreCase(name))
      .findFirst();
    if (first.isPresent())
    {
      String attributename = first.get().getAttributename();
      if (attributename == null ||
        attributename.trim().equals("") ||
        (getAttribute(attributename) != null &&
          "true".equalsIgnoreCase(getAttribute(attributename).getValue())))
      {
        return first;
      }
    }
    return Optional.empty();
  }

  /**
   * Removes a macro from the existing macros. Returns false if the removal
   * failed (for instance if the macro did not exist.)
   *
   * @param string the name of the macro to remove
   * @return true if success, otherwise false.
   */
  public boolean removeMacro(String string)
  {
    Macro found = getMacro(string);
    if (found == null)
    {
      return false;
    }
    return macroCollection.remove(found);
  }

  /**
   * Adds a macro to the list of macros. Will do nothing if the macro already
   * exists.
   *
   * @param macro the macro to add.
   */
  public void addMacro(Macro macro)
  {
    if (getMacro(macro.getMacroname()) != null)
    {
      return;
    }
    macroCollection.add(macro);
  }

  /**
   * Returns the macro based on the name of the macro.
   *
   * @param string the name of the macro
   * @return the macro itself.
   */
  public Macro getMacro(String string)
  {
    Macro found = null;
    for (Macro macro : macroCollection)
    {
      if (macro.getMacroname().equalsIgnoreCase(string))
      {
        found = macro;
        break;
      }
    }
    return found;
  }

  /**
   * Adds a user to be ignored to the big ignore list.
   *
   * @param aUser
   */
  public void addAnnoyingUser(User aUser)
  {
    if (aUser == null)
    {
      return;
    }
    ignoringSet.add(aUser);
    aUser.ignoredSet.add(this);
  }

  /**
   * Removes a user from the big ignore list. Assumedly, because he is no longer
   * annoying.
   *
   * @param aUser
   * @return true if the user was found, and successfully removed from the set.
   */
  public boolean removeAnnoyingUser(User aUser)
  {
    if (aUser == null)
    {
      return false;
    }
    boolean result = ignoringSet.remove(aUser);
    if (result == false)
    {
      return false;
    }
    return aUser.ignoredSet.remove(aUser);
  }

  /**
   * Whether or not you are ignoring a certain person. The default is that
   * nobody is ignoring you.
   *
   * @param aPerson the person to check in your ignore list.
   * @return true if the person should be ignored, false otherwise.
   */
  @Override
  public boolean isIgnoring(Person aPerson)
  {
    return ignoringSet.contains(aPerson);
  }

  public Guild getGuild()
  {
    return guild;
  }

  /**
   * Sets the guild a person (user) belongs to. Also can be used to remove
   * someone from the guild by providing a "null". When the latter case, also
   * the guildrank is automatically cleared.
   *
   * @param guild the new guild, or null if the person is removed from the
   *              guild.
   */
  public void setGuild(Guild guild)
  {
    if (guild == null)
    {
      this.guildrank = null;
    }
    this.guild = guild;
  }

  public Guildrank getGuildrank()
  {
    return guildrank;
  }

  public void setGuildrank(Guildrank guildrank)
  {
    if (this.guild == null)
    {
      throw new MudException("Unable to set guildrank for " + this.getName() + ". User is not part of a guild yet.");
    }
    this.guildrank = guildrank;
  }

  @Override
  public String getStatistics() throws MudException
  {
    StringBuilder stuff = new StringBuilder();

    if (getGuild() != null)
    {
      if (getGuild().getBoss().getName().equals(getName()))
      {
        stuff.append("You are the Guildmaster of <b>").append(getGuild().getTitle()).append("</b>.<br/>");
      } else
      {
        stuff.append("You are a member of <b>").append(getGuild().getTitle()).append("</b>.<br/>");
      }
    }
    return super.getStatistics() + stuff.toString();
  }

  @Override
  public boolean canReceive()
  {
    return !verifyAttribute(Attributes.CANRECEIVE, "false");
  }

  @Override
  public God getGod()
  {
    return God.DEFAULT_USER;
  }

  /**
   * Sets the timeout, i.e. the amount of minutes that a user is not allowed to
   * login to the game.
   *
   * @param minutes the number of minutes
   */
  public void setTimeout(int minutes)
  {
    if (minutes <= 0)
    {
      timeout = null;
      return;
    }
    timeout = LocalDateTime.now().plusMinutes(minutes);
  }

  /**
   * Returns the number of minutes that a user is not allowed to login to the
   * game.
   *
   * @return
   */
  public long getTimeout()
  {
    if (timeout == null)
    {
      return 0;
    }
    LocalDateTime now = LocalDateTime.now();
    if (timeout.isBefore(now))
    {
      return 0;
    }
    return Duration.between(now, timeout).toMinutes();
  }

  public int getFrogging()
  {
    if (rrribbits == null)
    {
      rrribbits = 0;
    }
    return rrribbits;
  }

  public int getJackassing()
  {
    if (heehaws == null)
    {
      heehaws = 0;
    }
    return heehaws;
  }

  public void setFrogging(int i)
  {
    if (i < 0)
    {
      throw new MudException("Frogging should not be less than 0, but was " + i + ".");
    }
    rrribbits = i;
  }

  public void lessFrogging()
  {
    rrribbits--;
    if (rrribbits < 0)
    {
      rrribbits = 0;
    }
  }

  public void setJackassing(int i)
  {
    if (i < 0)
    {
      throw new MudException("Jackassing should not be less than 0, but was " + i + ".");
    }
    heehaws = i;
  }

  public void lessJackassing()
  {
    heehaws--;
    if (heehaws < 0)
    {
      heehaws = 0;
    }
  }

  @Override
  public String getRace()
  {
    if (getFrogging() > 0)
    {
      return "frog";
    }
    if (getJackassing() > 0)
    {
      return "jackass";
    }
    return super.getRace();
  }

  public boolean getOoc()
  {
    return ooc == null ? false : ooc;
  }

  public void setOoc(boolean b)
  {
    this.ooc = b;
  }


}
