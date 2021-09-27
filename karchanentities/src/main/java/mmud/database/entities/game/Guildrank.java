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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <img src="doc-files/Guildrank.png"/>
 * @author maartenl
 *
 *
 * @startuml doc-files/Guildrank.png
 *
 * class User {
 * -name
 * -guild
 * -guildlevel
 * }
 * class Guild {
 * -name
 * -
 * }
 * class Guildrank {
 * -guildlevel
 * -guildname
 * }
 * Guild "1" *-- "many" Guildrank : contains
 * Guild "1" *-- "many" User : memberOf
 * User "many" *-- "1" Guildrank : hasRank
 * @enduml
 */
@Entity
@Table(name = "mm_guildranks")
@NamedQueries(
        {
          @NamedQuery(name = "Guildrank.findAll", query = "SELECT g FROM Guildrank g")
          ,
            @NamedQuery(name = "Guildrank.findByGuildlevel", query = "SELECT g FROM Guildrank g WHERE g.guildrankPK.guildlevel = :guildlevel")
          ,
            @NamedQuery(name = "Guildrank.findByGuildname", query = "SELECT g FROM Guildrank g WHERE g.guildrankPK.guildname = :guildname")
        })
public class Guildrank implements Serializable
{

  private static final long serialVersionUID = 1L;
  @EmbeddedId
  protected GuildrankPK guildrankPK;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "title")
  private String title;
  @Basic(optional = false)
  @NotNull
  @Column(name = "accept_access")
  private boolean acceptAccess;
  @Basic(optional = false)
  @NotNull
  @Column(name = "reject_access")
  private boolean rejectAccess;
  @Basic(optional = false)
  @NotNull
  @Column(name = "settings_access")
  private boolean settingsAccess;
  @Basic(optional = false)
  @NotNull
  @Column(name = "logonmessage_access")
  private boolean logonmessageAccess;
  @JoinColumn(name = "guildname", referencedColumnName = "name", insertable = false, updatable = false)
  @ManyToOne(optional = false)
  private Guild guild;

  public Guildrank()
  {
  }

  public Guildrank(GuildrankPK guildrankPK)
  {
    this.guildrankPK = guildrankPK;
  }

  public Guildrank(GuildrankPK guildrankPK, String title, boolean acceptAccess, boolean rejectAccess, boolean settingsAccess, boolean logonmessageAccess)
  {
    this.guildrankPK = guildrankPK;
    this.title = title;
    this.acceptAccess = acceptAccess;
    this.rejectAccess = rejectAccess;
    this.settingsAccess = settingsAccess;
    this.logonmessageAccess = logonmessageAccess;
  }

  public Guildrank(int guildlevel, String guildname)
  {
    this.guildrankPK = new GuildrankPK(guildlevel, guildname);
  }

  public GuildrankPK getGuildrankPK()
  {
    return guildrankPK;
  }

  public void setGuildrankPK(GuildrankPK guildrankPK)
  {
    this.guildrankPK = guildrankPK;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public boolean getAcceptAccess()
  {
    return acceptAccess;
  }

  public void setAcceptAccess(boolean acceptAccess)
  {
    this.acceptAccess = acceptAccess;
  }

  public boolean getRejectAccess()
  {
    return rejectAccess;
  }

  public void setRejectAccess(boolean rejectAccess)
  {
    this.rejectAccess = rejectAccess;
  }

  public boolean getSettingsAccess()
  {
    return settingsAccess;
  }

  public void setSettingsAccess(boolean settingsAccess)
  {
    this.settingsAccess = settingsAccess;
  }

  public boolean getLogonmessageAccess()
  {
    return logonmessageAccess;
  }

  public void setLogonmessageAccess(boolean logonmessageAccess)
  {
    this.logonmessageAccess = logonmessageAccess;
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
    hash += (guildrankPK != null ? guildrankPK.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object)
  {
    // Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Guildrank))
    {
      return false;
    }
    Guildrank other = (Guildrank) object;
    if ((this.guildrankPK == null && other.guildrankPK != null) || (this.guildrankPK != null && !this.guildrankPK.equals(other.guildrankPK)))
    {
      return false;
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "mmud.database.entities.game.Guildrank[ guildrankPK=" + guildrankPK + " ]";
  }

}
